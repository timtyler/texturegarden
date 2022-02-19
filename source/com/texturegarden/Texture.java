package com.texturegarden;

import com.texturegarden.arrays.ComplexArray1D;
import com.texturegarden.arrays.ComplexArray2D;
import com.texturegarden.effects.ca.DiffusionLimitedAggregation;
import com.texturegarden.effects.ca.FractalDrainage;
import com.texturegarden.effects.ca.IsingModel;
import com.texturegarden.effects.ca.ReactionDiffusion;
import com.texturegarden.effects.particle.ParticleManager;
import com.texturegarden.image.ImageWrapper;
import com.texturegarden.instructions.InstructionManager;
import com.texturegarden.maps.colour.ColourMap;
import com.texturegarden.maps.height.HeightMap2D;
import com.texturegarden.program.Program;
import com.texturegarden.program.ProgramProcessor;
import com.texturegarden.utilities.JUR;
import com.texturegarden.utilities.Utils;

public class Texture {
  public ParticleManager particle_manager = new ParticleManager();
  public int height_map_scale;

  final static int MAX_BUFFER_1D = 6;
  final static int MAX_BUFFER_2D = 4;

  public int width = 512; //1 << width_log2;
  public int height = 512; //1 << height_log2;
  public int depth = 512; //1 << height_log2;

  public int seed;

  public int xor_colour; // colour xor...? /eventually/ remove...?
  // int xor_height; // height xor...? /eventually/ remove...?

  public int initial_offset_x;
  public int initial_offset_y;

  public int time; // controls animation...
  public int speed; // controls animation speed... ( = 65535 / width) ;-)

  public JUR rnd = new JUR(); // main RNG for texture - treat with care - no size-dependant operations...
  public JUR rnd2 = new JUR(); //  secondary RNG for texture - can be used for size-dependant operations...

  public int generation = 0;
  public int direction = 1;

  public boolean hflip;
  public boolean vflip;
  public boolean height_map = true;

  public int colour; // used by instructions to return values...
  public int elevation; // used by instructions to return values...

  public int colour_map_type;
  final static int COLOUR_MAP_NORMAL = 0;
  public final static int COLOUR_MAP_MARGOLUS = 1;

  public int animation_frames_remaining = 0;

  public ImageWrapper texture_image;

  public ComplexArray1D[] c_a1D = new ComplexArray1D[MAX_BUFFER_1D];
  public ComplexArray2D[] c_a2D = new ComplexArray2D[MAX_BUFFER_2D];

  long start_time;
  long elapsed_time;
  long total_time;
  public int iterations;
  public Program program;

  public ColourMap main_colour_map;
  public ColourMap second_colour_map;

  public int iteration = 0;

  public int x, y, z;
  public int c;
  public int n, s, e, w;

  public boolean raining = false;
  public boolean clear = false;
  public int rain_intensity = 16;

  // Reaction-Diffusion
  public int red_value = 255;
  public int green_value = 255;
  public int blue_value = 255;

  // RD STUFF... - needs moving...
  public int tfu_max;
  public int tfv_max;
  public int tfu_min;
  public int tfv_min;

  public TG applet;

  public Texture(TG applet, int width, int height) {
    // make new image...

    this.width = width;
    this.height = height; // (1 << 8) - 0; // 16 * 5 * 7; // 13 * 32; // 512; // 5 * 7 * 8;
    this.applet = applet;

    c_a2D[0] = new ComplexArray2D(width, height);

    main_colour_map = new ColourMap();
    second_colour_map = new ColourMap();
    reset();
  }

  public void reset() {
    iterations = 0;
    height_map_scale = 0;
    animation_frames_remaining = 0; // ?
    particle_manager.particles.setSize(0);

    tfu_max = 0xFFFF;
    tfv_max = 0xFFFF;
    tfu_min = 0x0000;
    tfv_min = 0x0000;

    colour_map_type = COLOUR_MAP_NORMAL;

    // default values for per-texture settings...
    height_map = true;
    hflip = false;
    vflip = false;
    xor_colour = 0;
    speed = 512;
    setOffsets(0, 0);
    time = 0;
    rnd.setSeed(1);
  }

  public void randomise() {
    if (TG.ising_model) {
      IsingModel.mess(this);
    } else {
      c_a2D[0].r.randomise();
      c_a2D[0].i.clear();
    }
  }

  public void clear() {
    c_a2D[0].r.clear();
    c_a2D[0].i.clear();
  }

  // main method - 
  public void makeImageFromScratch() {
    restart();
    makeImage();
  }

