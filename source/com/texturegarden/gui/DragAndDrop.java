package com.texturegarden.gui;

import java.awt.Graphics;

import com.texturegarden.IdleMessage;
import com.texturegarden.TG;
import com.texturegarden.gui.*;
import com.texturegarden.messages.*;

public class DragAndDrop {
  public boolean dragging = false;
  boolean drag_ending = false;

  int xo = 0;
  int yo = 0;
  TextureContainer tc;

  int current_x = 0;
  int current_y = 0;

  int min_x = 0;
  int min_y = 0;
  int max_x = 0;
  int max_y = 0;

  final static int border_width = 1;
  static int ds_width = 8;
  static int ds_height = 8;
  static int ds_max = 16;
  static int ds_initial = 1;

  /**
   * DragAndDrop start
   */

  TG applet;
  public DragAndDrop(TG applet) {
    this.applet = applet;
  }

  public void start(int _x, int _y, TextureContainer _tc) {
    xo = _x - _tc.x;
    yo = _y - _tc.y;
    current_x = _x;
    current_y = _y;
    tc = _tc;
    dragging = true;
    drag_ending = false;
    ds_width = ds_initial;
    ds_height = ds_initial;

    applet.pointer.cursorMove();
  }

  public void end(int _x, int _y) {
    drag_ending = true;
    // Log.log("Drag ended");

    TextureContainer _tc = applet.texture_array.findTextureContainer(_x, _y);

    if (tc != _tc) { // only if it has moved...
      if (null != _tc) { // only if it exists...
        if ((_tc.selection_type & TextureContainer.IMMUNE_MASK) == 0) {
          tc.applet.idle_message.send(IdleMessage.IMSG_CLONE_TEXTURE, new MsgCloneTexture(tc, _tc));
        }
      }
    }

    applet.pointer.cursorDefault();
  }

  /**
   * DragAndDrop end
   */
  public void update(Graphics g) {
    if (dragging) {
      boolean bother = (drag_ending || (ds_width < ds_max) || (min_x != current_x - xo) || (min_y != current_y - yo));
      if (bother) {
        removeDS(g);
        remove(g);
      }

      if (drag_ending) {
        dragging = false;
      } else {

        if (ds_width < ds_max) {
          ds_width++;
          ds_height++;
        }

        min_x = current_x - xo;
        min_y = current_y - yo;
        max_x = min_x + tc.width + (tc.total_margin << 1) + ds_width;
        max_y = min_y + tc.height + (tc.total_margin << 1) + ds_height;

        int temp_x = tc.x;
        int temp_y = tc.y;
        tc.x = min_x;
        tc.y = min_y;
        tc.update_texture = true;
        tc.update_selection = true;
        tc.is_on_top = true;
        tc.update(g);
        tc.is_on_top = false;
        tc.x = temp_x;
        tc.y = temp_y;

        if (bother) {
          // drop shadow...
          // H
          renderDropShadow(g, min_x + ds_width, max_y - ds_height, tc.width + (tc.total_margin << 1) - ds_width, ds_height);
          // V
          renderDropShadow(g, max_x - ds_width, min_y + ds_height, ds_width, tc.height + (tc.total_margin << 1));
        }
      }
    }
  }

  public void remove(Graphics g) {
    dragging = false;
    g.setClip(min_x, min_y, max_x - min_x, max_y - min_y);

    applet.texture_array.clearBackground(g);

    applet.texture_array.markAllTexturesForUpdate();
    applet.texture_array.markAllSelectionsForUpdate();
    applet.texture_array.redrawGrid(g);

    applet.render.noClipRect(g);
    dragging = true;
  }

  public void removeDS(Graphics g) {
    dragging = false;
    // H
    g.setClip(min_x + ds_width, max_y, tc.width + (tc.total_margin << 1) - ds_width, ds_height);

    applet.texture_array.clearBackground(g);

    applet.texture_array.markAllTexturesForUpdate();
    applet.texture_array.markAllSelectionsForUpdate();
    applet.texture_array.redrawGrid(g);

    // V
    g.setClip(max_x, min_y + ds_height, ds_width, tc.height + (tc.total_margin << 1));

    applet.texture_array.clearBackground(g);

    applet.texture_array.markAllTexturesForUpdate();
    applet.texture_array.markAllSelectionsForUpdate();
    applet.texture_array.redrawGrid(g);

    applet.render.noClipRect(g);
    dragging = true;
  }

  void renderDropShadow(Graphics g, int _x, int _y, int _w, int _h) {
    g.clearRect(_x, _y, _w, _h);
  }

  void updateCoordinates(int _x, int _y) {
    current_x = _x;
    current_y = _y;
  }
}
