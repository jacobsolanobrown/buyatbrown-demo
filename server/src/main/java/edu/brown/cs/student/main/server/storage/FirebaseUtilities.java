package edu.brown.cs.student.main.server.storage;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FirebaseUtilities implements StorageInterface {
  public FirebaseUtilities() throws IOException {
    // TODO: FIRESTORE PART 0:
    // Create /resources/ folder with firebase_config.json and
    // add your admin SDK from Firebase. see:
    // https://docs.google.com/document/d/10HuDtBWjkUoCaVj_A53IFm5torB_ws06fW3KYFZqKjc/edit?usp=sharing
    String workingDirectory = System.getProperty("user.dir");
    System.out.println("Working Directory: " + workingDirectory);

    Path firebaseConfigPath =
        Paths.get(workingDirectory, "server/resources", "firebase_config.json");
    System.out.println("Full Firebase Config Path: " + firebaseConfigPath.toString());
    System.out.println("Absolute Path: " + firebaseConfigPath.toAbsolutePath());
    System.out.println("File Exists: " + Files.exists(firebaseConfigPath));

    try {
      if (!Files.exists(firebaseConfigPath)) {
        // Try alternative paths
        Path[] alternativePaths = {
          Paths.get(workingDirectory, "resources", "firebase_config.json"),
          Paths.get(workingDirectory, "firebase_config.json"),
          Paths.get("firebase_config.json"),
          Paths.get(System.getProperty("user.home"), "firebase_config.json")
        };

        for (Path altPath : alternativePaths) {
          System.out.println("Checking alternative path: " + altPath.toAbsolutePath());
          if (Files.exists(altPath)) {
            firebaseConfigPath = altPath;
            break;
          }
        }
      }

      System.out.println("Setting up Service Account");
      FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toString());

      FirebaseOptions options =
          new FirebaseOptions.Builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .build();

      System.out.println("Initializing Firebase");
      FirebaseApp.initializeApp(options);
      System.out.println("ekfrmew Firebase");
    } catch (Exception e) {
      System.err.println("Detailed Error: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }

    //    try {
    //      if (!Files.exists(firebaseConfigPath)) {
    //        // Try alternative paths
    //        Path[] alternativePaths = {
    //          Paths.get(workingDirectory, "resources", "firebase_config.json"),
    //          Paths.get(workingDirectory, "firebase_config.json"),
    //          Paths.get("firebase_config.json"),
    //          Paths.get(System.getProperty("user.home"), "firebase_config.json")
    //        };
    //
    //        for (Path altPath : alternativePaths) {
    //          System.out.println("Checking alternative path: " + altPath.toAbsolutePath());
    //          if (Files.exists(altPath)) {
    //            firebaseConfigPath = altPath;
    //            break;
    //          }
    //        }
    //      }
    //
    //      System.out.println("Setting up Service Account");
    ////      FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toString());
    //      FileInputStream serviceAccount = new FileInputStream("resources/google_cred.json");
    //
    ////      FirebaseOptions options =
    ////          new FirebaseOptions.Builder()
    ////              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
    ////              .build();
    //      FirebaseOptions options = new FirebaseOptions.Builder()
    //          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
    //          .setStorageBucket("buy-at-brown-listing-images")
    //          .build();
    //
    //      System.out.println("Initializing Firebase");
    //      FirebaseApp.initializeApp(options);
    //      System.out.println("ekfrmew Firebase");
    //    } catch (Exception e) {
    //      System.err.println("Detailed Error: " + e.getMessage());
    //      e.printStackTrace();
    //      throw e;
    //    }
  }

  /**
   * Generates a signed URL for accessing a file in Firebase Storage.
   *
   * @param bucketName The name of the Firebase Storage bucket.
   * @param objectName The path of the file in the bucket.
   * @return A signed URL for the file.
   */
  public String generateSignedUrl(String bucketName, String objectName) {
    // Get the default Firebase Storage bucket
    Bucket bucket = StorageClient.getInstance().bucket(bucketName);

    // Get the blob (file) from the bucket
    Blob blob = bucket.get(objectName);

    if (blob == null) {
      throw new IllegalArgumentException("Object not found: " + objectName);
    }

    // Generate a signed URL that expires in 1 hour
    return blob.signUrl(1, TimeUnit.HOURS).toString();
  }

  @Override
  public List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException, IllegalArgumentException {
    if (uid == null || collection_id == null) {
      throw new IllegalArgumentException("getCollection: uid and/or collection_id cannot be null");
    }

    // gets all documents in the collection 'collection_id' for user 'uid'

    Firestore db = FirestoreClient.getFirestore();
    // 1: Make the data payload to add to your collection
    CollectionReference dataRef = db.collection("users").document(uid).collection(collection_id);

    // 2: Get pin documents
    QuerySnapshot dataQuery = dataRef.get().get();

    // 3: Get data from document queries
    List<Map<String, Object>> data = new ArrayList<>();
    for (QueryDocumentSnapshot doc : dataQuery.getDocuments()) {
      //      System.out.print(doc);
      data.add(doc.getData());
    }

    return data;
  }

  @Override
  public void addListing(Map<String, Object> listing) {}

  @Override
  public void addDocument(String uid, String collection_id, String doc_id, Map<String, Object> data)
      throws IllegalArgumentException {
    if (uid == null || collection_id == null || doc_id == null || data == null) {
      throw new IllegalArgumentException(
          "addDocument: uid, collection_id, doc_id, or data cannot be null");
    }
    // adds a new document 'doc_name' to colleciton 'collection_id' for user 'uid'
    // with data payload 'data'.

    // TODO: FIRESTORE PART 1:
    // use the guide below to implement this handler
    // - https://firebase.google.com/docs/firestore/quickstart#add_data

    Firestore db = FirestoreClient.getFirestore();
    // 1: Get a ref to the collection that you created
    DocumentReference docRef =
        db.collection("users").document(uid).collection(collection_id).document(doc_id);
    // 2: Write data to the collection ref
    docRef.set(data);
    //    System.out.println("addDocument called with uid: " + uid + ", collection_id: " +
    // collection_id + ", doc_id: " + doc_id + ", data: " + data);

  }

  @Override
  public void removeDocument(String uid, String collection_id, String doc_id)
      throws IllegalArgumentException {
    if (uid == null || collection_id == null || doc_id == null) {
      throw new IllegalArgumentException(
          "removeDocument: uid, collection_id, or doc_id cannot be null");
    }

    // Log operation for debugging purposes
    System.out.println(
        "Attempting to delete document: "
            + doc_id
            + " from collection: "
            + collection_id
            + " for user: "
            + uid);
    // Initialize Firestore instance
    Firestore db = FirestoreClient.getFirestore();

    // Get reference to the document
    DocumentReference docRef =
        db.collection("users").document(uid).collection(collection_id).document(doc_id);
    // Retrieve the document to get the imageUrl
    ApiFuture<DocumentSnapshot> future = docRef.get();
    try {
      DocumentSnapshot document = future.get();
      if (document.exists()) {
        // Extract the imageUrl field
        String imageUrl = document.getString("imageUrl");
        System.out.println(imageUrl);
        if (imageUrl != null && !imageUrl.isEmpty()) {
          // Delete the image from Firebase Storage
          deleteImageFromStorage(imageUrl);
        } else {
          System.out.println("No imageUrl found for document: " + doc_id);
        }

        // Delete the document from Firestore
        docRef
            .delete()
            .addListener(
                () -> {
                  System.out.println("Document deleted successfully: " + doc_id);
                },
                Runnable::run);
      } else {
        System.out.println("Document not found: " + doc_id);
      }
    } catch (Exception e) {
      System.err.println("Error while deleting document or image: " + e.getMessage());
    }
    // Delete the document
    //    docRef.delete();
  }

  // Helper method to delete image from Firebase Storage
  public void deleteImageFromStorage(String imageUrl) {
    try {
      // Get the Firebase Storage bucket name
      //      String bucketName = FirebaseApp.getInstance().getOptions().getStorageBucket();
      String bucketName = "buy-at-brown-listing-images";
      System.out.println("Bucket name: " + bucketName);

      // Extract the object path from the imageUrl
      // Assuming imageUrl is something like:
      // https://storage.googleapis.com/{bucketName}/images/{objectName}
      //      String objectName = imageUrl.substring(imageUrl.indexOf("/images/") + 1);
      String objectName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
      System.out.println("Parsed object name: " + objectName);

      // Get the Blob object and delete it
      Blob blob = StorageClient.getInstance().bucket(bucketName).get(objectName);
      if (blob != null) {
        blob.delete();
        System.out.println("Image deleted successfully: " + objectName);
      } else {
        System.out.println("No image found with path: " + objectName);
      }
    } catch (Exception e) {
      System.err.println("Error while deleting image from storage: " + e.getMessage());
    }
  }

  /**
   * Return singular listing for a user.
   *
   * @param uid
   * @param listingId
   * @return
   * @throws InterruptedException
   * @throws ExecutionException
   */
  @Override
  public Map<String, Object> getListingForUser(String uid, String listingId)
      throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();

    // 1. Get a reference to the user's document
    DocumentReference userRef = db.collection("users").document(uid);

    // 2. Get the user's listings collection
    CollectionReference listingsRef = userRef.collection("listings");

    // 3. Fetch all listings for the user
    ApiFuture<QuerySnapshot> future = listingsRef.get();

    // 4. Retrieve the query result (list of listings)
    QuerySnapshot querySnapshot = future.get();

    // 5. Search for the listing with the specified listingId
    Map<String, Object> listing =
        querySnapshot.getDocuments().stream()
            .filter(doc -> doc.getId().equalsIgnoreCase(listingId)) // Match based on listingId
            .map(DocumentSnapshot::getData) // Convert to Map<String, Object>
            .findFirst()
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "No listing found with the given ID: " + listingId));

    // 6. Return the listing data
    return listing;
  }

  /**
   * This method gets all the listings for a user. This is a separate handler as it adds a listingId
   * field in each listing document. It is used to list all the listings for a user. It is used in
   * the ListListingsHandler.
   *
   * @param uid The user's ID
   * @return A map of listing data, including the listing id (Map<String, Object>)
   * @throws ExecutionException if the query execution fails
   * @throws InterruptedException if the query is interrupted
   */
  @Override
  public List<Map<String, Object>> getUserListings(String uid)
      throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();

    // 1. Get a reference to the user's document
    DocumentReference userRef = db.collection("users").document(uid);

    // 2. Get the user's listings collection
    CollectionReference listingsRef = userRef.collection("listings");

    // 3. Fetch all listings for the user
    ApiFuture<QuerySnapshot> future = listingsRef.get();

    // 4. Retrieve the query result (list of listings)
    QuerySnapshot querySnapshot = future.get();

    // 5. Create a list to store the listings
    List<Map<String, Object>> userListings = new ArrayList<>();

    // 6. Loop through each document in the query result and add it to the map
    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
      // 5. Create a map to store the listings (listingId -> listingData)
      Map<String, Object> listingData;
      listingData = doc.getData();
      assert listingData != null;
      listingData.put("listingId", doc.getId());
      // Add the listing to the list
      userListings.add(listingData);
    }
    return userListings;
  }

  // clears the collections inside of a specific user.
  @Override
  public void clearUser(String uid) throws IllegalArgumentException {
    if (uid == null) {
      throw new IllegalArgumentException("removeUser: uid cannot be null");
    }
    try {
      // removes all data for user 'uid'
      Firestore db = FirestoreClient.getFirestore();
      // 1: Get a ref to the user document
      DocumentReference userDoc = db.collection("users").document(uid);
      // 2: Delete the user document
      deleteDocument(userDoc);
    } catch (Exception e) {
      System.err.println("Error removing user : " + uid);
      System.err.println(e.getMessage());
    }
  }

  @Override
  public List<Map<String, Object>> getAllUsers() throws InterruptedException, ExecutionException {
    // gets all listings for all users
    Firestore db = FirestoreClient.getFirestore();
    // 1: Get a ref to the users collection
    CollectionReference usersRef = db.collection("users");
    // 2: Fetch all documents from the users collection
    ApiFuture<QuerySnapshot> future = usersRef.get();
    // 3: Retrieve the query results (documents)
    QuerySnapshot querySnapshot = future.get();
    // 4: Create a list to hold all users
    List<Map<String, Object>> allUsers = new ArrayList<>();

    // 5: Loop through each document in the query result and add it to the list
    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
      // Convert each document to a Map and add it to the list
      allUsers.add(document.getData());
    }
    // Return the list of users
    return allUsers;
  }

  //  /**
  //   * This method gets all the user IDs in the database. This method is unused.
  //   *
  //   * @return
  //   * @throws InterruptedException
  //   * @throws ExecutionException
  //   */
  //  @Override
  //  public List<String> getAllUsersIds() throws InterruptedException, ExecutionException {
  //    // gets all listings for all users
  //    Firestore db = FirestoreClient.getFirestore();
  //    // 1: Get a ref to the users collection
  //    CollectionReference usersRef = db.collection("users");
  //    // 2: Create a list to hold all user IDs
  //    List<String> allUserIds = new ArrayList<>();
  //    // 3: Loop through each document in the users collection and add the user ID to the list
  //    for (DocumentReference userDoc : usersRef.listDocuments()) {
  //      allUserIds.add(userDoc.getId());
  //    }
  //    // Return the list of user IDs
  //    return allUserIds;
  //  }

  /**
   * This method will get the all the documents of liked listings for all users. It is primarily
   * used to delete a listing from all users' liked listings if that listing no longer exists.
   *
   * @return a list of all the liked listings for all users
   */
  public List<Map<String, List<String>>> getAllUserFavoritesIds()
      throws ExecutionException, InterruptedException {
    // gets all users in our database
    Firestore db = FirestoreClient.getFirestore();
    // 1: Get a ref to the users collection
    CollectionReference usersRef = db.collection("users");
    // Create a list to store the liked listings to the user
    List<Map<String, List<String>>> allUsers = new ArrayList<>();

    // 2: Get all user documents (goes into the user subcollection)
    for (DocumentReference userDoc : usersRef.listDocuments()) {
      // 3: Reference the subcollection: liked_listing for each user
      CollectionReference likedListingRef =
          userDoc.collection("liked_listings"); // each user only has one document - user
      List<String> likedListingsIds =
          new ArrayList<>(); // store a list of liked listings for each user
      // 4: Get all liked_listing documents for each user
      for (DocumentReference nestedListingDoc : likedListingRef.listDocuments()) {
        // 5: Add the liked listing id to the list
        likedListingsIds.add(nestedListingDoc.getId());
      }
      Map<String, List<String>> userLikedListings = new HashMap<>();
      userLikedListings.put(userDoc.getId(), likedListingsIds);
      allUsers.add(userLikedListings);
    }
    return allUsers;
  }

  /**
   * This gets the account details for all users in the database.
   *
   * @return
   * @throws ExecutionException
   * @throws InterruptedException
   */
  public List<Map<String, Object>> getAllUserDataMaps()
      throws ExecutionException, InterruptedException {
    // gets all users in our database
    Firestore db = FirestoreClient.getFirestore();

    // 1: Get a ref to the users collection
    CollectionReference usersRef = db.collection("users");

    // Create a list to store all the markers
    List<Map<String, Object>> allUsers = new ArrayList<>();
    // 2: Get all user documents (goes into the user subcollection) - nested tier 2
    for (DocumentReference userDoc : usersRef.listDocuments()) {
      // 3: Get all user documents for each user
      CollectionReference nestedUserRef =
          userDoc.collection("users"); // each user only has one document - user
      // 4: Get all user documents queries for each user
      QuerySnapshot usersQuery = nestedUserRef.get().get();

      // 5: Get data from the user's document queries (e.g. uid, username, email)
      for (QueryDocumentSnapshot nestedUserDoc : usersQuery.getDocuments()) {
        // 6: Add the user data to the list
        Map<String, Object> userData = nestedUserDoc.getData();
        // with all 3 fields (uid, username, email)
        String userId = nestedUserDoc.getId();
        // add the marker id to the marker data
        allUsers.add(userData);
      }
    }
    return allUsers;
  }

  /**
   * This method gets all the listings for all users in the database.
   *
   * @return
   * @throws InterruptedException
   * @throws ExecutionException
   */
  @Override
  public List<Map<String, Object>> getAllUsersListings()
      throws InterruptedException, ExecutionException {
    // gets all listings for all users
    Firestore db = FirestoreClient.getFirestore();
    // 1: Get a ref to the users collection
    CollectionReference usersRef = db.collection("users");
    // Create a list to store all the listings
    List<Map<String, Object>> allListings = new ArrayList<>();

    // 2: Get all user documents
    for (DocumentReference userDoc : usersRef.listDocuments()) {
      // 3: Get all listing documents for each user
      CollectionReference listingsRef = userDoc.collection("listings");
      // 4: Get all listing documents queries for each user
      QuerySnapshot listingsQuery = listingsRef.get().get();

      // 5. Get the user's email to put in the listing data
      CollectionReference nestedUserRef = userDoc.collection("users");
      QuerySnapshot usersQuery = nestedUserRef.get().get();
      String userEmail = "";
      for (QueryDocumentSnapshot nestedUserDoc : usersQuery.getDocuments()) {
        Map<String, Object> userData = nestedUserDoc.getData();
        if (nestedUserDoc.getId().equals(userDoc.getId())) {
          userEmail = (String) userData.get("email");
        }
      }

      // 5: Get data from document queries
      for (QueryDocumentSnapshot listingsDoc : listingsQuery.getDocuments()) {
        // 6: Add the listing data to the list
        Map<String, Object> listingData = listingsDoc.getData();
        System.out.println(listingData);
        // Add the user ID to the listing data
        listingData.put("uid", userDoc.getId());
        System.out.print("user ID: " + userDoc.getId());
        // Add the listing ID to the listing data
        listingData.put("listingId", listingsDoc.getId());
        System.out.println(", listing ID: " + listingsDoc.getId());
        // Add the listing to the allListings list

        listingData.put("email", userEmail);
        System.out.print(", email: " + userEmail);

        System.out.println("listingData with user id and listing id?: " + listingData);
        allListings.add(listingData);
      }
    }
    return allListings;
  }

  /**
   * This method gets a document from a collection for a user.
   *
   * @param uid
   * @param collectionId
   * @param docId
   * @return
   * @throws IllegalArgumentException
   * @throws ExecutionException
   * @throws InterruptedException
   */
  @Override
  public Map<String, Object> getDocument(String uid, String collectionId, String docId)
      throws IllegalArgumentException, ExecutionException, InterruptedException {
    if (uid == null || collectionId == null || docId == null) {
      throw new IllegalArgumentException(
          "getDocument: uid, collectionId, and docId cannot be null");
    }

    Firestore db = FirestoreClient.getFirestore();

    System.out.println("Fetching document for UID: " + uid);
    System.out.println("Collection ID: " + collectionId);
    System.out.println("Document ID: " + docId);

    DocumentReference docRef =
        db.collection("users").document(uid).collection(collectionId).document(docId);

    DocumentSnapshot documentSnapshot = docRef.get().get();

    if (!documentSnapshot.exists()) {
      System.out.println("No document found with ID: " + docId);
      throw new IllegalArgumentException("No document found with ID: " + docId);
    }
    System.out.println("Document Data: " + documentSnapshot.getData());
    return documentSnapshot.getData();
  }

  /**
   * This method deletes a document from a collection for a user.
   *
   * @param doc
   */
  private void deleteDocument(DocumentReference doc) {
    // for each subcollection, run deleteCollection()
    Iterable<CollectionReference> collections = doc.listCollections();
    for (CollectionReference collection : collections) {
      deleteCollection(collection);
    }
    // then delete the document
    doc.delete();
  }

  // recursively removes all the documents and collections inside a collection
  // https://firebase.google.com/docs/firestore/manage-data/delete-data#collections
  private void deleteCollection(CollectionReference collection) {
    try {

      // get all documents in the collection
      ApiFuture<QuerySnapshot> future = collection.get();
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();

      // delete each document
      for (QueryDocumentSnapshot doc : documents) {
        doc.getReference().delete();
      }

      // NOTE: the query to documents may be arbitrarily large. A more robust
      // solution would involve batching the collection.get() call.
    } catch (Exception e) {
      System.err.println("Error deleting collection : " + e.getMessage());
    }
  }

  //  /**
  //   * This method returns a list of document IDs for a given collection for a user. Ex: The
  // listing
  //   * ids for a user's listings collection. This method is unused but can be used to get a list
  // of
  //   * document IDs for a collection.
  //   *
  //   * @param uid The user's ID
  //   * @param collection_id The collection ID
  //   * @return A list of document IDs
  //   * @throws InterruptedException
  //   * @throws ExecutionException
  //   */
  //  @Override
  //  public List<String> getCollectionDocumentIds(String uid, String collection_id)
  //      throws InterruptedException, ExecutionException {
  //    if (uid == null || collection_id == null) {
  //      throw new IllegalArgumentException(
  //          "getCollectionDocumentIds: uid and/or collection_id cannot be null");
  //    }
  //
  //    // gets all documents in the collection 'collection_id' for user 'uid'
  //
  //    Firestore db = FirestoreClient.getFirestore();
  //    // 1: Make the data payload to add to your collection
  //    CollectionReference dataRef =
  // db.collection("users").document(uid).collection(collection_id);
  //
  //    // 2: Get pin documents
  //    QuerySnapshot dataQuery = dataRef.get().get();
  //
  //    // 3: Get data from document queries
  //    List<String> data = new ArrayList<>();
  //    for (QueryDocumentSnapshot doc : dataQuery.getDocuments()) {
  //      //      System.out.print(doc);
  //      data.add(doc.getId());
  //    }
  //
  //    return data;
  //  }
}
