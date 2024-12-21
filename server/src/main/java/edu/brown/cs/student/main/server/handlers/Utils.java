package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.Map;

public class Utils {

  public static String toMoshiJson(Map<String, Object> map) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    return adapter.toJson(map);
  }

  public static Map<String, Object> fromMoshiJson(String json) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, String.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    try {
      return adapter.fromJson(json);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to parse JSON to Map: " + e.getMessage(), e);
    }
  }
}