  public void restart() {
    texture_image = null;
    // animating = false;
    makeImage();
  }

  public void makeImage() {
    // make new image...
    //if (clear) {
    //c_a2D[0].r.clear();
    //c_a2D[0].i.clear();
    //clear = false;
    //}
    //else
    //{
    program = new Program();
    program.init();
  }

  public void regenerate() {
    reset();

    // program.setTexture(this);

    start_time = System.currentTimeMillis();

    //if (program.ip_of_SETUP != -1) {
    // program.instruction_pointer = program.ip_of_SETUP;
    //}
    //else
    //{
    // program.instruction_pointer = 0;
    //}

    if (program.ip_of_SETTINGS != -1) {
      program.executeAllInstructions(this, program.ip_of_SETTINGS);
    }

    if (program.ip_of_SETUP != -1) {
      program.executeAllInstructions(this, program.ip_of_SETUP);
    }

    if (program.ip_of_PALETTE != -1) {
      // program.instruction_pointer = program.ip_of_PALETTE;
      program.executeAllInstructions(this, program.ip_of_PALETTE);
    } else {
      newColourMap(); // random! - should be greyscale...
    }

    iterations++; // used for timing purposes *only*...

    // elapsed_time = System.currentTimeMillis() - start_time;
    // total_time += elapsed_time;

    // Log.log("Time:" + (System.currentTimeMillis() - start_time) + " Average:" + (total_time / iterations));

    // initialSetUp(); // Inital RD square, etc...

    render();
  }

  public void animation() {
    if (!applet.global_settings.paused) {
      iteration++;
      time = (time + speed) & 0xFFFF;

      program.executeAllInstructions(this, program.ip_of_ANIMATE);
      particle_manager.animate(c_a2D[0]);

      render();
    }
  }

  public void render() {
    if (texture_image == null) {
      int[] image_array = new int[width * height]; // !????
      texture_image = new ImageWrapper(image_array, width, height); // it will have an associated array at this point...
    }

    if (TG.diffusion_limited_aggregation) {
      // c_a2D[0].r.makeImageUsingColourMap(texture_image, main_colour_map); // !?!?!
      // c_a2D[0].r.makeImageUsingColourMap(texture_image, main_colour_map, second_colour_map, 0, 0xFFFF, 0, 0xFFFF);
      // c_a2D[0].r.makeImageUsingColourMapMargolus(texture_image, main_colour_map);
    } else {
      if (TG.ising_model) {
        IsingModel.render(this);
      } else {
        if (TG.dual_colour_map) {
          // c_a2D[0].r.makeImageUsingColourMap(texture_image, main_colour_map, second_colour_map, tfu_min, tfu_max, tfv_min, tfv_max);
        } else {
          switch (colour_map_type) {
            case COLOUR_MAP_MARGOLUS :
              c_a2D[0].r.makeImageUsingColourMapMargolus(texture_image, main_colour_map, hflip, vflip, xor_colour); // <--
              break;

            default :
              c_a2D[0].r.makeImageUsingColourMap(texture_image, main_colour_map, hflip, vflip, xor_colour); // <-- default...
              break;
          }
        }
      }
    }

    if (height_map) {
      if (height_map_scale == 0) {
        HeightMap2D.heightMapRender(texture_image, c_a2D[0].r, tfu_max - tfu_min,  this, hflip, vflip);
      } else {
        HeightMap2D.heightMapRender(texture_image, c_a2D[0].r, height_map_scale, this, hflip, vflip);
      }
    }

    texture_image.update();
  }

  void initialSetUp() {
    if (TG.reaction_diffusion) {
      c_a2D[0].r.rdSquare();
      // animating = true;
    }

    if (TG.fractal_drainage) {
      c_a2D[0].r.drainagePatternSetup(); // ?
      //animating = true;
    }

    if (TG.diffusion_limited_aggregation) {
      // c_a2D[0].r.drainagePatternSetup(); // ?
      c_a2D[0].r.diffusionLimitedAggregationSetup(); // ?
      //animating = true;
    }

    if (TG.ising_model) {
      IsingModel.randomise(this);
      //animating = true;
    }
  }

  void newColourMap() {
    if (TG.dual_colour_map) {
      setColour();
    } else {
      if (TG.bw_colour_map) {
        setColour(); // DLA
        // main_colour_map.randomise();

      } else {
        main_colour_map.randomise();
      }
    }
  }

  /*
     void newHeightIndex() {
        main_height_map = new HeightIndex();
        second_height_map = new HeightIndex();
     
        main_height_map.randomise();
        second_height_map.randomise();
     }
  */

