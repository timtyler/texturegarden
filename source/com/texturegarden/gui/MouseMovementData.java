package com.texturegarden.gui;

public class MouseMovementData {
  public int x, y, old_x, old_y;
  public int modifiers;

  public MouseMovementData(int x, int y, int old_x, int old_y, int modifiers) {
    this.x = x;
    this.y = y;
    this.old_x = old_x;
    this.old_y = old_y;
    this.modifiers = modifiers;
  }
}
