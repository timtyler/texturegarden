package com.texturegarden.settings;

import com.texturegarden.IdleMessage;
import com.texturegarden.TG;
import com.texturegarden.effects.ca.FractalDrainage;
import com.texturegarden.effects.ca.ReactionDiffusion;
import com.texturegarden.gui.TextureContainer;
import com.texturegarden.messages.MsgNewTexture;
import com.texturegarden.utilities.Log;

public class Presets {
  public TG applet;
  public Presets(TG applet) {
    this.applet = applet;
  }

  public  void setPreset(String s) {
    for (int j = 0; j < TG.TA_Y_MAX; j++) {
      for (int i = 0; i < TG.TA_X_MAX; i++) {
        int n = i + j * TG.TA_X_MAX;
        TextureContainer ctc;
        presetTexture(s, j, i, n);
      }
    }
  }

  private void presetTexture(String s, int j, int i, int n) {
    TextureContainer ctc = applet.texture_array.texture_array[n];
    if (((ctc.selection_type) & TextureContainer.IMMUNE_MASK) == 0) {
      String fname = s + i + j + ".txt";
      ctc.applet.idle_message.send(IdleMessage.IMSG_NEW_TEXTURE, new MsgNewTexture(n, fname));
    
      ctc.setSelectionType(ctc.selection_type & (-1 ^ TextureContainer.OFFSPRING_MASK ^ TextureContainer.MOTHER_MASK ^ TextureContainer.GROWING_MASK));
    }
  }

  public  void initialPreset(String s) {
    for (int j = 0; j < applet.TA_Y_MAX; j++) {
      for (int i = 0; i < applet.TA_X_MAX; i++) {
        int n = i + j * applet.TA_X_MAX;
        String fname = s + i + j + ".txt";

        Log.log("load_f-name:" + fname);
        applet.texture_array.texture_array[n].loadTexture(fname);
        applet.current_canvas.repaint();
      }
    }
  }

