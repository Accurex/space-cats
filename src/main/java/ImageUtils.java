/**
 * ImageUtils.java - Utility functions for image manipulation and IO
 *
 * Author: Mehmet Eren Aldemir
 * February 2021
 * DALI Lab API Challenge
 */


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ImageUtils {

	/**
	 * Fetches and returns the image at given url
	 * @param url The URL of the image to be returned
	 * @return Fetched image if success, null otherwise
	 */
	protected static BufferedImage fetchImage(URL url) {
		// Safety check
		if (url == null) {
			return null;
		}

		BufferedImage image = null;
		try {
			URL imageUrl = url;
			image = ImageIO.read(imageUrl);
		} catch (IOException e) {
			return null;
		}
		return image;
	}

	/**
	 * Saves the given image img as space.[type]
	 * @param img Image object to be saved
	 * @param name The name with which it'll be saved
	 * @return True if success, false otherwise
	 */
	protected static boolean saveSpaceImage(BufferedImage img, String name) {
		// Get image type -- assumes image extensions are 3 chars long
		String type = name.substring(name.length() - 3);
		try {
			File imgOut = new File("data/" + "space." + type);
			// If the directory doesn't exist, create it
			if (!imgOut.exists()) {
				imgOut.mkdirs();
			}

			// Save it
			ImageIO.write(img, type, imgOut);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Saves the given image img as space.jpg
	 * Omits image types other than jpg.
	 * @param img Image object to be saved
	 * @param name The name with which it'll be saved
	 * @return True if success, false otherwise
	 */
	protected static boolean saveSpaceImage(BufferedImage img, String name, String assertType) {
		// Get image type -- assumes image extensions are 3 chars long
		String type = name.substring(name.length() - 3);
		// If it's not jpg, return false
		if (type.compareTo(assertType) != 0) {
			return false;
		}
		return saveSpaceImage(img, name);
	}

	/**
	 * Saves the given image img as cat.[type]
	 * @param img Image object to be saved
	 * @param name The name with which it'll be saved
	 * @return True if success, false otherwise
	 */
	protected static boolean saveCatImage(BufferedImage img, String name) {
		String type = name.substring(name.length() - 3);
		name = name.substring(name.lastIndexOf("/") + 1);
		try {
			File imgOut = new File("data/" + "cat." + type);
			// If the directory doesn't exist, create it
			if (!imgOut.exists()) {
				imgOut.mkdirs();
			}
			ImageIO.write(img, type, imgOut);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Saves the given image img as spacecat.[type]
	 * @param img Image object to be saved
	 * @param name The name with which it'll be saved
	 * @return True if success, false otherwise
	 */
	protected static boolean saveSpacecatImage(BufferedImage img, String name) {
		String type = name.substring(name.length() - 3);
		try {
			File imgOut = new File("data/" + "spacecat." + type);
			if (!imgOut.exists()) {
				imgOut.mkdirs();
			}
			ImageIO.write(img, type, imgOut);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Reads the image at fileLocation
	 * @param fileLocation Location of the image to be read
	 * @return BufferedImage of image read if success, null otherwise
	 */
	public static BufferedImage readImage(String fileLocation) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(fileLocation));
        } catch (IOException e) {
        	return null;
        }
        return img;
    }

	/**
	 * Adapted from http://blog.icodejava.com/tag/java-draw-one-image-over-another/
	 * Accessed on 13 February 2021
	 * Places one image on top of the other.
	 * @param bgImage Background image
	 * @param fgImage Foreground image
	 * @return Combined image
	 */
	public static BufferedImage overlayImages(BufferedImage bgImage, BufferedImage fgImage) {

		// How much to scale down the cat
		final double scaleDownFactor = 5.0;

		// Calculation to not to change the aspect ratio of the image
		int aspectRatioHeight = (int) ((((double) bgImage.getWidth() / scaleDownFactor) / (double) fgImage.getWidth()) * (double) fgImage.getHeight());

		// Scale down the cat
		Image cat_scaled = fgImage.getScaledInstance(bgImage.getWidth() / (int) scaleDownFactor, aspectRatioHeight, Image.SCALE_SMOOTH);
		BufferedImage cat = new BufferedImage(bgImage.getWidth() / (int) scaleDownFactor, aspectRatioHeight, BufferedImage.TYPE_INT_ARGB);
		cat.getGraphics().drawImage(cat_scaled, 0, 0, null);

		Graphics2D g = bgImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw the background
		g.drawImage(bgImage, 0, 0, null);

		// Calculate random location and angle for the cat
		int randomX = (int) (Math.random() * (double) (bgImage.getWidth() - Math.max(cat.getHeight(), cat.getWidth())));
		int randomY = (int) (Math.random() * (double) (bgImage.getHeight() - Math.max(cat.getHeight(), cat.getWidth())));
		int randomR = (int) (Math.random() * 360.0);

		// Draw the cat
		g.drawImage(rotateImageByDegrees(cat, randomR), randomX, randomY, null);
		g.dispose();

		return bgImage;
	}

	/**
	 * Adapted from https://stackoverflow.com/a/37758533/7087180 by user MadProgrammer
	 * Accessed on 13 February 2021
	 * Rotates the given image by the given angle.
	 * @param img Image to be rotated
	 * @param angle Rotation angle
	 * @return Rotated image
	 */
	public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {

            double rads = Math.toRadians(angle);
            double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
            int w = img.getWidth();
            int h = img.getHeight();
            int newWidth = (int) Math.floor(w * cos + h * sin);
            int newHeight = (int) Math.floor(h * cos + w * sin);

            BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = rotated.createGraphics();
            AffineTransform at = new AffineTransform();
            at.translate((newWidth - w) / 2, (newHeight - h) / 2);

            int x = w / 2;
            int y = h / 2;

            at.rotate(rads, x, y);
            g2d.setTransform(at);
            g2d.drawImage(img, 0, 0, null);
            g2d.dispose();

            return rotated;
	}
}
