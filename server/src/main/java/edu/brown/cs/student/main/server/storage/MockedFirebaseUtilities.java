package edu.brown.cs.student.main.server.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

/**
 * This class is a mock implementation of the Firestore utilities. It simulates Firestore operations
 * as a way to test the server without connecting to Firestore, while still simulating the Firestore
 * data.
 */
public class MockedFirebaseUtilities implements StorageInterface {

  // user id -> maps to collection id -> maps to document id -> maps to document data (key-value
  // pairs)
  private final Map<String, Map<String, Map<String, Map<String, Object>>>> database =
      new HashMap<>();

  // Adding a document
  @Override
  public void addDocument(
      String uid, String collection_id, String doc_id, Map<String, Object> data) {
    if (uid == null || collection_id == null || doc_id == null || data == null) {
      throw new IllegalArgumentException(
          "addDocument: uid, collection_id, doc_id, or data cannot be null");
    }
    // Simulate adding a document to Firestore
    database
        .computeIfAbsent(uid, k -> new HashMap<>())
        .computeIfAbsent(collection_id, k -> new HashMap<>())
        .put(doc_id, new HashMap<>(data));
  }

  @Override
  public List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException {
    if (uid == null || collection_id == null) {
      throw new IllegalArgumentException("getCollection: uid and/or collection_id cannot be null");
    }
    // Fetch the collection for the given user
    Map<String, Map<String, Map<String, Object>>> userMop =
        database.getOrDefault(uid, Collections.emptyMap());

    Map<String, Map<String, Object>> collection =
        userMop.getOrDefault(collection_id, Collections.emptyMap());

    // Convert the Map<String, Map<String, Object>> to List<Map<String, Object>>
    List<Map<String, Object>> result = new ArrayList<>();

    for (Entry<String, Map<String, Object>> entry : collection.entrySet()) {
      Map<String, Object> documentData = new HashMap<>(entry.getValue());
      documentData.put("doc_id", entry.getKey()); // Optionally include the document ID
      result.add(documentData);
    }
    return result;
  }

  @Override
  public void clearUser(String uid) {
    if (uid == null) {
      throw new IllegalArgumentException("clearUser: uid cannot be null");
    }
    // Simulate clearing all user data
    database.remove(uid);
  }

  @Override
  public List<Map<String, Object>> getAllUsersListings() {
    List<Map<String, Object>> allListings = new ArrayList<>();
    // Simulate fetching all markers for all users
    // all listings -> listings for one user -> one listing one user -> map from listing to listing
    for (Map.Entry<String, Map<String, Map<String, Map<String, Object>>>> userEntry :
        database.entrySet()) {
      String uid = userEntry.getKey();
      Map<String, Map<String, Map<String, Object>>> collections = userEntry.getValue();
      Map<String, Map<String, Object>> listings =
          collections.getOrDefault("listing", Collections.emptyMap());
      // Add uid to each marker data
      for (Map<String, Object> listing : listings.values()) {
        // Add uid to the marker data
        Map<String, Object> listingWithUid = new HashMap<>(listing);
        listingWithUid.put("uid", uid);
        allListings.add(listingWithUid);
      }
    }
    return allListings;
  }
}
