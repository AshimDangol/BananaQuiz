package com.weatherquiz.peripherals;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
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
		UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 13));
		UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 13));
	}

	// flat button, white text, subtle darker edge
	public static void button(JButton b, Color bg) {
		b.setOpaque(true);
		b.setBackground(bg);
		b.setForeground(Color.WHITE);
		b.setFocusPainted(false);
		b.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(bg.darker()),
				BorderFactory.createEmptyBorder(7, 16, 7, 16)));
	}

	public static JLabel banner(String text, int size) {
		JLabel l = new JLabel(text, JLabel.CENTER);
		l.setFont(new Font("SansSerif", Font.BOLD, size));
		l.setOpaque(true);
		l.setBackground(HEADER_BG);
		l.setForeground(HEADER_FG);
		l.setBorder(BorderFactory.createEmptyBorder(14, 12, 14, 12));
		return l;
	}

	public static javax.swing.border.Border card() {
		return BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(CARD_BORDER),
				BorderFactory.createEmptyBorder(8, 8, 8, 8));
	}
}
