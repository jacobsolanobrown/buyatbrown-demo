package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.handlers.AddListingHandler;
import edu.brown.cs.student.main.server.handlers.CreateUserHandler;
import edu.brown.cs.student.main.server.handlers.DeleteListingHandler;
import edu.brown.cs.student.main.server.handlers.FilterListingsHandler;
import edu.brown.cs.student.main.server.handlers.LikeListingHandler;
import edu.brown.cs.student.main.server.handlers.ListAllUserListingsHandler;
import edu.brown.cs.student.main.server.handlers.ListListingsHandler;
import edu.brown.cs.student.main.server.handlers.UpdateListingHandler;
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

    after(
        (Filter)
            (request, response) -> {
              response.header("Access-Control-Allow-Origin", "*");
              response.header("Access-Control-Allow-Methods", "*");
            });

    StorageInterface firebaseUtils;
    try {
      //      firebaseUtils = new MockedFirebaseUtilities();
      firebaseUtils = new FirebaseUtilities();
      // JSONParser myDataSource = new JSONParser("server/data/geojson/fullDownload.geojson");
      //      GeoMapCollection geoMapCollection = myDataSource.getData();
      Spark.get("add-listings", new AddListingHandler(firebaseUtils));
      Spark.get("filter-listings", new FilterListingsHandler(firebaseUtils));
      Spark.get("list-listings", new ListListingsHandler(firebaseUtils));
      Spark.get("delete-listings", new DeleteListingHandler(firebaseUtils));
      Spark.get("create-user", new CreateUserHandler(firebaseUtils));
      Spark.get("update-listings", new UpdateListingHandler(firebaseUtils));
      Spark.get("list-all-listings", new ListAllUserListingsHandler(firebaseUtils));
      Spark.get("like-listings", new LikeListingHandler(firebaseUtils));

      Spark.notFound(
          (request, response) -> {
            response.status(404); // Not Found
            System.out.println("ERROR");
            return "404 Not Found - The requested endpoint does not exist.";
          });
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
