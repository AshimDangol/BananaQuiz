package com.bananaquiz.engine;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// scores.txt lines: name:score:tries:mode
// kept simple so it runs with no db
public class Board {

	static String file() {
		String home = System.getProperty("user.home");
		java.io.File d = new java.io.File(home, ".bananaquiz");
		d.mkdirs();
		return new java.io.File(d, "scores.txt").getAbsolutePath();
	}

	public static class Row {
		public String name;
		public int score;
		public int tries;
		public String mode;

		public Row(String name, int score, int tries, String mode) {
			this.name = name;
			this.score = score;
			this.tries = tries;
			this.mode = mode;
		}
	}

	public static void save(String name, int score, int tries, String mode) {
		String line = name + ":" + score + ":" + tries + ":" + mode + "\n";
		try {
			Files.write(Paths.get(file()), line.getBytes("UTF-8"),
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (Exception e) {
			System.out.println("could not save score");
		}
	}

	public static List<Row> top(int n) {
		List<Row> rows = new ArrayList<Row>();
		try {
			if (!Files.exists(Paths.get(file()))) {
				return rows;
			}
			for (String line : Files.readAllLines(Paths.get(file()))) {
				// one bad line should not kill the whole board
				try {
					String[] p = line.split(":");
					if (p.length >= 4) {
						rows.add(new Row(p[0], Integer.parseInt(p[1]),
								Integer.parseInt(p[2]), p[3]));
					}
				} catch (Exception badLine) {
					System.out.println("skipping bad score line: " + line);
				}
			}
		} catch (Exception e) {
			System.out.println("bad scores file");
		}
		Collections.sort(rows, new java.util.Comparator<Row>() {
			public int compare(Row a, Row b) {
				if (b.score != a.score) {
					return b.score - a.score;
				}
				return a.tries - b.tries;
			}
		});
		// one entry per player: the sort already puts each player's best
		// game first, so keep the first row seen for a name and drop the
		// rest. the file still keeps every game played
		java.util.LinkedHashMap<String, Row> best = new java.util.LinkedHashMap<String, Row>();
		for (Row r : rows) {
			if (!best.containsKey(r.name)) {
				best.put(r.name, r);
			}
		}
		rows = new ArrayList<Row>(best.values());
		if (rows.size() > n) {
			return rows.subList(0, n);
		}
		return rows;
	}
}
