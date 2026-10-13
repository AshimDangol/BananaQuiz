package com.weatherquiz.peripherals;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import com.perisic.tomato.peripherals.LoginData;
import com.weatherquiz.engine.Board;
import com.weatherquiz.engine.QuizEngine;
import com.weatherquiz.engine.Settings;

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
	JLabel picLabel = null;
	JPanel answers = null;
	JButton[] numBtns = new JButton[10];
	JComboBox<String> modeBox = null;
	Timer countTimer = null;
	Settings opts = null;
	int left = 15;
	int loadToken = 0;

	public QuizGUI(String player) {
		super("Weather Quiz - " + player);
		quiz = new QuizEngine(player);
		opts = new Settings(player);
		left = opts.seconds;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout(8, 8));

		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		scoreLabel = new JLabel("player: " + player + "  score: 0");
		timerLabel = new JLabel("15s");
		timerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

		String[] modes = { "Which hotter?", "Over / Under", "Banana puzzle" };
		modeBox = new JComboBox<String>(modes);
		modeBox.setSelectedItem(opts.mode);
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

		JButton skip = new JButton("skip");
		skip.setActionCommand("skip");
		skip.addActionListener(this);

		JButton save = new JButton("save score");
		save.setActionCommand("save");
		save.addActionListener(this);

		JButton menu = new JButton("menu");
		menu.setActionCommand("menu");
		menu.addActionListener(this);

		top.add(scoreLabel);
		top.add(timerLabel);
		top.add(modeBox);
		top.add(skip);
		top.add(save);
		top.add(menu);
		top.add(logout);
		add(top, BorderLayout.NORTH);

		JPanel center = new JPanel(new BorderLayout(6, 6));
		question = new JLabel("loading...");
		question.setFont(new Font("SansSerif", Font.BOLD, 14));
		center.add(question, BorderLayout.NORTH);

		picLabel = new JLabel("", JLabel.CENTER);
		JScrollPane picScroll = new JScrollPane(picLabel);
		picScroll.setPreferredSize(new Dimension(520, 300));
		center.add(picScroll, BorderLayout.CENTER);

		answers = new JPanel(new CardLayout());

		JPanel hotter = new JPanel(new FlowLayout());
		btnA = new JButton("A");
		btnA.setActionCommand("A");
		btnA.addActionListener(this);
		btnB = new JButton("B");
		btnB.setActionCommand("B");
		btnB.addActionListener(this);
		hotter.add(btnA);
		hotter.add(btnB);

		JPanel over = new JPanel(new FlowLayout());
		btnOver = new JButton("Over 12C");
		btnOver.setActionCommand("over");
		btnOver.addActionListener(this);
		btnUnder = new JButton("Under 12C");
		btnUnder.setActionCommand("under");
		btnUnder.addActionListener(this);
		over.add(btnOver);
		over.add(btnUnder);

		JPanel digits = new JPanel(new GridLayout(2, 5, 4, 4));
		for (int i = 0; i < 10; i++) {
			numBtns[i] = new JButton(String.valueOf(i));
			numBtns[i].setActionCommand("num" + i);
			numBtns[i].addActionListener(this);
			digits.add(numBtns[i]);
		}

		answers.add(hotter, "hotter");
		answers.add(over, "over");
		answers.add(digits, "banana");
		center.add(answers, BorderLayout.SOUTH);
		add(center, BorderLayout.CENTER);

		info = new JTextArea(4, 40);
		info.setEditable(false);
		info.setLineWrap(true);
		add(new JScrollPane(info), BorderLayout.SOUTH);

		countTimer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				left--;
				timerLabel.setText(left + "s");
				timerLabel.setForeground(left <= 5 ? Color.RED : Color.BLACK);
				if (left <= 0) {
					String mode = (String) modeBox.getSelectedItem();
					if (mode.startsWith("Banana")) {
						info.setText("time up! answer was " + quiz.bananaSolution);
					} else if (mode.startsWith("Over")) {
						info.setText("time up! " + quiz.cityA.name + " was " + quiz.tempA + "C");
					} else {
						info.setText("time up! was " + tempsText());
					}
					quiz.miss();
					updateScore();
					newQuestionDelayed();
				}
			}
		});

		pack();
		setMinimumSize(new Dimension(560, 560));
		setLocationRelativeTo(null);
		newQuestion();
	}

	ImageIcon fitImage(BufferedImage img) {
		int maxW = 480;
		if (img.getWidth() <= maxW) {
			return new ImageIcon(img);
		}
		double r = (double) maxW / img.getWidth();
		int h = (int) (img.getHeight() * r);
		return new ImageIcon(img.getScaledInstance(maxW, h, Image.SCALE_SMOOTH));
	}

	void showCard(String name) {
		((CardLayout) answers.getLayout()).show(answers, name);
	}

	String tempsText() {
		String tag = "";
		if (opts.showOffline) {
			tag = quiz.wasOffline() ? " (offline)" : " (live)";
		}
		return quiz.cityA.name + " " + quiz.tempA + "C vs "
				+ quiz.cityB.name + " " + quiz.tempB + "C" + tag;
	}

	void updateScore() {
		scoreLabel.setText("player: " + quiz.getPlayer()
				+ "  score: " + quiz.getScore() + " tries: " + quiz.getTries());
	}

	void allButtonsOff() {
		btnA.setEnabled(false);
		btnB.setEnabled(false);
		btnOver.setEnabled(false);
		btnUnder.setEnabled(false);
		for (int i = 0; i < 10; i++) {
			numBtns[i].setEnabled(false);
		}
	}

	void newQuestion() {
		final int myLoad = ++loadToken;
		final String mode = (String) modeBox.getSelectedItem();
		countTimer.stop();
		timerLabel.setForeground(Color.BLACK);
		allButtonsOff();
		question.setText("loading...");
		info.setText("fetching, wait...");
		// fetch off the button thread so the window never freezes,
		// done() runs back on the button thread to touch labels
		new SwingWorker<Void, Void>() {
			protected Void doInBackground() {
				if (mode.startsWith("Banana")) {
					quiz.nextBanana();
				} else {
					quiz.nextQuestion();
				}
				return null;
			}

			protected void done() {
				if (myLoad != loadToken) {
					return;
				}
				showLoaded(mode);
			}
		}.execute();
	}

	void showLoaded(String mode) {
		if (mode.startsWith("Banana")) {
			left = 20;
			timerLabel.setText("20s");
			countTimer.restart();
			question.setText("Banana puzzle: missing number?"
					+ (quiz.bananaOffline ? " (offline)" : " (live)"));
			BufferedImage img = quiz.getBananaImage();
			if (img != null) {
				picLabel.setIcon(fitImage(img));
				picLabel.setText("");
			} else {
				picLabel.setIcon(null);
				picLabel.setText("no image, check net");
			}
			showCard("banana");
			for (int i = 0; i < 10; i++) {
				numBtns[i].setEnabled(true);
			}
			info.setText("pick 0-9...");
			return;
		}
		left = opts.seconds;
		timerLabel.setText(left + "s");
		countTimer.restart();
		picLabel.setIcon(null);
		picLabel.setText("");
		if (mode.startsWith("Which")) {
			question.setText("Which is hotter: " + quiz.cityA.name + " or " + quiz.cityB.name + "?");
			btnA.setText(quiz.cityA.name);
			btnB.setText(quiz.cityB.name);
			showCard("hotter");
			btnA.setEnabled(true);
			btnB.setEnabled(true);
		} else {
			question.setText("Is " + quiz.cityA.name + " over or under " + quiz.overLine + "C?");
			btnOver.setText("Over " + quiz.overLine + "C");
			btnUnder.setText("Under " + quiz.overLine + "C");
			showCard("over");
			btnOver.setEnabled(true);
			btnUnder.setEnabled(true);
		}
		info.setText("pick an answer..." + (quiz.wasOffline() ? " (offline data)" : ""));
	}

	// short pause so you can read the result before next loads
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
		if (cmd.equals("skip")) {
			newQuestion();
			return;
		}
		if (cmd.equals("menu")) {
			countTimer.stop();
			MenuGUI m = new MenuGUI(quiz.getPlayer());
			m.setVisible(true);
			dispose();
			return;
		}
		if (cmd.equals("save")) {
			Board.save(quiz.getPlayer(), quiz.getScore(), quiz.getTries(),
					(String) modeBox.getSelectedItem());
			info.setText("saved! " + tempsText());
			return;
		}
		boolean ok = false;
		if (cmd.equals("A")) {
			ok = quiz.checkAnswer(0);
		} else if (cmd.equals("B")) {
			ok = quiz.checkAnswer(1);
		} else if (cmd.equals("over")) {
			ok = quiz.checkOverUnder(quiz.cityA.name, quiz.overLine, true);
		} else if (cmd.equals("under")) {
			ok = quiz.checkOverUnder(quiz.cityA.name, quiz.overLine, false);
		} else if (cmd.startsWith("num")) {
			int guess = Integer.parseInt(cmd.substring(3));
			ok = quiz.checkBanana(guess);
			if (ok) {
				info.setText("right! answer was " + quiz.bananaSolution);
			} else {
				info.setText("wrong. answer was " + quiz.bananaSolution);
			}
			updateScore();
			newQuestionDelayed();
			return;
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
