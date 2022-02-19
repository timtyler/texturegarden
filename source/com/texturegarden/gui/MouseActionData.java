package com.texturegarden.gui;

public class MouseActionData {
  public int x, y;
  public int modifiers;
  
  public MouseActionData(int x, int y, int modifiers) {
    this.x = x;
    this.y = y;
    this.modifiers = modifiers;
  }
}