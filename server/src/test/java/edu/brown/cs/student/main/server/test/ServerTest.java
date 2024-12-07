package edu.brown.cs.student.main.server.test;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import spark.Spark;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {

  private static final String BASE_URL = "http://localhost:3232";

//  @BeforeAll
//  public static void setUp() {
//    // Ensure server is running before tests
//    Server.setUpServer();
//  }

  /**
   * Helper method to send a GET request to the server.
   *
   * @param endpoint The endpoint to hit.
   * @return The server's response as a string.
   * @throws IOException If an I/O error occurs during the request.
   */
  private String sendGetRequest(String endpoint) throws IOException {
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

  //Testing the interactions between creating a user and adding a listing
  @Test
  void testCreateUserAndAddListing() throws IOException {
    // Create user
    String createUserResponse = sendGetRequest(
        "/create-user?uid=testsample1&username=testerdanger&email=testuser1@brown.com&password=testpassword");
    System.out.println(createUserResponse);
    assertTrue(createUserResponse.contains("\"response_type\":\"success\""),
        "User creation should be successful");

    // Extract user ID
    String userId = extractUserId(createUserResponse);
    System.out.println(userId);

    // Add listing for the new user
//    http://localhost:3232/add-listings?uid=bibif&username=bibifol&title=Summersandalst&price=49.99&imageUrl=server/src/data/IMG_4132.PNG&condition=new&tags=summer&description=sandals
    String addListingResponse = sendGetRequest(
        "/add-listings?uid=testsample1&username=testerdanger&title=Test%20Listing&price=100&imageUrl=server/src/data/IMG_4132.PNG&condition=new&tags=CS320&description=Integration");
  System.out.println(addListingResponse);
    assertTrue(addListingResponse.contains("\"response_type\":\"success\""),
        "Listing addition should be successful");
    assertTrue(addListingResponse.contains("\"title\":\"Test Listing\""),
        "Listing should have correct title");
  }
  //Testing the interactions between creating a user, adding a listing, updating a listing and deleting a listing
  @Test
  void testListingLifecycle() throws IOException {
    String userId = "testuser_" + System.currentTimeMillis();

    // Create User
    String createUserResponse = sendGetRequest(
        "/create-user?uid=testsample2&username=stangerdanger&email=testuser2@brown.com&password=testpassword");
    System.out.println(createUserResponse);
    assertTrue(createUserResponse.contains("\"response_type\":\"success\""),
        "User creation should be successful");

    // Add Listing
    String addListingResponse = sendGetRequest(
        "/add-listings?uid=testsample2&username=stangerdanger&title=Cargo%20Listing&price=356&imageUrl=server/src/data/IMG_4132.PNG&condition=used&tags=CS320&description=bags");
    System.out.println(addListingResponse);
    assertTrue(addListingResponse.contains("\"response_type\":\"success\""),
        "Listing addition should be successful");


    // Extract Listing ID
    String listingId = extractListingId(addListingResponse);
    System.out.println(listingId);

    String updateurl = "/update-listings?uid=testsample2&listingId=" + listingId;
    System.out.println(updateurl);
    // Update Listing
    String updateResponse = sendGetRequest(updateurl);



    System.out.println("this is the req:" + updateResponse);
    assertTrue(updateResponse.contains("\"response_type\":\"success\""));
    assertTrue(updateResponse.contains("\"title\":\"Cargo Listing\""));
    assertTrue(updateResponse.contains("\"price\":\"356\""));

    String deleteurl= "/delete-listings?uid=testsample2" + "&listingId=" + listingId;

    // Delete Listing
    String deleteResponse = sendGetRequest(deleteurl);
    System.out.println(deleteResponse);
    assertTrue(deleteResponse.contains("\"response_type\":\"success\""));
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
  //Testing the interactions between adding a listing and deleting a listing
  @Test
  void testAddAndDeleteListing() throws IOException {
    // Define test user and listing details
    String userId = "testsample2";
    String username = "strangerdanger";
    String title = "New Porsche";
    String description = "Integration test listing";
    String price = "7999";
    String imageUrl = "server/src/data/IMG_4132.PNG";
    String condition = "new";
    String tags = "CS320";


    // Add listing
    String addListingResponse = sendGetRequest(
        "/add-listings?uid=" + userId +
            "&username=" + username +
            "&title=" + title +
            "&tags=" + tags +
            "&price=" + price +
            "&imageUrl=" + imageUrl +
            "&condition=" + condition +
            "&description=" + description
    );

    // Verify listing was added successfully
    assertTrue(addListingResponse.contains("\"response_type\":\"success\""),
        "Listing addition should be successful");
    assertTrue(addListingResponse.contains("\"title\":\"" + title + "\""),
        "Listing should have correct title");

    // Extract listing ID from response (you may need to implement JSON parsing)
    String listingId = extractListingId(addListingResponse);

    // Delete the newly added listing
    String deleteResponse = sendGetRequest(
        "/delete-listings?uid=" + userId +
            "&listingId=" + listingId
    );
    String deleteResponse2 = sendGetRequest(
        "/delete-listings?uid=" + userId +
            "&listingId=" + listingId
    );


    // Verify listing was deleted successfully
    assertTrue(deleteResponse.contains("\"response_type\":\"success\""),
        "Listing deletion should be successful");
    assertTrue(deleteResponse2.contains("\"response_type\":\"failure\""));
  }

  //Testing the interactions for listing all available listings
  @Test
  void testListAllUserListings() throws IOException {
    String alluserlistings = sendGetRequest("/list-all-listings");
    System.out.println(alluserlistings);
    assertTrue(alluserlistings.contains("\"response_type\":\"success\""));
    assertTrue(alluserlistings.length() > 2, "Listings array should not be empty");
  }
  //Testing the interactions for list listings for a user
  @Test
  void testListListings() throws IOException {
    String userlistings1 = sendGetRequest("/list-listings?uid=bibif&listing-id=listing-0");
    System.out.println(userlistings1);
    assertTrue(userlistings1.contains("\"response_type\":\"success\""));
    assertTrue(userlistings1.contains("condition=new, price=49.99"));
  }
  //Testing the interactions for like listings and add-listing
  @Test
  void testLikeListings() throws IOException {
    String likelisting1 = sendGetRequest("/like-listings?uid=bibif&listingId=listing-02024-12-06%2012:27:19");
    assertTrue(likelisting1.contains("\"response_type\":\"success\""));
    assertTrue(likelisting1.contains("listing-02024-12-06 12:27:19"));

//
    // Add Listing 1
    String addListingResponse = sendGetRequest(
        "/add-listings?uid=bibif&username=bibifol&title=Parrot%20Listing&price=200&imageUrl=server/src/data/IMG_4132.PNG&condition=used&tags=CS320&description=bags");
    System.out.println(addListingResponse);
    assertTrue(addListingResponse.contains("\"response_type\":\"success\""),
        "Listing addition should be successful");

    String listingId = extractListingId(addListingResponse);
    System.out.println(listingId);
    String url = "/like-listings?uid=bibif&listingId=" + listingId;
    System.out.println(url);
    String likeListing2 = sendGetRequest(url);
    assertTrue(likeListing2.contains("\"response_type\":\"success\""));
    assert listingId != null;
    assertTrue(likeListing2.contains(listingId));
//
//    // Add Listing 2
//    String addListingResponse2 = sendGetRequest(
//        "/add-listings?uid=testsample3&username=justachillguy&title=Benz%20Listing&price=10000&imageUrl=server/src/data/IMG_4132.PNG&condition=used&tags=CS320&description=bags");
//    System.out.println(addListingResponse2);
//    assertTrue(addListingResponse.contains("\"response_type\":\"success\""),
//        "Listing addition should be successful");





  }

  //Testing the interactions for filtering-listing
    @Test
  void testFilterListings() throws IOException {
    String filterResponse = sendGetRequest(
        "/filter-listings?keyword=Cargo%20Listing&filterByTitle=true&filterByCondition=false&filterByTag=false&filterByDescription=false");

   System.out.println(filterResponse);
    assertTrue(filterResponse.contains("\"response_type\":\"success\""),
        "Listing filter should be successful");

    assertTrue(filterResponse.contains("filtered_listings\":[{\"uid\":\"bibif\""));
  }

  // Utility method to extract user ID
  private String extractUserId(String response) {
    return "extracted-user-id";
  }

}
