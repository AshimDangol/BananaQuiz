package com.bananaquiz.peripherals;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

// picture panel that fades the image in instead of popping it on screen.
// a Swing Timer bumps one alpha value every STEP_MS and paintComponent
// draws straight through an AlphaComposite, so no per-frame images get
// built the way the old fadeIn loop did
public class FadeImagePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int STEPS = 12;   // 12 steps -> ~0.4s fade
	private static final int STEP_MS = 35;
	private static final int MAX_W = 460;
	private static final int MAX_H = 280;
	// never blow the photo up past 1.5x its real size, past that it goes soft
	private static final double ZOOM = 1.5;

	private BufferedImage image = null;
	private String placeholder = "";
	private float alpha = 1f;
	private Timer fade = null;

	public FadeImagePanel() {
		setOpaque(true);
	}

	// keeps the full size picture and lets paintComponent scale it, so the
	// photo stays sharp whatever the window ends up being
	public void setImage(BufferedImage img) {
		stopFade();
		this.image = img;
		this.placeholder = "";
		this.alpha = 0f;
		revalidate();
		repaint();
		startFade();
	}

	public void setPlaceholder(String text) {
		stopFade();
		this.image = null;
		this.placeholder = text;
		this.alpha = 1f;
		revalidate();
		repaint();
	}

	// called on menu/logout/skip so no timer outlives the question
	public void stopFade() {
		if (fade != null && fade.isRunning()) {
			fade.stop();
		}
	}

	// dropping the panel off the screen stops the timer too, so a closed
	// window cannot keep ticking in the background
	@Override
	public void removeNotify() {
		super.removeNotify();
		stopFade();
	}

	private void startFade() {
		fade = new Timer(STEP_MS, e -> {
			alpha += 1f / STEPS;
			if (alpha >= 1f) {
				alpha = 1f;
				((Timer) e.getSource()).stop();
			}
			repaint();
		});
		fade.start();
	}

	@Override
	public Dimension getPreferredSize() {
		if (image != null) {
			return new Dimension(image.getWidth(), image.getHeight());
		}
		return new Dimension(MAX_W, MAX_H);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			if (image != null) {
				g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2.setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);
				double r = Math.min(
						Math.min((double) getWidth() / image.getWidth(),
								(double) getHeight() / image.getHeight()),
						ZOOM);
				int w = (int) (image.getWidth() * r);
				int h = (int) (image.getHeight() * r);
				g2.drawImage(image, (getWidth() - w) / 2, (getHeight() - h) / 2, w, h, null);
			} else if (placeholder != null && !placeholder.isEmpty()) {
				g2.setComposite(AlphaComposite.SrcOver.derive(1f));
				g2.setColor(Theme.TEXT);
				int w = g2.getFontMetrics().stringWidth(placeholder);
				g2.drawString(placeholder, (getWidth() - w) / 2, getHeight() / 2);
			}
		} finally {
			g2.dispose();
		}
	}
}
