package com.perisic.tomato.peripherals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// base code from Marc Conrad TomatoFull, extended by me
// hashing idea from https://stackoverflow.com/questions/33036467/sha-256-password-hashing-in-java
// session file works like a cookie, idea from week 5 slides on cookies
public class LoginData {

	static final String USER_FILE = "users.txt";
	static final String SESSION_FILE = "session.cookie";
	static final long SESSION_MINS = 30;

	Map<String, String> salts = new HashMap<String, String>();
	Map<String, String> hashes = new HashMap<String, String>();
	Map<String, String> roles = new HashMap<String, String>();

	public LoginData() {
		loadUsers();
		if (!hashes.containsKey("Ada")) {
			try {
				addUser("Ada", "hello23");
				roles.put("Ada", "admin");
				saveUsers();
			} catch (Exception e) {
				System.out.println("could not create default user");
			}
		}
		// old files had no role column, Ada stays admin
		if (!roles.containsKey("Ada")) {
			roles.put("Ada", "admin");
		}
	}

	String toHex(byte[] bytes) {
		String out = "";
		for (byte b : bytes) {
			String h = Integer.toHexString(b & 0xff);
			if (h.length() == 1) {
				out += "0";
			}
			out += h;
		}
		return out;
	}

	byte[] fromHex(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	String hashWithSalt(String passwd, byte[] salt) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(salt);
		byte[] hashed = md.digest(passwd.getBytes("UTF-8"));
		return toHex(hashed);
	}

	void loadUsers() {
		Path p = Paths.get(USER_FILE);
		if (!Files.exists(p)) {
			return;
		}
		try {
			for (String line : Files.readAllLines(p)) {
				String[] parts = line.split(":");
				if (parts.length == 3) {
					salts.put(parts[0], parts[1]);
					hashes.put(parts[0], parts[2]);
					roles.put(parts[0], "user");
				} else if (parts.length == 4) {
					salts.put(parts[0], parts[1]);
					hashes.put(parts[0], parts[2]);
					roles.put(parts[0], parts[3]);
				}
			}
		} catch (IOException e) {
			System.out.println("could not read " + USER_FILE);
		}
	}

	void saveUsers() {
		StringBuilder sb = new StringBuilder();
		for (String user : hashes.keySet()) {
			String role = roles.get(user);
			if (role == null) {
				role = "user";
			}
			sb.append(user + ":" + salts.get(user) + ":" + hashes.get(user) + ":" + role + "\n");
		}
		try {
			Files.write(Paths.get(USER_FILE), sb.toString().getBytes("UTF-8"));
		} catch (IOException e) {
			System.out.println("could not write " + USER_FILE);
		}
	}

	public boolean userExists(String username) {
		return hashes.containsKey(username);
	}

	public boolean addUser(String username, String passwd) throws Exception {
		if (username == null || passwd == null) {
			return false;
		}
		username = username.trim();
		if (username.equals("") || passwd.length() < 4) {
			return false;
		}
		if (userExists(username)) {
			return false;
		}
		SecureRandom rnd = new SecureRandom();
		byte[] salt = new byte[16];
		rnd.nextBytes(salt);
		String saltHex = toHex(salt);
		String hashHex = hashWithSalt(passwd, salt);
		salts.put(username, saltHex);
		hashes.put(username, hashHex);
		roles.put(username, "user");
		saveUsers();
		return true;
	}

	// roles are only ever set here or for Ada, never from a form
	public String getRole(String username) {
		String r = roles.get(username);
		return r == null ? "user" : r;
	}

	public boolean isAdmin(String username) {
		return getRole(username).equals("admin");
	}

	public boolean checkPassword(String username, String passwd) {
		return checkLogin(username, passwd);
	}

	public boolean checkLogin(String username, String passwd) {
		if (username == null || passwd == null) {
			return false;
		}
		if (!hashes.containsKey(username)) {
			return false;
		}
		try {
			byte[] salt = fromHex(salts.get(username));
			String tryHash = hashWithSalt(passwd, salt);
			return tryHash.equals(hashes.get(username));
		} catch (Exception e) {
			return false;
		}
	}

	public String createSession(String username) {
		String token = UUID.randomUUID().toString();
		long expiry = System.currentTimeMillis() + SESSION_MINS * 60 * 1000;
		String line = token + ":" + username + ":" + expiry + "\n";
		try {
			Files.write(Paths.get(SESSION_FILE), line.getBytes("UTF-8"),
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			System.out.println("could not write session");
		}
		return token;
	}

	public String checkSession() {
		Path p = Paths.get(SESSION_FILE);
		if (!Files.exists(p)) {
			return null;
		}
		try {
			String line = new String(Files.readAllBytes(p), "UTF-8").trim();
			String[] parts = line.split(":");
			if (parts.length < 3) {
				return null;
			}
			long expiry = Long.parseLong(parts[2]);
			if (System.currentTimeMillis() > expiry) {
				Files.deleteIfExists(p);
				return null;
			}
			return parts[1];
		} catch (Exception e) {
			return null;
		}
	}

	public void clearSession() {
		try {
			Files.deleteIfExists(Paths.get(SESSION_FILE));
		} catch (IOException e) {
			System.out.println("could not clear session");
		}
	}
}
