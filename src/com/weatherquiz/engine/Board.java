package com.weatherquiz.engine;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// scores.txt lines: name:score:tries:mode
// kept simple so it runs with no db
public class Board {

	static final String FILE = "scores.txt";

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
			Files.write(Paths.get(FILE), line.getBytes("UTF-8"),
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (Exception e) {
			System.out.println("could not save score");
		}
	}

	public static List<Row> top(int n) {
		List<Row> rows = new ArrayList<Row>();
		try {
			if (!Files.exists(Paths.get(FILE))) {
				return rows;
			}
			for (String line : Files.readAllLines(Paths.get(FILE))) {
				String[] p = line.split(":");
				if (p.length >= 4) {
					rows.add(new Row(p[0], Integer.parseInt(p[1]),
							Integer.parseInt(p[2]), p[3]));
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
		if (rows.size() > n) {
			return rows.subList(0, n);
		}
		return rows;
	}
}
