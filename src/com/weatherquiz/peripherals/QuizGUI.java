package com.weatherquiz.peripherals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import com.weatherquiz.engine.Board;
import com.weatherquiz.engine.QuizEngine;

// main window for the banana quiz
// button + timer events all in here
public class QuizGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	QuizEngine quiz = null;
	JLabel question = null;
	JLabel scoreLabel = null;
	JLabel timerLabel = null;
	JTextArea info = null;
	JLabel picLabel = null;
	JButton[] numBtns = new JButton[10];
	Timer countTimer = null;
	int left = 20;
	int loadToken = 0;

	public QuizGUI(String player) {
		super("Banana Quiz - " + player);
		Theme.apply();
		quiz = new QuizEngine(player);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		((JPanel) getContentPane()).setBorder(new javax.swing.border.EmptyBorder(8, 8, 8, 8));
		setLayout(new BorderLayout(0, 8));

		JPanel top = new JPanel(new BorderLayout(0, 4));
		JPanel status = new JPanel(new BorderLayout());
		scoreLabel = new JLabel("player: " + player + "  score: 0");
		timerLabel = new JLabel("20s", JLabel.RIGHT);
		timerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		status.add(scoreLabel, BorderLayout.WEST);
		status.add(timerLabel, BorderLayout.EAST);
		top.add(status, BorderLayout.NORTH);

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));

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

		controls.add(skip);
		controls.add(save);
		controls.add(menu);
		controls.add(logout);
		top.add(controls, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);

		JPanel center = new JPanel(new BorderLayout(0, 6));
		question = new JLabel("loading...", JLabel.CENTER);
		question.setFont(new Font("SansSerif", Font.BOLD, 16));
		question.setForeground(Theme.HEADER_BG);
		question.setBorder(new javax.swing.border.EmptyBorder(6, 4, 6, 4));
		center.add(question, BorderLayout.NORTH);

		picLabel = new JLabel("", JLabel.CENTER);
		JScrollPane picScroll = new JScrollPane(picLabel);
		picScroll.setPreferredSize(new Dimension(500, 300));
		center.add(picScroll, BorderLayout.CENTER);

		JPanel digits = new JPanel(new GridLayout(2, 5, 6, 6));
		for (int i = 0; i < 10; i++) {
			numBtns[i] = new JButton(String.valueOf(i));
			numBtns[i].setFont(new Font("SansSerif", Font.BOLD, 15));
			numBtns[i].setActionCommand("num" + i);
			numBtns[i].addActionListener(this);
			digits.add(numBtns[i]);
		}
		center.add(digits, BorderLayout.SOUTH);
		add(center, BorderLayout.CENTER);

		info = new JTextArea(3, 40);
		info.setEditable(false);
		info.setLineWrap(true);
		info.setWrapStyleWord(true);
		add(new JScrollPane(info), BorderLayout.SOUTH);

		countTimer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				left--;
				timerLabel.setText(left + "s");
				timerLabel.setForeground(left <= 5 ? Color.RED : Color.BLACK);
				if (left <= 0) {
					info.setText("time up! answer was " + quiz.bananaSolution);
					quiz.miss();
					updateScore();
					newQuestionDelayed();
				}
			}
		});

		pack();
		setMinimumSize(new Dimension(620, 600));
		setLocationRelativeTo(null);
		newQuestion();
	}

	ImageIcon fitImage(BufferedImage img) {
		int maxW = 460;
		int maxH = 280;
		int w = img.getWidth();
		int h = img.getHeight();
		double r = Math.min((double) maxW / w, (double) maxH / h);
		if (r >= 1.0) {
			return new ImageIcon(img);
		}
		return new ImageIcon(img.getScaledInstance((int) (w * r), (int) (h * r), Image.SCALE_SMOOTH));
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
		timerLabel.setForeground(Color.BLACK);
		digitsOff();
		question.setText("loading...");
		info.setText("fetching, wait...");
		// fetch off the button thread so the window never freezes,
		// done() runs back on the button thread to touch labels
		new SwingWorker<Void, Void>() {
			protected Void doInBackground() {
				quiz.nextBanana();
				return null;
			}

			protected void done() {
				if (myLoad != loadToken) {
					return;
				}
				showLoaded();
			}
		}.execute();
	}

	void showLoaded() {
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
		digitsOn();
		info.setText("pick 0-9...");
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
			Board.save(quiz.getPlayer(), quiz.getScore(), quiz.getTries(), "banana");
			info.setText("saved! answer was " + quiz.bananaSolution);
			return;
		}
		if (cmd.startsWith("num")) {
			int guess = Integer.parseInt(cmd.substring(3));
			boolean ok = quiz.checkBanana(guess);
			if (ok) {
				info.setText("right! answer was " + quiz.bananaSolution);
			} else {
				info.setText("wrong. answer was " + quiz.bananaSolution);
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
