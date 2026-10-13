package com.weatherquiz.peripherals;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.weatherquiz.engine.Board;
import com.perisic.tomato.peripherals.LoginData;

public class BoardGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	JTextArea area = new JTextArea(15, 35);
	String user;
	boolean admin;

	public BoardGUI(String user) {
		super("Leaderboard");
		this.user = user;
		this.admin = new LoginData().isAdmin(user);
		if (!admin) {
			setTitle("My scores - " + user);
		} else {
			setTitle("All scores (admin)");
		}
		area.setEditable(false);
		add(new JScrollPane(area), BorderLayout.CENTER);

		JButton refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});
		add(refresh, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		load();
	}

	void load() {
		List<Board.Row> rows = Board.top(50);
		String out;
		int i = 1;
		if (admin) {
			out = "all scores (admin)\n\n";
			for (Board.Row r : rows) {
				if (i > 10) {
					break;
				}
				out += i++ + ". " + r.name + " " + r.score + "/" + r.tries + " " + r.mode + "\n";
			}
		} else {
			out = "my scores - " + user + "\n\n";
			for (Board.Row r : rows) {
				if (r.name.equals(user)) {
					out += i++ + ". " + r.score + "/" + r.tries + " " + r.mode + "\n";
				}
			}
		}
		if (i == 1) {
			out += "no scores yet, play first";
		}
		area.setText(out);
	}
}
