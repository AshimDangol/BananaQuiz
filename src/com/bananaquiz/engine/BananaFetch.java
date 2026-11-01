package com.bananaquiz.engine;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

import javax.imageio.ImageIO;

// banana bonus round, uses https://marcconrad.com/uob/banana/doc.php
// same csv+base64 style as the tomato api, so easy to compare in video
public class BananaFetch {

	// bundled together so image and answer can never get mixed up
	// between two fetches running at the same time
	public static class Question {
		public final BufferedImage image;
		public final int solution;
		public final boolean offline;

		Question(BufferedImage image, int solution, boolean offline) {
			this.image = image;
			this.solution = solution;
			this.offline = offline;
		}
	}

	public BufferedImage image = null;
	public int solution = -1;
	public boolean offline = false;

	String readUrl(String urlString) {
		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(6000);
			conn.setReadTimeout(6000);
			try (InputStream in = conn.getInputStream()) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[4096];
				int n;
				while ((n = in.read(buf)) != -1) {
					out.write(buf, 0, n);
				}
				return out.toString("UTF-8");
			}
		} catch (Exception e) {
			System.out.println("banana api fail: " + e.getMessage());
			return null;
		}
	}

	// fetches without touching the public fields, the caller decides
	// when to commit the result, so a stale fetch can be thrown away
	public Question fetch() {
		String api = "https://marcconrad.com/uob/banana/api.php?out=csv&base64=yes";
		String raw = readUrl(api);
		if (raw == null || !raw.contains(",")) {
			return offlineQuestion();
		}
		try {
			// csv is base64,png solution is after last comma
			int cut = raw.lastIndexOf(',');
			String b64 = raw.substring(0, cut).trim();
			int sol = Integer.parseInt(raw.substring(cut + 1).trim());
			byte[] bytes = Base64.getDecoder().decode(b64);
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
			if (img == null) {
				return offlineQuestion();
			}
			return new Question(img, sol, false);
		} catch (Exception e) {
			System.out.println("bad banana data: " + e.getMessage());
			return offlineQuestion();
		}
	}

	Question offlineQuestion() {
		try (InputStream in = BananaFetch.class.getResourceAsStream("/banana1.png")) {
			if (in == null) {
				return null;
			}
			BufferedImage img = ImageIO.read(in);
			if (img == null) {
				return null;
			}
			return new Question(img, 4, true);
		} catch (Exception e) {
			return null;
		}
	}

	public void commit(Question q) {
		image = q.image;
		solution = q.solution;
		offline = q.offline;
	}

	// fetch and commit in one go, used by tests
	public boolean next() {
		Question q = fetch();
		if (q == null) {
			return false;
		}
		commit(q);
		return true;
	}

	// loads the bundled picture straight away, used by tests
	boolean offline() {
		Question q = offlineQuestion();
		if (q == null) {
			return false;
		}
		commit(q);
		return true;
	}
}
