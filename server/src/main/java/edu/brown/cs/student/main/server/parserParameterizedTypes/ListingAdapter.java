package edu.brown.cs.student.main.server.parserParameterizedTypes;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.parserParameterizedTypes.ListingsCollection.Listing;
import edu.brown.cs.student.main.server.parserParameterizedTypes.ListingsCollection.ListingsCollection;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ListingAdapter {

  private final Moshi moshi = new Moshi.Builder().build();

  Type listingtype = Types.newParameterizedType(String.class, String.class, String.class, Double.class, String.class);
  Type type = Types.newParameterizedType(ListingsCollection.class, String.class, String.class, String.class, Double.class, String.class);

  @ToJson
  public String toJson(ListingsCollection map) {
    JsonAdapter<ListingsCollection> adapter = moshi.adapter(type);
    return adapter.toJson(map);
  }

  @FromJson
  public ListingsCollection fromJson(String map) throws IOException {
    JsonAdapter<ListingsCollection> adapter = moshi.adapter(type);
    return adapter.fromJson(map);
  }
}
