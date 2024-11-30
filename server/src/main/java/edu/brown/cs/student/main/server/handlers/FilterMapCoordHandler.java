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

public class FilterMapCoordHandler implements Route {

  private final GeoMapCollection geoMapCollection;

  public FilterMapCoordHandler(GeoMapCollection geoMapCollection) {
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
      //      System.out.println("null parameter");
      // Get the location that the request is for
      String minlat = request.queryParams("minlat");
      String maxlat = request.queryParams("maxlat");
      String minlon = request.queryParams("minlon");
      String maxlon = request.queryParams("maxlon");

      System.out.println("GeoMapFilterHandler Received Request");

      if (minlat.isBlank() || maxlat.isBlank() || minlon.isBlank() || maxlon.isBlank()) {
        System.out.println("null parameter");
        // Bad request! Send an error response.
        responseMap.put("query_minlat", minlat);
        responseMap.put("query_maxlat", maxlat);
        responseMap.put("query_minlon", minlon);
        responseMap.put("query_maxlon", maxlon);
        responseMap.put("response_type", "failure");
        responseMap.put(
            "error_type",
            (minlat == null || maxlat == null)
                ? "missing latitude bound for bounding box filter"
                : "missing longitude bound for bounding box filter");
        return Utils.toMoshiJson(responseMap);
      }

      try {
        double minLat_double = Double.parseDouble(minlat);
        double maxLat_double = Double.parseDouble(maxlat);
        double minLon_double = Double.parseDouble(minlon);
        double maxLon_double = Double.parseDouble(maxlon);

        if (minLat_double >= maxLat_double || minLon_double >= maxLon_double) {
          // Bad request! Send an error response.
          responseMap.put("query_minlat", minlat);
          responseMap.put("query_maxlat", maxlat);
          responseMap.put("query_minlon", minlon);
          responseMap.put("query_maxlon", maxlon);
          responseMap.put("response_type", "failure");
          responseMap.put(
              "error_type",
              Double.parseDouble(minlat) >= Double.parseDouble(maxlat)
                  ? "minimum latitude should be smaller than maximum latitude"
                  : "minimum longitude should be smaller than maximum longitude");
          return Utils.toMoshiJson(responseMap);
        }

      } catch (NumberFormatException nfe) {
        responseMap.put("query_minlat", minlat);
        responseMap.put("query_maxlat", maxlat);
        responseMap.put("query_minlon", minlon);
        responseMap.put("query_maxlon", maxlon);
        responseMap.put("response_type", "failure");
        responseMap.put("error_type", "bounding box bounds should be numbers");
        return Utils.toMoshiJson(responseMap);
      }

      // Temporary list to hold features to remove
      List<GeoMap> filteredFeatures = new ArrayList<>();

      // filter each feature
      for (GeoMap feature : this.geoMapCollection.features) {
        boolean shouldAdd = true;

        if (feature.geometry == null) {
          System.out.println("feature had null coordinates: " + feature);
        } else {
          // check if each coordinate is in the bounds
          for (List<Double> coordinates : feature.geometry.coordinates.get(0).get(0)) {
            Double lat = coordinates.get(0);
            Double lon = coordinates.get(1);

            if (lat > Double.parseDouble(maxlat)
                || lat < Double.parseDouble(minlat)
                || lon > Double.parseDouble(maxlon)
                || lon < Double.parseDouble(minlon)) {
              shouldAdd = false;
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
        responseMap.put("error_type", "no features found matching search");
        System.out.println(responseMap);
      } else {
        System.out.println(this.geoMapCollection.features.size());
        GeoMapCollection filteredGeoMap = new GeoMapCollection();
        filteredGeoMap.type = this.geoMapCollection.type;
        filteredGeoMap.features = filteredFeatures;
        System.out.println("Number of features after filtering: " + filteredGeoMap.features.size());
        responseMap.put("response_type", "success");
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
