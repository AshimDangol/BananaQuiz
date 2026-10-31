package com.weatherquiz.peripherals;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.border.EmptyBorder;

// login + signup window, opens the menu when logged in
// base layout from https://best-programming-tricks.blogspot.com/2011/07/how-to-make-login-form-with-java-gui.html
public class QuizLoginGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	JButton blogin = new JButton("Login");
	JButton bsignup = new JButton("Sign up");
	JCheckBox remember = new JCheckBox("remember me", true);
	JTextField txuser = new JTextField(14);
	JPasswordField pass = new JPasswordField(14);

	LoginData ldata = new LoginData();

	public static void main(String[] args) {
		QuizLoginGUI l = new QuizLoginGUI();
		l.setVisible(true);
	}

	public QuizLoginGUI() {
		super("Banana Quiz Login");
		Theme.apply();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(Theme.banner("Banana Quiz", 24), BorderLayout.NORTH);

		JPanel form = new JPanel(new GridBagLayout());
		form.setBorder(new EmptyBorder(18, 24, 10, 24));
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(6, 6, 6, 6);
		c.anchor = GridBagConstraints.WEST;

		c.gridx = 0;
		c.gridy = 0;
		form.add(new JLabel("username:"), c);
		c.gridx = 1;
		form.add(txuser, c);

		c.gridx = 0;
		c.gridy = 1;
		form.add(new JLabel("password:"), c);
		c.gridx = 1;
		form.add(pass, c);

		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.CENTER;
		form.add(remember, c);
		add(form, BorderLayout.CENTER);

		JPanel buttons = new JPanel();
		buttons.setBorder(new EmptyBorder(4, 24, 18, 24));
		Theme.button(blogin, Theme.ACCENT);
		Theme.button(bsignup, Theme.SURFACE);
		buttons.add(blogin);
		buttons.add(bsignup);
		add(buttons, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(blogin);
		pack();
		setMinimumSize(new Dimension(340, getHeight()));
		setLocationRelativeTo(null);
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
