package com.texturegarden.program;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.texturegarden.TG;
import com.texturegarden.Texture;
import com.texturegarden.instructions.Instruction;
import com.texturegarden.instructions.InstructionManager;
import com.texturegarden.utilities.JUR;
import com.texturegarden.utilities.Log;

public class Program implements Cloneable {
  final static int ARRAY_SIZE_INCREMENT = 16;

  public Texture texture;
  public int[] program_array;

  public int instruction_pointer = -1;
  public int instruction_count = -1;
  public int instruction_total = -1;

  String[] comment_string_array; // later...
  String[] function_name_array; // later...

  int[] return_address_array; // later...
  int return_address_pointer = -1; // later...

  String[] variable_name_array; // later...
  int[] variable_array; // later...

  public int ip_of_SETUP; // later...
  public int ip_of_PALETTE; // etc...
  public  int ip_of_ANIMATE; // etc...
  public int ip_of_GET_COLOUR; // etc...
  public int ip_of_GET_ELEVATION; // etc...
  public int ip_of_SETTINGS; // etc...

  public boolean finished;

  JUR rnd = new JUR(); // use this for mutation only!!!

  final static int MAX_PROGRAM_SIZE = 512; //  !!!!!!

  final static int instruction_mask = InstructionManager.instruction_mask;
  final static int instruction_marker = InstructionManager.instruction_marker;

  // MakePalette
  // SetUp
  // ValueAt2D
  // ValueAt3D
  // ValueAt2DAA - does interpolation
  // ValueAt3DAA - does interpolation
  // SetUpAnimation2D
  // Animate2D

  public Object clone() {
    Program o = null;
    try {
      o = (Program) super.clone();
    } catch (CloneNotSupportedException e) {
      Log.log("CloneNotSupportedException");
      e.printStackTrace();
    }

    program_array = (int[]) program_array.clone();
  
    return o;
  }

  public void init() {
    program_array = new int[MAX_PROGRAM_SIZE];
    reset();
  }

  public void claimTexture(Texture t) {
    texture = t;
    InstructionManager.claimTexture(t);
  }

  public void freeTexture() {
    InstructionManager.freeTexture();
  }

  public void reset() {
    instruction_pointer = 0;
    instruction_total = 0;
    instruction_count = 0;
    return_address_pointer = 0;
    finished = false;

    ip_of_SETUP = -1;
    ip_of_PALETTE = -1;
    ip_of_ANIMATE = -1;
    ip_of_SETTINGS = -1;
    ip_of_GET_COLOUR = -1;
    ip_of_GET_ELEVATION = -1;
    ip_of_SETTINGS = -1;
  }

  // multiple ones...
  //void executeInstructions(int number) {}

  public void executeAllInstructions(Texture t, int ip) {
    finished = false;
    claimTexture(t);
    instruction_pointer = ip;

    do {
      executeInstruction();
    } while (!finished);

    freeTexture();
  }

  // can be faster if lots of calls are needed...
  public void executeAllInstructionsWithLock() {
    finished = false;

    do {
      executeInstruction();
    } while (!finished);
  }

  // can be called recursively...
  public void executeInstruction() {
    int current_instruction = program_array[instruction_pointer++];
    instruction_count++;

    // Log.log("Instruction:" + current_instruction);

    if ((current_instruction & instruction_mask) == instruction_marker) {
      Instruction instr_temp = InstructionManager.instruction_array[current_instruction & 0xFFFF];
      // Log.log("Instruction:" + instr_temp.name);
      instr_temp.executeInstruction();
      // InstructionManager.instruction_array[current_instruction & 0xFFFF].executeInstruction();
    } else {
      Log.log("Instruction error");
    }
  }

  public int getArgument() {
    // instruction_count++;
    int current_instruction = program_array[instruction_pointer++];

    if ((current_instruction & instruction_mask) == instruction_marker) {
      Instruction i = InstructionManager.instruction_array[current_instruction & 0xFFFF];
      if (i.type == Instruction.FUNCTION) {
        return i.executeFunction();
      } else {
        Log.log("Expected Function call");
        return -1; // error...
      }
    } else {
      return current_instruction;
    }
  }

  public void addInstruction(int inum) {
    // program_array[instruction_total++] = inum | instruction_marker;
    addData(inum | instruction_marker);
  }

