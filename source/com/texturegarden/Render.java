package com.texturegarden;

import java.awt.Graphics;

import com.texturegarden.image.*;
import com.texturegarden.utilities.*;

public class Render {
  static int w;
  static int h;

  static JUR rnd = new JUR();
  static ImageWrapper texture_image;

  TG applet;
  Render(TG applet) {
    this.applet = applet;
  }

  // main method
  public void updateAll(Graphics g) {
    applet.texture_array.markAllSelectionsForUpdate();
    applet.texture_array.markAllTexturesForUpdate();
    applet.texture_array.update(g);
  }

  public void update(Graphics g) {
    applet.texture_array.update(g);
  }

  public void noClipRect(Graphics g) {
    g.setClip(0, 0, 19999, 19999);
  }
}
