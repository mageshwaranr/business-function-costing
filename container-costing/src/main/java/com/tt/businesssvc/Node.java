package com.tt.businesssvc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mageshwaranr on 9/29/2016.
 */
public class Node {

  private String label,id;
  private double cost;

  public Node(String id) {
    this.id = id;
  }

  public Node(String label, String id) {
    this.label = label;
    this.id = id;
  }

  public Node(String label, String id, double cost) {
    this.label = label;
    this.id = id;
    this.cost = cost;
  }

  private List<Node> childs = new ArrayList<>();

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public double getCost() {
    return cost;
  }

  public void setCost(double cost) {
    this.cost = cost;
  }

  public List<Node> getChilds() {
    return childs;
  }

  public void setChilds(List<Node> childs) {
    this.childs = childs;
  }
}
