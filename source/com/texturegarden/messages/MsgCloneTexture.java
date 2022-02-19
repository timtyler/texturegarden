package com.texturegarden.messages;

import com.texturegarden.gui.TextureContainer;

/**
 * MsgCloneTexture - Tim Tyler 2001 - 2001.
 */

public class MsgCloneTexture {
  public TextureContainer from;
  public TextureContainer to;

  public  MsgCloneTexture(TextureContainer from, TextureContainer to) {
    this.from = from;
    this.to = to;
  }
}
