package com.bananaquiz.peripherals;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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

	private BufferedImage image = null;
	private String placeholder = "";
	private float alpha = 1f;
	private Timer fade = null;

	public FadeImagePanel() {
		setOpaque(true);
	}

	// scale to fit the picture area, then run the fade from blank
	public void setImage(BufferedImage img) {
		stopFade();
		this.image = scaleTo(img);
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
				int x = (getWidth() - image.getWidth()) / 2;
				int y = (getHeight() - image.getHeight()) / 2;
				g2.drawImage(image, x, y, null);
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

	private static BufferedImage scaleTo(BufferedImage src) {
		if (src == null) {
			return null;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		double r = Math.min((double) MAX_W / w, (double) MAX_H / h);
		if (r >= 1.0) {
			return src;
		}
		int nw = (int) (w * r);
		int nh = (int) (h * r);
		BufferedImage out = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = out.createGraphics();
		try {
			g.drawImage(src.getScaledInstance(nw, nh, Image.SCALE_SMOOTH), 0, 0, null);
		} finally {
			g.dispose();
		}
		return out;
	}
}
