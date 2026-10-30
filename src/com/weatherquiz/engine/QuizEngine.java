package com.weatherquiz.engine;

// keeps score and talks to BananaFetch for each question
public class QuizEngine {

	BananaFetch banana = new BananaFetch();

	public int bananaSolution = -1;
	public boolean bananaOffline = false;

	public int score = 0;
	int tries = 0;
	String player = null;

	public QuizEngine(String player) {
		this.player = player;
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

	// timeout counts as a miss with no point
	public void miss() {
		tries++;
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
}
