package com.weatherquiz.peripherals;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.weatherquiz.engine.Board;

// scores table, admin sees every row, normal users only their own
public class BoardGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	String[] cols = { "#", "name", "score", "tries", "mode" };
	JTable table = new JTable();
	String user;
	boolean admin;

	public BoardGUI(String user) {
		super("Leaderboard");
		Theme.apply();
		this.user = user;
		this.admin = new LoginData().isAdmin(user);
		if (!admin) {
			setTitle("My scores - " + user);
		} else {
			setTitle("All scores (admin)");
		}

		table.setRowHeight(24);
		table.setFillsViewportHeight(true);
		table.setDefaultEditor(Object.class, null);
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(460, 320));
		add(scroll, BorderLayout.CENTER);

		JButton refresh = new JButton("Refresh");
		Theme.button(refresh, Theme.ACCENT);
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});
		JPanel south = new JPanel();
		south.add(refresh);
		add(south, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		load();
	}

	void load() {
		List<Board.Row> rows = Board.top(50);
		java.util.ArrayList<String[]> list = new java.util.ArrayList<String[]>();
		int i = 1;
		if (admin) {
			for (Board.Row r : rows) {
				list.add(new String[] { String.valueOf(i++), r.name,
						String.valueOf(r.score), String.valueOf(r.tries), r.mode });
			}
		} else {
			for (Board.Row r : rows) {
				if (r.name.equals(user)) {
					list.add(new String[] { String.valueOf(i++), r.name,
							String.valueOf(r.score), String.valueOf(r.tries), r.mode });
				}
			}
		}
		String[][] out;
		if (list.isEmpty()) {
			out = new String[][] { { "", "no scores yet, play first", "", "", "" } };
		} else {
			out = list.toArray(new String[0][]);
		}
		table.setModel(new DefaultTableModel(out, cols) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
	}
}
