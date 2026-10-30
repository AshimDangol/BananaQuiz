package com.weatherquiz.peripherals;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

// login + signup window, opens the menu when logged in
// base layout from https://best-programming-tricks.blogspot.com/2011/07/how-to-make-login-form-with-java-gui.html
public class QuizLoginGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	JButton blogin = new JButton("Login");
	JButton bsignup = new JButton("Sign up");
	JCheckBox remember = new JCheckBox("remember me", true);
	JPanel panel = new JPanel();
	JTextField txuser = new JTextField(15);
	JPasswordField pass = new JPasswordField(15);

	LoginData ldata = new LoginData();

	public static void main(String[] args) {
		QuizLoginGUI l = new QuizLoginGUI();
		l.setVisible(true);
	}

	public QuizLoginGUI() {
		super("Banana Quiz Login");
		Theme.apply();
		setSize(320, 300);
		setLocation(500, 280);
		panel.setLayout(null);

		JLabel title = new JLabel("Banana Quiz", JLabel.CENTER);
		title.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 18));
		title.setForeground(Theme.HEADER_BG);
		title.setBounds(40, 8, 220, 28);
		panel.add(title);

		txuser.setBounds(70, 45, 150, 24);
		pass.setBounds(70, 80, 150, 24);
		blogin.setBounds(40, 115, 80, 26);
		bsignup.setBounds(140, 115, 90, 26);
		remember.setBounds(70, 150, 150, 24);

		panel.add(blogin);
		panel.add(bsignup);
		panel.add(txuser);
		panel.add(pass);
		panel.add(remember);

		getContentPane().add(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		String saved = ldata.checkSession();
		if (saved != null) {
			openQuiz(saved);
			return;
		}
		actions();
	}

	void openQuiz(String user) {
		MenuGUI m = new MenuGUI(user);
		m.setVisible(true);
		dispose();
	}

	void actions() {
		blogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String name = txuser.getText().trim();
				String pw = String.valueOf(pass.getPassword());
				if (ldata.checkPassword(name, pw)) {
					if (remember.isSelected()) {
						ldata.createSession(name);
					} else {
						ldata.clearSession();
					}
					openQuiz(name);
				} else {
					JOptionPane.showMessageDialog(null, "Wrong Password / Username");
					pass.setText("");
					txuser.requestFocus();
				}
			}
		});
		bsignup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String name = txuser.getText().trim();
				String pw = String.valueOf(pass.getPassword());
				if (name.equals("") || pw.length() < 4) {
					JOptionPane.showMessageDialog(null, "pick a name and 4+ char password");
					return;
				}
				try {
					boolean ok = ldata.addUser(name, pw);
					if (ok) {
						JOptionPane.showMessageDialog(null, "account made, log in now");
					} else {
						JOptionPane.showMessageDialog(null, "name taken or bad input");
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "error making account");
				}
			}
		});
	}
}
