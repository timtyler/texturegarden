package com.texturegarden.effects.ca;
/**
* MooreOperation Tim Tyler 2001
*/

public abstract class MooreOperation {
  public int c;
  public int n;
  public int s;
  public int e;
  public  int w;
  public int ne;
  public int nw;
  public int se;
  public int sw;

  public void init() {}

  public void init(int i) {}

  public int process() {
    return 0;
  }

  public void init(int c, int n, int s, int e, int w, int ne, int nw, int se, int sw) {}

  public int process(int c, int n, int s, int e, int w, int ne, int nw, int se, int sw) {
    return 0;
  }
}
