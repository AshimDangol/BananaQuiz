package com.bananaquiz.peripherals;

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

import com.bananaquiz.engine.Board;

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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
