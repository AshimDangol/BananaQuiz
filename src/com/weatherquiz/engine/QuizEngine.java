package com.weatherquiz.engine;

import java.util.Random;

import com.weatherquiz.engine.WeatherServer.City;

// picks two cities and remembers temps + right answer
public class QuizEngine {

	WeatherServer server = new WeatherServer();
	BananaFetch banana = new BananaFetch();
	Random rnd = new Random();

	public City cityA = null;
	public City cityB = null;
	public double tempA = 0;
	public double tempB = 0;

	public int bananaSolution = -1;
	public boolean bananaOffline = false;

	public int score = 0;
	int tries = 0;
	String player = null;

	public QuizEngine(String player) {
		this.player = player;
	}

	public void nextQuestion() {
		int n = server.getCities().size();
		int i = rnd.nextInt(n);
		int j = rnd.nextInt(n);
		while (j == i) {
			j = rnd.nextInt(n);
		}
		cityA = server.getCities().get(i);
		cityB = server.getCities().get(j);
		tempA = server.getTempOrFallback(cityA);
		tempB = server.getTempOrFallback(cityB);
		// avoid draws, nudge one temp if too close
		if (Math.abs(tempA - tempB) < 0.2) {
			tempB += 1.0;
		}
	}

	// choice 0 = A hotter, 1 = B hotter
	public boolean checkAnswer(int choice) {
		tries++;
		int right = tempA > tempB ? 0 : 1;
		if (choice == right) {
			score++;
			return true;
		}
		return false;
	}

	// over/under mode, line e.g. 12.0
	public boolean checkOverUnder(String cityName, double line, boolean sayOver) {
		tries++;
		double t = 0;
		if (cityA.name.equals(cityName)) {
			t = tempA;
		} else {
			t = tempB;
		}
		boolean isOver = t > line;
		if (isOver == sayOver) {
			score++;
			return true;
		}
		return false;
	}

	public boolean nextBanana() {
		boolean ok = banana.next();
		if (ok) {
			bananaSolution = banana.solution;
			bananaOffline = banana.offline;
		}
		return ok;
	}

	public java.awt.image.BufferedImage getBananaImage() {
		return banana.image;
	}

	public boolean checkBanana(int guess) {
		tries++;
		if (guess == bananaSolution) {
			score++;
			return true;
		}
		return false;
	}

	public String getPlayer() {
		return player;
	}

	public int getScore() {
		return score;
	}

	public int getTries() {
		return tries;
	}

	public boolean wasOffline() {
		return server.wasOffline();
	}
}
