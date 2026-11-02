package com.bananaquiz.peripherals;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import com.bananaquiz.engine.Board;

// scores table on its own so the leaderboard window and the live panel in
// the quiz can share it. everyone gets the same full ranked list
public class BoardPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	static final String[] COLS = { "#", "name", "score", "tries", "mode" };
	static final int REFRESH_MS = 5000;

	JTable table = new JTable();
	Timer live = null;

	public BoardPanel() {
		super(new BorderLayout());
		table.setRowHeight(24);
		table.setFillsViewportHeight(true);
		table.setDefaultEditor(Object.class, null);
		add(new JScrollPane(table), BorderLayout.CENTER);
		load();
	}

	// re-read scores.txt and repaint the rows
	public void load() {
		List<Board.Row> rows = Board.top(50);
		String[][] out;
		if (rows.isEmpty()) {
			out = new String[][] { { "", "no scores yet, play first", "", "", "" } };
		} else {
			out = new String[rows.size()][COLS.length];
			for (int i = 0; i < rows.size(); i++) {
				Board.Row r = rows.get(i);
				// rank is the position in the full list, so # means the
				// same thing for every player
				out[i] = new String[] { String.valueOf(i + 1), r.name,
						String.valueOf(r.score), String.valueOf(r.tries), r.mode };
			}
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
