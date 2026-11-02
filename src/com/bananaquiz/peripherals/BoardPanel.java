package com.bananaquiz.peripherals;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import com.bananaquiz.engine.Board;

// scores table on its own so the leaderboard window and the live panel in
// the quiz can share it. admin sees every row, a normal user only their own
public class BoardPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	static final String[] COLS = { "#", "name", "score", "tries", "mode" };
	static final int REFRESH_MS = 5000;

	String user;
	boolean admin;
	JTable table = new JTable();
	Timer live = null;

	public BoardPanel(String user, boolean admin) {
		super(new BorderLayout());
		this.user = user;
		this.admin = admin;
		table.setRowHeight(24);
		table.setFillsViewportHeight(true);
		table.setDefaultEditor(Object.class, null);
		add(new JScrollPane(table), BorderLayout.CENTER);
		load();
	}

	// re-read scores.txt and repaint the rows
	public void load() {
		List<Board.Row> rows = Board.top(50);
		ArrayList<String[]> list = new ArrayList<String[]>();
		int i = 1;
		for (Board.Row r : rows) {
			if (admin || r.name.equals(user)) {
				list.add(new String[] { String.valueOf(i++), r.name,
						String.valueOf(r.score), String.valueOf(r.tries), r.mode });
			}
		}
		String[][] out;
		if (list.isEmpty()) {
			out = new String[][] { { "", "no scores yet, play first", "", "", "" } };
		} else {
			out = list.toArray(new String[0][]);
		}
		table.setModel(new DefaultTableModel(out, COLS) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
	}

	// polls scores.txt so another window saving a score shows up on its own
	public void startLive() {
		if (live != null && live.isRunning()) {
			return;
		}
		live = new Timer(REFRESH_MS, e -> load());
		live.start();
	}

	// called on menu/logout/close so no timer outlives the window
	public void stopLive() {
		if (live != null && live.isRunning()) {
			live.stop();
		}
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		stopLive();
	}
}