  public void addData(int data) {
    if (instruction_total >= program_array.length) {
      int[] temp_program_array = program_array;
      program_array = new int[temp_program_array.length + ARRAY_SIZE_INCREMENT];
      System.arraycopy(temp_program_array, 0, program_array, 0, temp_program_array.length);
      temp_program_array = null; // GC this...
    }

    program_array[instruction_total++] = data;
  }

  void insertInstructions(int offset, int[] from_array, int index_into_from_array, int length) {
    if ((instruction_total + length) >= program_array.length) {
      int[] temp_program_array = program_array;
      program_array = new int[instruction_total + length];
      System.arraycopy(temp_program_array, 0, program_array, 0, offset);
      System.arraycopy(from_array, index_into_from_array, program_array, offset, length);
      System.arraycopy(temp_program_array, offset, program_array, offset + length, instruction_total - offset);

      temp_program_array = null; // GC this...
    } else {
      System.arraycopy(program_array, offset, program_array, offset + length, instruction_total - offset);
      System.arraycopy(from_array, index_into_from_array, program_array, offset, length);
    }

    instruction_total += length;
  }

  public void saveText(FileOutputStream fos) throws IOException {
    int current_instruction;
    Instruction i;

    instruction_pointer = 0;

    // fos.write("Texture Garden file.  File format version:0.01\n".getBytes());
    do {
      current_instruction = program_array[instruction_pointer++];
      if ((current_instruction & instruction_mask) == instruction_marker) {
        i = InstructionManager.instruction_array[current_instruction & 0xFFFF];
        if ((i.type == Instruction.COMMAND) && (instruction_pointer > 1)) {
          fos.write("\n".getBytes());
        }

        fos.write((i.name + " ").getBytes());
      } else {
        fos.write(("" + current_instruction + " ").getBytes());
      }
    } while (instruction_pointer < instruction_total);

    fos.write("\n".getBytes());
  }

  public void newProgram(byte[] ba) { // InstructionManager instruction_manager, 
    int string_number;
    String[] sa = parseIntoStrings(ba);
    string_number = sa.length;
    String s;
    int temp;

    reset();
    program_array = new int[string_number];

    for (int i = 0; i < string_number; i++) {
      // Log.log("INS: " + sa[i]);
      s = sa[i];
      char ca0 = s.charAt(0);
      // if ((s.charAt(0) >= '0') && (s.charAt(0) <= '9') || ()) {
      if ("0123456789+-".indexOf("" + ca0) >= 0) { // (s.charAt(0) >= '0') && (s.charAt(0) <= '9') || ()) {
        if ((s.charAt(0) == '0') && (s.length() > 2) && (s.charAt(1) == 'x')) {
          // addData(Integer.parseInt(s.substring(2), 16));
          // Log.log("HEX!");
          addData((int) Long.parseLong(s.substring(2), 16));
        } else {
          // parse int.
          // addData(Integer.parseInt(s)); // need hex too...
          // addData(Integer.decode(s).intValue()); // need hex too...
          //addData((int)(Long.decode(s).longValue())); // need hex too...
          addData((int) Long.parseLong(s));
        }
      } else {
        temp = InstructionManager.getInstructionNumber(s);
        if (temp >= 0) {
          addInstruction(temp);
          switch (temp) {
            case InstructionManager.SETUP :
              ip_of_SETUP = instruction_total;
              break;

            case InstructionManager.PALETTE :
              ip_of_PALETTE = instruction_total;
              break;

            case InstructionManager.ANIMATE :
              ip_of_ANIMATE = instruction_total;
              break;

            case InstructionManager.GET_COLOUR :
              ip_of_GET_COLOUR = instruction_total;
              break;

            case InstructionManager.GET_ELEVATION :
              ip_of_GET_ELEVATION = instruction_total;
              break;

            case InstructionManager.SETTINGS :
              ip_of_SETTINGS = instruction_total;
              break;
          }
        } else {
          Log.log("Unrecognised instruction: " + s + " (token:" + i + ")");
        }
      }
    }
  }

  String[] parseIntoStrings(byte[] ba) {
    int max_number_of_strings = 10;
    int string_number = 0;
    //int index = 0;
    ///StringBuffer csb;
    //boolean fin = false;

    String bastr = new String(ba);
    StringTokenizer st = new StringTokenizer(bastr);

    string_number = st.countTokens();
    max_number_of_strings = string_number;
    String[] results = new String[max_number_of_strings];

    for (int i = 0; i < max_number_of_strings; i++) {
      results[i] = st.nextToken();
    }
    /*
     do {
        csb = new stringBuffer();
     	getWordAt(ba, index, 
     } while (!fin);
    */

    return results;
  }

