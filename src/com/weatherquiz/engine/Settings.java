package com.weatherquiz.engine;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

// tiny per-user settings file, settings_<name>.txt with key=value lines
public class Settings {

	String user;
	public int seconds = 15;
	public String mode = "Which hotter?";
	public boolean showOffline = true;

	public Settings(String user) {
		this.user = user;
		load();
	}

	String file() {
		return "settings_" + user + ".txt";
	}

	void load() {
		try {
			if (!Files.exists(Paths.get(file()))) {
				return;
			}
			Map<String, String> m = new HashMap<String, String>();
			for (String line : Files.readAllLines(Paths.get(file()))) {
				String[] p = line.split("=", 2);
				if (p.length == 2) {
					m.put(p[0].trim(), p[1].trim());
				}
			}
			if (m.containsKey("seconds")) {
				seconds = Integer.parseInt(m.get("seconds"));
			}
			if (m.containsKey("mode")) {
				mode = m.get("mode");
			}
			if (m.containsKey("showOffline")) {
				showOffline = m.get("showOffline").equals("true");
			}
		} catch (Exception e) {
			System.out.println("bad settings, using defaults");
		}
		if (seconds < 5) {
			seconds = 5;
		}
		if (seconds > 60) {
			seconds = 60;
		}
	}

	public void save() {
		String out = "seconds=" + seconds + "\nmode=" + mode + "\nshowOffline=" + showOffline + "\n";
		try {
			Files.write(Paths.get(file()), out.getBytes("UTF-8"));
		} catch (Exception e) {
			System.out.println("could not save settings");
		}
	}
}
