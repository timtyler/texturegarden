/**
 * InstructionManager - Tim Tyler 2001.
 * 
 * *If* threads are to be used, this needs to be made thread safe - one IM per texture.<P>
 *
 * This code has been placed in the public domain.<P>
 * You can do what you like with it.<P>
 * Note that this code comes with no warranty.<P>
 *
 */

/*
 * ToDo
 * ====
 * Loops
 * Conditionals...
 * ROT180 TRANSLATE_1D? TRANSLATE_2D??
 * Ninety-degree rotation - and "Diagonal mirrors" (which work *much* faster if the texture is square...)
 * Buffer naming strategy....
 * Best way to call "library scripts"....
 * Named variables...
 * Subroutines...
 * ValueAt3DAA - does interpolation  
 *
 * Done
 * ValueAt2DAA - does interpolation
 * Buffer creation 
 * BUFSWAP
 */

/*
Perlin Noise....
=====
Function Noise3D (X,Y,Z: LongInt): Word;

Var

  N: LongInt;

Begin

  N := X+Y*57+Z*17;

  Result := ((N*(N*N*15731+789221)+1376312589) AND $FFFF);

End;




function IntNoise(32-bit integer: x)			 

    x = (x<<13) ^ x;
    return ( 1.0 - ( (x * (x * x * 15731 + 789221) + 1376312589) & 7fffffff) / 1073741824.0);    

  end IntNoise function

*/

package com.texturegarden.instructions;

import java.awt.Color;

import com.texturegarden.IdleMessage;
import com.texturegarden.Texture;
import com.texturegarden.arrays.ComplexArray1D;
import com.texturegarden.arrays.ComplexArray2D;
import com.texturegarden.arrays.SimpleArray1D;
import com.texturegarden.arrays.SimpleArray2D;
import com.texturegarden.effects.Voronoi;
import com.texturegarden.effects.ca.CAProcess2D;
import com.texturegarden.effects.ca.DiffusionLimitedAggregation;
import com.texturegarden.effects.ca.FractalDrainage;
import com.texturegarden.effects.ca.FractalLandscape;
import com.texturegarden.effects.ca.LiquidCrystal;
import com.texturegarden.effects.ca.ReactionDiffusion;
import com.texturegarden.effects.ca.waves.Schrodinger;
import com.texturegarden.effects.ca.waves.Water;
import com.texturegarden.effects.ca.waves.WaterReversible;
import com.texturegarden.effects.particle.Particle;
import com.texturegarden.fourier.FFT1D;
import com.texturegarden.fourier.FFT2D;
import com.texturegarden.gui.TextureContainer;
import com.texturegarden.messages.MsgTextureSelection;
import com.texturegarden.utilities.Executor;
import com.texturegarden.utilities.SquareRoot;

public class InstructionManager implements InstructionNumbers {
  public final static int instruction_mask = 0xFFFF0000;
  public final static int instruction_marker = 0xBABE0000;

  public final static int _COMBINATION_MINIMISE = 0xDEF9;
  public final static int _COMBINATION_MAXIMISE = 0xDEFA;
  public final static int _COMBINATION_ADD = 0xDEFB;
  public final static int _COMBINATION_MULTIPLY = 0xDEFC;
  public final static int _COMBINATION_AVERAGE = 0xDEFD;

  public final static int MAX_NUMBER = 256;

  // -------------------------------------------------------------------------

  // public final static int WAVES_LD                             = 40;
  // public final static int WAVES_TD                             = 41;

  // N E W S NE NW SE SW...?

  static Texture texture; // !?!?!?
  static boolean locked;

  static public Instruction[] instruction_array;

  // implement synchronised access...

  // TextureGarden applet; // get rid of this!
  // to be removed...

  //InstructionManager(TextureGarden applet) {
  // this.applet = applet;
  //}

  // blocks until free...
  public static void claimTexture(Texture t) {
    while (locked) {
      try {
        Thread.sleep(20);
      } catch (Exception e) {}
    }

    texture = t;
    locked = true;
  }

  public static void freeTexture() {
    locked = false;
  }

  // "System" functions
  // ==================
  // MakePalette
  // MakeBuffers
  // ValueAt2D
  // ValueAt3D
  // Animate2D // regenerates palette if necessary...
  // Animate3D
  // 
  // Other FNs
  // =========
  // Process2D(function_of_x_and_y and v)
  // SUBFLOOR

  //public void setTexture(Texture _t) {
  //texture = _t;
  // }

