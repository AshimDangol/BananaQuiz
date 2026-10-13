package com.weatherquiz.peripherals;

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

import com.perisic.tomato.peripherals.LoginData;

// same login as tomato game, just opens the quiz instead
// base layout from https://best-programming-tricks.blogspot.com/2011/07/how-to-make-login-form-with-java-gui.html
public class QuizLoginGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	JTextField txuser = new JTextField(15);
	JPasswordField pass = new JPasswordField(15);
	JCheckBox remember = new JCheckBox("remember me", true);
	JButton blogin = new JButton("Login");
	JButton bsignup = new JButton("Sign up");

	LoginData ldata = new LoginData();

	public static void main(String[] args) {
		QuizLoginGUI l = new QuizLoginGUI();
		l.setVisible(true);
	}

	public QuizLoginGUI() {
		super("Weather Quiz Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(6, 6, 6, 6);
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 0;
		panel.add(new JLabel("Username:"), c);
		c.gridx = 1;
		panel.add(txuser, c);

		c.gridx = 0;
		c.gridy = 1;
		panel.add(new JLabel("Password:"), c);
		c.gridx = 1;
		panel.add(pass, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		panel.add(remember, c);

		JPanel btns = new JPanel();
		btns.add(blogin);
		btns.add(bsignup);
		c.gridy = 3;
		panel.add(btns, c);

		getContentPane().add(panel);
		pack();
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
		QuizGUI g = new QuizGUI(user);
		g.setVisible(true);
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
		// enter key logs in too
		pass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				blogin.doClick();
			}
		});
	}
}
