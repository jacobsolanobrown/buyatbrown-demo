package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.parserParameterizedTypes.GeoMapCollection.GeoMap.GeoMap;
import edu.brown.cs.student.main.server.parserParameterizedTypes.GeoMapCollection.GeoMapCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class FilterMapKeywordHandler implements Route {

  private final GeoMapCollection geoMapCollection;

  public FilterMapKeywordHandler(GeoMapCollection geoMapCollection) {
    this.geoMapCollection = geoMapCollection;
  }

  /**
   * Invoked when a request is made on this route's corresponding path e.g. '/hello'
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return The content to be set in the response
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();

    try {
      System.out.println("area_name");
      String keyword = request.queryParams("keyword");
      System.out.println("FilterMapKeywordHandler Received Request");

      if (keyword.isBlank()) {
        System.out.println("null parameter");
        // Bad request! Send an error response.
        responseMap.put("query_keyword", keyword);
        responseMap.put("response_type", "failure");
        responseMap.put("error_type", "search requires nonempty keyword field");
        return Utils.toMoshiJson(responseMap);
      }

      // Temporary list to hold features to remove
      List<GeoMap> filteredFeatures = new ArrayList<>();

      // filter each feature
      for (GeoMap feature : this.geoMapCollection.features) {
        boolean shouldAdd = false;

        if (feature.properties == null) {
          System.out.println("feature had null properties: " + feature);
        } else {
          // check if each coordinate is in the bounds
          for (String description : feature.properties.area_description_data.values()) {
            if (description.toLowerCase().contains(keyword.toLowerCase())) {
              shouldAdd = true;
              break;
            }
          }

          if (shouldAdd) {
            filteredFeatures.add(feature);
          }
        }
      }

      if (filteredFeatures.isEmpty()) {
        responseMap.put("response_type", "failure");
        responseMap.put("query_keyword", keyword);
        responseMap.put("error_type", "no features found matching search");
        System.out.println(responseMap);
      } else {
        // Remove the collected features
        System.out.println(this.geoMapCollection.features.size());
        GeoMapCollection filteredGeoMap = new GeoMapCollection();
        filteredGeoMap.type = this.geoMapCollection.type;
        filteredGeoMap.features = filteredFeatures;
        System.out.println("Number of features after filtering: " + filteredGeoMap.features.size());
        responseMap.put("response_type", "success");
        responseMap.put("query_keyword", keyword);
        responseMap.put("geodata", filteredGeoMap);
        System.out.println(responseMap);
      }
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error_type", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
