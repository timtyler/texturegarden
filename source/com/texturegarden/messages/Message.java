package com.texturegarden.messages;

import com.texturegarden.TG;
import com.texturegarden.gui.MouseActionData;
import com.texturegarden.gui.MouseMovementData;
import com.texturegarden.gui.TextureContainer;
import com.texturegarden.utilities.Executor;

/**
 * Message Tim Tyler 2000-2001.
 */

/**
* To do
* =====
* Both monsters reverse direction on contact with another monster...
*/

public class Message {
  // messages...
  public final static int MSG_NULL = 0;
  public final static int MOUSE_CLICK = 1;
  public final static int MOUSE_DRAG = 2;
  public final static int MOUSE_ENTRY = 3;
  public final static int MOUSE_RELEASE = 4;
  public final static int MOUSE_PRESS = 5;
  public final static int MSG_CLEAR = 6;
  public final static int MSG_STEP = 7;
  public final static int MSG_PAUSE = 8;
  public final static int MSG_STOP = 9; // stop threads...
  public final static int MSG_REVERSE = 10;
  public final static int MSG_SET = 11;
  public final static int MSG_ADDITION = 12;
  public final static int MSG_DENSITY_RESET = 13;
  public final static int MSG_NEW_TYPE = 14;
  public final static int MSG_FREEZE = 15;
  public final static int MSG_MELT = 16;
  public final static int MSG_CANCEL = 22;
  public final static int MSG_SETNEWSIZE = 23;
  public final static int MSG_DB = 29;
  public final static int MSG_RESTART = 30;
  public final static int MSG_RESIZE = 31;
  public final static int MSG_CONTROL = 32;
  public final static int MSG_RESOLUTION = 33;
  public final static int MSG_RESOLUTION1 = 34;
  public final static int MSG_RESOLUTION2 = 35;
  public final static int MSG_IMAGE_CACHE_INVALID = 36;
  public final static int MSG_ALTERSIZE = 37;
  public final static int MSG_ALTERE = 38;
  public final static int MSG_REPRODUCTION = 39;
  public final static int MSG_BLEACHLINKS = 40;
  public final static int MSG_RANDOMISE = 41;
  public final static int MSG_SUICIDE = 42;
  public final static int MSG_RESET_LEVEL = 43;
  public final static int MSG_LOAD = 44;
  public final static int MSG_NEW_PRESET = 45;
  public final static int MOUSE_DOUBLE_CLICK = 46;
  public final static int MSG_TEXTURE_SELECTION = 47;

  public Object object;
  public int type;

  int current_message;
  Message[] msgq;

  String temp_string;

  final static int MSGQ_SIZE = 32;

  int counter; // ... for delayed messsages...

  TG applet;

  public Message() {}

  public Message(TG applet) {
    this.applet = applet;

    msgq = new Message[MSGQ_SIZE];

    for (int c = 0; c < MSGQ_SIZE; c++) {
      msgq[c] = new Message();
    }
  }

