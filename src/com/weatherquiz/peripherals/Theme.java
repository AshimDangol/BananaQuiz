package com.weatherquiz.peripherals;

import java.awt.Color;
import java.awt.Font;

import javax.swing.UIManager;

// one place for look and feel so every window matches
public class Theme {

	public static final Color HEADER_BG = new Color(30, 58, 95);
	public static final Color HEADER_FG = new Color(255, 255, 255);
	public static final Color ACCENT = new Color(41, 128, 185);
	public static final Color GOOD = new Color(39, 174, 96);
	public static final Color BAD = new Color(192, 57, 43);

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
}
