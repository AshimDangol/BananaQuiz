package com.weatherquiz.peripherals;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import com.perisic.tomato.peripherals.LoginData;
import com.weatherquiz.engine.QuizEngine;

// main window for the weather quiz
// button + dropdown + timer events all in here
public class QuizGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	QuizEngine quiz = null;
	JLabel question = null;
	JLabel scoreLabel = null;
	JLabel timerLabel = null;
	JTextArea info = null;
	JButton btnA = null;
	JButton btnB = null;
	JButton btnOver = null;
	JButton btnUnder = null;
	JComboBox<String> modeBox = null;
	Timer countTimer = null;
	int left = 15;

	public QuizGUI(String player) {
		super("Weather Quiz - " + player);
		quiz = new QuizEngine(player);
		setSize(560, 380);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();

		scoreLabel = new JLabel("player: " + player + "  score: 0");
		panel.add(scoreLabel);

		timerLabel = new JLabel("15s");
		panel.add(timerLabel);

		String[] modes = { "Which hotter?", "Over / Under 12C" };
		modeBox = new JComboBox<String>(modes);
		panel.add(modeBox);
		modeBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					newQuestion();
				}
			}
		});

		JButton logout = new JButton("logout");
		logout.setActionCommand("logout");
		logout.addActionListener(this);
		panel.add(logout);

		question = new JLabel("loading...");
		panel.add(question);

		btnA = new JButton("A");
		btnA.setActionCommand("A");
		btnA.addActionListener(this);
		panel.add(btnA);

		btnB = new JButton("B");
		btnB.setActionCommand("B");
		btnB.addActionListener(this);
		panel.add(btnB);

		btnOver = new JButton("Over 12C");
		btnOver.setActionCommand("over");
		btnOver.addActionListener(this);
		panel.add(btnOver);

		btnUnder = new JButton("Under 12C");
		btnUnder.setActionCommand("under");
		btnUnder.addActionListener(this);
		panel.add(btnUnder);

		info = new JTextArea(4, 40);
		info.setEditable(false);
		panel.add(new JScrollPane(info));

		getContentPane().add(panel);

		countTimer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				left--;
				timerLabel.setText(left + "s");
				if (left <= 0) {
					info.setText("time up! was " + tempsText());
					quiz.checkAnswer(-1);
					updateScore();
					newQuestionDelayed();
				}
			}
		});

		newQuestion();
	}

	String tempsText() {
		return quiz.cityA.name + " " + quiz.tempA + "C vs "
				+ quiz.cityB.name + " " + quiz.tempB + "C"
				+ (quiz.wasOffline() ? " (offline)" : " (live)");
	}

	void updateScore() {
		scoreLabel.setText("player: " + quiz.getPlayer()
				+ "  score: " + quiz.getScore() + " tries: " + quiz.getTries());
	}

	void newQuestion() {
		quiz.nextQuestion();
		left = 15;
		timerLabel.setText("15s");
		countTimer.restart();
		String mode = (String) modeBox.getSelectedItem();
		if (mode.startsWith("Which")) {
			question.setText("Which is hotter: " + quiz.cityA.name + " or " + quiz.cityB.name + "?");
			btnA.setText(quiz.cityA.name);
			btnB.setText(quiz.cityB.name);
			btnA.setEnabled(true);
			btnB.setEnabled(true);
			btnOver.setEnabled(false);
			btnUnder.setEnabled(false);
		} else {
			question.setText("Is " + quiz.cityA.name + " over or under 12C?");
			btnA.setEnabled(false);
			btnB.setEnabled(false);
			btnOver.setEnabled(true);
			btnUnder.setEnabled(true);
		}
		info.setText("pick an answer..." + (quiz.wasOffline() ? " (offline data)" : ""));
	}

	// short pause so you can read the temps before next loads
	void newQuestionDelayed() {
		countTimer.stop();
		Timer once = new Timer(1800, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newQuestion();
			}
		});
		once.setRepeats(false);
		once.start();
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("logout")) {
			countTimer.stop();
			new LoginData().clearSession();
			QuizLoginGUI login = new QuizLoginGUI();
			login.setVisible(true);
			dispose();
			return;
		}
		boolean ok = false;
		if (cmd.equals("A")) {
			ok = quiz.checkAnswer(0);
		} else if (cmd.equals("B")) {
			ok = quiz.checkAnswer(1);
		} else if (cmd.equals("over")) {
			ok = quiz.checkOverUnder(quiz.cityA.name, 12.0, true);
		} else if (cmd.equals("under")) {
			ok = quiz.checkOverUnder(quiz.cityA.name, 12.0, false);
		}
		if (ok) {
			info.setText("right! " + tempsText());
		} else {
			info.setText("wrong. " + tempsText());
		}
		updateScore();
		newQuestionDelayed();
	}

	public static void main(String[] args) {
		QuizGUI g = new QuizGUI("test");
		g.setVisible(true);
	}
}
