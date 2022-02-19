package com.texturegarden.instructions;

public interface InstructionNumbers {
  public final static int RETURN = 0;
  public final static int FREQUENCY_FRACTAL_NOISE_2D = 1;
  public final static int BANDWIDTH_LIMITED_FREQUENCY_NOISE_2D = 2;
  public final static int FREQUENCY_NOISE_2D = 3;
  public final static int FREQUENCY_NOISE_2D_QUICK = 4;
  public final static int INVERSE_FFT_2D = 5;
  public final static int SQUARE_ROOT_2D = 6; // OTT?
  public final static int FREQUENCY_NOISE_1D = 7;
  public final static int BUFFER_WIDTH = 8; // F
  public final static int BUFFER_HEIGHT = 9; // F
  public final static int BUFFER_DEPTH = 10; // F

  public final static int C = 11; // F  // C
  public final static int X = 12; // F 1D
  public final static int Y = 13; // F 2D
  public final static int Z = 14; // F 3D
  public final static int SAWTOOTH = 15; // F 1D // scaling issues...
  public final static int SQUARE = 16; // F 1D // scaling issues...

  public final static int AVERAGE = 17; // F 1D // scaling issues...
  public final static int DIVIDE = 18; // F
  public final static int ADD_CEILING = 19; // F
  public final static int SUBTRACT_FLOOR = 20; // F
  public final static int ADD_CYCLIC = 21; // F
  public final static int MINIMUM = 22; // F
  public final static int MAXIMUM = 23; // F
  public final static int SUBTRACT_CYCLIC = 24; // F
  public final static int RAW_MULTIPLY = 25; // F
  public final static int RAW_DIVIDE = 26; // F
  public final static int RAW_ADD = 27; // F
  public final static int RAW_SUBTRACT = 28; // F
  public final static int MULTIPLY = 29; // F

  public final static int SHIFT_LEFT = 30; // F
  public final static int SHIFT_RIGHT = 31; // F
  public final static int SQUARE_ROOT = 32; // F

  public final static int SET_SEED = 33;
  public final static int SET_PHASE = 34;
  public final static int SET_EXPECTED_SIZE = 35; // ?
  public final static int SMEAR_H = 36;
  public final static int SMEAR_V = 37;
  public final static int WAVES_H = 38;
  public final static int WAVES_V = 39;

  public final static int INVERSE_FFT_1D = 42;
  public final static int NEW_BUFFER_1D = 43;
  public final static int NEW_BUFFER_2D = 44; // X
  public final static int EQUALISE_SCALED_1D = 45;
  public final static int N = 46; // F 1D
  public final static int E = 47; // F 2D
  public final static int W = 48; // F 3D
  public final static int S = 49; // F 3D
  public final static int ZERO_R_1D = 50; // F 3D // Not yet implemented
  public final static int ZERO_I_1D = 51; // F 3D // Not yet implemented
  public final static int ZERO_R_2D = 52; // F 3D // Not yet implemented
  public final static int ZERO_I_2D = 53; // F 3D // Not yet implemented
  public final static int RANDOM = 54; // F
  public final static int IF_FUNCTION = 55; // F  // Not yet implemented
  public final static int IF = 56; // X  // Not yet implemented

  public final static int TIME = 57; // F
  public final static int SPEED = 58; // F

  public final static int FFT_1D = 59; //
  public final static int FFT_2D = 60; //
  public final static int RIDGE_FINDER_2D = 61; // Not yet implemented
  public final static int BLUR_2D = 62; //
  public final static int EQUALISE_2D = 63; //
  public final static int MIX_2D = 64; // !??
  public final static int ANGULAR_2D = 65; // !??
  //public final static int HALF_SAW_TOOTH = xx; // F 1D // scaling issues...

  public final static int NOP = 66; // NULL
  public final static int SETUP = 67; // 
  public final static int PALETTE = 68; // 
  public final static int ANIMATE = 69; // 
  public final static int GET_COLOUR = 70; // 
  public final static int GET_ELEVATION = 71; // 

  public final static int MAKE_COLOUR_MAP_RGB = 72; // 
  public final static int MAKE_COLOUR_MAP_HSB = 73; // 

  public final static int RETURN_COLOUR = 74; // currently redundant...!
  public final static int RETURN_ELEVATION = 75; //
  public final static int SET_OFFSET_X = 76; // 
  public final static int SET_OFFSET_Y = 77; // 

  public final static int INDEX_BUFFER_2D_QUICK = 78;

  public final static int OFFSET_X = 79; // F
  public final static int OFFSET_Y = 80; // F
  public final static int INDEX_2D = 81; //
  public final static int INDEX_2D_QUICK = 82; //
  public final static int INDEX_1D = 83; //
  public final static int INDEX_1D_QUICK = 84; //

  public final static int INDEX_BUFFER_2D = 85; //

  public final static int STORE_BUFFER_1D = 86;
  public final static int STORE_BUFFER_2D = 87;
  public final static int RETREIVE_BUFFER_1D = 88;
  public final static int RETREIVE_BUFFER_2D = 89;
  public final static int SWAP_BUFFER_1D = 90;
  public final static int SWAP_BUFFER_2D = 91;

