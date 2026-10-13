package com.weatherquiz.peripherals;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.perisic.tomato.peripherals.LoginData;

// hub after login, keeps single quiz window from doing everything
public class MenuGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	String user;

	public MenuGUI(String user) {
		super("Menu - " + user);
		this.user = user;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
		panel.add(new JLabel("hi " + user + ", pick one:"));

		JButton play = new JButton("Play quiz");
		JButton multi = new JButton("2 player hotseat");
		JButton board = new JButton("Leaderboard");
		JButton settings = new JButton("Settings");
		JButton logout = new JButton("Logout");

		panel.add(play);
		panel.add(multi);
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
		multi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String p2 = JOptionPane.showInputDialog("player 2 name?");
				if (p2 == null || p2.trim().equals("")) {
					return;
				}
				MultiGUI m = new MultiGUI(user, p2.trim());
				m.setVisible(true);
				dispose();
			}
		});
		board.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BoardGUI b = new BoardGUI(user);
				b.setVisible(true);
			}
		});
		settings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SettingsGUI s = new SettingsGUI(user);
				s.setVisible(true);
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

		getContentPane().add(panel);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
