package edu.brown.cs.student.main.server.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.main.server.handlers.DeleteListingHandler;
import edu.brown.cs.student.main.server.storage.MockedFirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

public class DeleteListingsHandlerTest {
  private StorageInterface mockStorage;
  private DeleteListingHandler deleteListingHandler;

  @BeforeEach
  void setUp() throws ExecutionException, InterruptedException {
    // Initialize the mock storage
    mockStorage = new MockedFirebaseUtilities();
    deleteListingHandler = new DeleteListingHandler(mockStorage);

    // Populate the mock storage with initial data
    Map<String, Object> listing = new HashMap<>();
    listing.put("listingId", "listing-0");
    listing.put("title", "Car");
    listing.put("price", 28);
    listing.put("description", "New Benz");

    mockStorage.addDocument("user123", "listings", "listing-0", listing);
    System.out.print("this is in storage" + mockStorage.getCollection("user123", "listings"));
  }

  // Mocked test: testing success case
  @Test
  void testDeleteListing_Success() throws Exception {
    Request mockRequest =
        createMockRequest(
            Map.of(
                "uid", "user123",
                "listingId", "listing-0"));
    Response mockResponse = createMockResponse();

    String response = (String) deleteListingHandler.handle(mockRequest, mockResponse);

    System.out.println("Response: " + response);
    assertTrue(response.contains("\"response_type\":\"success\""));

    // Verify the listing is deleted
    Map<String, Object> listing = mockStorage.getListingForUser("user123", "listing-0");
    System.out.println("Listing: " + listing);
    System.out.print("this is still in storage" + mockStorage.getCollection("user123", "listings"));
  }

  // Mocked test: testing invalid lisitng (edge case)
  @Test
  void testDeleteListing_Failure_InvalidListingId() throws Exception {
    Request mockRequest =
        createMockRequest(
            Map.of(
                "uid", "user123",
                "listingId", "invalid-listing-id"));
    Response mockResponse = createMockResponse();

    String response = (String) deleteListingHandler.handle(mockRequest, mockResponse);

    System.out.println("Response: " + response);
    assertTrue(response.contains("\"response_type\":\"failure\""));
    assertTrue(response.contains("Listing with ID invalid-listing-id does not exist"));
  }

  // Mocked test: testing missing params (edge case)
  @Test
  void testDeleteListing_Failure_MissingParams() throws Exception {
    Request mockRequest =
        createMockRequest(
            Map.of(
                "uid", "user123"
                // Missing "listingId"
                ));
    Response mockResponse = createMockResponse();

    String response = (String) deleteListingHandler.handle(mockRequest, mockResponse);

    System.out.println("Response: " + response);
    assertTrue(response.contains("\"response_type\":\"failure\""));
    assertTrue(response.contains("Both 'uid' and 'listingId' are required"));
  }

  private Request createMockRequest(Map<String, String> params) {
    return new Request() {
      @Override
      public String queryParams(String key) {
        return params.get(key);
      }
    };
  }

  private Response createMockResponse() {
    return new Response() {
      // Implement as needed
    };
  }
}
