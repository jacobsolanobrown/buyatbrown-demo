package edu.brown.cs.student.main.server.test;

import java.util.Map;
import spark.Request;
import spark.Response;

public class MockRequest extends Request {
  private final Map<String, String> queryParams;
  private final String body;

  public MockRequest(Map<String, String> queryParams) {
    this.queryParams = queryParams;
    this.body = null; // Default body is null
  }

  public MockRequest(Map<String, String> queryParams, String body) {
    this.queryParams = queryParams;
    this.body = body; // Set the body for testing
  }

  @Override
  public String body() {
    return body;
  }

  @Override
  public String queryParams(String key) {
    return queryParams.get(key);
  }
}