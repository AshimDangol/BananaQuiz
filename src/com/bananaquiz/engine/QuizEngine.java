package com.bananaquiz.engine;

// keeps score and talks to BananaFetch for each question
// structure follows GameEngine from the TomatoFull example (Marc Conrad, unit BREO site)
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

	// background thread only, does not touch the public fields,
	// the result goes back through acceptBanana on the UI thread
	public BananaFetch.Question fetchBanana() {
		return banana.fetch();
	}

	// UI thread only, swaps in a finished fetch, returns false if it
	// was null so the caller can bail before starting the timer
	public boolean acceptBanana(BananaFetch.Question q) {
		if (q == null) {
			return false;
		}
		banana.commit(q);
		bananaSolution = q.solution;
		bananaOffline = q.offline;
		return true;
	}

	// fetch and commit in one go, kept for tests
	public boolean nextBanana() {
		return acceptBanana(banana.fetch());
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
