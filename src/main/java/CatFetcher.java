/**
 * CatFetcher.java - Class for fetching the cat images using The Cat API and remove.bg API
 *
 * Author: Mehmet Eren Aldemir
 * February 2021
 * DALI Lab API Challenge
 */

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class CatFetcher {

	private static final String catApiKey = "YOUR_API_KEY";
	private static final String bgApiKey = "YOUR_API_KEY";

	/**
	 * Gets the URL of the random cat image at the API response.
	 * @return The URL if success, null otherwise.
	 */
	private static URL getImageUrl() {
		String resultStr;

		// Build the request string
		String requestStr = "https://api.thecatapi.com/v1/images/search" + "?mime_types=jpg&api_key=" + catApiKey;

		HttpResponse<String> httpResponse = null;
		try {
			// Make the GET request
			httpResponse = Unirest.get(requestStr).asString();
		} catch (UnirestException e) {
			return null;
		}

		// The result is a JSON Array, get the first object and its URL field
		JSONArray arr = new JSONArray(httpResponse.getBody().toString());
		JSONObject json = arr.getJSONObject(0);
		resultStr = json.getString("url");
		URL result = null;
		try {
			result = new URL(resultStr);
		} catch (MalformedURLException e) {
			return null;
		}

		return result;
	}

	/**
	 * Get a random cat, remove its background and save it.
	 * @return True if success, false otherwise.
	 */
	public static boolean run() {
		// Get cat image URL
		URL imageUrl = getImageUrl();
		if (imageUrl == null) {
			return false;
		}
		// Get its background removed
		return removeBG(imageUrl);
	}

	/**
	 * Get a random cat and save it.
	 * @return True if success, false otherwise.
	 */
	public static boolean runWithBg() {
		// Get cat image URL
		URL imageUrl = getImageUrl();
		if (imageUrl == null) {
			return false;
		}

		// Fetch the image at URL
		String imageName = imageUrl.getFile();
		BufferedImage img = ImageUtils.fetchImage(imageUrl);
		if (img == null) {
			return false;
		}

		// Save it
		if (!ImageUtils.saveCatImage(img, imageName)) {
			return false;
		}
		return true;
	}

	/**
	 * Send the URL of the cat image to remove.bg API,
	 * receive and save the background-removed cat
	 * image as cat.png.
	 * @param url The URL of the cat image
	 * @return True if success, false otherwise.
	 */
	private static boolean removeBG(URL url) {
		String requestStr = "https://api.remove.bg/v1.0/removebg";

		// Build the request for remove.bg
		HttpResponse<InputStream> response = null;
		try {
			response = Unirest.post(requestStr)
				.header("X-Api-Key", bgApiKey)
				.field("size", "auto")
				.field("image_url", url.toString())
				.asBinary();
		} catch (UnirestException e) {
			return false;
		}

		// Response contains the image in its body
		// Read it
		byte[] img;
		try {
			img = response.getBody().readAllBytes();
		} catch (IOException e) {
			return false;
		}

		// Save it to data/cat.png
		try (OutputStream stream = new FileOutputStream("data/cat.png")) {
		    stream.write(img);
		} catch (IOException e) {
			return false;
		}

		return true;
	}

}
