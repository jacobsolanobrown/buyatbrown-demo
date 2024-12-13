package edu.brown.cs.student.main.server;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.options;

import edu.brown.cs.student.main.server.handlers.filterListingsHandlers.FilterListingsHandler;
import edu.brown.cs.student.main.server.handlers.filterListingsHandlers.LikeListingHandler;
import edu.brown.cs.student.main.server.handlers.listingHandlers.AddListingHandler;
import edu.brown.cs.student.main.server.handlers.listingHandlers.DeleteListingHandler;
import edu.brown.cs.student.main.server.handlers.listingHandlers.ListAllUserListingsHandler;
import edu.brown.cs.student.main.server.handlers.listingHandlers.ListListingsHandler;
import edu.brown.cs.student.main.server.handlers.listingHandlers.UpdateListingHandler;
import edu.brown.cs.student.main.server.handlers.userAccountHandlers.CreateUserHandler;
import edu.brown.cs.student.main.server.handlers.userAccountHandlers.QueryUsernameHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.io.FileNotFoundException;
import java.io.IOException;
import spark.Filter;
import spark.Spark;

/** Top Level class for our project, utilizes spark to create and maintain our server. */
public class Server {

  public static void setUpServer() {
    int port = 3232;
    Spark.port(port);

    // Enable CORS for all routes
    options(
      "/*",
      (request, response) -> {
        response.header("Access-Control-Allow-Origin", "http://localhost:8000"); // Frontend origin
        response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.header("Access-Control-Allow-Credentials", "true");
        return "OK";
      });
    after(
      (Filter)
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "http://localhost:8000"); // Frontend origin
          response.header("Access-Control-Allow-Credentials", "true");
          response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
          response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

    StorageInterface firebaseUtils;
    try {
      // Initialize Firebase
      firebaseUtils = new FirebaseUtilities();

      // Define routes
      System.out.println("Server's routes are being defined...");
      Spark.post("add-listings", new AddListingHandler(firebaseUtils));
      Spark.get("filter-listings", new FilterListingsHandler(firebaseUtils));
      Spark.get("list-listings", new ListListingsHandler(firebaseUtils));
      Spark.get("delete-listings", new DeleteListingHandler(firebaseUtils));
      Spark.get("create-user", new CreateUserHandler(firebaseUtils));
      Spark.get("query-username", new QueryUsernameHandler(firebaseUtils));
      Spark.get("update-listings", new UpdateListingHandler(firebaseUtils));
      Spark.get("list-all-listings", new ListAllUserListingsHandler(firebaseUtils));
      Spark.get("like-listings", new LikeListingHandler(firebaseUtils));

      // Handle undefined routes
      Spark.notFound(
        (request, response) -> {
          response.status(404); // Not Found
          System.out.println("ERROR");
          return "404 Not Found - The requested endpoint does not exist.";
        });

      // Start server
      Spark.init();
      Spark.awaitInitialization();
      System.out.println("Server started at http://localhost:" + port);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println(
        "Error: Could not initialize Firebase. Likely due to firebase_config.json not being found. Exiting.");
      System.exit(1);
    }
  }

  /**
   * Runs Server.
   *
   * @param args none
   */
  public static void main(String[] args) throws FileNotFoundException {
    setUpServer();
  }
}
