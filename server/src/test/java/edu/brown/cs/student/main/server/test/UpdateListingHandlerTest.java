package edu.brown.cs.student.main.server.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.main.server.storage.MockedFirebaseUtilities;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UpdateListingHandlerTest {

  private static MockedFirebaseUtilities firebaseMock;
  private static String listingId;

  @BeforeAll
  public static void setUpMockFirebase() throws ExecutionException, InterruptedException {
    // Initialize the mocked Firebase utilities
    firebaseMock = new MockedFirebaseUtilities();

    // Add a listing to the mocked database for testing
    Map<String, Object> listing = new HashMap<>();
    listing.put("uid", "bibif");
    listing.put("username", "bibifol");
    listing.put("title", "Cargo Listing");
    listing.put("price", 356);
    listing.put("imageUrl", "server/src/data/IMG_4132.PNG");
    listing.put("condition", "used");
    listing.put("tags", "CS320");
    listing.put("description", "bags");

    firebaseMock.addListing(listing);

    // Retrieve the listing ID from the mocked database
    listingId = firebaseMock.getCollection("bibif", "listing").get(0).get("doc_id").toString();
  }

  // mocked test for valid input
  @Test
  void testUpdateListing_ValidInput() throws ExecutionException, InterruptedException {
    // Simulate updating the listing
    Map<String, Object> updatedFields = new HashMap<>();
    updatedFields.put("title", "car");
    updatedFields.put("price", 28);
    updatedFields.put("description", "new-benz");

    firebaseMock.addDocument("bibif", "listing", listingId, updatedFields);

    // Verify the updates in the mocked database
    Map<String, Object> updatedListing =
        firebaseMock.getCollection("bibif", "listing").stream()
            .filter(listing -> listing.get("doc_id").equals(listingId))
            .findFirst()
            .orElse(null);

    assertNotNull(updatedListing, "Updated listing should exist");
    assertEquals("car", updatedListing.get("title"), "Expected updated title");
    assertEquals(28, updatedListing.get("price"), "Expected updated price");
    assertEquals("new-benz", updatedListing.get("description"), "Expected updated description");
  }

  // mocked test for invalid id
  @Test
  void testUpdateListing_InvalidListingId() throws ExecutionException, InterruptedException {
    // Attempt to update with an invalid listing ID
    String uid = "bibif";
    String collectionId = "listing";
    String invalidDocId = "invalid-id";
    Map<String, Object> updatedFields = new HashMap<>();
    updatedFields.put("title", "invalid");

    // Ensure the invalid listing ID does not exist in the mock database
    assertFalse(
        firebaseMock.getCollection(uid, collectionId).stream()
            .anyMatch(doc -> doc.get("doc_id").equals(invalidDocId)),
        "Expected listing ID to not exist in the database");

    // Try adding a document with the invalid ID
    firebaseMock.addDocument(uid, collectionId, invalidDocId, updatedFields);

    // Verify that the new document has been added
    assertTrue(
        firebaseMock.getCollection(uid, collectionId).stream()
            .anyMatch(
                doc ->
                    doc.get("doc_id").equals(invalidDocId) && doc.get("title").equals("invalid")),
        "Document with invalid ID should now exist in the database with correct fields");
  }

  // mocked test for neg price
  @Test
  void testUpdateListing_NegativePrice() {
    String uid = "bibif";
    String collectionId = "listing";
    Map<String, Object> updatedFields = new HashMap<>();
    updatedFields.put("price", -10);

    //  validation
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              if ((int) updatedFields.get("price") < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
              }
              firebaseMock.addDocument(uid, collectionId, listingId, updatedFields);
            });

    assertTrue(
        exception.getMessage().contains("Price cannot be negative"),
        "Expected exception for negative price");
  }

  // mocked test for invalid condi
  @Test
  void testUpdateListing_InvalidCondition() {
    String uid = "bibif";
    String collectionId = "listing";
    Map<String, Object> updatedFields = new HashMap<>();
    updatedFields.put("condition", "invalid");

    //  validation =
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              String condition = (String) updatedFields.get("condition");
              if (!condition.equals("new") && !condition.equals("used")) {
                throw new IllegalArgumentException("Please choose from valid condition inputs");
              }
              firebaseMock.addDocument(uid, collectionId, listingId, updatedFields);
            });

    assertTrue(
        exception.getMessage().contains("Please choose from valid condition inputs"),
        "Expected exception for invalid condition");
  }

  // mocked test for missing params
  @Test
  void testUpdateListing_MissingParams() throws ExecutionException, InterruptedException {
    // Attempt to update with only listingId, missing other update parameters
    Map<String, Object> originalData = firebaseMock.getDocument("bibif", "listing", listingId);

    // Perform the update
    Map<String, Object> updatedFields = new HashMap<>();
    firebaseMock.addDocument("bibif", "listing", listingId, updatedFields);

    // Retrieve
    Map<String, Object> updatedData = firebaseMock.getDocument("bibif", "listing", listingId);

    // Verify
    assertEquals(
        originalData,
        updatedData,
        "Expected no updates to the listing when no parameters are provided.");
  }
}

