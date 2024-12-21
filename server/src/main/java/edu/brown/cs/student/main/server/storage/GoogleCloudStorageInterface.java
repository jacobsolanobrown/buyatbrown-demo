package edu.brown.cs.student.main.server.storage;

import com.google.cloud.storage.Storage;
import java.io.IOException;

public interface GoogleCloudStorageInterface {
  Storage makeStorage() throws IOException;

  String uploadImageToGCS(String base64Image, String imageName) throws IOException;
}
