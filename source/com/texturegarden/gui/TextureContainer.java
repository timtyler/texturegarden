/*
 * TextureContainer - Tim Tyler 2000-2001
 */
package com.texturegarden.gui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.texturegarden.TG;
import com.texturegarden.Texture;
import com.texturegarden.utilities.ResourceLoader;

public class TextureContainer {
  public Texture texture;

  public boolean update_texture;
  public boolean update_selection;
  public boolean is_on_top = false;
  public int x;
  public int y;
  public int width;
  public int height;
  public int selection_type;

  final static int outer_margin = 1; // black
  final static int sel1_margin = 2; // Yellow
  final static int sel2_margin = 3; // red
  final static int sel3_margin = 2; // Yellow
  final static int inner_margin = 1; // black
  final static int sel1_margin_s = outer_margin;
  final static int sel2_margin_s = sel1_margin_s + sel1_margin;
  final static int sel3_margin_s = sel2_margin_s + sel2_margin;
  final static int total_margin = inner_margin + outer_margin + sel1_margin + sel2_margin + sel3_margin;

  public final static int SELECTED_MASK = 1;
  public final static int IMMUNE_MASK = 2;
  public final static int MOTHER_MASK = 4;
  public final static int OFFSPRING_MASK = 8;
  public final static int GROWING_MASK = 16;

  int tile_x = 1;
  int tile_y = 1;

  float plot_scale_factor_x = 1.0f;
  float plot_scale_factor_y = 1.0f;

  public TG applet;

  public TextureContainer(TG applet, int x, int y, int w, int h) {
    this.applet = applet;
    this.x = x;
    this.y = y;
    this.width = w;
    this.height = h;

    texture = new Texture(applet, (int) (width * plot_scale_factor_x), (int) (height * plot_scale_factor_y));
    update_texture = true;
    update_selection = true;
  }

  void updateTiled(Graphics g) {
    // get dimensions....
    // tile_x tile_y
    int min_x = 0;
    int min_y = 0;

    int nx = 1 + applet.tv_canvas.x_pixels / width;
    int ny = 1 + applet.tv_canvas.y_pixels / height;

    // Log.log("nx:" + nx);

    if (texture.texture_image != null) {
      for (int i = 0; i < nx; i++) {
        for (int j = 0; j < ny; j++) {
          g.drawImage(texture.texture_image.i, min_x + (i * width), min_y + (j * height), width, height, null);
        }
      }
    }

    update_texture = false; // bit of a hack...
  }

  void update(Graphics g) {
    DragAndDrop dnd = applet.drag_and_drop;

    if ((update_texture) || (update_selection)) {
      if (is_on_top) {
        updateRect(g);
      } else { // divide into rectangles if necessary...
        if (dnd.dragging) {
          boolean updated = false;
          if (dnd.min_x < (x + width + (total_margin << 1))) {
            if (dnd.max_x > x) {
              if (dnd.min_y < (y + height + (total_margin << 1))) {
                if (dnd.max_y > y) {
                  boolean temp_update_texture = update_texture;
                  boolean temp_update_selection = update_selection;

                  int y1 = y;
                  int y2 = (dnd.min_y > y) ? dnd.min_y : y;
                  int y3 = (dnd.max_y < y + height + (total_margin << 1)) ? dnd.max_y : y + height + (total_margin << 1);
                  int y4 = y + height + (total_margin << 1);

                  if (dnd.min_y > y) {
                    g.setClip(x, y1, width + (total_margin << 1), y2 - y1);
                    updateRect(g);
                    update_texture = temp_update_texture;
                    update_selection = temp_update_selection;
                    applet.render.noClipRect(g);
                  }

                  if (dnd.max_y < y4) {
                    g.setClip(x, y3, width + (total_margin << 1), y4 - y3);
                    updateRect(g);
                    update_texture = temp_update_texture;
                    update_selection = temp_update_selection;
                    applet.render.noClipRect(g);
                  }

                  // wastefull...
                  if (dnd.min_x > x) {
                    g.setClip(x, y2, dnd.min_x - x, y3 - y2);
                    updateRect(g);
                    update_texture = temp_update_texture;
                    update_selection = temp_update_selection;
                    applet.render.noClipRect(g);
                  }

                  if (dnd.max_x < x + width + (total_margin << 1)) {
                    g.setClip(dnd.max_x, y2, x + width + (total_margin << 1) - dnd.max_x, y3 - y2);
                    updateRect(g);
                    //update_texture   = temp_update_texture;
                    //update_selection = temp_update_selection; // last time so not needed ;-)
                    applet.render.noClipRect(g);
                  }

                  // rectangles...
                  updated = true;
                }
              }
            }
          }

          if (!updated) {
            updateRect(g);
          }
          //}
        } else {
          updateRect(g);
        }
      }
    }
  }

