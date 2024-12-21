package edu.brown.cs.student.main.server.test;

import java.util.Map;
import spark.Request;
import spark.Response;

public class MockResponse extends Response {
  private String body;

  @Override
  public void body(String body) {
    this.body = body;
  }

  @Override
  public String body() {
    return body;
  }
}