  public void animate() {
    // if (!TextureGarden.paused) {

    if (TG.reaction_diffusion) {
      for (int count = 0; count <= TG.frame_frequency; count++) {
        ReactionDiffusion.animateRDVN3(this);
      }
    }

    if (TG.fractal_drainage) {
      for (int count = 0; count <= TG.frame_frequency; count++) {
        FractalDrainage.animate(this, 0);
      }
    }

    if (TG.diffusion_limited_aggregation) {
      for (int count = 0; count <= TG.frame_frequency; count++) {
        DiffusionLimitedAggregation.animate(this);
      }
    }

    if (TG.ising_model) {
      for (int count = 0; count <= TG.frame_frequency; count++) {
        IsingModel.animate(this);
      }
    }

    if (raining) {
      for (int i = 0; i < rain_intensity; i++) {
        action(rnd.nextInt(width), rnd.nextInt(height), 1);
        // c_a2D[0].r[rnd.nextInt(width)][rnd.nextInt(height)] = rnd.nextBoolean() ? 0x0000FFFF : 0xFFFF0000;
      }
    }
  }

  public void action(int x, int y, int m) {
    //if ((m & 8) == 0) {
    //x = x / TextureGarden.plot_scale_factor_x;
    //y = y / TextureGarden.plot_scale_factor_y;

    // Log.log("Mxxx:" + m);

    int and = ((m & 16) != 16) ? 0 : 0;
    int or = ((m & 16) != 16) ? 0 : 0xFFFFFFFF;

    rainAt(x, y, and, or);

    rainAt(x, y - 1, and, or);
    rainAt(x, y + 1, and, or);
    rainAt(x - 1, y, and, or);
    rainAt(x + 1, y, and, or);
  }

  public void actionDragged(int ox, int oy, int x, int y, int m) {
    int _dx = ((hflip ? ox - x : x - ox) << 16) / width;
    int _dy = ((vflip ? oy - y : y - oy) << 16) / height;

    moveOffsetsBy(_dx, _dy);

    //setOffsets(Utils.bound(c_a2D[0].r.offset_x + _dx, 0x10000),Utils.bound(c_a2D[0].r.offset_y + _dy, 0x10000));
    //setOffsets(Utils.bound(c_a2D[0].r.offset_x + _dx, 0x10000),Utils.bound(c_a2D[0].r.offset_y + _dy, 0x10000));
  }

  void moveOffsetsBy(int _dx, int _dy) {
    c_a2D[0].r.offset_x = Utils.bound(c_a2D[0].r.offset_x + _dx, 0x10000);
    c_a2D[0].r.offset_y = Utils.bound(c_a2D[0].r.offset_y + _dy, 0x10000);

    initial_offset_x = Utils.bound(initial_offset_x + _dx, 0x10000);
    initial_offset_y = Utils.bound(initial_offset_y + _dy, 0x10000);

    ProgramProcessor.setTwoValues(program, InstructionManager.SETTINGS, InstructionManager.SET_OFFSETS, initial_offset_x, initial_offset_y);
  }

  public void setOffsets(int ox, int oy) {
    c_a2D[0].r.offset_x = ox;
    c_a2D[0].r.offset_y = oy;

    initial_offset_x = ox;
    initial_offset_y = oy;

    // ProgramProcessor.setTwoValues(program, InstructionManager.SETTINGS, InstructionManager.SET_OFFSETS, ox, oy);
  }

  void rainAt(int x, int y, int and, int or) {
    while (y < 0) {
      y += height;
    }

    while (x < 0) {
      x += width;
    }

    while (y >= height) {
      y -= height;
    }

    while (x >= width) {
      x -= width;
    }

    c_a2D[0].r.safeWriteWithOffset(x, y, (c_a2D[0].r.safeReadWithOffset(x, y) & and) | or);
  }

  int getRedValue() {
    return red_value;
  }

  int getGreenValue() {
    return green_value;
  }

  int getBlueValue() {
    return blue_value;
  }

  void setRedValue(int v) {
    red_value = v;
  }

  void setGreenValue(int v) {
    green_value = v;
  }

  void setBlueValue(int v) {
    blue_value = v;
  }

