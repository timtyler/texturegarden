package com.texturegarden.instructions;

import com.texturegarden.Texture;

public class Instruction {
  public int type = 0;
  public int number_of_arguments = -1;
  public int flags = 0;
  public String name;

  // types...
  public final static int NONE = 0;
  public final static int COMMAND = 1;
  public final static int FUNCTION = 2;
  public final static int COMMENT = 3;

  // flags...
  public final static int MUTATE_RANDOMLY = 1;

  // MakePalette
  // MakeBuffers
  // ValueAt2D
  // ValueAt3D
  // ValueAt2DAA - does interpolation
  // ValueAt3DAA - does interpolation
  // Return

  // can be called recursively...
  Instruction(String n, int t, int noa) {
    name = n;
    type = t;
    number_of_arguments = noa;
    flags = 0;
  }

  Instruction(String n, int t, int noa, int f) {
    name = n;
    type = t;
    number_of_arguments = noa;
    flags = f;
  }

  public int executeFunction() {
    return -1;
  }

  public void executeInstruction() {}

  // update instruction pointer...
  void updateIP(Texture t) {}

  // used for:
  // * Typesafety.
  // * Mutating arguments intelligently...
  int getArgumentType() {
    return 0;
  }
}
