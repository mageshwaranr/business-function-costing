package com.tt.businesssvc.zipkin;

import java.util.List;

/**
 * Created by mageshwaranr on 9/29/2016.
 */
public class Trace {

  private String traceId, name, parentId, id;

  private int duration;

  private List<Annotation> annotations;

  private List<Annotation> binaryAnnotations;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTraceId() {
    return traceId;
  }

  public void setTraceId(String traceId) {
    this.traceId = traceId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public void setAnnotations(List<Annotation> annotations) {
    this.annotations = annotations;
  }

  public List<Annotation> getBinaryAnnotations() {
    return binaryAnnotations;
  }

  public void setBinaryAnnotations(List<Annotation> binaryAnnotations) {
    this.binaryAnnotations = binaryAnnotations;
  }

  @Override
  public String toString() {
    return "Trace{" +
        "traceId='" + traceId + '\'' +
        ", name='" + name + '\'' +
        ", parentId='" + parentId + '\'' +
        ", id='" + id + '\'' +
        ", duration=" + duration +
        ", annotations=" + annotations +
        ", binaryAnnotations=" + binaryAnnotations +
        '}';
  }
}
