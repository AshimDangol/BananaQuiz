package com.weatherquiz.peripherals;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import com.weatherquiz.engine.Board;
import com.weatherquiz.engine.MultiGame;
import com.weatherquiz.engine.Settings;

// 2 players, same screen, take turns. 5 rounds each.
public class MultiGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	MultiGame game;
	JLabel turn = null;
	JLabel scores = null;
	JLabel question = null;
	JLabel timerLabel = null;
	JTextArea info = null;
	JButton btnA = null;
	JButton btnB = null;
	Timer timer = null;
	int left = 15;

	public MultiGUI(String p1, String p2) {
		super("2 player - " + p1 + " vs " + p2);
		game = new MultiGame(p1, p2);
		Settings s = new Settings(p1);
		left = s.seconds;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout(8, 8));

		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		turn = new JLabel("turn");
		turn.setFont(new Font("SansSerif", Font.BOLD, 14));
		scores = new JLabel("");
		timerLabel = new JLabel(left + "s");
		JButton quit = new JButton("quit");
		quit.setActionCommand("quit");
		quit.addActionListener(this);
		top.add(turn);
		top.add(scores);
		top.add(timerLabel);
		top.add(quit);
		add(top, BorderLayout.NORTH);

		JPanel mid = new JPanel(new FlowLayout());
		question = new JLabel("loading...");
		question.setFont(new Font("SansSerif", Font.BOLD, 13));
		btnA = new JButton("A");
		btnA.setActionCommand("A");
		btnA.addActionListener(this);
		btnB = new JButton("B");
		btnB.setActionCommand("B");
		btnB.addActionListener(this);
		mid.add(question);
		mid.add(btnA);
		mid.add(btnB);
		add(mid, BorderLayout.CENTER);

		info = new JTextArea(5, 35);
		info.setEditable(false);
		add(new JScrollPane(info), BorderLayout.SOUTH);

		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				left--;
				timerLabel.setText(left + "s");
				if (left <= 0) {
					info.setText("time up! " + answerText());
					game.nextTurn();
					nextRoundDelayed();
				}
			}
		});

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		newRound();
	}

	String answerText() {
		return game.quiz.cityA.name + " " + game.quiz.tempA + "C vs "
				+ game.quiz.cityB.name + " " + game.quiz.tempB + "C";
	}

	void updateLabels() {
		scores.setText(game.p1 + " " + game.score1 + " - " + game.score2 + " " + game.p2
				+ "  round " + Math.min(game.round, game.maxRounds) + "/" + game.maxRounds);
		turn.setText("turn: " + game.currentPlayer());
	}

	void newRound() {
		if (game.done()) {
			timer.stop();
			String w = game.winner();
			turn.setText("winner: " + w);
			question.setText("game over");
			info.setText(w + " wins! " + game.p1 + " " + game.score1 + " - " + game.score2 + " " + game.p2);
			Board.save(game.p1, game.score1, game.maxRounds, "multi");
			Board.save(game.p2, game.score2, game.maxRounds, "multi");
			btnA.setEnabled(false);
			btnB.setEnabled(false);
			return;
		}
		game.quiz.nextQuestion();
		left = 15;
		timerLabel.setText(left + "s");
		timer.restart();
		updateLabels();
		question.setText("Which hotter: " + game.quiz.cityA.name + " or " + game.quiz.cityB.name + "?");
		btnA.setText(game.quiz.cityA.name);
		btnB.setText(game.quiz.cityB.name);
		btnA.setEnabled(true);
		btnB.setEnabled(true);
		info.setText(game.currentPlayer() + ", your pick...");
	}

	void nextRoundDelayed() {
		timer.stop();
		btnA.setEnabled(false);
		btnB.setEnabled(false);
		updateLabels();
		Timer once = new Timer(1800, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newRound();
			}
		});
		once.setRepeats(false);
		once.start();
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("quit")) {
			timer.stop();
			MenuGUI m = new MenuGUI(game.p1);
			m.setVisible(true);
			dispose();
			return;
		}
		int choice = cmd.equals("A") ? 0 : 1;
		int right = game.quiz.tempA > game.quiz.tempB ? 0 : 1;
		if (choice == right) {
			game.addPoint();
			info.setText("right! " + answerText());
		} else {
			info.setText("wrong. " + answerText());
		}
		game.nextTurn();
		nextRoundDelayed();
	}
}
