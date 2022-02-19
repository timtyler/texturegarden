package com.texturegarden;

import com.texturegarden.instructions.Instruction;
import com.texturegarden.instructions.InstructionManager;
import com.texturegarden.program.*;
import com.texturegarden.settings.GlobalSettings;
import com.texturegarden.utilities.JUR;

class Mutator {
  final static int ARRAY_SIZE_INCREMENT = 16;

  static JUR rnd = new JUR(); // use this for mutation only!!!

  static int[] ins_array;

  static {
    ins_array = new int[] { InstructionManager.instructionNumber(InstructionManager.SIN), InstructionManager.instructionNumber(InstructionManager.BELL), InstructionManager.instructionNumber(InstructionManager.ISIN), InstructionManager.instructionNumber(InstructionManager.IBELL), InstructionManager.instructionNumber(InstructionManager.SAWTOOTH), InstructionManager.instructionNumber(InstructionManager.SAWTOOTH1), InstructionManager.instructionNumber(InstructionManager.SAWTOOTH2), InstructionManager.instructionNumber(InstructionManager.SAWTOOTH3)};
  }

  static void mutate(Program p, GlobalSettings global_settings) {
    int imo = -1;
    int last_instruction = -1;
    int flags_of_last_instruction = 0;

    int iv;
    int section = p.ip_of_SETUP;

    for (int i = 1; i < p.instruction_total; i++) { // 1!
      iv = p.program_array[i];

      boolean is_ins = (iv & InstructionManager.instruction_mask) == InstructionManager.instruction_marker;

      if (i == p.ip_of_PALETTE) {
        section = p.ip_of_PALETTE;
      }

      if (i == p.ip_of_SETUP) {
        section = p.ip_of_SETUP;
      }

      if (i == p.ip_of_ANIMATE) {
        section = p.ip_of_SETUP;
      }

      if (i == p.ip_of_SETTINGS) {
        section = p.ip_of_SETTINGS;
      }

      if (i == p.ip_of_GET_COLOUR) {
        section = p.ip_of_GET_COLOUR;
      }

      if (i == p.ip_of_GET_ELEVATION) {
        section = p.ip_of_GET_ELEVATION;
      }

      boolean set_seed = (imo == InstructionManager.instructionNumber(InstructionManager.SET_SEED));

      boolean mutating_this_section = ((section == p.ip_of_SETUP) && (global_settings.mut_tex)) || ((section == p.ip_of_PALETTE) && (global_settings.mut_pal)) || ((section == p.ip_of_GET_ELEVATION) && (global_settings.mut_hei)) || (set_seed && (global_settings.mut_see));

      if (!is_ins) {
        if (mutating_this_section) {
          if ((!set_seed) || (global_settings.mut_see)) {
            if ((rnd.nextInt(100) <= global_settings.mutation_rate) || set_seed) {
              if (imo != InstructionManager.instructionNumber(InstructionManager.PRESERVE)) {
                if (imo != InstructionManager.instructionNumber(InstructionManager.MERGE_N_BUFFERS_SHIFTED_2D)) {
                  // Log.log("set_seed" + set_seed + " mut_see:" + global_settings.mut_see);
                  if ((flags_of_last_instruction & Instruction.MUTATE_RANDOMLY) != 0) {
                    p.program_array[i] = rnd.nextInt(0x10000);
                    // Log.log("MUTATE_RANDOMLY");
                  } else {
                    int mom = iv >> 4;
                    if (mom < 2) {
                      mom = 2;
                    }
                    int res = iv + rnd.nextInt(mom) - rnd.nextInt(mom);

                    if (res < 0) {
                      res = 0;
                    }
                    if (res > 0xFFFF) {
                      res = 0xFFFF;
                    }
                    p.program_array[i] = res;
                  }
                }
              }
            }
          }
        }
      } else {
        last_instruction = iv;
        flags_of_last_instruction = InstructionManager.getFlags(iv);
        // texture.applet.instruction_manager.

        if (mutating_this_section) {
          if (rnd.nextInt(100) <= global_settings.mutation_rate) {
            if (iv == InstructionManager.instructionNumber(InstructionManager.NO_INVERT)) {
              if (rnd.nextInt(2) == 1) {
                p.program_array[i] = InstructionManager.instructionNumber(InstructionManager.INVERT);
              }
            } else {
              if (iv == InstructionManager.instructionNumber(InstructionManager.INVERT)) {
                if (rnd.nextInt(2) == 1) {
                  p.program_array[i] = InstructionManager.instructionNumber(InstructionManager.NO_INVERT);
                }
              } else {
                if ((iv == InstructionManager.instructionNumber(InstructionManager.SIN)) || (iv == InstructionManager.instructionNumber(InstructionManager.BELL)) || (iv == InstructionManager.instructionNumber(InstructionManager.ISIN)) || (iv == InstructionManager.instructionNumber(InstructionManager.IBELL)) || (iv == InstructionManager.instructionNumber(InstructionManager.SAWTOOTH)) || (iv == InstructionManager.instructionNumber(InstructionManager.SAWTOOTH1)) || (iv == InstructionManager.instructionNumber(InstructionManager.SAWTOOTH2)) || (iv == InstructionManager.instructionNumber(InstructionManager.SAWTOOTH3))) {
                  int temp = rnd.nextInt(8);
                  p.program_array[i] = InstructionManager.instructionNumber(ins_array[temp]);
                } else {
                  // parabola/circle code...
                }
              }
            }
          }
        }
      }

      imo = iv;
    }
  }
}
