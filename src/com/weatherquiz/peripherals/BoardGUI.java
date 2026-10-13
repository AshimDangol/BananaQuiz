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

public class BoardGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	JTextArea area = new JTextArea(15, 35);

	public BoardGUI(String user) {
		super("Leaderboard");
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
		List<Board.Row> rows = Board.top(10);
		String out = "top 10\n\n";
		int i = 1;
		for (Board.Row r : rows) {
			out += i++ + ". " + r.name + " " + r.score + "/" + r.tries + " " + r.mode + "\n";
		}
		if (rows.size() == 0) {
			out += "no scores yet, play first";
		}
		area.setText(out);
	}
}
