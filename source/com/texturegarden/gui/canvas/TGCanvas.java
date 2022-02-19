package com.texturegarden.gui.canvas;

import java.awt.Graphics;

import com.texturegarden.TG;

public class TGCanvas extends TTCanvas {
  public TGCanvas(TG applet) {
    super(applet);
  }

  final public void update(Graphics g) {
    applet.render.update(g);
  }

  final public void paint(Graphics g) {
    applet.render.updateAll(g);
  }
}
