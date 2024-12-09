package edu.brown.cs.student.main.server.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UpdateListingHandlerTest {

  private static final String BASE_URL = "http://localhost:3232";
  private static String listingId;

  @BeforeAll
  public static void setUp() throws Exception {
    // Add Listing
    String addListingResponse =
        sendGetRequest(
            "/add-listings?uid=bibif&username=bibifol&title=Cargo%20Listing&price=356&imageUrl=server/src/data/IMG_4132.PNG&condition=used&tags=CS320&description=bags");
    System.out.println(addListingResponse);
    assertTrue(
        addListingResponse.contains("\"response_type\":\"success\""),
        "Listing addition should be successful");
    listingId = extractListingId(addListingResponse);
  }

  /**
   * Helper method to send a GET request to the server.
   *
   * @param endpoint The endpoint to hit (e.g., "/update-listings").
   * @return The server's response as a string.
   * @throws IOException If an I/O error occurs during the request.
   */
  private static String sendGetRequest(String endpoint) throws IOException {
    // URL encode the endpoint to handle spaces and special characters
    String encodedEndpoint = endpoint.replaceAll(" ", "%20");
    URL url = new URL(BASE_URL + encodedEndpoint);

    System.out.println("Sending GET request to " + url);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.connect();

    int responseCode = connection.getResponseCode();
    assertEquals(200, responseCode, "Expected HTTP 200 response");

    try (Scanner scanner = new Scanner(connection.getInputStream())) {
      StringBuilder response = new StringBuilder();
      while (scanner.hasNext()) {
        response.append(scanner.nextLine());
      }
      return response.toString();
    }
  }

  /**
   * Extracts the listingId from the given JSON string.
   *
   * @param jsonString the JSON string containing the listing information
   * @return the listingId if present, otherwise null
   */
  public static String extractListingId(String jsonString) {
    try {
      JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
      if (jsonObject.has("listingId")) {
        return jsonObject.get("listingId").getAsString();
      }
    } catch (Exception e) {
      System.err.println("Error parsing JSON or extracting listingId: " + e.getMessage());
    }
    return null;
  }

  // Testing a valid test input by adding and updating listing
  @Test
  void testUpdateListing_ValidInput() throws IOException {

    //    // Add Listing
    //    String addListingResponse = sendGetRequest(
    //
    // "/add-listings?uid=bibif&username=bibifol&title=Cargo%20Listing&price=356&imageUrl=server/src/data/IMG_4132.PNG&condition=used&tags=CS320&description=bags");
    //    System.out.println(addListingResponse);
    //    assertTrue(addListingResponse.contains("\"response_type\":\"success\""),
    //        "Listing addition should be successful");
    //
    //
    //    // Extract Listing ID
    //    String listingId = extractListingId(addListingResponse);
    String response =
        sendGetRequest(
            "/update-listings?uid=bibif&listingId="
                + listingId
                + "&title=car&price=28&description=new-benz");
    System.out.println(response);

    // Verify the response contains success
    assertTrue(response.contains("\"response_type\":\"success\""), "Expected a success response");

    // Verify the updated fields in the response
    assertTrue(response.contains("\"title\":\"car\""), "Expected updated title in response");
    assertTrue(response.contains("\"price\":\"28\""), "Expected updated price in response");
    assertTrue(
        response.contains("\"description\":\"new-benz\""),
        "Expected updated description in response");
  }

  //// Testing invalid id reqs
  @Test
  void testUpdateListing_InvalidListingId() throws IOException {
    String response = sendGetRequest("/update-listings?uid=bibif&listingId=invalid-id");

    // Verify the response contains failure
    assertTrue(response.contains("\"response_type\":\"failure\""), "Expected a failure response");
    assertTrue(
        response.contains("No listing found with the given ID: invalid-id"),
        "Expected error message for invalid listing ID");
  }

  // Testing invalid addition update
  @Test
  void testUpdateListing_NegativePrice() throws IOException {
    String response =
        sendGetRequest("/update-listings?uid=bibif&listingId=" + listingId + "&price=-10");

    // Verify the response contains failure for negative price
    assertTrue(
        response.contains("\"response_type\":\"failure\""),
        "Expected a failure response for negative price");
    assertTrue(
        response.contains("Price cannot be negative"), "Expected error message for negative price");
  }

  // Testing invalid params
  @Test
  void testUpdateListing_MissingParams() throws IOException {
    String response = sendGetRequest("/update-listings?uid=bibif&listingId=" + listingId);

    // Verify the response contains success even with no updates
    assertTrue(
        response.contains("\"response_type\":\"success\""),
        "Expected a success response with no updates");
  }

  // Testing invalid condition
  @Test
  void testUpdateListing_InvalidCondition() throws IOException {
    String response =
        sendGetRequest("/update-listings?uid=bibif&listingId=" + listingId + "&condition=invalid");

    // Verify the response contains failure for invalid condition
    assertTrue(
        response.contains("\"response_type\":\"failure\""),
        "Expected a failure response for invalid condition");
    assertTrue(
        response.contains("Please choose from valid condition inputs"),
        "Expected error message for invalid condition");
  }
}
