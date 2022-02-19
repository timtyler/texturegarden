package com.texturegarden;

import java.awt.Point;

import com.texturegarden.gui.TextureArray;
import com.texturegarden.gui.TextureContainer;
import com.texturegarden.messages.MsgCloneTexture;
import com.texturegarden.messages.MsgCopyTexture;
import com.texturegarden.messages.MsgNewTexture;
import com.texturegarden.messages.MsgTextureSelection;
import com.texturegarden.program.Program;

public class IdleMessage {
  // messages...
  public final static int IMSG_NULL = 0;
  public final static int IMSG_NEW_TEXTURE = 1;
  public final static int IMSG_COPY_TEXTURE = 2;
  public final static int IMSG_CLONE_TEXTURE = 3;
  public final static int IMSG_TEXTURE_SELECTION = 4;
  public final static int IMSG_INITIATE_ANIMATION_UPDATE = 5;

  public  final static int MSGQ_SIZE = 96;

  public Object object;
  public int type;

  int current_message;
  IdleMessage[] msgq;

  String temp_string;

  int counter; // ... for delayed messsages...

  TG applet;
  IdleMessage() {}

  IdleMessage(TG applet) {
    this.applet = applet;

    msgq = new IdleMessage[MSGQ_SIZE];

    for (int c = 0; c < MSGQ_SIZE; c++) {
      msgq[c] = new IdleMessage();
    }
  }

  final void execute() {
    int msgn = 0;
    MsgNewTexture mnt;
    TextureContainer tc;
    Point pt;
    MsgTextureSelection mts;
    Program p;
    Texture ct;
    TextureArray ta;
    Texture from_t;
    Texture to_t;

    int msg_number = current_message;

    // Log.log("CM:" + current_message);
    if (current_message > 0) {
      if ((counter++ & 0) == 0) {
        msgn = 0;

        IdleMessage m = msgq[msgn];
        switch (m.type) {
          case IMSG_NEW_TEXTURE :
            mnt = (MsgNewTexture) m.object;

            // Log.log("load_f-name:" + mnt.filename);
            applet.texture_array.texture_array[mnt.n].loadTexture(mnt.filename);
            applet.current_canvas.repaint();

            break;

          case IMSG_COPY_TEXTURE :
            MsgCopyTexture mct = (MsgCopyTexture) m.object;
            ta = applet.texture_array;

            from_t = ta.texture_array[mct.from].texture;

            TextureContainer ctc = ta.texture_array[mct.to];
            to_t = ctc.texture;

            textureCopy(from_t, to_t);

            // mutate...
            Mutator.mutate(to_t.program, applet.global_settings);
            // to_t.program.mutate(applet.global_settings);
            ctc.regenerateTexture();
            applet.current_canvas.repaint();

            break;

          case IMSG_CLONE_TEXTURE :
            MsgCloneTexture mclt = (MsgCloneTexture) m.object;

            from_t = mclt.from.texture;
            to_t = mclt.to.texture;

            p = from_t.program;

            textureCopy(from_t, to_t);

            mclt.to.regenerateTexture();
            applet.current_canvas.repaint();

            break;

          case IMSG_TEXTURE_SELECTION :
            applet.message.changeSelection((MsgTextureSelection) m.object);

            break;

          case IMSG_INITIATE_ANIMATION_UPDATE :
            applet.texture_array.initiateAnimationUpdate();

            break;

        }

        // expensive...
        // reduce by one...
        IdleMessage temp_msg;
        temp_msg = msgq[0];

        for (int i = 0; i < current_message - 1; i++) {
          msgq[i] = msgq[i + 1];
        }

        msgq[--current_message] = temp_msg;
      }
    }
  }

  final public void send(int t, Object o) {
    if (current_message < MSGQ_SIZE) {
      msgq[current_message].type = t;
      msgq[current_message].object = o;

      current_message++;
    }
  }

  final public void send(int t) {
    send(t, null);
  }

  public void resetMessageQueue() {
    current_message = 0;
  }

  void textureCopy(Texture from_t, Texture to_t) {
    Program p = from_t.program;

    to_t.program = (Program) p.clone();
  }
}
