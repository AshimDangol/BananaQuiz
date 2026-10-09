package com.weatherquiz.engine;

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

	public BufferedImage image = null;
	public int solution = -1;
	public boolean offline = false;

	String readUrl(String urlString) {
		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(6000);
			conn.setReadTimeout(6000);
			InputStream in = conn.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[4096];
			int n;
			while ((n = in.read(buf)) != -1) {
				out.write(buf, 0, n);
			}
			in.close();
			return out.toString("UTF-8");
		} catch (Exception e) {
			System.out.println("banana api fail: " + e.getMessage());
			return null;
		}
	}

	public boolean next() {
		String api = "https://marcconrad.com/uob/banana/api.php?out=csv&base64=yes";
		String raw = readUrl(api);
		if (raw == null || !raw.contains(",")) {
			return offline();
		}
		try {
			// csv is base64,png solution is after last comma
			int cut = raw.lastIndexOf(',');
			String b64 = raw.substring(0, cut).trim();
			int sol = Integer.parseInt(raw.substring(cut + 1).trim());
			byte[] bytes = Base64.getDecoder().decode(b64);
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
			if (img == null) {
				return offline();
			}
			image = img;
			solution = sol;
			offline = false;
			return true;
		} catch (Exception e) {
			System.out.println("bad banana data: " + e.getMessage());
			return offline();
		}
	}

	boolean offline() {
		try {
			InputStream in = BananaFetch.class.getResourceAsStream("/banana1.png");
			image = ImageIO.read(in);
			solution = 4;
			offline = true;
			return image != null;
		} catch (Exception e) {
			return false;
		}
	}
}
