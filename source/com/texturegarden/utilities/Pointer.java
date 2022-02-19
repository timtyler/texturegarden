package com.texturegarden.utilities;

import java.awt.Cursor;

import com.texturegarden.TG;

public class Pointer {
  TG applet;
  public Pointer(TG applet) {
    this.applet = applet;
  }

  final public void cursorCross() {
    applet.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
  }

  final public void cursorHand() {
    applet.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  final public void cursorDefault() {
    applet.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); // ?!?!??!?
  }

  final public void cursorMove() {
    applet.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)); // ?!?!??!?
  }
}