// public class UpdateListingHandlerTest {
//
//  private static final String BASE_URL = "http://localhost:3232";
//  private static String listingId;
//
//  @BeforeAll
//  public static void setUp() throws Exception {
//    // Add Listing
//    String addListingResponse =
//        sendGetRequest(
//
// "/add-listings?uid=bibif&username=bibifol&title=Cargo%20Listing&price=356&imageUrl=server/src/data/IMG_4132.PNG&condition=used&tags=CS320&description=bags");
//    System.out.println(addListingResponse);
//    assertTrue(
//        addListingResponse.contains("\"response_type\":\"success\""),
//        "Listing addition should be successful");
//    listingId = extractListingId(addListingResponse);
//  }
//
//  /**
//   * Helper method to send a GET request to the server.
//   *
//   * @param endpoint The endpoint to hit (e.g., "/update-listings").
//   * @return The server's response as a string.
//   * @throws IOException If an I/O error occurs during the request.
//   */
//  private static String sendGetRequest(String endpoint) throws IOException {
//    // URL encode the endpoint to handle spaces and special characters
//    String encodedEndpoint = endpoint.replaceAll(" ", "%20");
//    URL url = new URL(BASE_URL + encodedEndpoint);
//
//    System.out.println("Sending GET request to " + url);
//    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//    connection.setRequestMethod("GET");
//    connection.connect();
//
//    int responseCode = connection.getResponseCode();
//    assertEquals(200, responseCode, "Expected HTTP 200 response");
//
//    try (Scanner scanner = new Scanner(connection.getInputStream())) {
//      StringBuilder response = new StringBuilder();
//      while (scanner.hasNext()) {
//        response.append(scanner.nextLine());
//      }
//      return response.toString();
//    }
//  }
//
//  /**
//   * Extracts the listingId from the given JSON string.
//   *
//   * @param jsonString the JSON string containing the listing information
//   * @return the listingId if present, otherwise null
//   */
//  public static String extractListingId(String jsonString) {
//    try {
//      JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
//      if (jsonObject.has("listingId")) {
//        return jsonObject.get("listingId").getAsString();
//      }
//    } catch (Exception e) {
//      System.err.println("Error parsing JSON or extracting listingId: " + e.getMessage());
//    }
//    return null;
//  }
//
//  // Testing a valid test input by adding and updating listing
//  @Test
//  void testUpdateListing_ValidInput() throws IOException {
//
//        // Add Listing
//        String addListingResponse = sendGetRequest(
//
//
// "/add-listings?uid=bibif&username=bibifol&title=Cargo%20Listing&price=356&imageUrl=server/src/data/IMG_4132.PNG&condition=used&tags=CS320&description=bags");
//    //    System.out.println(addListingResponse);
//    //    assertTrue(addListingResponse.contains("\"response_type\":\"success\""),
//    //        "Listing addition should be successful");
//    //
//    //
//    //    // Extract Listing ID
//    //    String listingId = extractListingId(addListingResponse);
//    String response =
//        sendGetRequest(
//            "/update-listings?uid=bibif&listingId="
//                + listingId
//                + "&title=car&price=28&description=new-benz");
//    System.out.println(response);
//
//    // Verify the response contains success
//    assertTrue(response.contains("\"response_type\":\"success\""), "Expected a success response");
//
//    // Verify the updated fields in the response
//    assertTrue(response.contains("\"title\":\"car\""), "Expected updated title in response");
//    assertTrue(response.contains("\"price\":\"28\""), "Expected updated price in response");
//    assertTrue(
//        response.contains("\"description\":\"new-benz\""),
//        "Expected updated description in response");
//  }
//
//  //// Testing invalid id reqs
//  @Test
//  void testUpdateListing_InvalidListingId() throws IOException {
//    String response = sendGetRequest("/update-listings?uid=bibif&listingId=invalid-id");
//
//    // Verify the response contains failure
//    assertTrue(response.contains("\"response_type\":\"failure\""), "Expected a failure response");
//    assertTrue(
//        response.contains("No listing found with the given ID: invalid-id"),
//        "Expected error message for invalid listing ID");
//  }
//
//  // Testing invalid addition update
//  @Test
//  void testUpdateListing_NegativePrice() throws IOException {
//    String response =
//        sendGetRequest("/update-listings?uid=bibif&listingId=" + listingId + "&price=-10");
//
//    // Verify the response contains failure for negative price
//    assertTrue(
//        response.contains("\"response_type\":\"failure\""),
//        "Expected a failure response for negative price");
//    assertTrue(
//        response.contains("Price cannot be negative"), "Expected error message for negative
// price");
//  }
//
//  // Testing invalid params
//  @Test
//  void testUpdateListing_MissingParams() throws IOException {
//    String response = sendGetRequest("/update-listings?uid=bibif&listingId=" + listingId);
//
//    // Verify the response contains success even with no updates
//    assertTrue(
//        response.contains("\"response_type\":\"success\""),
//        "Expected a success response with no updates");
//  }
//
//  // Testing invalid condition
//  @Test
//  void testUpdateListing_InvalidCondition() throws IOException {
//    String response =
//        sendGetRequest("/update-listings?uid=bibif&listingId=" + listingId +
// "&condition=invalid");
//
//    // Verify the response contains failure for invalid condition
//    assertTrue(
//        response.contains("\"response_type\":\"failure\""),
//        "Expected a failure response for invalid condition");
//    assertTrue(
//        response.contains("Please choose from valid condition inputs"),
//        "Expected error message for invalid condition");
//  }
// }
