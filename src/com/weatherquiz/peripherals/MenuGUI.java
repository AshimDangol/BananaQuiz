package com.weatherquiz.peripherals;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
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

		JLabel banner = new JLabel("Banana Quiz", JLabel.CENTER);
		banner.setFont(new Font("SansSerif", Font.BOLD, 22));
		banner.setOpaque(true);
		banner.setBackground(Theme.HEADER_BG);
		banner.setForeground(Theme.HEADER_FG);
		banner.setBorder(new EmptyBorder(12, 12, 12, 12));
		getContentPane().add(banner, BorderLayout.NORTH);

		JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
		panel.setBorder(new EmptyBorder(12, 16, 12, 16));
		JLabel hi = new JLabel("hi " + user + " (" + role + "), pick one:", JLabel.CENTER);
		panel.add(hi);

		JButton play = new JButton("Play banana quiz");
		JButton board = new JButton("Leaderboard");
		JButton logout = new JButton("Logout");

		panel.add(play);
		panel.add(board);
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
			}
		});
		logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new LoginData().clearSession();
				QuizLoginGUI l = new QuizLoginGUI();
				l.setVisible(true);
				dispose();
			}
		});

		getContentPane().add(panel, BorderLayout.CENTER);
		setMinimumSize(new Dimension(320, 300));
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