  void updateRect(Graphics g) {
    if (update_texture) {
      if (texture.texture_image != null) {
        for (int i = 0; i < tile_x; i++) {
          for (int j = 0; j < tile_y; j++) {
            g.drawImage(texture.texture_image.i, x + total_margin + ((i * width) / tile_x), y + total_margin + ((j * height) / tile_y), width / tile_x, height / tile_y, null);
            //g.draw
            //Graphics2D g2d = (Graphics2D) g;
            //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            //g2d.setColor(Color.red);
            //g2d.drawOval(x + total_margin + ((i * width) / tile_x), y + total_margin + ((j * height) / tile_y),width / tile_x, height / tile_y);
          }
        }
      }

      update_texture = false; // ?
    }

    if (update_selection) {
      int w_max = width + (total_margin << 1);
      int h_max = height + (total_margin << 1);

      Color outside_colour;
      Color inside_colour;

      outside_colour = ((selection_type & IMMUNE_MASK) == 0) ? Color.darkGray : Color.darkGray;
      inside_colour = ((selection_type & IMMUNE_MASK) == 0) ? Color.gray : Color.lightGray;

      if ((selection_type & SELECTED_MASK) != 0) {
        outside_colour = Color.yellow;
        inside_colour = ((selection_type & IMMUNE_MASK) == 0) ? Color.blue : Color.lightGray;
      }

      if ((selection_type & MOTHER_MASK) != 0) {
        inside_colour = ((selection_type & IMMUNE_MASK) == 0) ? Color.red : Color.magenta;
      }

      if ((selection_type & OFFSPRING_MASK) != 0) {
        inside_colour = Color.black;
        outside_colour = Color.lightGray;
      }

      if ((selection_type & GROWING_MASK) != 0) {
        inside_colour = Color.red;
        outside_colour = Color.darkGray;
      }

      g.setColor(outside_colour);
      g.fillRect(x + sel1_margin_s, y + sel1_margin_s, w_max - (sel1_margin_s << 1), sel1_margin);
      g.fillRect(x + sel1_margin_s, y + sel1_margin_s, sel1_margin, h_max - (sel1_margin_s << 1));
      g.fillRect(x + w_max - sel1_margin_s - sel1_margin, y + sel1_margin_s, sel1_margin, h_max - (sel1_margin_s << 1));
      g.fillRect(x + sel1_margin_s, y + h_max - sel1_margin_s - sel1_margin, w_max - (sel1_margin_s << 1), sel1_margin);

      g.setColor(outside_colour);
      g.fillRect(x + sel3_margin_s, y + sel3_margin_s, w_max - (sel3_margin_s << 1), sel3_margin);
      g.fillRect(x + sel3_margin_s, y + sel3_margin_s, sel3_margin, h_max - (sel3_margin_s << 1));
      g.fillRect(x + w_max - sel3_margin_s - sel3_margin, y + sel3_margin_s, sel3_margin, h_max - (sel3_margin_s << 1));
      g.fillRect(x + sel3_margin_s, y + h_max - sel3_margin_s - sel3_margin, w_max - (sel3_margin_s << 1), sel3_margin);

      g.setColor(inside_colour);
      g.fillRect(x + sel2_margin_s, y + sel2_margin_s, w_max - (sel2_margin_s << 1), sel2_margin);
      g.fillRect(x + sel2_margin_s, y + sel2_margin_s, sel2_margin, h_max - (sel2_margin_s << 1));
      g.fillRect(x + w_max - sel2_margin_s - sel2_margin, y + sel2_margin_s, sel2_margin, h_max - (sel2_margin_s << 1));
      g.fillRect(x + sel2_margin_s, y + h_max - sel2_margin_s - sel2_margin, w_max - (sel2_margin_s << 1), sel2_margin);

      g.setColor(Color.black);
      g.drawRect(x + total_margin - 1, y + total_margin - 1, width + 1, height + 1);

      // g.setColor(Color.black);
      g.drawRect(x, y, width + total_margin + total_margin - 1, height + total_margin + total_margin - 1);

      update_selection = false; // ?
    }
  }

  /// pass on to texture...
  void action(int _x, int _y, int _m) {
    if (_x > total_margin) {
      if (_x < total_margin + width) {
        if (_y > total_margin) {
          if (_y < total_margin + height) {
            int ax = tile_x * (_x - total_margin) % texture.width;
            int ay = tile_y * (_y - total_margin) % texture.height;
            texture.action(ax, ay, _m);
          }
        }
      }
    }
  }

  public void actionDragged(int _ox, int _oy, int _x, int _y, int _m) {
    // if (((_m & 4) != 0) || (TextureGarden.currently_showing == TextureGarden.SHOW_TEXTURE_VIEWER)) {
    int ax = tile_x * (_x - total_margin) % texture.width;
    int ay = tile_y * (_y - total_margin) % texture.height;
    int aox = tile_x * (_ox - total_margin) % texture.width;
    int aoy = tile_y * (_oy - total_margin) % texture.height;
    texture.actionDragged(aox, aoy, ax, ay, _m);
    // }
  }

  public void loadTexture(String filename) {
    byte[] ba = ResourceLoader.getByteArray("textures.zip", "textures_o/", filename);
    texture.program.newProgram(ba); // applet.instruction_manager, 
    regenerateTexture();
  }

  public void regenerateTexture() {
    texture.regenerate();
    update_texture = true;
  }

  public void setSelectionType(int st) {
    if (st != selection_type) {
      selection_type = st;
      update_selection = true;
    }
  }
}