  void setColour() {
    if (TG.bw_colour_map) {
      // main_colour_map.fadeToOrFromBlack(255,255,255, TextureGarden.reverse_fg, TextureGarden.colour_frequency_v);
      main_colour_map.fadeFromTo(0, 0, 0, 255, 255, 255, applet.global_settings.reverse_fg, applet.colour_frequency_v);
      //main_colour_map.colour[0] = 0;
      //main_colour_map.colour[1] = 0; // xFF0000;
    }
    /*
    else
    {
       switch (applet.colour_number) {
          case TextureGarden.COLOUR_SCHEME_YB:
             if (TextureGarden.colour_swap) {
                main_colour_map.fadeToOrFromBlack(0,0,255, TextureGarden.reverse_fg, TextureGarden.colour_frequency_v);
                second_colour_map.fadeToOrFromBlack(255,255,0, TextureGarden.reverse_bg, TextureGarden.colour_frequency_u);
             }
             else
             {
                main_colour_map.fadeToOrFromBlack(255,255,0, TextureGarden.reverse_fg, TextureGarden.colour_frequency_v);
                second_colour_map.fadeToOrFromBlack(0,0,255, TextureGarden.reverse_bg, TextureGarden.colour_frequency_u);
             }
             break;
          case TextureGarden.COLOUR_SCHEME_CR:
             if (TextureGarden.colour_swap) {
                main_colour_map.fadeToOrFromBlack(255,0,0, TextureGarden.reverse_fg, TextureGarden.colour_frequency_v);
                second_colour_map.fadeToOrFromBlack(0,255,255,TextureGarden.reverse_bg, TextureGarden.colour_frequency_u);
             }
             else
             {
                main_colour_map.fadeToOrFromBlack(0,255,255, TextureGarden.reverse_fg, TextureGarden.colour_frequency_v);
                second_colour_map.fadeToOrFromBlack(255,0,0, TextureGarden.reverse_bg, TextureGarden.colour_frequency_u);
             }
             break;
          case TextureGarden.COLOUR_SCHEME_MG:
             if (TextureGarden.colour_swap) {
                main_colour_map.fadeToOrFromBlack(0,255,0, TextureGarden.reverse_fg, TextureGarden.colour_frequency_v);
                second_colour_map.fadeToOrFromBlack(255,0,255, TextureGarden.reverse_bg, TextureGarden.colour_frequency_u);
             }
             else
             {
                main_colour_map.fadeToOrFromBlack(255,0,255, TextureGarden.reverse_fg, TextureGarden.colour_frequency_v);
                second_colour_map.fadeToOrFromBlack(0,255,0, TextureGarden.reverse_bg, TextureGarden.colour_frequency_u);
             }
             break;
       
          case TextureGarden.COLOUR_SCHEME_WB:
             if (TextureGarden.colour_swap) {
                main_colour_map.fadeToOrFromBlack(0,0,0, TextureGarden.reverse_fg, TextureGarden.colour_frequency_v);
                second_colour_map.fadeToOrFromBlack(255,255,255, TextureGarden.reverse_bg, TextureGarden.colour_frequency_u);
             }
             else
             {
                main_colour_map.fadeToOrFromBlack(255,255,255, TextureGarden.reverse_fg, TextureGarden.colour_frequency_v);
                second_colour_map.fadeToOrFromBlack(0,0,0, TextureGarden.reverse_bg, TextureGarden.colour_frequency_u);
             }
             break;
       }
    	*/
  }

  public ComplexArray1D newBuffer1D(int size) {
    for (int i = MAX_BUFFER_1D; --i >= 1;) {
      c_a1D[i] = c_a1D[i - 1];
    }

    c_a1D[0] = new ComplexArray1D(size);
    c_a1D[0].setSeed(seed);

    return c_a1D[0];
  }

  public ComplexArray2D newBuffer2D(int size_x, int size_y) {
    for (int i = MAX_BUFFER_2D; --i >= 1;) {
      c_a2D[i] = c_a2D[i - 1];
    }

    c_a2D[0] = new ComplexArray2D(size_x, size_y);

    c_a2D[0].setSeed(seed);

    return c_a2D[0];
  }

  public void setSeed(int s) {
    seed = s;
    rnd.setSeed(s);
    rnd2.setSeed(s);

    c_a2D[0].setSeed(s);

    if (c_a1D[0] != null) {
      c_a1D[0].setSeed(s);
    }
  }

  void setSpeed(int fa) {
    speed = fa;

    ProgramProcessor.setValue(program, InstructionManager.SETTINGS, InstructionManager.SET_SPEED, speed);
  }

  int getSpeed() {
    return speed;
  }

  void invertHFlip() {
    hflip = !hflip;
    ProgramProcessor.setBooleanFlag(program, InstructionManager.SETTINGS, InstructionManager.SET_HFLIP, hflip);
  }

