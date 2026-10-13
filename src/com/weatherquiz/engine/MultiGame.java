package com.weatherquiz.engine;

// hotseat multiplayer, 2 players take turns on same pc
// 5 rounds each by default, most points wins
public class MultiGame {

	public String p1;
	public String p2;
	public int score1 = 0;
	public int score2 = 0;
	public int round = 1;
	public int maxRounds = 5;
	public boolean p1turn = true;

	public QuizEngine quiz;

	public MultiGame(String p1, String p2) {
		this.p1 = p1;
		this.p2 = p2;
		quiz = new QuizEngine(p1 + "+" + p2);
	}

	public String currentPlayer() {
		return p1turn ? p1 : p2;
	}

	public boolean done() {
		return round > maxRounds;
	}

	public String winner() {
		if (score1 == score2) {
			return "draw";
		}
		return score1 > score2 ? p1 : p2;
	}

	public void addPoint() {
		if (p1turn) {
			score1++;
		} else {
			score2++;
		}
	}

	public void nextTurn() {
		if (!p1turn) {
			round++;
		}
		p1turn = !p1turn;
	}
}
