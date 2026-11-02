package com.bananaquiz.peripherals;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.bananaquiz.engine.Settings;

// per-user options, saved to ~/.bananaquiz/settings.txt
public class SettingsGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	String user;

	public SettingsGUI(final String user) {
		super("Settings - " + user);
		Theme.apply();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.user = user;
		final Settings s = new Settings(user);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(8, 10, 8, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;

		Integer[] secs = { 10, 15, 20, 30, 45, 60 };
		final JComboBox<Integer> secBox = new JComboBox<Integer>(secs);
		secBox.setSelectedItem(s.seconds);

		final JCheckBox boardBox = new JCheckBox("show live leaderboard while playing",
				s.liveBoard);
		final JCheckBox offBox = new JCheckBox("tag questions loaded offline", s.showOffline);

		c.gridx = 0;
		c.gridy = 0;
		panel.add(new JLabel("seconds per question:"), c);
		c.gridx = 1;
		panel.add(secBox, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		panel.add(boardBox, c);

		c.gridy = 2;
		panel.add(offBox, c);

		c.gridy = 3;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		JButton save = new JButton("save");
		Theme.button(save, Theme.ACCENT);
		panel.add(save, c);

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				s.seconds = (Integer) secBox.getSelectedItem();
				s.liveBoard = boardBox.isSelected();
				s.showOffline = offBox.isSelected();
				s.save();
				JOptionPane.showMessageDialog(SettingsGUI.this, "saved, applies next quiz");
				back();
			}
		});

		c.gridy = 4;
		JButton back = new JButton("back");
		Theme.button(back, Theme.SURFACE);
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				back();
			}
		});
		panel.add(back, c);

		getContentPane().add(panel);
		setMinimumSize(new java.awt.Dimension(360, 300));
		Theme.openCentered(this);
		setVisible(true);
	}

	// menu disposes itself when it opens this window, so close the loop here
	void back() {
		new MenuGUI(user);
		dispose();
	}
}
