package edu.brown.cs.student.main.server.storage;

import com.google.cloud.storage.Storage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface GoogleCloudStorageInterface {
  Storage makeStorage() throws IOException;
  String uploadImageToGCS(String base64Image, String imageName) throws IOException;
}

