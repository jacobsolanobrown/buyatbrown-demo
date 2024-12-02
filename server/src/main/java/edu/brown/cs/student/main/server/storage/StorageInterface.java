package edu.brown.cs.student.main.server.storage;

import edu.brown.cs.student.main.server.parserParameterizedTypes.ListingsCollection.Listing;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface StorageInterface {

  void addDocument(String uid, String collection_id, String doc_id, Map<String, Object> data);

  List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException;

  void removeDocument(String uid, String collection_id, String doc_id);

  Map<String, Object> getListingForUser(String uid,
      String listingId) throws InterruptedException, ExecutionException;

  void clearUser(String uid) throws InterruptedException, ExecutionException;

  List<Map<String, Object>> getAllUsers()
      throws InterruptedException, ExecutionException;

  List<Map<String, Object>> getAllUsersListings() throws InterruptedException, ExecutionException;
}
