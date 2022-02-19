package com.texturegarden.gui.canvas;

import java.awt.Graphics;

import com.texturegarden.TG;

public class TVCanvas extends TTCanvas {
  public TVCanvas(TG applet) {
    super(applet);
  }

  final public void update(Graphics g) {
    applet.texture_array.redrawTiled(g);
  }

  final public void paint(Graphics g) {
    dim = getSize();

    if ((dim.width != x_pixels) || (dim.height != y_pixels)) {
      x_pixels = dim.width;
      y_pixels = dim.height;
    }

    update(g);
  }
}