  public  void setPreset() {
    switch (applet.preset_number) {
      case TG.PRESET_1 :
        ReactionDiffusion.f = 500;
        ReactionDiffusion.k = 20000;

        ReactionDiffusion.mul_u = 237;
        ReactionDiffusion.mul_v = 70;

        ReactionDiffusion.scale_u = 16;
        ReactionDiffusion.scale_v = 14;
        break;

      case TG.PRESET_2 :
        ReactionDiffusion.f = 1500;
        ReactionDiffusion.k = 19406;

        ReactionDiffusion.mul_u = 209;
        ReactionDiffusion.mul_v = 82;

        ReactionDiffusion.scale_u = 13;
        ReactionDiffusion.scale_v = 13;
        break;

      case TG.PRESET_3 :
        ReactionDiffusion.f = 1500;
        ReactionDiffusion.k = 8282;

        ReactionDiffusion.mul_u = 230;
        ReactionDiffusion.mul_v = 75;

        ReactionDiffusion.scale_u = 15;
        ReactionDiffusion.scale_v = 15;
        break;

      case TG.PRESET_4 :
        ReactionDiffusion.f = 1500;
        ReactionDiffusion.k = 3340;

        ReactionDiffusion.mul_u = 222;
        ReactionDiffusion.mul_v = 70;

        ReactionDiffusion.scale_u = 17;
        ReactionDiffusion.scale_v = 17;
        break;

      case TG.PRESET_5 :
        ReactionDiffusion.f = 1500;
        ReactionDiffusion.k = 8289;

        ReactionDiffusion.mul_u = 202;
        ReactionDiffusion.mul_v = 47;

        ReactionDiffusion.scale_u = 14;
        ReactionDiffusion.scale_v = 14;
        break;

      case TG.PRESET_6 :
        ReactionDiffusion.f = 1500;
        ReactionDiffusion.k = 8357;

        ReactionDiffusion.mul_u = 187;
        ReactionDiffusion.mul_v = 60;

        ReactionDiffusion.scale_u = 14;
        ReactionDiffusion.scale_v = 14;
        break;

      case TG.PRESET_7 :
        ReactionDiffusion.f = 925;
        ReactionDiffusion.k = 20000;

        ReactionDiffusion.mul_u = 220;
        ReactionDiffusion.mul_v = 56;

        ReactionDiffusion.scale_u = 16;
        ReactionDiffusion.scale_v = 13;
        break;

      case TG.PRESET_8 :
        ReactionDiffusion.f = 1500;
        ReactionDiffusion.k = 12700;

        ReactionDiffusion.mul_u = 0;
        ReactionDiffusion.mul_v = 256;

        ReactionDiffusion.scale_u = 12;
        ReactionDiffusion.scale_v = 12;
        break;

      case TG.PRESET_9 :
        ReactionDiffusion.f = 1140;
        ReactionDiffusion.k = 18310;

        ReactionDiffusion.mul_u = 217;
        ReactionDiffusion.mul_v = 83;

        ReactionDiffusion.scale_u = 14;
        ReactionDiffusion.scale_v = 12;
        break;

      case TG.PRESET_10 :
        ReactionDiffusion.f = 1331;
        ReactionDiffusion.k = 11840;

        ReactionDiffusion.mul_u = 213;
        ReactionDiffusion.mul_v = 209;

        ReactionDiffusion.scale_u = 12;
        ReactionDiffusion.scale_v = 12;
        break;

      case TG.PRESET_11 :
        ReactionDiffusion.f = 635;
        ReactionDiffusion.k = 17180;

        ReactionDiffusion.mul_u = 217;
        ReactionDiffusion.mul_v = 75;

        ReactionDiffusion.scale_u = 15;
        ReactionDiffusion.scale_v = 12;
        break;

      case TG.PRESET_12 :
        ReactionDiffusion.f = 1500;
        ReactionDiffusion.k = 10191;

        ReactionDiffusion.mul_u = 85;
        ReactionDiffusion.mul_v = 25;

        ReactionDiffusion.scale_u = 13;
        ReactionDiffusion.scale_v = 13;
        break;

      case TG.PRESET_13 :
        ReactionDiffusion.f = 1500;
        ReactionDiffusion.k = 3559;

        ReactionDiffusion.mul_u = 48;
        ReactionDiffusion.mul_v = 24;

        ReactionDiffusion.scale_u = 17;
        ReactionDiffusion.scale_v = 17;
        break;

      case TG.PRESET_14 :
        ReactionDiffusion.f = 1116;
        ReactionDiffusion.k = 13027;

        ReactionDiffusion.mul_u = 220;
        ReactionDiffusion.mul_v = 90;

        ReactionDiffusion.scale_u = 13;
        ReactionDiffusion.scale_v = 13;
        break;

      case TG.PRESET_15 :
        ReactionDiffusion.f = 1500;
        ReactionDiffusion.k = 7800;

        ReactionDiffusion.mul_u = 202;
        ReactionDiffusion.mul_v = 47;

        ReactionDiffusion.scale_u = 14;
        ReactionDiffusion.scale_v = 14;
        /*
        ReactionDiffusion.f = 1171;
         ReactionDiffusion.k = 26666;
        
         ReactionDiffusion.mul_u = 240;
         ReactionDiffusion.mul_v = 100;
        
         ReactionDiffusion.scale_u = 16;
         ReactionDiffusion.scale_v = 12; */
        break;

      case TG.PRESET_16 :
        ReactionDiffusion.f = 1387;
        ReactionDiffusion.k = 26666;

        ReactionDiffusion.mul_u = 181;
        ReactionDiffusion.mul_v = 56;

        ReactionDiffusion.scale_u = 17;
        ReactionDiffusion.scale_v = 13;
        break;

      case TG.PRESET_17 :
        ReactionDiffusion.f = 1449;
        ReactionDiffusion.k = 25636;

        ReactionDiffusion.mul_u = 253;
        ReactionDiffusion.mul_v = 256;

        ReactionDiffusion.scale_u = 18;
        ReactionDiffusion.scale_v = 12;
        break;

      case TG.PRESET_18 :
        ReactionDiffusion.f = 1500;
        ReactionDiffusion.k = 8169;

        ReactionDiffusion.mul_u = 230;
        ReactionDiffusion.mul_v = 86;

        ReactionDiffusion.scale_u = 15;
        ReactionDiffusion.scale_v = 15;
        break;

      case TG.PRESET_19 :
        ReactionDiffusion.f = 1500;
        ReactionDiffusion.k = 1794;

        ReactionDiffusion.mul_u = 232;
        ReactionDiffusion.mul_v = 42;

        ReactionDiffusion.scale_u = 17;
        ReactionDiffusion.scale_v = 18;
        break;

      case TG.PRESET_20 :
        ReactionDiffusion.f = 1500;
        ReactionDiffusion.k = 3748;

        ReactionDiffusion.mul_u = 232;
        ReactionDiffusion.mul_v = 80;

        ReactionDiffusion.scale_u = 17;
        ReactionDiffusion.scale_v = 17;
        break;

      case TG.FD_PRESET_1 :
        FractalDrainage.add_rate = 130;
        FractalDrainage.seep_rate = 0;

        FractalDrainage.flow_rate = 7;
        FractalDrainage.log_resistance = 7;
        FractalDrainage.ground_rising = 64;

        FractalDrainage.upper_threshold = 0xF000;
        FractalDrainage.lower_threshold = 0x1000;

        break;

      case TG.FD_PRESET_2 :
        FractalDrainage.add_rate = 50;
        FractalDrainage.seep_rate = 0;

        FractalDrainage.flow_rate = 9;
        FractalDrainage.log_resistance = 6;
        FractalDrainage.ground_rising = 46;

        FractalDrainage.upper_threshold = 0xF000;
        FractalDrainage.lower_threshold = 0x1000;

        break;

      case TG.FD_PRESET_3 : // freeze and thaw
        FractalDrainage.add_rate = 110;
        FractalDrainage.seep_rate = 0;

        FractalDrainage.flow_rate = 7;
        FractalDrainage.log_resistance = 7;
        FractalDrainage.ground_rising = 64;

        FractalDrainage.upper_threshold = 0xF000;
        FractalDrainage.lower_threshold = 0x1000;

        break;

      case TG.FD_PRESET_4 : // freeze and thaw
        FractalDrainage.add_rate = 210;
        FractalDrainage.seep_rate = 0;

        FractalDrainage.flow_rate = 1;
        FractalDrainage.log_resistance = 8;
        FractalDrainage.ground_rising = 64;

        FractalDrainage.upper_threshold = 0xC000;
        FractalDrainage.lower_threshold = 0x1000;

        break;

      case TG.FD_PRESET_5 : // shallow
        FractalDrainage.add_rate = 110;
        FractalDrainage.seep_rate = 0;

        FractalDrainage.flow_rate = 1;
        FractalDrainage.log_resistance = 8;
        FractalDrainage.ground_rising = 32;

        FractalDrainage.upper_threshold = 0xF000;
        FractalDrainage.lower_threshold = 0x1000;

        break;

      case TG.FD_PRESET_6 : // deep
        FractalDrainage.add_rate = 490;
        FractalDrainage.seep_rate = 0;

        FractalDrainage.flow_rate = 1;
        FractalDrainage.log_resistance = 8;
        FractalDrainage.ground_rising = 100;

        FractalDrainage.upper_threshold = 0xF000;
        FractalDrainage.lower_threshold = 0x1000;

        break;

      case TG.RD2_PRESET_1 :
        ReactionDiffusion.f = 1500;
        ReactionDiffusion.k = 769;

        ReactionDiffusion.mul_u = 26;
        ReactionDiffusion.mul_v = 32;

        ReactionDiffusion.scale_u = 13;
        ReactionDiffusion.scale_v = 13;

        break;

      case TG.RD2_PRESET_2 :
        ReactionDiffusion.f = 250;
        ReactionDiffusion.k = 276;

        ReactionDiffusion.mul_u = 16;
        ReactionDiffusion.mul_v = 181;

        ReactionDiffusion.scale_u = 13;
        ReactionDiffusion.scale_v = 13;

        break;

      case TG.RD2_PRESET_3 :
        ReactionDiffusion.f = 1500;
        ReactionDiffusion.k = 2307;

        ReactionDiffusion.mul_u = 56;
        ReactionDiffusion.mul_v = 72;

        ReactionDiffusion.scale_u = 16;
        ReactionDiffusion.scale_v = 16;

        break;

      case TG.INT_PRESET_1 :
        FractalDrainage.add_rate = 210;
        FractalDrainage.seep_rate = 80;

        FractalDrainage.flow_rate = 9;
        FractalDrainage.log_resistance = 8;
        FractalDrainage.ground_rising = 150;

        FractalDrainage.upper_threshold = 46173;
        FractalDrainage.lower_threshold = 37981;

        break;

      case TG.INT_PRESET_2 :
        FractalDrainage.add_rate = 210;
        FractalDrainage.seep_rate = 80;

        FractalDrainage.flow_rate = 9;
        FractalDrainage.log_resistance = 9;
        FractalDrainage.ground_rising = 100;

        FractalDrainage.upper_threshold = 46173;
        FractalDrainage.lower_threshold = 37981;

        break;
    }

    applet.showAgentRate();
    applet.showInhibitorRate();
    applet.showAgentSpeed();
    applet.showInhibitorSpeed();
    applet.showAgentScale();
    applet.showInhibitorScale();
  }
}
