package edu.brown.cs.student.main.server.parserParameterizedTypes;

import edu.brown.cs.student.main.server.parserParameterizedTypes.ListingsCollection.ListingsCollection;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JSONParser {
  private ListingsCollection data;

  public JSONParser(String filePath) throws FileNotFoundException {
    try {
      // ***************** READING THE FILE *****************
      FileReader jsonReader = new FileReader(filePath);
      BufferedReader br = new BufferedReader(jsonReader);
      String fileString = "";
      String line = br.readLine();
      while (line != null) {
        fileString = fileString + line;
        line = br.readLine();
      }
      jsonReader.close();

      // ****************** CREATING THE ADAPTER ***********
      ListingAdapter myadapter = new ListingAdapter();
      this.data = myadapter.fromJson(fileString);

    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  public ListingsCollection getData() {
    return this.data;
  }
}