  public final static int RISE_UP_BUFFER_2D = 92;

  public final static int BOUND = 93; // F
  public final static int INVERT = 94; // F

  public final static int SIN = 95; // F 1D // scaling
  // public final static int SIN_HALF = xx; // F 1D // scaling
  public final static int BELL = 97; // F 1D // scaling
  // public final static int BELL_HALF  = xx; // F 1D // scaling

  public final static int FLIP_2D_H = 99; // Not yet implemented
  public final static int FLIP_2D_V = 100; // Not yet implemented
  //public final static int FLIP_2D_180 = xx; // Not yet implemented

  public final static int HMIRROR_2D = 101; // Not yet implemented
  public final static int VMIRROR_2D = 102; // Not yet implemented

  public final static int HMIRROR_BUFFER_2D = 103; // Not yet implemented
  public final static int VMIRROR_BUFFER_2D = 104; // Not yet implemented

  public final static int TRANSLATE_1D = 105; // Not yet implemented
  public final static int TRANSLATE_2D = 106; // Not yet implemented

  public final static int ADD_MIRROR = 107; // F
  public final static int SUBTRACT_MIRROR = 108; // F

  public final static int RELIEF_MAP_2D = 108; // Not yet implemented

  public final static int MERGE_N_BUFFERS_SHIFTED_2D = 109;

  public final static int COMBINATION_MAXIMISE = 110; // F
  public final static int COMBINATION_ADD = 111; // F
  public final static int COMBINATION_MULTIPLY = 112; // F

  public final static int PROCESS_2D = 113;
  public final static int PROCESS_2D_QUICK = 114;
  public final static int PROCESS_BUFFER_2D = 115;
  public final static int PROCESS_BUFFER_2D_QUICK = 116;
  public final static int PROCESS_1D = 117;
  public final static int PROCESS_1D_QUICK = 118;

  public final static int PROCESS_2D_VN = 119; // X
  public final static int PROCESS_2D_MOORE = 120; // X

  public final static int FRACTAL_DRAINAGE_1 = 121; //
  public final static int REACTION_DIFFUSION_1 = 122; //
  public final static int DIFFUSION_LIMITED_AGGREGATION_1 = 123;
  public final static int DIFFUSION_LIMITED_AGGREGATION_SETUP_1 = 124; // F

  public final static int MASK = 125; // F
  public final static int OR = 126; // F

  public final static int CLEAR_1D = 127; //
  public final static int CLEAR_2D = 128; //

  public final static int HFLIP_2D = 129; //
  public final static int HFLIP_BUFFER_2D = 130; //
  public final static int VFLIP_2D = 131; //
  public final static int VFLIP_BUFFER_2D = 132; //
  public final static int INDEX_COLOUR_QUICK = 133; //
  public final static int BUFFER_0 = 134; // F
  public final static int BUFFER_1 = 135; // F
  public final static int BUFFER_2 = 136; // F
  public final static int BUFFER_3 = 137; // F
  public final static int PRESERVE = 138; // F
  public final static int ISIN = 139; // F
  public final static int IBELL = 140; // F
  public final static int SAWTOOTH1 = 141; // F
  public final static int SAWTOOTH2 = 142; // F
  public final static int SAWTOOTH3 = 143; // F
  public final static int NO_INVERT = 144; // F

  public final static int RUG_2D_MOORE = 145; //
  public final static int RUG_2D_VN = 146; //
  public final static int RUG_2D_VN_SLOW = 147; //

  public final static int SMEAR_H_QUICK = 148;
  public final static int SMEAR_V_QUICK = 149;
  public final static int WAVES_H_QUICK = 150;
  public final static int WAVES_V_QUICK = 151;

  public final static int DO_ANIMATION = 152; //
  public final static int AVERAGE_COMPONENTS = 153; //

  public final static int CIRCLE = 154; // F
  public final static int PARABOLA = 155; // F
  public final static int WATER_ANIMATION_BUFFER = 156;
  public final static int WATER_REVERSIBLE_ANIMATION_BUFFER = 157;
  public final static int SCHRODINGER = 158;
    
  public final static int TRUE = 159; // F
  public final static int FALSE = 160; // F
  public final static int SET_HFLIP = 161;
  public final static int SET_VFLIP = 162;
  public final static int SET_HEIGHTMAP = 163;
  public final static int SET_XOR_COLOUR = 164;
  public final static int SET_SPEED = 165;
  public final static int SET_OFFSETS = 166;
  public final static int ROTATE_ONTO_2D = 167;
  public final static int ROTATE_ONTO_BUFFER_2D = 168;
  public final static int LIQUID_CRYSTAL_1 = 169;
  public final static int VORONOI = 170;
  public final static int PARTICLE = 171;
  public final static int SETTINGS = 172; // things that can be done at "render" time...
  public final static int HEIGHT_MAP_SCALE = 173;
  public final static int PARTICLE_FACTORY = 174;
  public final static int FRACTAL_LANSCAPE = 175;
}
