package com.texturegarden.messages;

import com.texturegarden.gui.TextureContainer;

/**
 * MsgTextureSelection - Tim Tyler 2001 - 2003
 */

public class MsgTextureSelection {
  TextureContainer tc;
  // public int n;
  // public int y;
  public int and;
  public int xor;

 public MsgTextureSelection(TextureContainer tc, int and, int xor) {

    // n = i;
    // y = j;
    this.tc = tc;
    this.and = and;
    this.xor = xor;
  }
}