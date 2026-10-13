package com.weatherquiz.engine;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

// uses Open-Meteo, docs at https://open-meteo.com/en/docs
// no json lib, I just search the string for "temperature_2m"
// idea from https://stackoverflow.com/questions/2591098/how-to-parse-json-in-java
public class WeatherServer {

	public static class City {
		public String name;
		public double lat;
		public double lon;

		public City(String name, double lat, double lon) {
			this.name = name;
			this.lat = lat;
			this.lon = lon;
		}

		public String toString() {
			return name;
		}
	}

	List<City> cities = new ArrayList<City>();
	boolean lastWasOffline = false;

	// canned temps if wifi/api down, so demo still runs
	double[] fallbackTemps = { 11.5, 10.0, 13.0, 9.5, 12.0, 11.0, 12.5, 9.0, 10.5, 8.5, 11.8, 10.2 };

	public WeatherServer() {
		cities.add(new City("Luton", 51.8787, -0.42));
		cities.add(new City("Leeds", 53.8008, -1.5491));
		cities.add(new City("London", 51.5074, -0.1278));
		cities.add(new City("Manchester", 53.4808, -2.2426));
		cities.add(new City("Bedford", 52.1364, -0.4609));
		cities.add(new City("Birmingham", 52.4862, -1.8904));
		cities.add(new City("Bristol", 51.4545, -2.5879));
		cities.add(new City("Glasgow", 55.8642, -4.2518));
		cities.add(new City("Cardiff", 51.4816, -3.1791));
		cities.add(new City("Newcastle", 54.9783, -1.6178));
		cities.add(new City("Liverpool", 53.4084, -2.9916));
		cities.add(new City("Edinburgh", 55.9533, -3.1883));
	}

	public List<City> getCities() {
		return cities;
	}

	public boolean wasOffline() {
		return lastWasOffline;
	}

	String readUrl(String urlString) {
		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(6000);
			conn.setReadTimeout(6000);
			InputStream in = conn.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n;
			while ((n = in.read(buf)) != -1) {
				out.write(buf, 0, n);
			}
			in.close();
			return out.toString("UTF-8");
		} catch (Exception e) {
			System.out.println("weather api fail: " + e.getMessage());
			return null;
		}
	}

	// pulls current temp for a city, null if it fails
	public Double getTemp(City c) {
		String url = "https://api.open-meteo.com/v1/forecast?latitude=" + c.lat
				+ "&longitude=" + c.lon + "&current=temperature_2m&timezone=Europe%2FLondon";
		String json = readUrl(url);
		if (json == null) {
			return null;
		}
		try {
			// json has two temperature_2m fields, units then value. want last one.
			String key = "\"temperature_2m\":";
			int i = json.lastIndexOf(key);
			if (i < 0) {
				return null;
			}
			String rest = json.substring(i + key.length()).trim();
			String num = "";
			for (int k = 0; k < rest.length(); k++) {
				char ch = rest.charAt(k);
				if ((ch >= '0' && ch <= '9') || ch == '.' || ch == '-') {
					num += ch;
				} else if (num.length() > 0) {
					break;
				} else if (ch == '\"') {
					// hit units string, not the number
					return null;
				} else {
					break;
				}
			}
			if (num.equals("")) {
				return null;
			}
			return Double.parseDouble(num);
		} catch (Exception e) {
			System.out.println("bad json: " + e.getMessage());
			return null;
		}
	}

	public double getTempOrFallback(City c) {
		Double live = getTemp(c);
		if (live != null) {
			lastWasOffline = false;
			return live;
		}
		lastWasOffline = true;
		for (int i = 0; i < cities.size(); i++) {
			if (cities.get(i).name.equals(c.name)) {
				return fallbackTemps[i % fallbackTemps.length];
			}
		}
		return 10.0;
	}
}