  void invertVFlip() {
    vflip = !vflip;
    ProgramProcessor.setBooleanFlag(program, InstructionManager.SETTINGS, InstructionManager.SET_VFLIP, vflip);
  }

  void invertHeightmap() {
    height_map = !height_map;
    ProgramProcessor.setBooleanFlag(program, InstructionManager.SETTINGS, InstructionManager.SET_HEIGHTMAP, height_map);
  }

  void setXORColour(int v) {
    xor_colour = v;

    ProgramProcessor.setValue(program, InstructionManager.SETTINGS, InstructionManager.SET_XOR_COLOUR, xor_colour);
  }
}

//void animateWater() {
//// int v;
//int new_v;
//int c, n, s, w, e, vel, vc;
//int[] temp = new int[height];
//SimpleArray2D _r = c_a2D[0].r;
//
//c_a2D[0].r.offset_x = c_a2D[0].r.offset_x - 1; //?
//if (c_a2D[0].r.offset_x < 0) {
//  c_a2D[0].r.offset_x += width;
//}
//
//for (int y = 0; y < _r.height; y++) {
//  n = _r.safeRead(1, y - 1);
//  s = _r.safeRead(1, y + 1);
//  e = _r.safeRead(2, y);
//  w = _r.safeRead(0, y);
//  c = _r.safeRead(1, y);
//
//  vel = (c & 0xffff) - (c >>> 16);
//
//  vc = (n & 0xffff) + (s & 0xffff) + (e & 0xffff) + (w & 0xffff);
//
//  new_v = (vc >> 2) + vel;
//
//  if (new_v <= 0) {
//    new_v = -new_v;
//  } else {
//    if (new_v > 0xffff) {
//      new_v = 0x1ffff - new_v;
//    }
//  }
//
//  temp[y] = new_v | (c << 16);
//}
//
//for (int x = 0; x < _r.width; x++) {
//  for (int y = 0; y < _r.height; y++) {
//    n = _r.safeRead(x + 1, y - 1);
//    s = _r.safeRead(x + 1, y + 1);
//    e = _r.safeRead(x + 2, y);
//    w = _r.safeRead(x, y);
//    c = _r.safeRead(x + 1, y);
//
//    vel = (c & 0xffff) - (c >>> 16);
//
//    vc = (n & 0xffff) + (s & 0xffff) + (e & 0xffff) + (w & 0xffff);
//
//    new_v = (vc >> 2) + vel;
//
//    if (new_v <= 0) {
//      new_v = -new_v;
//    } else {
//      if (new_v > 0xffff) {
//        new_v = 0x1ffff - new_v;
//      }
//    }
//
//    _r.v[x][y] = new_v | (c << 16);
//  }
//}
//
//for (int y = 0; y < height; y++) {
//  _r.v[0][y] = temp[y];
//}
//}

//// Swaps 2 adjacent points...
//void convection() {
//  int x;
//  int y;
//  int temp;
//  SimpleArray2D _r = c_a2D[0].r;
//  int totn = (_r.width * _r.height) >> 6;
//
//  for (int n = totn; --n >= 0;) {
//    x = rnd.nextInt(_r.height - 1);
//    y = rnd.nextInt(_r.height - 1);
//
//    temp = _r.v[x][y];
//    _r.v[x][y] = _r.v[x + 1][y];
//    _r.v[x + 1][y] = temp;
//  }
//
//  for (int n = totn; --n >= 0;) {
//    x = rnd.nextInt(_r.width - 1);
//    y = rnd.nextInt(_r.height - 1);
//
//    temp = _r.v[x][y];
//    _r.v[x][y] = _r.v[x][y + 1];
//    _r.v[x][y + 1] = temp;
//  }
//}
//
//// Rotates 4 adjacent points...
//void convection2() {
//  int x;
//  int y;
//  int temp;
//  SimpleArray2D _r = c_a2D[0].r;
//  int totn = (_r.width * _r.height) >> 9;
//
//  for (int n = totn; --n >= 0;) {
//    x = rnd.nextInt(_r.width - 2);
//    y = rnd.nextInt(_r.height - 2);
//
//    temp = _r.v[x][y];
//
//    _r.v[x][y] = _r.v[x + 1][y];
//    _r.v[x + 1][y] = _r.v[x + 1][y + 1];
//    _r.v[x + 1][y + 1] = _r.v[x][y + 1];
//    _r.v[x][y + 1] = temp;
//  }
//}
