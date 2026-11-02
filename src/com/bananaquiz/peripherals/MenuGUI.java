package com.bananaquiz.peripherals;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

// hub after login, keeps single quiz window from doing everything
public class MenuGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	String user;

	public MenuGUI(String user) {
		super("Menu - " + user);
		Theme.apply();
		this.user = user;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String role = new LoginData().getRole(user);

		getContentPane().add(Theme.banner("Banana Quiz", 22), BorderLayout.NORTH);

		JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
		panel.setBorder(new EmptyBorder(16, 24, 16, 24));
		JLabel hi = new JLabel("hi " + user + " (" + role + "), pick one:", JLabel.CENTER);
		hi.setForeground(Theme.TEXT);
		panel.add(hi);

		JButton play = new JButton("Play banana quiz");
		JButton board = new JButton("Leaderboard");
		JButton settings = new JButton("Settings");
		JButton logout = new JButton("Logout");
		play.setFont(play.getFont().deriveFont(15f));
		Theme.button(play, Theme.ACCENT);
		Theme.button(board, Theme.SURFACE);
		Theme.button(settings, Theme.SURFACE);
		Theme.button(logout, Theme.BAD);

		panel.add(play);
		panel.add(board);
		panel.add(settings);
		panel.add(logout);

		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				QuizGUI g = new QuizGUI(user);
				g.setVisible(true);
				dispose();
			}
		});
		board.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BoardGUI b = new BoardGUI(user);
				b.setVisible(true);
				dispose();
			}
		});
		settings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SettingsGUI(user);
				dispose();
			}
		});
		logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new LoginData().clearSession();
				// login window shows itself now, no setVisible after this
				new QuizLoginGUI();
				dispose();
			}
		});

		getContentPane().add(panel, BorderLayout.CENTER);
		setMinimumSize(new Dimension(340, 300));
		Theme.openCentered(this);
		setVisible(true);
	}
}
