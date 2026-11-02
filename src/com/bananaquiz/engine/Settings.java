package com.bananaquiz.engine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

// per-user settings, one shared file in ~/.bananaquiz like the other data
// files. lines are user:key=value, usernames cannot hold ':' or '=' so the
// split stays unambiguous, and the name never becomes a file path
public class Settings {

	public int seconds = 20;
	public boolean liveBoard = true;
	public boolean showOffline = true;

	String user;

	static String file() {
		String home = System.getProperty("user.home");
		File d = new File(home, ".bananaquiz");
		d.mkdirs();
		return new File(d, "settings.txt").getAbsolutePath();
	}

	public Settings(String user) {
		this.user = user == null ? "" : user;
		load();
	}

	public void load() {
		seconds = 20;
		liveBoard = true;
		showOffline = true;
		try {
			if (!Files.exists(Paths.get(file()))) {
				return;
			}
			String prefix = user + ":";
			for (String line : Files.readAllLines(Paths.get(file()))) {
				if (!line.startsWith(prefix)) {
					continue;
				}
				String rest = line.substring(prefix.length());
				int eq = rest.indexOf('=');
				if (eq < 1) {
					continue;
				}
				String key = rest.substring(0, eq).trim();
				String val = rest.substring(eq + 1).trim();
				if (key.equals("seconds")) {
					seconds = Integer.parseInt(val);
				} else if (key.equals("liveBoard")) {
					liveBoard = val.equals("true");
				} else if (key.equals("showOffline")) {
					showOffline = val.equals("true");
				}
			}
		} catch (Exception e) {
			System.out.println("bad settings, using defaults");
		}
		// a hand edited file cannot make the timer silly
		if (seconds < 10) {
			seconds = 10;
		}
		if (seconds > 60) {
			seconds = 60;
		}
	}

	public void save() {
		try {
			List<String> keep = new ArrayList<String>();
			if (Files.exists(Paths.get(file()))) {
				for (String line : Files.readAllLines(Paths.get(file()))) {
					// drop this user's old lines, keep everyone else's
					if (!line.startsWith(user + ":")) {
						keep.add(line);
					}
				}
			}
			keep.add(user + ":seconds=" + seconds);
			keep.add(user + ":liveBoard=" + liveBoard);
			keep.add(user + ":showOffline=" + showOffline);
			StringBuilder out = new StringBuilder();
			for (String line : keep) {
				out.append(line).append(System.lineSeparator());
			}
			Files.write(Paths.get(file()), out.toString().getBytes("UTF-8"),
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (Exception e) {
			System.out.println("could not save settings");
		}
	}
}
