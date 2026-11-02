package com.bananaquiz.peripherals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import com.bananaquiz.engine.BananaFetch;
import com.bananaquiz.engine.Board;
import com.bananaquiz.engine.QuizEngine;

// main window for the banana quiz
// button + timer events all in here
// actionPerformed pattern follows GameGUI from the TomatoFull example (Marc Conrad, unit BREO site)
public class QuizGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	QuizEngine quiz = null;
	JLabel question = null;
	JLabel scoreLabel = null;
	JLabel timerLabel = null;
	JTextArea info = null;
	FadeImagePanel picLabel = null;
	JPanel status = null;
	JProgressBar timeBar = null;
	JButton[] numBtns = new JButton[10];
	JButton saveBtn = null;
	Timer countTimer = null;
	Timer pauseTimer = null;
	Timer flashTimer = null;
	Timer lowFlash = null;
	boolean saved = false;
	int left = 20;
	int loadToken = 0;
	static final int TIME_MAX = 20;

	public QuizGUI(String player) {
		super("Banana Quiz - " + player);
		Theme.apply();
		quiz = new QuizEngine(player);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		((JPanel) getContentPane()).setBorder(new javax.swing.border.EmptyBorder(8, 8, 8, 8));
		setLayout(new BorderLayout(0, 8));

		JPanel top = new JPanel(new BorderLayout(0, 6));
		status = new JPanel(new BorderLayout());
		status.setOpaque(true);
		status.setBackground(Theme.HEADER_BG);
		status.setBorder(new javax.swing.border.EmptyBorder(6, 10, 6, 10));
		scoreLabel = new JLabel("player: " + player + "  score: 0");
		scoreLabel.setForeground(Theme.HEADER_FG);
		timerLabel = new JLabel("20s", JLabel.RIGHT);
		timerLabel.setForeground(Theme.HEADER_FG);
		timerLabel.setFont(Theme.font(Font.BOLD, 18));
		status.add(scoreLabel, BorderLayout.WEST);
		status.add(timerLabel, BorderLayout.EAST);
		top.add(status, BorderLayout.NORTH);

		// thin bar that drains with the timer, yellow when nearly out
		timeBar = new JProgressBar(0, TIME_MAX);
		timeBar.setValue(TIME_MAX);
		timeBar.setForeground(Theme.ACCENT);
		timeBar.setBackground(Theme.CARD_BORDER);
		timeBar.setPreferredSize(new Dimension(0, 6));
		timeBar.setBorderPainted(false);
		top.add(timeBar, BorderLayout.CENTER);

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));

		JButton logout = new JButton("logout");
		logout.setActionCommand("logout");
		logout.addActionListener(this);
		Theme.button(logout, Theme.BAD);

		JButton skip = new JButton("skip");
		skip.setActionCommand("skip");
		skip.addActionListener(this);
		Theme.button(skip, Theme.SURFACE);

		JButton save = new JButton("save score");
		save.setActionCommand("save");
		save.addActionListener(this);
		Theme.button(save, Theme.ACCENT);
		saveBtn = save;

		JButton menu = new JButton("menu");
		menu.setActionCommand("menu");
		menu.addActionListener(this);
		Theme.button(menu, Theme.SURFACE);

		controls.add(skip);
		controls.add(save);
		controls.add(menu);
		controls.add(logout);
		top.add(controls, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);

		JPanel center = new JPanel(new BorderLayout(0, 6));
		question = new JLabel("loading...", JLabel.CENTER);
		question.setFont(Theme.font(Font.BOLD, 16));
		question.setForeground(Theme.HEADER_BG);
		question.setBorder(new javax.swing.border.EmptyBorder(6, 4, 6, 4));
		center.add(question, BorderLayout.NORTH);

		picLabel = new FadeImagePanel();
		picLabel.setBackground(Theme.CARD_BG);
		JScrollPane picScroll = new JScrollPane(picLabel);
		picScroll.setPreferredSize(new Dimension(500, 300));
		picScroll.setBorder(Theme.card());
		picScroll.getViewport().setBackground(Theme.CARD_BG);
		center.add(picScroll, BorderLayout.CENTER);

		JPanel digits = new JPanel(new GridLayout(2, 5, 8, 8));
		digits.setBorder(new javax.swing.border.EmptyBorder(4, 0, 4, 0));
		for (int i = 0; i < 10; i++) {
			numBtns[i] = new JButton(String.valueOf(i));
			numBtns[i].setFont(Theme.font(Font.BOLD, 16));
			numBtns[i].setActionCommand("num" + i);
			numBtns[i].addActionListener(this);
			Theme.button(numBtns[i], Theme.ACCENT);
			digits.add(numBtns[i]);
		}
		center.add(digits, BorderLayout.SOUTH);
		add(center, BorderLayout.CENTER);

		info = new JTextArea(3, 40);
		info.setEditable(false);
		info.setLineWrap(true);
		info.setWrapStyleWord(true);
		info.setBackground(Theme.CARD_BG);
		info.setForeground(Theme.TEXT);
		info.setCaretColor(Theme.TEXT);
		info.setBorder(new javax.swing.border.EmptyBorder(6, 8, 6, 8));
		JScrollPane infoScroll = new JScrollPane(info);
		infoScroll.setBorder(Theme.card());
		add(infoScroll, BorderLayout.SOUTH);

		countTimer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				left--;
				timerLabel.setText(left + "s");
				boolean warn = left <= 5;
				if (warn) {
					// last 5s pulse the label instead of just recolouring it
					if (lowFlash == null || !lowFlash.isRunning()) {
						startLowFlash();
					}
				} else {
					stopLowFlash();
					timerLabel.setForeground(Theme.HEADER_FG);
				}
				timeBar.setValue(Math.max(left, 0));
				timeBar.setForeground(warn ? Theme.WARN : Theme.ACCENT);
				if (left <= 0) {
					stopLowFlash();
					timerLabel.setForeground(Theme.WARN);
					digitsOff();
					info.setText("time up! answer was " + quiz.bananaSolution);
					quiz.miss();
					updateScore();
					flash(Theme.WARN);
					newQuestionDelayed();
				}
			}
		});

		setMinimumSize(new Dimension(620, 600));
		Theme.openCentered(this);
		newQuestion();
	}

	void updateScore() {
		scoreLabel.setText("player: " + quiz.getPlayer()
				+ "  score: " + quiz.getScore() + " tries: " + quiz.getTries());
	}

	void digitsOff() {
		for (int i = 0; i < 10; i++) {
			numBtns[i].setEnabled(false);
		}
	}

	void digitsOn() {
		for (int i = 0; i < 10; i++) {
			numBtns[i].setEnabled(true);
		}
	}

	void newQuestion() {
		final int myLoad = ++loadToken;
		countTimer.stop();
		if (pauseTimer != null) {
			pauseTimer.stop();
		}
		if (flashTimer != null) {
			flashTimer.stop();
		}
		stopLowFlash();
		picLabel.stopFade();
		timerLabel.setForeground(Theme.HEADER_FG);
		timerLabel.setText("--");
		timeBar.setValue(TIME_MAX);
		timeBar.setForeground(Theme.ACCENT);
		digitsOff();
		question.setText("loading...");
		info.setText("fetching, wait...");
		// fetch off the button thread so the window never freezes,
		// done() runs back on the button thread to touch labels
		new SwingWorker<BananaFetch.Question, Void>() {
			protected BananaFetch.Question doInBackground() {
				return quiz.fetchBanana();
			}

			protected void done() {
				// window moved on while this fetch was in flight
				if (myLoad != loadToken) {
					return;
				}
				BananaFetch.Question q = null;
				try {
					q = get();
				} catch (Exception e) {
					System.out.println("fetch failed: " + e.getMessage());
				}
				if (!quiz.acceptBanana(q)) {
					question.setText("no question loaded");
					info.setText("could not load a banana puzzle, press skip to try again");
					return;
				}
				showLoaded();
			}
		}.execute();
	}

	void showLoaded() {
		left = TIME_MAX;
		timerLabel.setText(TIME_MAX + "s");
		stopLowFlash();
		timerLabel.setForeground(Theme.HEADER_FG);
		timeBar.setValue(TIME_MAX);
		timeBar.setForeground(Theme.ACCENT);
		countTimer.restart();
		question.setText("Banana puzzle: missing number?"
				+ (quiz.bananaOffline ? " (offline)" : " (live)"));
		BufferedImage img = quiz.getBananaImage();
		if (img != null) {
			picLabel.setImage(img);
		} else {
			picLabel.setPlaceholder("no image, check net");
		}
		digitsOn();
		info.setText("pick 0-9...");
	}

	// background pops to a color then fades back to navy
	void flash(final Color hit) {
		status.setBackground(hit);
		if (flashTimer != null) {
			flashTimer.stop();
		}
		flashTimer = new Timer(450, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				status.setBackground(Theme.HEADER_BG);
				((Timer) e.getSource()).stop();
			}
		});
		flashTimer.setRepeats(false);
		flashTimer.start();
	}

	// last 5 seconds: flip the timer text between warn and bad so the
	// countdown feels urgent instead of just sitting one static colour
	void startLowFlash() {
		stopLowFlash();
		lowFlash = new Timer(150, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timerLabel.setForeground(
						timerLabel.getForeground().equals(Theme.WARN) ? Theme.BAD : Theme.WARN);
			}
		});
		lowFlash.start();
	}

	void stopLowFlash() {
		if (lowFlash != null && lowFlash.isRunning()) {
			lowFlash.stop();
		}
	}

	// short pause so you can read the result before next loads
	void stopPause() {
		if (pauseTimer != null) {
			pauseTimer.stop();
		}
	}

	void newQuestionDelayed() {
		countTimer.stop();
		digitsOff();
		if (pauseTimer != null) {
			pauseTimer.stop();
		}
		pauseTimer = new Timer(1800, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newQuestion();
			}
		});
		pauseTimer.setRepeats(false);
		pauseTimer.start();
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("logout")) {
			loadToken++;
			countTimer.stop();
			stopPause();
			stopLowFlash();
			new LoginData().clearSession();
			// login window shows itself now, no setVisible after this
			new QuizLoginGUI();
			dispose();
			return;
		}
		if (cmd.equals("skip")) {
			newQuestion();
			return;
		}
		if (cmd.equals("menu")) {
			loadToken++;
			countTimer.stop();
			stopPause();
			stopLowFlash();
			MenuGUI m = new MenuGUI(quiz.getPlayer());
			m.setVisible(true);
			dispose();
			return;
		}
		if (cmd.equals("save")) {
			if (saved) {
				info.setText("already saved this game");
				return;
			}
			saved = true;
			saveBtn.setEnabled(false);
			Board.save(quiz.getPlayer(), quiz.getScore(), quiz.getTries(), "banana");
			info.setText("saved! answer was " + quiz.bananaSolution);
			return;
		}
		if (cmd.startsWith("num")) {
			int guess = Integer.parseInt(cmd.substring(3));
			boolean ok = quiz.checkBanana(guess);
			if (ok) {
				info.setText("right! answer was " + quiz.bananaSolution);
				flash(Theme.GOOD);
			} else {
				info.setText("wrong. answer was " + quiz.bananaSolution);
				flash(Theme.BAD);
			}
			updateScore();
			newQuestionDelayed();
		}
	}

	public static void main(String[] args) {
		QuizGUI g = new QuizGUI("test");
		g.setVisible(true);
	}
}
