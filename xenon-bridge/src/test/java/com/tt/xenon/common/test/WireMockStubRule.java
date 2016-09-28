package com.tt.xenon.common.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.gson.Gson;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by mageshwaranr on 8/22/2016.
 *
 *  Rule to read json resource and return it as a object
 *
 *  TODO : Accept JSON path and convert a sub-content to a Model
 */
public class WireMockStubRule implements TestRule {

  private final Gson gson = new Gson();
  private Object content;

  private final WireMockServer wireMock;

  public WireMockStubRule(WireMockServer wireMock) {
    this.wireMock = wireMock;
  }

  @SuppressWarnings("unchecked")
  public <T> T getValue() {
    return (T) content;
  }

  @Override
  public Statement apply(final Statement base, final Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        WireMockJsonResource jsonFileResource = description.getAnnotation(WireMockJsonResource.class);
        if (jsonFileResource != null) {
          Class<?> clazz = jsonFileResource.clazz();
          String resourceName = jsonFileResource.fileName();
          Class<?> testClass = description.getTestClass();
          InputStream in = testClass.getClassLoader().getResourceAsStream(resourceName);
          assert in != null : "Failed to load resource: " + resourceName + " from " + testClass;
          try (Reader reader = new BufferedReader(new InputStreamReader(in))) {
            content = gson.fromJson(reader, clazz);
            wireMock.addStubMapping(getValue());
          }
        }
        base.evaluate();
      }
    };
  }


}

