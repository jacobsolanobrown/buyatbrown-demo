package edu.brown.cs.student.main.server.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
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
  private final List<Map<String, Object>> listings = new ArrayList<>();

  // Adding a document
  @Override
  public void addDocument(
      String uid, String collection_id, String doc_id, Map<String, Object> data) {
    if (uid == null || collection_id == null || doc_id == null || data == null) {
      throw new IllegalArgumentException(
          "addDocument: uid, collection_id, doc_id, or data cannot be null");
    }
    // Simulate adding a document to Firestore
//    database
//        .computeIfAbsent(uid, k -> new HashMap<>())
//        .computeIfAbsent(collection_id, k -> new HashMap<>())
//        .put(doc_id, new HashMap<>(data));
    database.computeIfAbsent(uid, k -> new HashMap<>())
        .computeIfAbsent(collection_id, k -> new HashMap<>())
        .put(doc_id, data);
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
  public void addListing(Map<String, Object> listing) {
    // Ensure the database structure exists for this user
    String uid = (String) listing.get("uid");

    // Create nested structure if it doesn't exist
    database.putIfAbsent(uid, new HashMap<>());
    Map<String, Map<String, Map<String, Object>>> userCollections =
        database.get(uid);

    userCollections.putIfAbsent("listing", new HashMap<>());
    Map<String, Map<String, Object>> userListings =
        userCollections.get("listing");

    // Add the listing with a unique key (you might want to generate a unique ID)
    String listingId = UUID.randomUUID().toString(); // or use another unique ID generation method
    userListings.put(listingId, new HashMap<>(listing));
  }

  @Override
  public void removeDocument(String uid, String collection_id, String doc_id) {
    // Check if the user exists in the database
    if (!database.containsKey(uid)) {
      throw new IllegalArgumentException("User ID does not exist.");
    }

    // Get the collections for the user
    Map<String, Map<String, Map<String, Object>>> userCollections = database.get(uid);

    // Check if the specified collection exists
    if (!userCollections.containsKey(collection_id)) {
      throw new IllegalArgumentException("Collection ID does not exist for user.");
    }

    // Get the documents in the collection
    Map<String, Map<String, Object>> collection = userCollections.get(collection_id);

    // Check if the specified document exists
    if (!collection.containsKey(doc_id)) {
      throw new IllegalArgumentException("Listing with ID " + doc_id + " does not exist");
    }

    // Remove the specified document
    collection.remove(doc_id);

    // If the collection is empty after removal, optionally clean up the empty collection
    if (collection.isEmpty()) {
      userCollections.remove(collection_id);
    }
  }

  // Add helper methods for tests
  public void addListing(String uid, String collection, String listingId,
      Map<String, Object> listing) {
    // Ensure the user exists
    database.computeIfAbsent(uid, k -> new HashMap<>())
        // Ensure the collection exists for the user
        .computeIfAbsent(collection, k -> new HashMap<>())
        // Add or update the listing
        .put(listingId, listing);
  }


  @Override
  public Map<String, Object> getListingForUser(String uid, String listingId)
      throws InterruptedException, ExecutionException {
    return Map.of();
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
  public List<Map<String, Object>> getAllUsers() throws InterruptedException, ExecutionException {
//    List<String> userIds = new ArrayList<>();
//    List<String> collectionIds = new ArrayList<>();
//    List<String> documentIds = new ArrayList<>();
//    List<String> dataKeys = new ArrayList<>();
//
//    for (String userId : database.keySet()) {
//      userIds.add(userId);
//      Map<String, Map<String, Map<String, Object>>> collections = database.get(userId);
//
//      for (String collectionId : collections.keySet()) {
//        collectionIds.add(collectionId);
//        Map<String, Map<String, Object>> documents = collections.get(collectionId);
//
//        for (String documentId : documents.keySet()) {
//          documentIds.add(documentId);
//          Map<String, Object> documentData = documents.get(documentId);
//
//          dataKeys.addAll(documentData.keySet());
//        }
//      }
//    }

//    // Print or return the lists as needed
//    System.out.println("User IDs: " + userIds);
//    System.out.println("Collection IDs: " + collectionIds);
//    System.out.println("Document IDs: " + documentIds);
//    System.out.println("Data Keys: " + dataKeys);
//  return List.of("User IDs", userIds);
    List<Map<String, Object>> userIdList = new ArrayList<>();

    for (Map.Entry<String, Map<String, Map<String, Map<String, Object>>>> userEntry : database.entrySet()) {
      String userId = userEntry.getKey();
      Map<String, Map<String, Map<String, Object>>> collections = userEntry.getValue();

      Map<String, Object> userInfo = new HashMap<>();
      userInfo.put("userId", userId);
      userInfo.put("collections", collections);

      userIdList.add(userInfo);
    }

    return userIdList;
  }

  /**
   * TODO: THIS IS NOT IMPLEMENTED YET
   * @return
   * @throws ExecutionException
   * @throws InterruptedException
   */
  @Override
  public List<Map<String, Object>> getAllUserDataMaps()
    throws ExecutionException, InterruptedException {
    return List.of();
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

  @Override
  public Map<String, Object> getDocument(String uid, String collectionId, String docId)
      throws IllegalArgumentException, ExecutionException, InterruptedException {
    return Map.of();
  }
}

