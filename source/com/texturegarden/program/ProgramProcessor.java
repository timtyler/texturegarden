package com.texturegarden.program;

import com.texturegarden.TG;
import com.texturegarden.instructions.*;
import com.texturegarden.instructions.*;

public class ProgramProcessor {
  public static void setBooleanFlag(Program p, int section, int instruction, boolean value) {
    // ensure section exists - if not - append it...

    if (p.ip_of_SETTINGS < 0) {
      p.ip_of_SETTINGS = p.instruction_total;
      p.addInstruction(InstructionManager.SETTINGS);
      p.addInstruction(InstructionManager.RETURN);
    }

    // ensure instruction exists - if not - insert it...
    int adr = p.getOffsetOf(p.ip_of_SETTINGS, instruction | Program.instruction_marker);
    int v = (value ? InstructionManager.TRUE : InstructionManager.FALSE) | Program.instruction_marker;
    if (adr < 0) {
      // insert new instructions...
      int[] ia = new int[2];
      ia[0] = instruction | p.instruction_marker;
      ia[1] = v;
      p.insertInstructions(p.ip_of_SETTINGS + 1, ia, 0, 2);
    } else {
      p.alterOneValue(adr + 1, v);
    }
  }

  public static void setValue(Program p, int section, int instruction, int value) {
    // ensure section exists - if not - append it...
    if (p.ip_of_SETTINGS < 0) {
      p.ip_of_SETTINGS = p.instruction_total;
      p.addInstruction(InstructionManager.SETTINGS);
      p.addInstruction(InstructionManager.RETURN);
    }

    // ensure instruction exists - if not - insert it...
    int adr = p.getOffsetOf(p.ip_of_SETTINGS, instruction | Program.instruction_marker);
    if (adr < 0) {
      // insert new instructions...
      int[] ia = new int[2];
      ia[0] = instruction | p.instruction_marker;
      ia[1] = value;
      p.insertInstructions(p.ip_of_SETTINGS + 1, ia, 0, 2);
    } else {
      p.alterOneValue(adr + 1, value);
    }
  }

  public static void setTwoValues(Program p, int section, int instruction, int v1, int v2) {
    // ensure section exists - if not - append it...
    if (p.ip_of_SETTINGS < 0) {
      p.ip_of_SETTINGS = p.instruction_total;
      p.addInstruction(InstructionManager.SETTINGS);
      p.addInstruction(InstructionManager.RETURN);
    }

    // ensure instruction exists - if not - insert it...
    int adr = p.getOffsetOf(p.ip_of_SETTINGS, instruction | Program.instruction_marker);
    if (adr < 0) {
      // insert new instructions...
      int[] ia = new int[3];
      ia[0] = instruction | p.instruction_marker;
      ia[1] = v1;
      ia[2] = v2;
      p.insertInstructions(p.ip_of_SETTINGS + 1, ia, 0, 3);
    } else {
      p.alterOneValue(adr + 1, v1);
      p.alterOneValue(adr + 2, v2);
    }
  }
}
