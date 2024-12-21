//package edu.brown.cs.student.main.server.handlers.listingHandlers;
//
//import com.google.cloud.storage.Storage;
//import edu.brown.cs.student.main.server.storage.GoogleCloudStorageUtilities;
//import edu.brown.cs.student.main.server.storage.StorageInterface;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import spark.Request;
//import spark.Response;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class MockAddListingHandlerTest {
//
//  @Mock private StorageInterface mockStorageHandler;
//  @Mock private GoogleCloudStorageUtilities mockGcsHandler;
//  @Mock private Storage mockStorage;
//  @Mock private Request mockRequest;
//  @Mock private Response mockResponse;
//
//  private AddListingHandler handler;
//
//  @BeforeEach
//  void setUp() {
//    MockitoAnnotations.openMocks(this);
//    handler = new AddListingHandler(mockStorageHandler, mockGcsHandler);
//  }
//
//  @Test
//  void testValidateTags_ValidInput() {
//    String validTags = "tag1,tag2,tag3,tag4,tag5";
//    assertDoesNotThrow(() -> AddListingHandler.validateTags(validTags));
//  }
//
//  @Test
//  void testValidateTags_InvalidInput() {
//    String invalidTags = "tag1,tag2,tag1";
//    Exception exception =
//      assertThrows(IllegalArgumentException.class, () -> AddListingHandler.validateTags(invalidTags));
//    assertEquals("Please make sure all tags are unique", exception.getMessage());
//  }
//
//  @Test
//  void testValidatePrice_ValidInput() {
//    String validPrice = "99.99";
//    assertDoesNotThrow(() -> AddListingHandler.validatePrice(validPrice));
//  }
//
//  @Test
//  void testValidatePrice_InvalidInput() {
//    String invalidPrice = "-10";
//    Exception exception =
//      assertThrows(IllegalArgumentException.class, () -> AddListingHandler.validatePrice(invalidPrice));
//    assertEquals("Price cannot be negative or larger than 999999999.", exception.getMessage());
//  }
//
//  @Test
//  void testUploadImageToGCS() throws Exception {
//    String base64Image = "someBase64EncodedString";
//    String imageName = "testImage.jpg";
//
//    when(mockGcsHandler.makeStorage()).thenReturn(mockStorage);
//    when(mockStorage.signUrl(any(), anyLong(), any())).thenReturn(new URL("http://fake-url.com"));
//
//    String imageUrl = handler.uploadImageToGCS(base64Image, imageName);
//    assertEquals("http://fake-url.com", imageUrl);
//  }
//
//  @Test
//  void testHandle_ValidInput() throws Exception {
//    when(mockRequest.body()).thenReturn("someBase64EncodedString");
//    when(mockRequest.queryParams("uid")).thenReturn("user123");
//    when(mockRequest.queryParams("username")).thenReturn("JohnDoe");
//    when(mockRequest.queryParams("price")).thenReturn("99.99");
//    when(mockRequest.queryParams("title")).thenReturn("Sample Listing");
//    when(mockRequest.queryParams("category")).thenReturn("electronics");
//    when(mockRequest.queryParams("tags")).thenReturn("tag1,tag2");
//    when(mockRequest.queryParams("condition")).thenReturn("new");
//    when(mockRequest.queryParams("description")).thenReturn("A great product!");
//
//    when(mockGcsHandler.makeStorage()).thenReturn(mockStorage);
//    when(mockStorage.signUrl(any(), anyLong(), any())).thenReturn(new URL("http://fake-url.com"));
//
//    Object response = handler.handle(mockRequest, mockResponse);
//    assertNotNull(response);
//    assertTrue(response.toString().contains("Sample Listing"));
//  }
//
//  @Test
//  void testHandle_InvalidInput() {
//    when(mockRequest.body()).thenReturn("");
//    when(mockRequest.queryParams("uid")).thenReturn("");
//    when(mockRequest.queryParams("price")).thenReturn("-10");
//
//    Exception exception =
//      assertThrows(IllegalArgumentException.class, () -> handler.handle(mockRequest, mockResponse));
//    assertTrue(exception.getMessage().contains("All listings arguments are required"));
//  }
//}
//
//
//import static org.junit.jupiter.api.Assertions.*;
//  import edu.brown.cs.student.main.server.handlers.listingHandlers.AddListingHandler;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class AddListingHandlerTest {
//
//  private AddListingHandler addListingHandler;
//
//  @BeforeEach
//  void setUp() {
//    addListingHandler = new AddListingHandler();
//  }
//
//  @Test
//  void testAddListingHandler() throws Exception {
//    // Prepare mock request data
//    Map<String, String> queryParams = new HashMap<>();
//    queryParams.put("listingName", "Test Listing");
//    queryParams.put("price", "100");
//
//    MockRequest mockRequest = new MockRequest(queryParams);
//    MockResponse mockResponse = new MockResponse();
//
//    // Call the handler
//    Object result = addListingHandler.handle(mockRequest, mockResponse);
//
//    // Assert the response
//    assertNotNull(result);
//    assertEquals("Success: Listing added", result); // Replace with actual expected value
//  }
//}
//
