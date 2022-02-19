package com.texturegarden.gui;
import java.awt.Color;
import java.awt.Graphics;

import com.texturegarden.IdleMessage;
import com.texturegarden.TG;
import com.texturegarden.Texture;
import com.texturegarden.messages.Message;
import com.texturegarden.messages.MsgCopyTexture;
import com.texturegarden.messages.MsgTextureSelection;
import com.texturegarden.utilities.Executor;

public class TextureArray {
  // static Texture texture; //  = new Texture();
  public TextureContainer[] texture_array;
  int x;
  int y;
  int n;

  int selected_n;

  int i_size_x = 160; // 160
  int i_size_y = 160; // 128

  int possibly_animate_texture_number = 0;

  // boolean animation_update_in_progress = false;
  //boolean need_more_animation_updates = false;

  TG applet;

  public TextureArray(TG applet, int x, int y) {
    this.applet = applet;
    this.x = x;
    this.y = y;
    this.n = x * y;
    texture_array = new TextureContainer[n];
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < y; j++) {
        texture_array[i + (x * j)] = new TextureContainer(applet, i * (i_size_x + (TextureContainer.total_margin << 1)), j * (i_size_y + (TextureContainer.total_margin << 1)), i_size_x, i_size_y);
      }
    }

    selected_n = 0;
  }

  public TextureContainer getSelectedTextureContainer() {
    return texture_array[selected_n];
  }

  public void update(Graphics g) {
    redrawGrid(g);

    if (applet.drag_and_drop.dragging) {
      applet.drag_and_drop.update(g);
    }
  }

  public void redrawGrid(Graphics g) {
    for (int i = 0; i < n; i++) {
      texture_array[i].update(g);
    }
  }

  public void redrawTiled(Graphics g) {
    texture_array[selected_n].updateTiled(g);
  }

  public void updateViewer(Graphics g) {
    redrawTiled(g);
  }

  public void clearBackground(Graphics g) {
    g.setColor(Color.black);
    g.clearRect(0, 0, 9999, 9999);
  }

  void markAllForUpdate() {
    markAllSelectionsForUpdate();
    markAllTexturesForUpdate();
  }

  public void markAllSelectionsForUpdate() {
    for (int i = 0; i < n; i++) {
      texture_array[i].update_selection = true;
    }
  }

  public void markAllTexturesForUpdate() {
    for (int i = 0; i < n; i++) {
      texture_array[i].update_texture = true;
    }
  }

  public void animateAll() {
    Executor e = new Executor() {
      public void execute(Object o) {
        TextureContainer tc = (TextureContainer) o;
        Texture t = tc.texture;

        // not animating already...
        if (t.animation_frames_remaining <= 0) {
          boolean animating = false;
          switch (applet.animation_type) {
            case TG.ANIMATE_ALL :
              animating = true;
              break;

            case TG.ANIMATE_SELECTED :
              if ((tc.selection_type & TextureContainer.SELECTED_MASK) != 0) {
                animating = true;
              }
              break;

            case TG.ANIMATE_ACTIVE :
              if ((tc.selection_type & TextureContainer.IMMUNE_MASK) == 0) {
                animating = true;
              }
              break;

            case TG.ANIMATE_INACTIVE :
              if ((tc.selection_type & TextureContainer.IMMUNE_MASK) != 0) {
                animating = true;
              }
              break;
          }

          if (animating) {
            t.animation();
            tc.update_texture = true;
          }
        }
      }
    };

    //if (animation_update_in_progress) {
    possibly_animate_texture_number = (possibly_animate_texture_number + 1) % n;

    if (possibly_animate_texture_number == 0) {
      //if (!need_more_animation_updates) {
      //  animation_update_in_progress = false; // finished!
      //}

      //need_more_animation_updates = false;
    }

    if (texture_array[possibly_animate_texture_number].texture.animation_frames_remaining > 0) {
      TextureContainer tc = texture_array[possibly_animate_texture_number];
      Texture t = tc.texture;
      int nof = Math.min(8, t.animation_frames_remaining);

      for (int i = nof; --i >= 0;) {
        t.animation();
      }

      t.animation_frames_remaining -= nof;

      if (t.animation_frames_remaining > 0) {
        //need_more_animation_updates = true;
      } else {
        // deslect...
        applet.idle_message.send(IdleMessage.IMSG_TEXTURE_SELECTION, new MsgTextureSelection(tc, -1 ^ TextureContainer.GROWING_MASK, 0));
      }

      tc.update_texture = true;
    }
    //  } //else {
    performOperationOnEveryTexture(e);
    //}
  }

  public void initiateAnimationUpdate() {
    //animation_update_in_progress = true;
    possibly_animate_texture_number = 0;
    //need_more_animation_updates = false;
  }

  void endAnimationUpdate() {
    //animation_update_in_progress = false;
    //need_more_animation_updates = false;

    Executor e = new Executor() {
      public void execute(Object o) {
        TextureContainer tc = (TextureContainer) o;
        // if (tc.texture.animation_frames_remaining > 0) {
        tc.texture.animation_frames_remaining = 0;
        //IdleMessage.send(IdleMessage.IMSG_TEXTURE_SELECTION, new MsgTextureSelection(tc, -1 ^ TextureContainer.GROWING_MASK, 0));
        applet.message.send(Message.MSG_TEXTURE_SELECTION, new MsgTextureSelection(tc, -1 ^ TextureContainer.GROWING_MASK, 0));
        // Log.log("KILLED");
        //}
      }
    };

    performOperationOnEveryTexture(e);
  }

  public void renderAll() {
    for (int i = 0; i < n; i++) {
      texture_array[i].texture.makeImage();
    }
  }

  public void actionDragged(int _ox, int _oy, int _x, int _y, int _m) {
    TextureContainer tc;
    TextureContainer tco;

    tc = texture_array[selected_n];

    if (((_m & 16) == 0) || (applet.currently_showing == TG.SHOW_TEXTURE_VIEWER)) {
      tc.actionDragged(_ox - tc.x, _oy - tc.y, _x - tc.x, _y - tc.y, _m);
      // if (TextureGarden.currently_showing == TextureGarden.SHOW_TEXTURE_VIEWER) {
      //if () {
      // Log.log("DRAGGED"); 
      //}
      // }
      if (!tc.update_texture) {
        tc.texture.render();
        tc.update_texture = true; // mark texture as in need of update...
      }
      // need to regnerate...
    } else {
      if (TG.texture_modification_allowed) {
        if (!applet.drag_and_drop.dragging) {
          applet.drag_and_drop.start(_x, _y, tc);
        } else {
          applet.drag_and_drop.updateCoordinates(_ox, _oy);
        }
      } else {
        action(_x, _y, _m);
      }
    }
  }

  public void action(int _x, int _y, int _m) {
    TextureContainer tc;
    TextureContainer tco;

    switch (applet.currently_showing) {
      case TG.SHOW_TEXTURE_VIEWER :
        tc = texture_array[selected_n];
        tc.action(_x - tc.x, _y - tc.y, _m);
        break;

      default :
        for (int i = 0; i < n; i++) {
          tc = texture_array[i];
          if (_x > tc.x) {
            if (_x < tc.x + (tc.total_margin << 2) + tc.width) {
              if (_y > tc.y) {
                if (_y < tc.y + (tc.total_margin << 2) + tc.height) {
                  // if not selected, select it!
                  // if (currently_showing != SHOW_TEXTURE_VIEWER) {
                  if (i != selected_n) { // || (j != selected_y)) {
                    tco = texture_array[selected_n]; // [selected_y];
                    //.setSelectionType(0); //!
                    applet.message.changeSelection(new MsgTextureSelection(tco, -1 ^ TextureContainer.SELECTED_MASK, 0));

                    applet.message.changeSelection(new MsgTextureSelection(tc, -1 ^ TextureContainer.SELECTED_MASK, TextureContainer.SELECTED_MASK));

                    selected_n = i;
                    // selected_y = j;
                    applet.updateTextureUIDisplay(tc);
                  }

                  //if (animation_update_in_progress) {
                  endAnimationUpdate();
                  //}

                  if ((_m & 4) != 0) {
                    applet.message.changeSelection(new MsgTextureSelection(tc, -1, TextureContainer.IMMUNE_MASK));
                  }

                  tc.action(_x - tc.x, _y - tc.y, _m);
                }
              }
            }
          }
        }

        break;
    }

    applet.idle_message.resetMessageQueue(); // stop any existing activity...
    clearChildren(); // remove any selected objects...
    selectMothers(); // reselect any mother objects...
  }

  public void doubleClick(int _x, int _y, int _m) {
    if (TG.texture_modification_allowed) {
      action(_x, _y, _m);
      switch (applet.currently_showing) {
        case TG.SHOW_TEXTURE_VIEWER :
          break;
        default :
          reproduceSelected();
      }
    }
  }

  public void invertTileXSelected() {
    for (int i = 0; i < n; i++) {
      TextureContainer tc = texture_array[i];
      if ((tc.selection_type & tc.SELECTED_MASK) != 0) {
        tc.tile_x ^= 3;
        tc.plot_scale_factor_x = 1 / tc.tile_x;
      }
    }
  }

  public void invertTileYSelected() {
    for (int i = 0; i < n; i++) {
      //for (int j = 0; j < y; j++) {
      TextureContainer tc = texture_array[i];
      if ((tc.selection_type & tc.SELECTED_MASK) != 0) {
        tc.tile_y ^= 3;
        tc.plot_scale_factor_y = 1 / tc.tile_y;
      }
    }
  }

  public void performOperationOnSelectedTexture(Executor e) {
    for (int i = 0; i < n; i++) {
      TextureContainer tc = texture_array[i];
      if ((tc.selection_type & tc.SELECTED_MASK) != 0) {
        e.execute(tc);
      }
    }
  }

  public void performOperationOnEveryTexture(Executor e) {
    for (int i = 0; i < n; i++) {
      e.execute(texture_array[i]);
    }
  }

  public void performOperationOnEveryTextureContainerAssociatedWith(Texture t, Executor e) {
    for (int i = 0; i < n; i++) {
      if (texture_array[i].texture == t) {
        e.execute(texture_array[i]);
      }
    }
  }

  public void reproduceSelected() {
    applet.idle_message.send(IdleMessage.IMSG_TEXTURE_SELECTION, new MsgTextureSelection(texture_array[selected_n], -1 ^ TextureContainer.MOTHER_MASK, TextureContainer.MOTHER_MASK));

    for (int i = 0; i < n; i++) {
      TextureContainer ctc = texture_array[i];
      if (i != selected_n) { //  || (j != selected_y)) {
        if (((ctc.selection_type) & TextureContainer.IMMUNE_MASK) == 0) {
          applet.idle_message.send(IdleMessage.IMSG_TEXTURE_SELECTION, new MsgTextureSelection(ctc, -1 ^ TextureContainer.OFFSPRING_MASK, TextureContainer.OFFSPRING_MASK));
          applet.idle_message.send(IdleMessage.IMSG_COPY_TEXTURE, new MsgCopyTexture(selected_n, i));
          applet.idle_message.send(IdleMessage.IMSG_TEXTURE_SELECTION, new MsgTextureSelection(ctc, -1 ^ TextureContainer.OFFSPRING_MASK, 0));
        }
      }
    }

    applet.idle_message.send(IdleMessage.IMSG_TEXTURE_SELECTION, new MsgTextureSelection(texture_array[selected_n], -1 ^ TextureContainer.MOTHER_MASK, 0));
  }

  void clearChildren() {
    for (int i = 0; i < n; i++) {
      TextureContainer ctc = texture_array[i];
      if ((ctc.selection_type & TextureContainer.OFFSPRING_MASK) != 0) {
        applet.message.send(Message.MSG_TEXTURE_SELECTION, new MsgTextureSelection(ctc, -1 ^ TextureContainer.OFFSPRING_MASK, 0));
      }
    }
  }

  void selectMothers() {
    for (int i = 0; i < n; i++) {
      TextureContainer ctc = texture_array[i];
      if ((ctc.selection_type & TextureContainer.MOTHER_MASK) != 0) {
        applet.message.send(Message.MSG_TEXTURE_SELECTION, new MsgTextureSelection(ctc, -1 ^ TextureContainer.MOTHER_MASK ^ TextureContainer.SELECTED_MASK, 0)); // TextureContainer.SELECTED_MASK));
      }
    }
  }

  TextureContainer findTextureContainer(int _x, int _y) {
    TextureContainer tc;

    for (int i = 0; i < n; i++) {
      //for (int j = 0; j < y; j++) {
      tc = texture_array[i];
      if (_x > tc.x) {
        if (_x < tc.x + (tc.total_margin << 2) + tc.width) {
          if (_y > tc.y) {
            if (_y < tc.y + (tc.total_margin << 2) + tc.height) {
              return tc;
            }
          }
        }
      }
    }

    return null;
  }

  public static void main(String[] args) {
    TG.main(null);
  }
}