  public static void init() {
    instruction_array = new Instruction[MAX_NUMBER];

    instruction_array[DO_ANIMATION] = new Instruction("DoAnimation", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        int n = t.program.getArgument();

        t.animation_frames_remaining = n;

        t.applet.idle_message.send(IdleMessage.IMSG_INITIATE_ANIMATION_UPDATE);

        t.applet.texture_array.performOperationOnEveryTextureContainerAssociatedWith(t, new Executor() {

          public void execute(Object o) {
            TextureContainer tc = (TextureContainer) o;
            // IdleMessage.send(IdleMessage.IMSG_INITIATE_ANIMATION_UPDATE);
            tc.applet.idle_message.send(IdleMessage.IMSG_TEXTURE_SELECTION, new MsgTextureSelection(tc, -1 ^ TextureContainer.GROWING_MASK, TextureContainer.GROWING_MASK));
          }
        });
        /*
        // if backend...
        for (int i = 0; i < n; i++) {
           t.program.instruction_pointer = t.program.ip_of_ANIMATE;
           t.program.executeAllInstructions();
        }
        */
      }
    };
    instruction_array[RETURN] = new Instruction("Return", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        InstructionManager.texture.program.finished = true;
      }
    };

    instruction_array[RETURN_COLOUR] = new Instruction("ReturnColour", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        t.colour = t.program.getArgument();

        t.program.finished = true;
      }
    };

    instruction_array[RETURN_ELEVATION] = new Instruction("ReturnElevation", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        t.elevation = t.program.getArgument();
        t.program.finished = true;
      }
    };

    instruction_array[FREQUENCY_FRACTAL_NOISE_2D] = new Instruction("FrequencyFractalNoise2D", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        // int minimum_radius = rnd.nextInt(5);
        int intensity = t.program.getArgument();
        int minimum_radius = t.program.getArgument();

        t.c_a2D[0].simpleFractalNoise(intensity, minimum_radius, t);
        //t.c_a2D[0].r.simpleFractalNoiseHalf(intensity, minimum_radius, t);
        //t.c_a2D[0].i.simpleFractalNoiseHalf(intensity, minimum_radius, t);
      }
    };

    instruction_array[BANDWIDTH_LIMITED_FREQUENCY_NOISE_2D] = new Instruction("BandwidthLimitedFrequencyNoise2D", Instruction.COMMAND, 3) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        int intensity = t.program.getArgument();
        int r_inner = t.program.getArgument();
        int r_outer = t.program.getArgument();

        t.c_a2D[0].noiseBandwidthLimited(intensity, r_inner, r_outer, t);

        // t.c_a2D[0].noiseBandwidthLimitedHalf(intensity, r_inner, r_outer, t);
        // t.c_a2D[0].i.noiseBandwidthLimitedHalf(intensity, r_inner, r_outer, t);
      }
    };

    instruction_array[FREQUENCY_NOISE_2D] = new Instruction("FrequencyNoise2D", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        int intensity = t.program.getArgument();

        int r_outer = t.program.getArgument();

        t.c_a2D[0].noise(intensity, r_outer, t);
      }
    };

    instruction_array[FREQUENCY_NOISE_1D] = new Instruction("FrequencyNoise1D", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        int intensity = t.program.getArgument();
        int r_outer = t.program.getArgument();

        t.c_a1D[0].noise(intensity, r_outer, t);
      }
    };

    instruction_array[NEW_BUFFER_1D] = new Instruction("NewBuffer1D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int buffer_size = t.program.getArgument();
        t.newBuffer1D(buffer_size);
      }
    };

    instruction_array[NEW_BUFFER_2D] = new Instruction("NewBuffer2D", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        int buffer_width = t.program.getArgument();
        int buffer_height = t.program.getArgument();
        t.newBuffer2D(buffer_width, buffer_height);
      }
    };

    instruction_array[SWAP_BUFFER_1D] = new Instruction("SwapBuffer1D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int buffer_number = t.program.getArgument();

        ComplexArray1D temp_ca = t.c_a1D[0]; // (buffer_width, buffer_height);
        t.c_a1D[0] = t.c_a1D[buffer_number];
        t.c_a1D[buffer_number] = temp_ca;
      }
    };

    instruction_array[SWAP_BUFFER_2D] = new Instruction("SwapBuffer2D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int buffer_number = t.program.getArgument();

        ComplexArray2D temp_ca = t.c_a2D[0]; // (buffer_width, buffer_height);
        t.c_a2D[0] = t.c_a2D[buffer_number];
        t.c_a2D[buffer_number] = temp_ca;
      }
    };

    instruction_array[RETREIVE_BUFFER_1D] = new Instruction("RetreiveBuffer1D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int buffer_number = t.program.getArgument();

        ComplexArray1D.copyBuffer(t.c_a1D[buffer_number], t.c_a1D[0]);
      }
    };

    instruction_array[RETREIVE_BUFFER_2D] = new Instruction("RetreiveBuffer2D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int buffer_number = t.program.getArgument();

        ComplexArray2D.copyBuffer(t.c_a2D[buffer_number], t.c_a2D[0]);
      }
    };

    instruction_array[STORE_BUFFER_1D] = new Instruction("StoreBuffer1D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int buffer_number = t.program.getArgument();

        ComplexArray1D.copyBuffer(t.c_a1D[0], t.c_a1D[buffer_number]);
      }
    };

    instruction_array[STORE_BUFFER_2D] = new Instruction("StoreBuffer2D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int buffer_number = t.program.getArgument();

        ComplexArray2D.copyBuffer(t.c_a2D[0], t.c_a2D[buffer_number]);
      }
    };

    instruction_array[FREQUENCY_NOISE_2D_QUICK] = new Instruction("FrequencyNoise2DQuick", Instruction.COMMAND, 5) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int intensity = t.program.getArgument();
        int ix = t.program.getArgument();
        int iy = t.program.getArgument();
        int dx = t.program.getArgument();
        int dy = t.program.getArgument();

        t.c_a2D[0].noiseSquareSection(intensity, ix, iy, dx, dy, t);
      }
    };

    instruction_array[INVERSE_FFT_2D] = new Instruction("InverseFFT2D", Instruction.COMMAND, 0) {
      public void executeInstruction() {

        FFT2D.fft_inv(InstructionManager.texture.c_a2D[0]);
      }
    };

    instruction_array[FFT_2D] = new Instruction("FFT2D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        FFT2D.fft(InstructionManager.texture.c_a2D[0]);
      }
    };

    instruction_array[INVERSE_FFT_1D] = new Instruction("InverseFFT1D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
          // t.selected_buffer_1d = 
  FFT1D.fft_inv(InstructionManager.texture.c_a1D[0]); // 
      }
    };

    instruction_array[FFT_1D] = new Instruction("FFT1D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        FFT1D.fft(InstructionManager.texture.c_a1D[0]); // 
      }
    };

    instruction_array[EQUALISE_SCALED_1D] = new Instruction("EqualiseScaled1D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int range = t.program.getArgument();
        t.c_a1D[0].r.equaliseScaled(range);
      }
    };

    instruction_array[WAVES_H_QUICK] = new Instruction("WavesHQuick", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        InstructionManager.texture.c_a2D[0].r.horizontalWarpQuick(InstructionManager.texture.c_a1D[0].r);
      }
    };

    instruction_array[WAVES_V_QUICK] = new Instruction("WavesVQuick", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        InstructionManager.texture.c_a2D[0].r.verticalWarpQuick(InstructionManager.texture.c_a1D[0].r);
      }
    };

    instruction_array[WAVES_H] = new Instruction("WavesH", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        InstructionManager.texture.c_a2D[0].r.horizontalWarp(InstructionManager.texture);
      }
    };

    instruction_array[WAVES_V] = new Instruction("WavesV", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        InstructionManager.texture.c_a2D[0].r.verticalWarp(InstructionManager.texture);
      }
    };

    instruction_array[SMEAR_H] = new Instruction("SmearH", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        InstructionManager.texture.c_a2D[0].r.horizontalSmear(InstructionManager.texture);
      }
    };

    instruction_array[SMEAR_V] = new Instruction("SmearV", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        InstructionManager.texture.c_a2D[0].r.verticalSmear(InstructionManager.texture);
      }
    };

    instruction_array[SMEAR_H_QUICK] = new Instruction("SmearHQuick", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        InstructionManager.texture.c_a2D[0].r.horizontalSmearQuick(InstructionManager.texture);
      }
    };

    instruction_array[SMEAR_V_QUICK] = new Instruction("SmearVQuick", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        InstructionManager.texture.c_a2D[0].r.verticalSmearQuick(InstructionManager.texture);
      }
    };

    instruction_array[AVERAGE_COMPONENTS] = new Instruction("AverageComponents", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        InstructionManager.texture.c_a2D[0].averageComponents();
      }
    };

    instruction_array[SQUARE_ROOT_2D] = new Instruction("SquareRoot2D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        InstructionManager.texture.c_a2D[0].takeSquareRoots();
      }
    };

    instruction_array[BUFFER_WIDTH] = new Instruction("BufferWidth", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.width;
      }
    };

    instruction_array[BUFFER_HEIGHT] = new Instruction("BufferHeight", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.height;
      }
    };

    instruction_array[BUFFER_DEPTH] = new Instruction("BufferDepth", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.depth;
      }
    };

    instruction_array[ZERO_R_1D] = new Instruction("ZeroR1D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        InstructionManager.texture.c_a1D[0].r.clear();
      }
    };

    instruction_array[ZERO_I_1D] = new Instruction("ZeroI1D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        InstructionManager.texture.c_a1D[0].r.clear();
        InstructionManager.texture.c_a1D[0].i.clear();
      }
    };

    /*
    instruction_array[BUFFER_HEIGHT] = 
          new Instruction("BufferTypeY", Instruction.FUNCTION, 0) {
             public int executeFunction() {
                return 1;
             }
          };
    */

    instruction_array[COMBINATION_ADD] = new Instruction("CombinationAdd", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return _COMBINATION_ADD;
      }
    };

    instruction_array[COMBINATION_MAXIMISE] = new Instruction("CombinationMaximise", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return _COMBINATION_MAXIMISE;
      }
    };

    instruction_array[COMBINATION_MULTIPLY] = new Instruction("CombinationMultiply", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return _COMBINATION_MULTIPLY;
      }
    };

    instruction_array[BOUND] = new Instruction("Bound", Instruction.FUNCTION, 1) {
      public int executeFunction() {
        return InstructionManager.texture.program.getArgument() & 0xFFFF;
      }
    };

    instruction_array[C] = new Instruction("C", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.c;
      }
    };

    instruction_array[X] = new Instruction("X", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.x;
      }
    };

    instruction_array[Y] = new Instruction("Y", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.y;
      }
    };

    instruction_array[Z] = new Instruction("Z", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.z;
      }
    };

    instruction_array[TIME] = new Instruction("Time", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.time;
      }
    };

    instruction_array[SPEED] = new Instruction("Speed", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.speed;
      }
    };

    instruction_array[OFFSET_X] = new Instruction("OffsetX", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.c_a2D[0].r.offset_x; // << 16) / InstructionManager.texture.c_a2D[0].r.width;
      }
    };

    instruction_array[OFFSET_Y] = new Instruction("OffsetY", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.c_a2D[0].r.offset_y; // << 16) / InstructionManager.texture.c_a2D[0].r.height;
      }
    };

    instruction_array[SET_OFFSET_X] = new Instruction("SetOffsetX", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        InstructionManager.texture.c_a2D[0].r.offset_x = InstructionManager.texture.program.getArgument() & 0xFFFF; // InstructionManager.texture.c_a2D[0].r.width) >>> 16;
      }
    };

    instruction_array[SET_OFFSET_Y] = new Instruction("SetOffsetY", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        InstructionManager.texture.c_a2D[0].r.offset_y = InstructionManager.texture.program.getArgument() & 0xFFFF; // InstructionManager.texture.c_a2D[0].r.height) >>> 16;
      }
    };

    instruction_array[N] = new Instruction("N", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.n;
      }
    };

    instruction_array[E] = new Instruction("E", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.e;
      }
    };

    instruction_array[S] = new Instruction("S", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.s;
      }
    };

    instruction_array[W] = new Instruction("W", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return InstructionManager.texture.w;
      }
    };

    instruction_array[TRUE] = new Instruction("True", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return -1;
      }
    };

    instruction_array[FALSE] = new Instruction("False", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return 0;
      }
    };

    instruction_array[SET_HFLIP] = new Instruction("SetHFlip", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int b = InstructionManager.texture.program.getArgument();
        t.hflip = (b != 0);
      }
    };

    instruction_array[SET_VFLIP] = new Instruction("SetVFlip", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int b = InstructionManager.texture.program.getArgument();
        t.vflip = (b != 0);
      }
    };

    instruction_array[SET_HEIGHTMAP] = new Instruction("SetHeightMap", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int b = InstructionManager.texture.program.getArgument();
        t.height_map = (b != 0);
      }
    };

    instruction_array[SET_XOR_COLOUR] = new Instruction("SetXORColour", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int a = InstructionManager.texture.program.getArgument();
        t.xor_colour = a;
      }
    };

    instruction_array[SET_SPEED] = new Instruction("SetSpeed", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int a = InstructionManager.texture.program.getArgument();
        t.speed = a;
      }
    };

    instruction_array[SET_OFFSETS] = new Instruction("SetOffsets", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int ox = InstructionManager.texture.program.getArgument();
        int oy = InstructionManager.texture.program.getArgument();
        t.setOffsets(ox, oy); // won't work...
      }
    };

    instruction_array[BUFFER_0] = new Instruction("Buffer0", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return 0;
      }
    };

    instruction_array[BUFFER_1] = new Instruction("Buffer1", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return 1;
      }
    };

    instruction_array[BUFFER_2] = new Instruction("Buffer2", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return 2;
      }
    };

    instruction_array[BUFFER_3] = new Instruction("Buffer3", Instruction.FUNCTION, 0) {
      public int executeFunction() {
        return 3;
      }
    };

    instruction_array[PRESERVE] = new Instruction("Preserve", Instruction.FUNCTION, 1) {
      public int executeFunction() {
        return InstructionManager.texture.program.getArgument();
      }
    };

    instruction_array[RISE_UP_BUFFER_2D] = new Instruction("RiseUpBuffer2D", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        int bn = InstructionManager.texture.program.getArgument();
        int rise_by = InstructionManager.texture.program.getArgument();

        riseUpBuffer2D(bn, rise_by);
      }
    };

    instruction_array[HFLIP_2D] = new Instruction("HFlip2D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        hflipBuffer2D(0, 0);
      }
    };

    instruction_array[HFLIP_BUFFER_2D] = new Instruction("HFlipBuffer2D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        int bn = InstructionManager.texture.program.getArgument();

        hflipBuffer2D(bn, 0);
      }
    };

    instruction_array[VFLIP_2D] = new Instruction("VFlip2D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        vflipBuffer2D(0, 0);
      }
    };

    instruction_array[VFLIP_BUFFER_2D] = new Instruction("VFlipBuffer2D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        int bn = InstructionManager.texture.program.getArgument();

        vflipBuffer2D(bn, 0);
      }
    };

    instruction_array[HMIRROR_2D] = new Instruction("HMirror2D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        int ct = InstructionManager.texture.program.getArgument();
        hmirrorBuffer2D(0, 0, ct);
      }
    };

    instruction_array[HMIRROR_BUFFER_2D] = new Instruction("HMirrorBuffer2D", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        int bn = InstructionManager.texture.program.getArgument();
        int ct = InstructionManager.texture.program.getArgument();

        hmirrorBuffer2D(bn, 0, ct);
      }
    };

    instruction_array[VMIRROR_2D] = new Instruction("VMirror2D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        int ct = InstructionManager.texture.program.getArgument();

        vmirrorBuffer2D(0, 0, ct);
      }
    };

    instruction_array[VMIRROR_BUFFER_2D] = new Instruction("VMirrorBuffer2D", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        int bn = InstructionManager.texture.program.getArgument();
        int ct = InstructionManager.texture.program.getArgument();

        vmirrorBuffer2D(bn, 0, ct);
      }
    };

    instruction_array[ROTATE_ONTO_BUFFER_2D] = new Instruction("RotateOnto2D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        int ct = InstructionManager.texture.program.getArgument();
        rotateOntoBuffer2D(0, 0, ct);
      }
    };

    instruction_array[ROTATE_ONTO_2D] = new Instruction("RotateOntoBuffer2D", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        int bn = InstructionManager.texture.program.getArgument();
        int ct = InstructionManager.texture.program.getArgument();
        rotateOntoBuffer2D(bn, 0, ct);
      }
    };

    instruction_array[PARTICLE] = new Instruction("Particle", Instruction.COMMAND, 8) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int x = t.program.getArgument();
        int y = t.program.getArgument();
        int dx = t.program.getArgument();
        int dy = t.program.getArgument();
        int sx = t.program.getArgument();
        int sy = t.program.getArgument();
        int intensity = t.program.getArgument();
        int max = t.program.getArgument();
        Particle p = new Particle(x, y, dx, dy, sx, sy, intensity, max);
        t.particle_manager.add(p);
      }
    };

    instruction_array[PARTICLE_FACTORY] = new Instruction("ParticleFactory", Instruction.COMMAND, 5) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int number = t.program.getArgument();
        int speed = t.program.getArgument();
        int radius = t.program.getArgument();
        int intensity = t.program.getArgument();
        int max = t.program.getArgument();
        for (int i = 0; i < number; i++) {
          int x = t.rnd.nextInt(0x10000);
          int y = t.rnd.nextInt(0x10000);
          int dx = t.rnd.nextInt(speed) - t.rnd.nextInt(speed);
          int dy = t.rnd.nextInt(speed) - t.rnd.nextInt(speed);

          Particle p = new Particle(x, y, dx, dy, radius, radius, intensity, max);
          t.particle_manager.add(p);
        }
      }
    };

    instruction_array[PROCESS_2D] = new Instruction("Process2D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        int temp_ip = InstructionManager.texture.program.instruction_pointer;

        processBuffer2D(0, temp_ip);
      }
    };

    instruction_array[PROCESS_2D_QUICK] = new Instruction("Process2DQuick", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        int temp_ip = InstructionManager.texture.program.instruction_pointer;

        processBuffer2DQuick(0, temp_ip);
      }
    };

    instruction_array[PROCESS_1D] = new Instruction("Process1D", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        int temp_ip = InstructionManager.texture.program.instruction_pointer;

        processBuffer1D(0, temp_ip);
      }
    };

    instruction_array[PROCESS_1D_QUICK] = new Instruction("Process1DQuick", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        int temp_ip = InstructionManager.texture.program.instruction_pointer;

        processBuffer1DQuick(0, temp_ip);
      }
    };

    instruction_array[PROCESS_BUFFER_2D] = new Instruction("ProcessBuffer2D", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        int bn = InstructionManager.texture.program.getArgument();
        int temp_ip = InstructionManager.texture.program.instruction_pointer;

        processBuffer2D(bn, temp_ip);
      }
    };

    instruction_array[PROCESS_BUFFER_2D_QUICK] = new Instruction("ProcessBuffer2DQuick", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        int bn = InstructionManager.texture.program.getArgument();
        int temp_ip = InstructionManager.texture.program.instruction_pointer;

        processBuffer2DQuick(bn, temp_ip);
      }
    };

    // needs to check w and h...
      instruction_array[MERGE_N_BUFFERS_SHIFTED_2D] = new Instruction("MergeNBuffersShifted2D", Instruction.COMMAND, 0) {//?!?!?!
  public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int width = t.width;
        int height = t.height;
        //int act_x;
        //int act_y;
        int sum;
        int temp_n = t.program.getArgument();

        //Log.log("temp_n:" + temp_n);

        int temp_bn[] = new int[temp_n];
        int temp_dx[] = new int[temp_n];
        int temp_dy[] = new int[temp_n];

        for (int k = 0; k < temp_n; k++) {
          temp_bn[k] = t.program.getArgument();
          temp_dx[k] = (t.program.getArgument() * width) >>> 16;
          temp_dy[k] = (t.program.getArgument() * height) >>> 16;

          // sanity check...
          if (temp_dx[k] >= width) {
            temp_dx[k] = 0;
          }

          if (temp_dy[k] >= height) {
            temp_dy[k] = 0;
          }

          if (temp_dx[k] <= 0) {
            temp_dx[k] = 0;
          }

          if (temp_dy[k] <= 0) {
            temp_dy[k] = 0;
          }
        }

        int temp_combination_type = t.program.getArgument();
        int i_sum = (temp_combination_type == _COMBINATION_MULTIPLY) ? 0xFFFF : 0;

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            sum = i_sum;
            for (int k = 0; k < temp_n; k++) {
              switch (temp_combination_type) {
                case _COMBINATION_ADD :
                case _COMBINATION_AVERAGE :
                  sum = (sum + t.c_a2D[temp_bn[k]].r.v[temp_dx[k]][temp_dy[k]]); /// combination?
                  break;

                  //case _COMBINATION_AVERAGE:
                  //sum = (sum + t.c_a2D[temp_bn[k]].r.v[temp_dx[k]][temp_dy[k]]) >> 1; /// combination?
                  // break;

                case _COMBINATION_MULTIPLY :
                  sum = (sum * t.c_a2D[temp_bn[k]].r.v[temp_dx[k]][temp_dy[k]]) >>> 16; /// combination?
                  break;

                case _COMBINATION_MAXIMISE :
                  //Log.log("Xtemp_bn[k]:" + temp_bn[k]);
                  //Log.log("temp_dx[k]:" + temp_dx[k]);
                  //Log.log("temp_dy[k]:" + temp_dy[k]);
                  int tmp = t.c_a2D[temp_bn[k]].r.v[temp_dx[k]][temp_dy[k]];
                  sum = (tmp > sum) ? tmp : sum; /// combination?
                  break;
              }

              if (++temp_dx[k] >= width) {
                temp_dx[k] -= width;
              }
            }

            //if (temp_combination_type == _COMBINATION_ADD) {
            //sum &= 0xFFFF;
            //}

            //	if (temp_combination_type == _COMBINATION_AVERAGE) {
            //  sum /= temp_n;
            //}

            t.c_a2D[0].r.v[x][y] = sum;
          }

          for (int k = 0; k < temp_n; k++) {
            if (++temp_dy[k] >= height) {
              temp_dy[k] -= height;
            }
          }
        }
      }
    };

    instruction_array[MAKE_COLOUR_MAP_RGB] = new Instruction("MakeColourMapRGB", Instruction.COMMAND, 3) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        int ip_1 = t.program.instruction_pointer;
        for (int x = 0x100; --x >= 0;) {
          t.x = x << 8;
          t.program.instruction_pointer = ip_1;
          int c_red = (t.program.getArgument() >>> 8) & 0xFF;
          int c_green = (t.program.getArgument() >>> 8) & 0xFF;
          int c_blue = (t.program.getArgument() >>> 8) & 0xFF;

          t.main_colour_map.colour[x] = (c_red << 16) | (c_green << 8) | c_blue;
        }
      }
    };

    instruction_array[MAKE_COLOUR_MAP_HSB] = new Instruction("MakeColourMapHSB", Instruction.COMMAND, 3) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        int ip_1 = t.program.instruction_pointer;
        for (int x = 0x100; --x >= 0;) {
          t.x = x << 8;
          t.program.instruction_pointer = ip_1;
          float c_h = t.program.getArgument() / 65536.0f;
          float c_s = t.program.getArgument() / 65536.0f;
          float c_v = t.program.getArgument() / 65536.0f;

          InstructionManager.texture.main_colour_map.colour[x] = Color.HSBtoRGB(c_h, c_s, c_v);

        }
      }
    };

    instruction_array[RELIEF_MAP_2D] = new Instruction("ReliefMap2D", Instruction.COMMAND, 3) {};

    instruction_array[RIDGE_FINDER_2D] = new Instruction("RidgeFinder2D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        CAProcess2D.ridgeFinderMoore(InstructionManager.texture.c_a2D[0]);
      }
    };

    instruction_array[BLUR_2D] = new Instruction("Blur2D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        CAProcess2D.blurUsingWeightedSumMoore(InstructionManager.texture.c_a2D[0]);
      }
    };

    instruction_array[RUG_2D_VN] = new Instruction("Rug2DVN", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        int increment = InstructionManager.texture.program.getArgument();
        CAProcess2D.rugVN(InstructionManager.texture.c_a2D[0], increment);
      }
    };

    instruction_array[RUG_2D_VN_SLOW] = new Instruction("Rug2DVNSlow", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        int increment = InstructionManager.texture.program.getArgument();
        int shift = InstructionManager.texture.program.getArgument();

        CAProcess2D.rugVNSlow(InstructionManager.texture.c_a2D[0], increment, shift);
      }
    };

    instruction_array[RUG_2D_MOORE] = new Instruction("Rug2DMoore", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        int increment = InstructionManager.texture.program.getArgument();
        CAProcess2D.rugVN(InstructionManager.texture.c_a2D[0], increment);
      }
    };

    instruction_array[WATER_ANIMATION_BUFFER] = new Instruction("WaterAnimationBuffer", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        int bn = InstructionManager.texture.program.getArgument();
        int type = InstructionManager.texture.program.getArgument();
        Water.animate(InstructionManager.texture.c_a2D[bn], type);
      }
    };

    instruction_array[WATER_REVERSIBLE_ANIMATION_BUFFER] = new Instruction("WaterReversibleAnimationBuffer", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int bn = InstructionManager.texture.program.getArgument();
        int type = InstructionManager.texture.program.getArgument();
        WaterReversible.animate(InstructionManager.texture.c_a2D[bn], type, t.speed);
      }
    };

    instruction_array[SCHRODINGER] = new Instruction("SchrodingerAnimationBuffer", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        int bn = InstructionManager.texture.program.getArgument();
        int type = InstructionManager.texture.program.getArgument();
        Schrodinger.animate(InstructionManager.texture.c_a2D[bn], type);
      }
    };

    // uses spatial division.
    // doesn't scale well at the moment - (a point-sensitive RNG function would help it to do so)...
    // needs code to deal with width != height != 2^n...
    // fast - but currently has rectangular artefacts...
    instruction_array[FRACTAL_LANSCAPE] = new Instruction("FractalLandscape", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        FractalLandscape.generate(InstructionManager.texture.c_a2D[0].r);
      }
    };

      instruction_array[MIX_2D] = //?
  new Instruction("Mix2D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        CAProcess2D.mixUsingWeightedSumMoore(InstructionManager.texture.c_a2D[0]);
      }
    };

      instruction_array[ANGULAR_2D] = //?
  new Instruction("Angular2D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        CAProcess2D.angularUsingWeightedSumMoore(InstructionManager.texture.c_a2D[0]);
      }
    };

    instruction_array[EQUALISE_2D] = new Instruction("Equalise2D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        t.c_a2D[0].r.equaliseFully();
        t.tfu_min = 0;
        t.tfu_max = 0xFFFF;
      }
    };

    instruction_array[SET_SEED] = new Instruction("SetSeed", Instruction.COMMAND, 1, Instruction.MUTATE_RANDOMLY) {
      public void executeInstruction() {
        int arg = InstructionManager.texture.program.getArgument();

        InstructionManager.texture.setSeed(arg);
      }
    };

    /*
     instruction_array[CONCERTINA_2D] = 
           new Instruction("Concertina2D", Instruction.COMMAND, 0) {
              public void executeInstruction()  {
                 int arg = InstructionManager.texture.program.getArgument();
                 InstructionManager.texture.c_a2D[0].r.concertina(arg);
              }
           };
     */

    /*
    instruction_array[SEED] =
          new Instruction("Seed", Instruction.COMMAND, 1) {
             public void executeInstruction()  {
             }
          };
    		*/

      instruction_array[INVERT] = // don't use this...
  new Instruction("Invert", Instruction.FUNCTION, 1) {
      public int executeFunction() {
        return InstructionManager.texture.program.getArgument() ^ 0xFFFF;
      }
    };

      instruction_array[NO_INVERT] = // don't use this...
  new Instruction("NoInvert", Instruction.FUNCTION, 1) {
      public int executeFunction() {
        return InstructionManager.texture.program.getArgument();
      }
    };

      instruction_array[MASK] = // don't use this...
  new Instruction("Mask", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        return InstructionManager.texture.program.getArgument() & InstructionManager.texture.program.getArgument();
      }
    };

      instruction_array[SHIFT_LEFT] = // nasty unbounded operator...
  new Instruction("ShiftLeft", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        return InstructionManager.texture.program.getArgument() << InstructionManager.texture.program.getArgument();
      }
    };

      instruction_array[SHIFT_RIGHT] = // UNsigned operator...
  new Instruction("ShiftRight", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        return InstructionManager.texture.program.getArgument() >>> InstructionManager.texture.program.getArgument();
      }
    };

      instruction_array[AVERAGE] = // don't use this...
  new Instruction("Average", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        return (arg1 + arg2) >>> 1;
      }
    };

      instruction_array[RAW_ADD] = // don't use this...
  new Instruction("Add", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        return arg1 + arg2;
      }
    };

      instruction_array[RAW_SUBTRACT] = // don't use this...
  new Instruction("Subtract", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        return arg1 - arg2;
      }
    };

      instruction_array[RAW_MULTIPLY] = // don't use this...
  new Instruction("RawMultiply", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        return arg1 * arg2;
      }
    };

      instruction_array[RAW_DIVIDE] = // don't use this...
  new Instruction("RawDivide", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        return (arg2 == 0) ? 0 : arg1 / arg2;
      }
    };

      instruction_array[RAW_DIVIDE] = // don't use this...
  new Instruction("Xor", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        return arg1 ^ arg2;
      }
    };

    instruction_array[ADD_CEILING] = new Instruction("AddCeiling", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();
        int res = arg1 + arg2;
        return (res > 0xFFFF) ? 0xFFFF : res;
      }
    };

    instruction_array[SUBTRACT_FLOOR] = new Instruction("SubtractFloor", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();
        int res = arg1 - arg2;
        return (res < 0) ? 0 : res;
      }
    };

    instruction_array[ADD_MIRROR] = new Instruction("AddMirror", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();
        int res = arg1 + arg2;
        return (res > 0xFFFF) ? 0x1FFF - res : res;
      }
    };

    instruction_array[SUBTRACT_MIRROR] = new Instruction("SubtractMirror", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();
        int res = arg1 - arg2;
        return (res < 0) ? -res : res;
      }
    };

    instruction_array[ADD_CYCLIC] = new Instruction("AddCyclic", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();
        return (arg1 + arg2) & 0xFFFF;
      }
    };

    instruction_array[SUBTRACT_CYCLIC] = new Instruction("SubtractCyclic", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();
        return (arg1 - arg2) & 0xFFFF;
      }
    };

    instruction_array[MINIMUM] = new Instruction("Minimum", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();
        return (arg1 < arg2) ? arg1 : arg2;
      }
    };

    instruction_array[MAXIMUM] = new Instruction("Maximum", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();
        return (arg1 > arg2) ? arg1 : arg2;
      }
    };

    //!!!
    instruction_array[SQUARE] = new Instruction("Square", Instruction.FUNCTION, 3) {
      public int executeFunction() {
        int intensity = InstructionManager.texture.program.getArgument();
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        return (((arg1 * arg2 << 1) & 0x10000) == 0) ? intensity : 0;
      }
    };

    instruction_array[SAWTOOTH] = new Instruction("Sawtooth", Instruction.FUNCTION, 3) {
      public int executeFunction() {
        int intensity = InstructionManager.texture.program.getArgument();
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        int temp = (arg1 * arg2 << 1) % (intensity << 1);

        return (temp >= intensity) ? (intensity << 1) - temp : temp;
      }
    };

    instruction_array[SAWTOOTH1] = new Instruction("Sawtooth1", Instruction.FUNCTION, 3) {
      public int executeFunction() {
        int intensity = InstructionManager.texture.program.getArgument();
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        int temp = ((arg1 + 0x4000) * arg2 << 1) % (intensity << 1);

        return (temp >= intensity) ? (intensity << 1) - temp : temp;
      }
    };

    instruction_array[SAWTOOTH2] = new Instruction("Sawtooth2", Instruction.FUNCTION, 3) {
      public int executeFunction() {
        int intensity = InstructionManager.texture.program.getArgument();
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        int temp = ((arg1 + 0x8000) * arg2 << 1) % (intensity << 1);

        return (temp >= intensity) ? (intensity << 1) - temp : temp;
      }
    };

    instruction_array[SAWTOOTH3] = new Instruction("Sawtooth3", Instruction.FUNCTION, 3) {
      public int executeFunction() {
        int intensity = InstructionManager.texture.program.getArgument();
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        int temp = ((arg1 + 0xC000) * arg2 << 1) % (intensity << 1);

        return (temp >= intensity) ? (intensity << 1) - temp : temp;
      }
    };

    // instruction_array[SAWTOOTH3] =

    /*
       new Instruction("Chequerboard2D", Instruction.FUNCTION, 3) {
               
                  
          public int executeFunction() {
                  
             int intensity = InstructionManager.texture.program.getArgument();
             int arg1      = InstructionManager.texture.program.getArgument();
             int arg2      = InstructionManager.texture.program.getArgument();
          
             int temp = ((arg1 + 0xC000) * arg2 << 1) % (intensity << 1);
          
             return (temp >= intensity) ? (intensity << 1) - temp : temp;
          }
       };
    */

    /*
    instruction_array[HALF_SAW_TOOTH] =
       new Instruction("HalfSawTooth", Instruction.FUNCTION, 2) {
          public int executeFunction() {
             int arg1 = InstructionManager.texture.program.getArgument();
             int arg2 = InstructionManager.texture.program.getArgument();
          
             int temp = (arg1 * arg2) & 0x1ffff;
             if (temp >= 0x10000) {
                temp = 0x1FFFF - temp;
             }
          
             return temp;
          }
       };
    */

    instruction_array[CIRCLE] = new Instruction("Circle", Instruction.FUNCTION, 1) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument() - 0x8000;

        // return (int)(SquareRoot.sqrt((0x8000 * 0x8000) - (arg1 * arg1)));
        return ((int) (Math.sqrt((0x8000 * 0x8000) - (arg1 * arg1)))) & 0xFFFF;
      }
    };

    instruction_array[PARABOLA] = new Instruction("Parabola", Instruction.FUNCTION, 1) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();

        return ((arg1 * arg1) - (arg1 << 16)) >> 14;
      }
    };

    instruction_array[SIN] = new Instruction("Sin", Instruction.FUNCTION, 3) {
      public int executeFunction() {
        int intensity = InstructionManager.texture.program.getArgument() >> 1;
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        return intensity + (int) (intensity * Math.sin(Math.PI * arg1 * arg2 / 32768.0f));
      }
    };

    instruction_array[ISIN] = new Instruction("ISin", Instruction.FUNCTION, 3) {
      public int executeFunction() {
        int intensity = InstructionManager.texture.program.getArgument() >> 1;
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        return intensity - (int) (intensity * Math.sin(Math.PI * arg1 * arg2 / 32768.0f));
      }
    };

    /*
    instruction_array[SIN_HALF] =
          new Instruction("SinHalf", Instruction.FUNCTION, 2) {
             public int executeFunction() {
                int arg1 = InstructionManager.texture.program.getArgument();
                int arg2 = InstructionManager.texture.program.getArgument();
             
                return 32768 + (int)(32767 * Math.sin(Math.PI * arg1 * arg2 / 65536));
             }
          };
    */

      instruction_array[BELL] = // Bell(a) = -cos(a)
  new Instruction("Bell", Instruction.FUNCTION, 3) {
      public int executeFunction() {
        int intensity = InstructionManager.texture.program.getArgument() >> 1;
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        return intensity - (int) (intensity * Math.cos(Math.PI * arg1 * arg2 / 32768.0f));
      }
    };

      instruction_array[IBELL] = // IBell(a) = cos(a)
  new Instruction("IBell", Instruction.FUNCTION, 3) {
      public int executeFunction() {
        int intensity = InstructionManager.texture.program.getArgument() >> 1;
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        return intensity + (int) (intensity * Math.cos(Math.PI * arg1 * arg2 / 32768.0f));
      }
    };

    /*
    instruction_array[BELL_HALF] =
          new Instruction("BellHalf", Instruction.FUNCTION, 2) {
             public int executeFunction() {
                int arg1 = InstructionManager.texture.program.getArgument();
                int arg2 = InstructionManager.texture.program.getArgument();
             
                return 32768 - (int)(32767 * Math.cos(Math.PI * arg1 * arg2 / 65536));
             }
          };
    		*/

    instruction_array[MULTIPLY] = new Instruction("Multiply", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        return (arg1 * arg2) >>> 16;
      }
    };

    instruction_array[DIVIDE] = new Instruction("Divide", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        int arg1 = InstructionManager.texture.program.getArgument();
        int arg2 = InstructionManager.texture.program.getArgument();

        if (arg2 == 0) {
          return 0;
        }

        int res = arg1 / arg2;

        if (res > 0xFFFF) {
          return 0xFFFF;
        }

        if (res < 0) {
          return 0;
        }

        return res;
      }
    };

    instruction_array[SQUARE_ROOT] = new Instruction("SquareRoot", Instruction.FUNCTION, 1) {
      public int executeFunction() {
        int arg = InstructionManager.texture.program.getArgument();

        return (arg < 0) ? 0 : SquareRoot.sqrt(arg);
      }
    };

    instruction_array[INDEX_COLOUR_QUICK] = new Instruction("IndexColourQuick", Instruction.FUNCTION, 1) {
      public int executeFunction() {
        int _x = InstructionManager.texture.program.getArgument() & 0xFFFF;

        return InstructionManager.texture.main_colour_map.colour[_x >>> 8];
      }
    };

      instruction_array[INDEX_2D] = // use anti-aliasing...
  new Instruction("Index2D", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        Texture t = InstructionManager.texture;

        int _x = t.program.getArgument() & 0xFFFF;
        int _y = t.program.getArgument() & 0xFFFF;

        return t.c_a2D[0].r.getPointAA(_x, _y);
      }
    };

      instruction_array[INDEX_BUFFER_2D] = // use anti-aliasing...
  new Instruction("IndexBuffer2D", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        Texture t = InstructionManager.texture;

        int _bn = t.program.getArgument();
        int _x = t.program.getArgument() & 0xFFFF;
        int _y = t.program.getArgument() & 0xFFFF;

        return t.c_a2D[_bn].r.getPointAA(_x, _y);
      }
    };

    instruction_array[INDEX_2D_QUICK] = new Instruction("Index2DQuick", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        Texture t = InstructionManager.texture;
        SimpleArray2D sa = t.c_a2D[0].r;

        int _x = t.program.getArgument() & 0xFFFF;
        int _y = t.program.getArgument() & 0xFFFF;

        // Log.log("UH:" + sa.v[(_x * sa.width) >>> 16][(_y * sa.height) >>> 16]);
        // Log.log("X:" + ((_x * sa.width) >>> 16) + ",Y:" + ((_y * sa.height) >>> 16) + " = " + (sa.v[(_x * sa.width) >>> 16][(_y * sa.height) >>> 16]));
        // Log.log("UH:" + sa.v[(_x * sa.width) >>> 16][(_y * sa.height) >>> 16]);

        return sa.v[((_x * sa.width) >>> 16)][((_y * sa.height) >>> 16)];
      }
    };

    instruction_array[INDEX_BUFFER_2D_QUICK] = new Instruction("IndexBuffer2DQuick", Instruction.FUNCTION, 3) {
      public int executeFunction() {
        Texture t = InstructionManager.texture;

        int _bn = t.program.getArgument();
        int _x = t.program.getArgument() & 0xFFFF;
        int _y = t.program.getArgument() & 0xFFFF;

        SimpleArray2D sa = t.c_a2D[_bn].r;

        return sa.v[(_x * sa.width) >>> 16][(_y * sa.height) >>> 16];
      }
    };

    instruction_array[INDEX_1D_QUICK] = new Instruction("Index1DQuick", Instruction.FUNCTION, 1) {
      public int executeFunction() {
        Texture t = InstructionManager.texture;
        int[] a = t.c_a1D[0].r.v;
        int _x = ((t.program.getArgument() & 0xFFFF) * a.length) >>> 16;

        return a[_x];
      }
    };

    instruction_array[RANDOM] = new Instruction("Random", Instruction.FUNCTION, 1) {
      public int executeFunction() {
        Texture t = InstructionManager.texture;

        int arg = t.program.getArgument();

        if (arg < 1) {
          return t.rnd.nextInt();
        } else {
          return t.rnd.nextInt(arg);
        }
      }
    };

    instruction_array[NOP] = new Instruction("Nop", Instruction.COMMAND, 0) {};

    instruction_array[SETUP] = new Instruction("Setup", Instruction.COMMAND, 0) {};

    instruction_array[PALETTE] = new Instruction("Palette", Instruction.COMMAND, 0) {};

    instruction_array[ANIMATE] = new Instruction("Animate", Instruction.COMMAND, 0) {};

    instruction_array[SETTINGS] = new Instruction("Settings", Instruction.COMMAND, 0) {};

    instruction_array[GET_COLOUR] = new Instruction("GetColour", Instruction.COMMAND, 0) {};

    instruction_array[GET_ELEVATION] = new Instruction("GetElevation", Instruction.COMMAND, 0) {};

    instruction_array[REACTION_DIFFUSION_1] = new Instruction("ReactionDiffusion1", Instruction.COMMAND, 6) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        int bn = t.program.getArgument();

        ReactionDiffusion.f = t.program.getArgument();
        ReactionDiffusion.k = t.program.getArgument();

        ReactionDiffusion.mul_u = t.program.getArgument();
        ReactionDiffusion.mul_v = t.program.getArgument();

        ReactionDiffusion.scale_u = t.program.getArgument();
        ReactionDiffusion.scale_v = t.program.getArgument();

        ReactionDiffusion.animateRDVN(t, bn);
      }
    };

    instruction_array[FRACTAL_DRAINAGE_1] = new Instruction("FractalDrainage1", Instruction.COMMAND, 8) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        int bn = t.program.getArgument();

        FractalDrainage.add_rate = t.program.getArgument();
        FractalDrainage.seep_rate = t.program.getArgument();

        FractalDrainage.flow_rate = t.program.getArgument();
        FractalDrainage.log_resistance = t.program.getArgument();
        FractalDrainage.ground_rising = t.program.getArgument();

        FractalDrainage.upper_threshold = t.program.getArgument();
        FractalDrainage.lower_threshold = t.program.getArgument();
        FractalDrainage.animate(t, bn);
      }
    };

    instruction_array[LIQUID_CRYSTAL_1] = new Instruction("LiquidCrystal1", Instruction.COMMAND, 8) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        int bn = t.program.getArgument();
        int s = t.program.getArgument();
        int n = t.program.getArgument();

        LiquidCrystal.animate(t, bn, s, n);
      }
    };

    instruction_array[VORONOI] = new Instruction("Voronoi", Instruction.COMMAND, 8) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;
        int bn = t.program.getArgument();

        Voronoi.animate(t, bn);
      }
    };

    instruction_array[DIFFUSION_LIMITED_AGGREGATION_SETUP_1] = new Instruction("DiffusionLimitedAggregationSetup1", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        // int bn = t.program.getArgument();

        DiffusionLimitedAggregation.density = t.program.getArgument();
        DiffusionLimitedAggregation.number_of_seeds = t.program.getArgument();

        DiffusionLimitedAggregation.diffusionLimitedAggregationSetup(t);
        t.colour_map_type = Texture.COLOUR_MAP_MARGOLUS;
      }
    };

    instruction_array[DIFFUSION_LIMITED_AGGREGATION_1] = new Instruction("DiffusionLimitedAggregation1", Instruction.COMMAND, 2) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        // int bn = t.program.getArgument();

        DiffusionLimitedAggregation.threshold = t.program.getArgument();
        DiffusionLimitedAggregation.initial_value = t.program.getArgument();

        DiffusionLimitedAggregation.animate(t);
      }
    };

    instruction_array[CLEAR_1D] = new Instruction("Clear1D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        InstructionManager.texture.c_a1D[0].clear();
      }
    };

    instruction_array[CLEAR_2D] = new Instruction("Clear2D", Instruction.COMMAND, 0) {
      public void executeInstruction() {
        InstructionManager.texture.c_a2D[0].clear();
      }
    };

    instruction_array[OR] = new Instruction("Or", Instruction.FUNCTION, 2) {
      public int executeFunction() {
        Texture t = InstructionManager.texture;

        int arg1 = t.program.getArgument();
        int arg2 = t.program.getArgument();

        return arg1 | arg2;
      }
    };

    instruction_array[HEIGHT_MAP_SCALE] = new Instruction("HeightMapScale", Instruction.COMMAND, 1) {
      public void executeInstruction() {
        Texture t = InstructionManager.texture;

        t.height_map_scale = t.program.getArgument();
      }
    };
  }

  /*
     instruction_array[CEILING] = // don't use this...
           new Instruction("Ceiling", Instruction.FUNCTION, 1) {
              public int executeFunction() {
                 int arg = InstructionManager.texture.program.getArgument();
                 return (arg <= 0xFFFF) ? arg : 0xFFFF;
              }
           };
  
  
     instruction_array[FLOOR] = // don't use this...
           new Instruction("Floor", Instruction.FUNCTION, 2) {
              public int executeFunction() {
                 int arg = InstructionManager.texture.program.getArgument();
              
                 return (arg < 0) ? 0 : arg;
              }
           };
  */

  public static int getInstructionNumber(String s) {
    String s2;
    for (int i = 0; i < MAX_NUMBER; i++) {
      if (instruction_array[i] != null) {
        s2 = instruction_array[i].name;
        if (s2 != null) {
          // Log.log("S:" + s + " - " + s2);
          if (s.equals(s2)) {
            return i;
          }
        }
      }
    }

    return -1;
  }

  // Fast...
  static void hflipBuffer2D(int bn_in, int bn_out) {
    SimpleArray2D sa_in = InstructionManager.texture.c_a2D[bn_in].r;
    SimpleArray2D sa_out = InstructionManager.texture.c_a2D[bn_out].r;
    // int[] temp_array = new int[sa_in.height];

    for (int x = 0; x < (sa_in.width >> 1); x++) {

      int[] ta2 = sa_in.v[x];
      sa_out.v[x] = sa_in.v[sa_in.width - 1 - x];
      sa_out.v[sa_out.width - 1 - x] = ta2;

      // System.arraycopy(sa_out.v[x], 0, temp_array, 0, sa_in.height);
      //for (int y = 0; y < sa_in.height; y++) {
      /*
         int temp = sa_in.v[x][y];
         sa_out.v[x                   ][y] = sa_in.v[sa_in.width - 1 - x][y];
         sa_out.v[sa_out.width - 1 - x][y] = temp;
      }
      */
    }
  }

  // *Much* slower than hflipBuffer2D...
  static void vflipBuffer2D(int bn_in, int bn_out) {
    SimpleArray2D sa_in = InstructionManager.texture.c_a2D[bn_in].r;
    SimpleArray2D sa_out = InstructionManager.texture.c_a2D[bn_out].r;

    for (int x = 0; x < sa_in.width; x++) {
      for (int y = 0; y < (sa_in.height >> 1); y++) {
        int temp = sa_in.v[x][y];
        sa_out.v[x][y] = sa_in.v[x][sa_in.height - 1 - y];
        sa_out.v[x][sa_out.height - 1 - y] = temp;
      }
    }
  }

  static void vmirrorBuffer2D(int bn_in, int bn_out, int temp_combination_type) {
    SimpleArray2D sa_in = InstructionManager.texture.c_a2D[bn_in].r;
    SimpleArray2D sa_out = InstructionManager.texture.c_a2D[bn_out].r;

    for (int x = 0; x < sa_in.width; x++) {
      for (int y = 0; y < (sa_in.height >> 1); y++) {
        int temp1 = sa_in.v[x][y];
        int temp2 = sa_in.v[x][sa_out.height - 1 - y];
        int sum = 0;

        switch (temp_combination_type) {
          case _COMBINATION_ADD :
          case _COMBINATION_AVERAGE :
            sum = temp1 + temp2; /// combination?
            break;

            //case _COMBINATION_AVERAGE:
            //sum = (temp1 + temp2) >> 1; /// combination?
            //break;

          case _COMBINATION_MULTIPLY :
            sum = (temp1 * temp2) >>> 16; /// combination?
            break;

          case _COMBINATION_MAXIMISE :
            sum = (temp1 > temp2) ? temp1 : temp2; /// combination?
            break;

          case _COMBINATION_MINIMISE :
            sum = (temp1 < temp2) ? temp1 : temp2; /// combination?
            break;
        }

        sa_out.v[x][y] = sum;
        sa_out.v[x][sa_out.height - 1 - y] = sum;
      }
    }
  }

  static void hmirrorBuffer2D(int bn_in, int bn_out, int temp_combination_type) {
    SimpleArray2D sa_in = InstructionManager.texture.c_a2D[bn_in].r;
    SimpleArray2D sa_out = InstructionManager.texture.c_a2D[bn_out].r;

    for (int y = 0; y < sa_in.height; y++) {
      for (int x = 0; x < (sa_in.width >> 1); x++) {
        int temp1 = sa_in.v[x][y];
        int temp2 = sa_in.v[sa_out.width - 1 - x][y];
        int sum = 0;

        switch (temp_combination_type) {
          case _COMBINATION_ADD :
          case _COMBINATION_AVERAGE :
            sum = temp1 + temp2; /// combination?
            break;

            //case _COMBINATION_AVERAGE:
            //sum = (temp1 + temp2) >> 1; /// combination?
            //break;

          case _COMBINATION_MULTIPLY :
            sum = (temp1 * temp2) >>> 16; /// combination?
            break;

          case _COMBINATION_MAXIMISE :
            sum = (temp1 > temp2) ? temp1 : temp2; /// combination?
            break;

          case _COMBINATION_MINIMISE :
            sum = (temp1 < temp2) ? temp1 : temp2; /// combination?
            break;
        }

        sa_out.v[x][y] = sum;
        sa_out.v[sa_out.width - 1 - x][y] = sum;
      }
    }
  }

  static void rotateOntoBuffer2D(int bn_in, int bn_out, int temp_combination_type) {
    SimpleArray2D sa_in = InstructionManager.texture.c_a2D[bn_in].r;
    SimpleArray2D sa_out = InstructionManager.texture.c_a2D[bn_out].r;

    int wmo = sa_out.width - 1;
    int hmo = sa_out.height - 1;

    for (int y = 0; y <= hmo; y++) {
      for (int x = 0; x < (sa_in.width >> 1); x++) {
        int temp1 = sa_in.v[x][y];
        int temp2 = sa_in.v[wmo - x][hmo - y];
        int sum = 0;

        switch (temp_combination_type) {
          case _COMBINATION_ADD :
          case _COMBINATION_AVERAGE :
            sum = temp1 + temp2; /// combination?
            break;

          case _COMBINATION_MULTIPLY :
            sum = (temp1 * temp2) >>> 16; /// combination?
            break;

          case _COMBINATION_MAXIMISE :
            sum = (temp1 > temp2) ? temp1 : temp2; /// combination?
            break;

          case _COMBINATION_MINIMISE :
            sum = (temp1 < temp2) ? temp1 : temp2; /// combination?
            break;
        }

        sa_out.v[x][y] = sum;
        sa_out.v[wmo - x][hmo - y] = sum;
      }
    }
  }

  static void riseUpBuffer2D(int bn, int rise_by) {
    SimpleArray2D sa = InstructionManager.texture.c_a2D[bn].r;

    for (int x = 0; x < sa.width; x++) {
      for (int y = 0; y < sa.height; y++) {
        sa.v[x][y] = (sa.v[x][y] + rise_by) & 0xFFFF;
      }
    }
  }

  static void processBuffer2D(int bn, int temp_ip) {
    Texture t = InstructionManager.texture;
    SimpleArray2D sa = t.c_a2D[bn].r;

    int xm = 0x80000000 / sa.width;
    int ym = 0x80000000 / sa.height;

    for (int x = 0; x < sa.width; x++) {
      for (int y = 0; y < sa.height; y++) {
        // Log.log("before :" + t.c);
        t.c = sa.v[x][y];
        t.x = (x * xm) >> 15;
        t.y = (y * ym) >> 15;

        t.program.instruction_pointer = temp_ip;
        sa.v[x][y] = t.program.getArgument();
        // Log.log("After :" + sa.v[x][y]);
      }
    }
  }

  static void processBuffer2DQuick(int bn, int temp_ip) {
    Texture t = InstructionManager.texture;
    SimpleArray2D sa = t.c_a2D[bn].r;

    for (int x = 0; x < sa.width; x++) {
      for (int y = 0; y < sa.height; y++) {
        t.c = sa.v[x][y];

        t.program.instruction_pointer = temp_ip;
        sa.v[x][y] = t.program.getArgument();
      }
    }
  }

  static void processBuffer1DQuick(int bn, int temp_ip) {
    Texture t = InstructionManager.texture;
    SimpleArray1D sa = t.c_a1D[bn].r;

    for (int x = 0; x < sa.size; x++) {
      t.c = sa.v[x];

      t.program.instruction_pointer = temp_ip;
      sa.v[x] = t.program.getArgument();
    }
  }

  static void processBuffer1D(int bn, int temp_ip) {
    Texture t = InstructionManager.texture;
    SimpleArray1D sa = t.c_a1D[bn].r;

    int xm = 0x80000000 / sa.size;

    for (int x = 0; x < sa.size; x++) {
      t.c = sa.v[x];
      t.x = (x * xm) >> 15;

      t.program.instruction_pointer = temp_ip;
      sa.v[x] = t.program.getArgument();
    }
  }

  public static int getFlags(int in) {
    return instruction_array[in & 0xFFFF].flags;
  }

  public static int instructionNumber(int i) {
    return i | instruction_marker;
  }
}
