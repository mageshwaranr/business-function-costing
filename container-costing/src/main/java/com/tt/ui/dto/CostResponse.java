package com.tt.ui.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class CostResponse {

  private String name;

  // this refers to cost
  private double size;

  private List<CostResponse> children = new ArrayList<>();

  public CostResponse() {
  }

  public CostResponse(String name, double size) {
    this.name = name;
    this.size = size;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getSize() {
    return size;
  }

  public void setSize(double size) {
    this.size = size;
  }

  public List<CostResponse> getChildren() {
    return children;
  }

  public void setChildren(List<CostResponse> children) {
    this.children = children;
  }

  public void addChild(CostResponse childCost){
    this.children.add(childCost);
  }
}
