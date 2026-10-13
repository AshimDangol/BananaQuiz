package com.weatherquiz.peripherals;

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

import com.weatherquiz.engine.Settings;

public class SettingsGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	public SettingsGUI(String user) {
		super("Settings - " + user);
		Settings s = new Settings(user);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(6, 6, 6, 6);
		c.fill = GridBagConstraints.HORIZONTAL;

		Integer[] secs = { 10, 15, 20, 30 };
		JComboBox<Integer> secBox = new JComboBox<Integer>(secs);
		secBox.setSelectedItem(s.seconds);

		String[] modes = { "Which hotter?", "Over / Under 12C", "Banana puzzle" };
		JComboBox<String> modeBox = new JComboBox<String>(modes);
		modeBox.setSelectedItem(s.mode);

		JCheckBox offBox = new JCheckBox("show offline tag", s.showOffline);
		JButton save = new JButton("Save");

		c.gridx = 0;
		c.gridy = 0;
		panel.add(new JLabel("Seconds per question:"), c);
		c.gridx = 1;
		panel.add(secBox, c);

		c.gridx = 0;
		c.gridy = 1;
		panel.add(new JLabel("Start mode:"), c);
		c.gridx = 1;
		panel.add(modeBox, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		panel.add(offBox, c);

		c.gridy = 3;
		panel.add(save, c);

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				s.seconds = (Integer) secBox.getSelectedItem();
				s.mode = (String) modeBox.getSelectedItem();
				s.showOffline = offBox.isSelected();
				s.save();
				JOptionPane.showMessageDialog(null, "saved");
				dispose();
			}
		});

		getContentPane().add(panel);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
