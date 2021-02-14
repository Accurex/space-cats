/**
 * SpaceFetcher.java - Class for fetching the space images using NASA API
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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;

public class SpaceFetcher {

	/**
	 * Gets the URL of the random image at the API response.
	 * @return The URL if success, null otherwise.
	 */
	private static URL getImageUrl() {
		String resultStr;

		// Request URL also containing the API key
		String requestStr = "https://api.nasa.gov/planetary/apod?count=1&api_key=DEMO_KEY";

		HttpResponse<String> httpResponse = null;
		try {
			// Make a GET request to the server and store the response
			httpResponse = Unirest.get(requestStr).asString();
		} catch (UnirestException e) {
			return null;
		}

		// The response is an array with a single image
		JSONArray arr = new JSONArray(httpResponse.getBody().toString());

		// Get the URL
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
	 * Gets the URL of the image on a specific date from the NASA API.
	 * @param date Date of image
	 * @return The URL if success, false otherwise
	 */
	private static URL getImageUrl(LocalDate date) {
		String resultStr;

		// Convert the date to the format API accepts
		String dateStr = date.getYear() + "-" + new DecimalFormat("00").format(date.getMonthValue()) + "-" + date.getDayOfMonth();
		String requestStr = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY" + "&date=" + dateStr;
		HttpResponse<String> httpResponse = null;
		try {
			httpResponse = Unirest.get(requestStr).asString();
		} catch (UnirestException e) {
			return null;
		}

		// The response is a single JSON Object
		JSONObject json = new JSONObject(httpResponse.getBody().toString());

		// Get the URL
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
	 * Obtain and save a random space image as space.jpg.
	 * @return True if success, false otherwise
	 */
	public static boolean run() {
		// Get the URL
		URL imageUrl = getImageUrl();
		if (imageUrl == null) {
			return false;
		}

		// Fetch the image
		BufferedImage img = ImageUtils.fetchImage(imageUrl);
		if (img == null) {
			return false;
		}

		// Get the file name
		String imageName = imageUrl.getFile();

		// Save it as jpg
		if (!ImageUtils.saveSpaceImage(img, imageName, "jpg")) {
			return false;
		}

		return true;
	}

	/**
	 * Obtain and save the space image on the specific date as space.jpg.
	 * @return True if success, false otherwise
	 */
	public static boolean run(LocalDate date) {
		// Get the URL
		URL imageUrl = getImageUrl(date);
		if (imageUrl == null) {
			return false;
		}

		// Fetch the image
		BufferedImage img = ImageUtils.fetchImage(imageUrl);
		if (img == null) {
			return false;
		}

		// Get the file name
		String imageName = imageUrl.getFile();

		// Save it as jpg
		if (!ImageUtils.saveSpaceImage(img, imageName, "jpg")) {
			return false;
		}

		return true;
	}

}
