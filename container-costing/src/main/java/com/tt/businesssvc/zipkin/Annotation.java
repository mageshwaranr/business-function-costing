package com.tt.businesssvc.zipkin;

/**
 * Created by mageshwaranr on 9/29/2016.
 */
public class Annotation {

  private long timestamp;

  private String key, value;

  private Endpoint endpoint;

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Endpoint getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(Endpoint endpoint) {
    this.endpoint = endpoint;
  }

  public static class Endpoint {
    private String name, ipv4;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getIpv4() {
      return ipv4;
    }

    public void setIpv4(String ipv4) {
      this.ipv4 = ipv4;
    }
  }

  @Override
  public String toString() {
    return "Annotation{" +
        "timestamp=" + timestamp +
        ", key='" + key + '\'' +
        ", value='" + value + '\'' +
        ", endpoint=" + endpoint +
        '}';
  }
}
