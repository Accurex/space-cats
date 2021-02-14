/**
 * GUI.java - Main GUI for SpaceCats
 *
 * Author: Mehmet Eren Aldemir
 * February 2021
 * DALI Lab API Challenge
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class GUI implements ActionListener {
	static JFrame frame;
	static JLabel label;    // The part that displays spacecat image
	static JLabel errLabel;      // The part that displays error messages
	static ImageIcon icon;  // The image object that contains the spacecat image

	public static void main(String args[]) {
		GUI obj = new GUI();

		frame = new JFrame("SpaceCats");
		frame.setSize(300, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(Color.white);
		frame.setLayout(new FlowLayout());

		// Astronaut cat image
		BufferedImage windowIcon = ImageUtils.readImage("data/icon.jpg");
		// Set window icon
		frame.setIconImage(windowIcon);

		JButton button = new JButton("Get new cat"); // New cat button
		frame.add(button);
		button.addActionListener(obj);  // Associate actions to the button

		label = new JLabel();
		errLabel = new JLabel();
		icon = new ImageIcon();
		label.setIcon(icon);
		frame.add(label);
		frame.add(errLabel);
		frame.pack();
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		errLabel.setText("");

		// Display error if space image fetching fails
		if (!SpaceFetcher.run()) {
			displayError("Error fetching space!");
			return;
		}

		// Display error if cat image fetching fails
		if (!CatFetcher.run()) {
			displayError("Error fetching cat!");
			return;
		}

		// Display error if reading image fails
		BufferedImage space = ImageUtils.readImage("data/space.jpg");
		if (space == null) {
			displayError("Error saving space!");
			return;
		}

		// Display error if reading image fails
		BufferedImage cat = ImageUtils.readImage("data/cat.png");
		if (cat == null) {
			displayError("Error saving cat!");
			return;
		}

		// Display error if combining the images fails
		BufferedImage spacecat = ImageUtils.overlayImages(space, cat);
		if (spacecat == null) {
			displayError("Error combining images!");
			return;
		}

		// Display error if saving fails
		if (!ImageUtils.saveSpacecatImage(spacecat, "spacecat.jpg")) {
			displayError("Error saving combined image!");
			return;
		}

		// Set the displayed image to our generated image
		icon.setImage(spacecat);

		// Resize the frame to our image size plus a margin
		frame.setSize(icon.getIconWidth() + 100, icon.getIconHeight() + 100);

		// Refresh the screen
		label.repaint();
		frame.validate();
		frame.repaint();
	}

	private void displayError(String err) {
		frame.setSize(300, 600);
		errLabel.setText(err);
	}
}