  public final void execute() {
    int msgn = 0;
    MouseMovementData ttmm;
    MouseActionData ttma;
    MsgTextureSelection mts;
    TextureContainer tc;

    int msg_number = current_message;

    for (msgn = 0; msgn < msg_number; msgn++) {
      Message m = msgq[msgn];
      switch (m.type) {
        case MOUSE_DRAG :
          ttmm = (MouseMovementData) m.object;
          applet.texture_array.actionDragged(ttmm.old_x, ttmm.old_y, ttmm.x, ttmm.y, ttmm.modifiers);
          break;

        case MOUSE_CLICK :
          ttma = (MouseActionData) m.object;
          applet.texture_array.action(ttma.x, ttma.y, ttma.modifiers);
          break;

        case MOUSE_PRESS :
          ttma = (MouseActionData) m.object;
          applet.texture_array.action(ttma.x, ttma.y, ttma.modifiers);
          break;

        case MOUSE_RELEASE :
          //Rockz.mouseReleased(msgq[msgn].data1,msgq[msgn].data2);

          break;

        case MOUSE_ENTRY :
          //Rockz.mouseEntered(msgq[msgn].data1,msgq[msgn].data2);

          break;

        case MOUSE_DOUBLE_CLICK :
          ttma = (MouseActionData) m.object;
          applet.texture_array.doubleClick(ttma.x, ttma.y, ttma.modifiers);
          break;

        case MSG_RESTART :
        //applet.texture_array.getSelectedTextureContainer().texture.clear();
        applet.texture_array.getSelectedTextureContainer().texture.reset();
        applet.texture_array.getSelectedTextureContainer().texture.regenerate();
        
        //applet.texture_array.getSelectedTextureContainer().texture.makeImageFromScratch();
          break;

        case MSG_CLEAR :
          applet.texture_array.getSelectedTextureContainer().texture.clear();

          break;

        case MSG_RANDOMISE :
          applet.texture_array.getSelectedTextureContainer().texture.randomise();

          break;

        case MSG_RESET_LEVEL :
          //if (!Render.redraw_suspended) {
          //_repton = PlayerManager.player[0].repton;
          //_repton.death();
          //_repton.levelOver();
          //}

          break;

        case MSG_SUICIDE :
          //if (!Render.redraw_suspended) {
          // _repton = PlayerManager.player[0].repton;
          //  _repton.death();
          //}

          break;

        case MSG_RESIZE :
          //Rockz.resolutionx = msgq[msgn].data1;
          /// Rockz.resolutiony = Rockz.resolutionx;
          // Rockz.performResize();

          break;

          // Message.send(Message.MSG_LOAD, returnedstring);
        case MSG_LOAD :
          temp_string = (String) m.object;

          applet.texture_array.performOperationOnSelectedTexture(new Executor() {
            public void execute(Object o) {
              TextureContainer tc2 = (TextureContainer) o;
              tc2.texture.clear();
              tc2.loadTexture(applet.message.temp_string);
            }
          });

          break;
          // case MSG_RESOLUTION:
          /*Render.activateSimpleMessage("Resizing...");
          GUI.removeAdBanner();
          GUI.removeControls();
          counter = 0;
          newmessage(MSG_RESOLUTION1, msgq[msgn].data1, 0);
          break;
          
          case MSG_RESOLUTION1:
          counter++;
          if (counter == 6) {
             newmessage(MSG_RESOLUTION2, msgq[msgn].data1, 0);
          }
          else
          {
             newmessage(MSG_RESOLUTION1, msgq[msgn].data1, 0);
          }
          
          break;
          
          case MSG_RESOLUTION2:
          // debug("midl:");
          int old_obj_width = Rockz.coords.obj_width;
          
          Rockz.view.coords.init(msgq[msgn].data1);
          
          if (Rockz.editor_option && Rockz.editor) {
             Editor.view.coords.init(msgq[msgn].data1);
             Editor.freq_view.coords.init(msgq[msgn].data1);
          }
          
          if (old_obj_width != Rockz.coords.obj_width) {
             GfxManager.reloadAndResizeEverything();
          }
          
          // Render.RepaintAll = true;
          newmessage(MSG_RESOLUTION3, 0, 0);
          */
          // break;

          // case MSG_RESOLUTION3:
          // debug("false:");
          /*
             Render.deactivateAll();
             Render.RepaintAll = true;
          
             GUI.addAdBanner();
             GUI.addControls();
             GUI.greyLazyScroll();
          
             if (Rockz.coords.bitcount != 1) {
                Rockz.follow = true;
                GUI.checkbox_follow.setState(false);
             }
          
             break;
          
          case MSG_STEP:
             Rockz.paused = false;
          
             try {
                Rockz.stepping = Integer.parseInt(GUI.textfield_step_size.getText());
                if (!Render.double_buffering) {
                   Rockz.stepping++;
                }
             }
                catch (Exception e) {
                   Rockz.stepping = 1;
                }
          
             GUI.grey_selected_three();
             GUI.button_step.setLabel(GUI.CANCEL);
          
             break;
          
          case MSG_STOP:
             Rockz.threadfinished = true;
          
             break;
          
          case MSG_CONTROL:
             Rockz.controls_visible = !Rockz.controls_visible;
             if (Rockz.controls_visible) {
                // applet.add("East", GUI.panelX);
             }
             else
             {
             // ?
             }
          
             break;
          
          case MSG_DB:
             Render.double_buffering = !Render.double_buffering;
             Render.RepaintAll = true;
             Rockz_Canvas.forceResize();
             Rockz_Canvas.getImageHandle();
             Rockz_Canvas.nullifyOffScreen();
          //Rockz_Canvas.getImageHandle();
          
             break;
          
          case MSG_PAUSE:
             Rockz.paused = !Rockz.paused;
             GUI.grey_selected_two();
          
             break;
          
          case MSG_CANCEL:
             Rockz.endStepping();
          
             break;
          
          case MSG_IMAGE_CACHE_INVALID:
             LevelManager _lm = PlayerManager.player[0].level_manager;
             _lm.current_level.performInvalidationOfImageCache();
          
             break;
          	*/

        case MSG_NEW_PRESET :
          applet.presets.setPreset((String) m.object); // ?!?!
          break;

        case MSG_TEXTURE_SELECTION :
          changeSelection((MsgTextureSelection) m.object);

          break;
      }
    }

    // swap around
    Message temp_msg;
    if (msg_number != current_message) {
      for (int i = 0; i < (current_message - msg_number); i++) {
        temp_msg = msgq[i];
        msgq[i] = msgq[msg_number + i];
        msgq[msg_number + i] = temp_msg;
      }
    }

    current_message -= msg_number;
  }

  public void changeSelection(MsgTextureSelection mts) {
    TextureContainer tc = mts.tc;
    tc.setSelectionType((tc.selection_type & mts.and) ^ mts.xor);
  }

  public void send(int t, Object o) {
    if (current_message < MSGQ_SIZE) {
      msgq[current_message].type = t;
      msgq[current_message].object = o;

      current_message++;
    }
  }

  public void send(int t) {
    send(t, null);
  }

  void resetMessageQueue() {
    current_message = 0;
  }
}