  int getOffsetOf(int scan_from, int scan_for) {
    for (int i = scan_from; i < instruction_total; i++) {
      if (program_array[i] == scan_for) {
        return i;
      }
    }

    return -1; // not found...
  }

  void alterOneValue(int offset, int value) {
    program_array[offset] = value;
  }
}

/*
    void mutate(GlobalSettings global_settings) {
       int imo = -1;
       int last_instruction = -1;
       int flags_of_last_instruction = 0;
     
       int iv;
       int section = ip_of_SETUP;
     
       for (int i = 1; i < instruction_total; i++) { // 1!
          iv = program_array[i];
        
          boolean is_ins = (iv & instruction_mask) == instruction_marker;
        
          if (i == ip_of_PALETTE) {
             section = ip_of_PALETTE;
          }
        
          if (i == ip_of_SETUP) {
             section = ip_of_SETUP;
          }
        
          if (i == ip_of_ANIMATE) {
             section = ip_of_SETUP;
          }
        
          if (i == ip_of_SETTINGS) {
             section = ip_of_SETTINGS;
          }
        
          if (i == ip_of_GET_COLOUR) {
             section = ip_of_GET_COLOUR;
          }
        
          if (i == ip_of_GET_ELEVATION) {
             section = ip_of_GET_ELEVATION;
          }
        
          boolean set_seed = (imo == instructionNumber(InstructionManager.SET_SEED));
        
          boolean mutating_this_section =
             ((section == ip_of_SETUP)         && (global_settings.mut_tex)) ||
             ((section == ip_of_PALETTE)       && (global_settings.mut_pal)) ||
             ((section == ip_of_GET_ELEVATION) && (global_settings.mut_hei)) ||
             (set_seed                         && (global_settings.mut_see));
        
          if (!is_ins) {
             if (mutating_this_section) {
                if ((!set_seed) || (global_settings.mut_see)) {
                   if ((rnd.nextInt(100) <= global_settings.mutation_rate) || set_seed) {
                      if (imo != instructionNumber(InstructionManager.PRESERVE)) {
                         if (imo != instructionNumber(InstructionManager.MERGE_N_BUFFERS_SHIFTED_2D)) {
                            // Log.log("set_seed" + set_seed + " mut_see:" + global_settings.mut_see);
                            if ((flags_of_last_instruction & Instruction.MUTATE_RANDOMLY) != 0) {
                               program_array[i] = rnd.nextInt(0x10000);
                               // Log.log("MUTATE_RANDOMLY");
                            }
                            else
                            {
                               int mom = iv >> 4;
                               if (mom < 2) {
                                  mom = 2;
                               }
                               program_array[i] = iv + rnd.nextInt(mom) - rnd.nextInt(mom);
                            }
                         }
                      }
                   }
                }
             }
          }
          else
          {
             last_instruction = iv;
             flags_of_last_instruction = texture.applet.instruction_manager.getFlags(iv);
           
             if (mutating_this_section) {
                if (rnd.nextInt(100) <= global_settings.mutation_rate) {
                   if (iv == instructionNumber(InstructionManager.NO_INVERT)) {
                      if (rnd.nextInt(2) == 1) {
                         program_array[i] = instructionNumber(InstructionManager.INVERT);
                      }
                   }
                   else
                   {
                      if (iv == instructionNumber(InstructionManager.INVERT)) {
                         if (rnd.nextInt(2) == 1) {
                            program_array[i] = instructionNumber(InstructionManager.NO_INVERT);
                         }
                      }
                      else
                      {
                         if (  (iv == instructionNumber(InstructionManager.SIN))       ||
                               (iv == instructionNumber(InstructionManager.BELL))      ||
                               (iv == instructionNumber(InstructionManager.ISIN))      ||
                               (iv == instructionNumber(InstructionManager.IBELL))     ||
                               (iv == instructionNumber(InstructionManager.SAWTOOTH))  ||
                               (iv == instructionNumber(InstructionManager.SAWTOOTH1)) ||
                               (iv == instructionNumber(InstructionManager.SAWTOOTH2)) ||
                               (iv == instructionNumber(InstructionManager.SAWTOOTH3)))
                         {
                            int temp = rnd.nextInt(8);
                            program_array[i] = instructionNumber(ins_array[temp]);
                         }
                         else
                         {
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
 */
