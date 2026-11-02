package com.bananaquiz.peripherals;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

// scores window, built on BoardPanel so the table logic lives in one place
public class BoardGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	BoardPanel panel;
	String user;

	public BoardGUI(String user) {
		super("Leaderboard");
		Theme.apply();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.user = user;

		panel = new BoardPanel();
		// polls like the quiz panel, so a score saved elsewhere turns up
		// without pressing Refresh (timer stops itself when we dispose)
		panel.startLive();
		add(panel, BorderLayout.CENTER);

		JButton refresh = new JButton("Refresh");
		Theme.button(refresh, Theme.ACCENT);
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.load();
			}
		});
		JButton back = new JButton("back");
		Theme.button(back, Theme.SURFACE);
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MenuGUI m = new MenuGUI(user);
				m.setVisible(true);
				dispose();
			}
		});
		JPanel south = new JPanel();
		south.add(refresh);
		south.add(back);
		add(south, BorderLayout.SOUTH);

		setMinimumSize(new Dimension(480, 380));
		Theme.openCentered(this);
		setVisible(true);
	}
}
