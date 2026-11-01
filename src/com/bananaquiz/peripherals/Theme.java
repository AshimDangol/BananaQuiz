package com.bananaquiz.peripherals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

// one place for colors and styling so every window matches
public class Theme {

	public static final Color HEADER_BG = new Color(30, 58, 95);
	public static final Color HEADER_FG = new Color(255, 255, 255);
	public static final Color ACCENT = new Color(41, 128, 185);
	public static final Color SURFACE = new Color(108, 117, 125);
	public static final Color GOOD = new Color(39, 174, 96);
	public static final Color BAD = new Color(192, 57, 43);
	public static final Color WARN = new Color(255, 214, 51);
	public static final Color CARD_BG = new Color(245, 247, 250);
	public static final Color CARD_BORDER = new Color(210, 218, 228);
	public static final Color TEXT = new Color(44, 62, 80);

	static final String UI_FONT = "Segoe UI";

	// segoe ui on windows, falls back to plain sans on other systems
	public static Font font(int style, float size) {
		Font f = new Font(UI_FONT, style, (int) size);
		if (!f.getFamily().equalsIgnoreCase(UI_FONT)) {
			f = new Font("SansSerif", style, (int) size);
		}
		return f.deriveFont(size);
	}

	public static void apply() {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if (info.getName().equals("Nimbus")) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("nimbus missing, using default");
		}
		UIManager.put("Button.font", font(Font.BOLD, 13));
		UIManager.put("Label.font", font(Font.PLAIN, 13));
	}

	// flat button, white text, subtle darker edge, lightens on hover
	public static void button(final JButton b, final Color bg) {
		b.setOpaque(true);
		b.setBackground(bg);
		b.setForeground(Color.WHITE);
		b.setFocusPainted(false);
		b.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(bg.darker()),
				BorderFactory.createEmptyBorder(7, 16, 7, 16)));
		b.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				if (b.isEnabled()) {
					b.setBackground(bg.brighter());
				}
			}

			public void mouseExited(MouseEvent e) {
				b.setBackground(bg);
			}
		});
	}

	// navy to blue gradient header, nicer than a flat block
	public static JPanel banner(String text, int size) {
		final JLabel l = new JLabel(text, JLabel.CENTER);
		l.setFont(font(Font.BOLD, size));
		l.setForeground(HEADER_FG);
		l.setOpaque(false);
		JPanel p = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setPaint(new GradientPaint(0, 0, HEADER_BG, getWidth(), 0, ACCENT));
				g2.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		p.add(l, BorderLayout.CENTER);
		p.setBorder(BorderFactory.createEmptyBorder(14, 12, 14, 12));
		return p;
	}

	public static javax.swing.border.Border card() {
		return BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(CARD_BORDER),
				BorderFactory.createEmptyBorder(8, 8, 8, 8));
	}

	// every window opens through here: pack, never smaller than its minimum,
	// never bigger than the usable screen, always centered
	public static void openCentered(JFrame w) {
		w.pack();
		Dimension min = w.getMinimumSize();
		int width = Math.max(w.getWidth(), min.width);
		int height = Math.max(w.getHeight(), min.height);
		// taskbar-aware usable area, so it never opens off-screen
		Rectangle max = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds();
		width = Math.min(width, max.width);
		height = Math.min(height, max.height);
		w.setSize(width, height);
		w.setLocationRelativeTo(null);
	}
}
