package com.texturegarden.effects.ca;

/**
 * VNOperation Tim Tyler 2001
 */

public abstract class VNOperation {
  public int c, n, s, e, w;
  public void init() {}

  public void init(int i) {}

  public  void init(int i, int j) {}

  public  void init(int c, int n, int s, int e, int w) {}

  public  int process(int c, int n, int s, int e, int w) {
    return 0;
  }

  public int process() {
    return 0;
  }
}
