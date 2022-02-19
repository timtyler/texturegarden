package com.texturegarden;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.net.URL;

import com.texturegarden.effects.ca.DiffusionLimitedAggregation;
import com.texturegarden.effects.ca.FractalDrainage;
import com.texturegarden.effects.ca.IsingModel;
import com.texturegarden.effects.ca.ReactionDiffusion;
import com.texturegarden.gui.DragAndDrop;
import com.texturegarden.gui.TextureArray;
import com.texturegarden.gui.TextureContainer;
import com.texturegarden.gui.canvas.TGCanvas;
import com.texturegarden.gui.canvas.TTCanvas;
import com.texturegarden.gui.canvas.TVCanvas;
import com.texturegarden.instructions.InstructionManager;
import com.texturegarden.messages.Message;
import com.texturegarden.settings.GlobalSettings;
import com.texturegarden.settings.Presets;
import com.texturegarden.utilities.AppletFrame;
import com.texturegarden.utilities.Log;
import com.texturegarden.utilities.Pointer;
import com.texturegarden.utilities.TTChoice;

public class TG extends java.applet.Applet implements AdjustmentListener, ActionListener, ItemListener, Runnable {
  public final static boolean development_version = true; // disable any non-browser-friendly bits...

  public final static boolean single_texture = false;
  public final static boolean texture_modification_allowed = true;

  public final static boolean build_big = false; // used to reduce executable size...
  public final static boolean front_end = true; // used to reduce executable size...?

  public final static boolean reaction_diffusion = false;
  public final static boolean fractal_drainage = false;
  public final static boolean diffusion_limited_aggregation = false;
  public final static boolean ising_model = false;
  public final static boolean interpenetrating = false;

  public final static int TA_X_MAX = single_texture ? 1 : 4;
  public final static int TA_Y_MAX = TA_X_MAX;

  public final static boolean extra_controls = reaction_diffusion | fractal_drainage | diffusion_limited_aggregation | ising_model | single_texture | true;
  public final static boolean dual_colour_map = reaction_diffusion | fractal_drainage; // | diffusion_limited_aggregation;
  public final static boolean bw_colour_map = diffusion_limited_aggregation | ising_model;

  public final static boolean enable_screens = true;

  TG applet;

  TGCanvas tg_canvas;
  TTCanvas tl_canvas;
  TTCanvas te_canvas;
  public TVCanvas tv_canvas;
  public TTCanvas current_canvas;
  Frame frame;

  String temp_string; // comms???

  public TextureArray texture_array;

  Point current_mouse = new Point();
  Point last_mouse = new Point();

  String document_base;
  URL base_url;

  String java_version; // = System.getProperty("java.version");
  String directory_separator; // = System.getProperty("file.separator");

  boolean application = false;

  int modifiers;
  boolean button_pressed;

  int colour_frequency_u = reaction_diffusion ? 1 : 0;
  int colour_frequency_v = (reaction_diffusion | diffusion_limited_aggregation) ? 1 : 0;
  boolean normalise = !(diffusion_limited_aggregation || ising_model);
  //  boolean height_map = !(diffusion_limited_aggregation || ising_model);

  boolean thread_finished = false;

  int plot_scale_factor; // = 1; // reaction_diffusion ? 2 : 1; // 2 : 1

  int plot_scale_factor_x; // = plot_scale_factor;
  int plot_scale_factor_y; // = plot_scale_factor;

  int tile_x = 1; // 3 - plot_scale_factor_x;
  int tile_y = 1; // 3 - plot_scale_factor_y;

  Font std_font = new Font("Helevetica", Font.BOLD, 16);

  final String RESTART = "Restart";
  final static String STATIC = "Static";
  final static String CLEAR = "Clear";
  final static String STEP = "Step";
  final static String SAVE_JPEG = "Save JPEG";
  final static String SAVE_TEXT = "Save text";
  final static String LOAD_TEXT = "Load text";

  final static String NEW_CMAP = "New cmap";

  final static String PAUSE = "Pause";
  final static String REVERSE = "Reverse";
  final static String COLOUR_SWAP = "Swap";
  final static String COLOUR_INVERT = "Invert";
  final static String INVERT_BG = "Invert fg";
  final static String INVERT_FG = "Invert bg";
  final static String NORMALISE = "Normalise";
  final static String HEIGHTMAP = "Relief";

  final static String TILE_X = "Tile horizontally";
  final static String TILE_Y = "Tile vertically";

  final static String HFLIP = "H";
  final static String VFLIP = "V";

  final static String INVERT_R = "R";
  final static String INVERT_G = "G";
  final static String INVERT_B = "B";

  final static String MUT_TEX = "Texture";
  final static String MUT_PAL = "Palette";
  final static String MUT_HEI = "Height";
  final static String MUT_SEE = "Seed";

  final static String TEXTURE_GARDEN = "Texture Garden";
  final static String TEXTURE_LAB = "Texture Lab";
  final static String TEXTURE_EDITOR = "Texture Editor";
  final static String TEXTURE_VIEWER = "Texture Viewer";

  final static int _FRAME_1 = 0;
  final static int _FRAME_2 = 1;
  final static int _FRAME_4 = 3;
  final static int _FRAME_8 = 7;
  final static int _FRAME_16 = 15;
  final static int _FRAME_32 = 31;
  final static int _FRAME_64 = 63;
  final static int _FRAME_128 = 126;
  final static int _FRAME_256 = 255;

  static int frame_frequency = _FRAME_8;

  public final static int PRESET_1 = 1;
  public final static int PRESET_2 = 2;
  public final static int PRESET_3 = 3;
  public final static int PRESET_4 = 4;
  public final static int PRESET_5 = 5;
  public final static int PRESET_6 = 6;
  public final static int PRESET_7 = 7;
  public final static int PRESET_8 = 8;
  public final static int PRESET_9 = 9;
  public final static int PRESET_10 = 10;
  public final static int PRESET_11 = 11;
  public final static int PRESET_12 = 12;
  public final static int PRESET_13 = 13;
  public final static int PRESET_14 = 14;
  public final static int PRESET_15 = 15;
  public final static int PRESET_16 = 16;
  public final static int PRESET_17 = 17;
  public final static int PRESET_18 = 18;
  public final static int PRESET_19 = 19;
  public final static int PRESET_20 = 20;

  public final static int FD_PRESET_1 = 101;
  public final static int FD_PRESET_2 = 102;
  public final static int FD_PRESET_3 = 103;
  public final static int FD_PRESET_4 = 104;
  public final static int FD_PRESET_5 = 105;
  public final static int FD_PRESET_6 = 106;

  public final static int RD2_PRESET_1 = 201;
  public final static int RD2_PRESET_2 = 202;
  public final static int RD2_PRESET_3 = 203;

  public final static int INT_PRESET_1 = 301;
  public final static int INT_PRESET_2 = 302;
  public final static int INT_PRESET_3 = 303;

  public final static int PRESET_TEXTURE_A = 401;
  public final static int PRESET_TEXTURE_B = 402;
  public final static int PRESET_TEXTURE_C = 403;
  public final static int PRESET_TEXTURE_D = 404;
  public final static int PRESET_TEXTURE_E = 405;
  public final static int PRESET_TEXTURE_F = 406;

  public final static int ANIMATE_NONE = 0;
  public final static int ANIMATE_SELECTED = 1;
  public final static int ANIMATE_ACTIVE = 2;
  public final static int ANIMATE_INACTIVE = 3;
  public final static int ANIMATE_ALL = 4;

  public int animation_type = ANIMATE_SELECTED;

  public int preset_number = reaction_diffusion ? PRESET_12 : FD_PRESET_1;
  public int texture_preset_number = PRESET_TEXTURE_A;

  Scrollbar k_slider;
  Scrollbar f_slider;
  Scrollbar u_slider;
  Scrollbar v_slider;
  Scrollbar scaleu_slider;
  Scrollbar scalev_slider;

  Scrollbar red_slider;
  Scrollbar green_slider;
  Scrollbar blue_slider;

  Scrollbar fluid_add_rate_slider;
  Scrollbar fluid_seep_rate_slider;
  Scrollbar upper_threshold_slider;
  Scrollbar lower_threshold_slider;
  Scrollbar fluid_flow_rate_slider;
  Scrollbar ground_rising_slider;
  Scrollbar log_resistance_slider;

  Scrollbar dla_threshold_slider;
  Scrollbar dla_density_slider;
  Scrollbar dla_initial_value_slider;
  Scrollbar speed_slider;
  Scrollbar mutation_rate_slider;

  Label f_label;
  Label k_label;
  Label u_label;
  Label v_label;
  Label scaleu_label;
  Label scalev_label;
  Label red_label;
  Label green_label;
  Label blue_label;
  Label fluid_add_rate_label;
  Label fluid_seep_rate_label;
  Label upper_threshold_label;
  Label lower_threshold_label;
  Label fluid_flow_rate_label;
  Label ground_rising_label;
  Label log_resistance_label;
  Label texture_preset_label;
  Label ani_speed_label;
  Label mutation_rate_label;

  Label dla_threshold_label;
  Label dla_density_label;
  Label dla_initial_value_label;

  TTChoice choose_frequency;
  TTChoice choose_preset;
  TTChoice choose_colour;
  TTChoice choose_colour_frequency_u;
  TTChoice choose_colour_frequency_v;
  TTChoice choose_texture_preset;
  TTChoice choose_animate;

  Checkbox checkbox_pause;
  Checkbox checkbox_rain;
  Checkbox checkbox_colour_swap;
  Checkbox checkbox_colour_invert;

  Checkbox checkbox_normalise;
  Checkbox checkbox_height_map;

  Checkbox checkbox_tile_x;
  Checkbox checkbox_tile_y;

  Checkbox checkbox_invert_r;
  Checkbox checkbox_invert_g;
  Checkbox checkbox_invert_b;

  Checkbox checkbox_hflip;
  Checkbox checkbox_vflip;

  Checkbox checkbox_mut_tex;
  Checkbox checkbox_mut_pal;
  Checkbox checkbox_mut_hei;
  Checkbox checkbox_mut_see;

  Checkbox checkbox_reverse_fg;
  Checkbox checkbox_reverse_bg;
  Checkbox checkbox_reverse;

  Button button_restart;
  Button button_clear;
  Button button_static;
  Button button_step;
  Button button_save_jpeg;
  Button button_save_text;
  Button button_load_text;
  Button button_new_cmap;

  Button button_col_invert_all;
  Button button_col_invert_none;
  Button button_mut_invert_all;
  Button button_mut_invert_none;

  Button button_texture_garden;
  Button button_texture_lab;
  Button button_texture_editor;
  Button button_texture_viewer;

  public final static int SHOW_TEXTURE_GARDEN = 0;
  public final static int SHOW_TEXTURE_LAB = 1;
  public final static int SHOW_TEXTURE_EDITOR = 2;
  public final static int SHOW_TEXTURE_VIEWER = 3;

  public int currently_showing = SHOW_TEXTURE_GARDEN;

  Panel panel_west = new Panel();
  Panel panel_east = new Panel();
  Panel panel_north = new Panel();
  Panel panel_south = new Panel();

  Panel panel_mutation;

  public Message message;
  public IdleMessage idle_message;
  // InstructionManager instruction_manager;
  public Presets presets;
  public Render render;
  public DragAndDrop drag_and_drop;
  public GlobalSettings global_settings;
  public Pointer pointer;

  public void start() {
    applet = this;

    message = new Message(applet);
    idle_message = new IdleMessage(applet);

    // instruction_manager = new InstructionManager(applet);

    presets = new Presets(applet);
    render = new Render(applet);
    drag_and_drop = new DragAndDrop(applet);
    global_settings = new GlobalSettings(); // (applet);
    pointer = new Pointer(applet);

    setTileScaleFactors();
    InstructionManager.init();
    getDocBase();

    // Log.log("" + getSize()); // ...

    setLayout(new BorderLayout());

    setBackground(Color.black);
    setForeground(Color.white);

    checkbox_pause = new Checkbox(PAUSE);
    checkbox_pause.setState(global_settings.paused);
    checkbox_pause.addItemListener(this);
    // checkbox_pause.setFont(std_font);

    //Texture Editor - Text editing window - with "refresh" and "QuickSave" buttons...
    //* Texture Lab    - knobs and sliders...
    //* Texture Viewer TEXTURE_GARDEN TEXTURE_LAB TEXTURE_EDITOR TEXTURE_VIEWER

    button_texture_garden = new Button(TEXTURE_GARDEN);
    button_texture_garden.addActionListener(this);
    button_texture_garden.setFont(std_font);

    button_texture_lab = new Button(TEXTURE_LAB);
    button_texture_lab.addActionListener(this);
    button_texture_lab.setFont(std_font);

    button_texture_editor = new Button(TEXTURE_EDITOR);
    button_texture_editor.addActionListener(this);
    button_texture_editor.setFont(std_font);

    button_texture_viewer = new Button(TEXTURE_VIEWER);
    button_texture_viewer.addActionListener(this);
    button_texture_viewer.setFont(std_font);

    buttonBarColourUpdate();

    button_col_invert_all = new Button(single_texture ? "+" : "All");
    button_col_invert_all.addActionListener(this);
    button_col_invert_all.setForeground(Color.black);
    button_col_invert_all.setFont(std_font);

    button_col_invert_none = new Button(single_texture ? "-" : "None");
    button_col_invert_none.addActionListener(this);
    button_col_invert_none.setForeground(Color.black);
    button_col_invert_none.setFont(std_font);

    button_mut_invert_all = new Button("All");
    button_mut_invert_all.addActionListener(this);
    button_mut_invert_all.setForeground(Color.black);
    button_mut_invert_all.setFont(std_font);

    button_mut_invert_none = new Button("None");
    button_mut_invert_none.addActionListener(this);
    button_mut_invert_none.setForeground(Color.black);
    button_mut_invert_none.setFont(std_font);

    button_step = new Button(STEP);
    button_step.addActionListener(this);
    button_step.setForeground(Color.blue);
    button_step.setFont(std_font);

    //Panel step_pause_panel = new Panel();
    //step_pause_panel.add(checkbox_pause);
    //step_pause_panel.add(button_step);

    button_save_jpeg = new Button(SAVE_JPEG);
    button_save_jpeg.addActionListener(this);
    button_save_jpeg.setForeground(Color.blue);
    button_save_jpeg.setFont(std_font);

    //button_save_jpeg.setFont(std_font);

    button_save_text = new Button(SAVE_TEXT);
    button_save_text.addActionListener(this);
    button_save_text.setForeground(Color.blue);
    button_save_text.setFont(std_font);

    button_load_text = new Button(LOAD_TEXT);
    button_load_text.addActionListener(this);
    button_load_text.setForeground(Color.blue);
    button_load_text.setFont(std_font);

    button_new_cmap = new Button(NEW_CMAP);
    button_new_cmap.addActionListener(this);

    checkbox_normalise = new Checkbox(NORMALISE);
    checkbox_normalise.setState(normalise);
    checkbox_normalise.addItemListener(this);

    checkbox_reverse = new Checkbox(REVERSE);
    checkbox_reverse.setState(normalise);
    checkbox_reverse.addItemListener(this);

    checkbox_height_map = new Checkbox(HEIGHTMAP);
    checkbox_height_map.setState(true);
    checkbox_height_map.addItemListener(this);

    // ...

    checkbox_tile_x = new Checkbox(TILE_X);
    checkbox_tile_x.setState(tile_x == 2);
    checkbox_tile_x.addItemListener(this);

    checkbox_tile_y = new Checkbox(TILE_Y);
    checkbox_tile_y.setState(tile_y == 2);
    checkbox_tile_y.addItemListener(this);

    // ...

    checkbox_hflip = new Checkbox(HFLIP);
    checkbox_hflip.setState(false);
    checkbox_hflip.addItemListener(this);

    checkbox_vflip = new Checkbox(VFLIP);
    checkbox_vflip.setState(false);
    checkbox_vflip.addItemListener(this);

    // ...

    checkbox_invert_r = new Checkbox(INVERT_R);
    //checkbox_invert_r.setState(true);
    checkbox_invert_r.addItemListener(this);

    checkbox_invert_g = new Checkbox(INVERT_G);
    //checkbox_invert_g.setState(true);
    checkbox_invert_g.addItemListener(this);

    checkbox_invert_b = new Checkbox(INVERT_B);
    //checkbox_invert_b.setState(true);
    checkbox_invert_b.addItemListener(this);

    checkbox_mut_tex = new Checkbox(MUT_TEX);
    checkbox_mut_tex.setState(true);
    checkbox_mut_tex.addItemListener(this);

    checkbox_mut_pal = new Checkbox(MUT_PAL);
    checkbox_mut_pal.setState(true);
    checkbox_mut_pal.addItemListener(this);

    checkbox_mut_hei = new Checkbox(MUT_HEI);
    checkbox_mut_hei.setState(true);
    checkbox_mut_hei.addItemListener(this);

    checkbox_mut_see = new Checkbox(MUT_SEE);
    checkbox_mut_see.setState(true);
    checkbox_mut_see.addItemListener(this);

    // ...

    checkbox_colour_swap = new Checkbox(COLOUR_SWAP);
    checkbox_colour_swap.setState(global_settings.colour_swap);
    checkbox_colour_swap.addItemListener(this);

    checkbox_colour_invert = new Checkbox(COLOUR_INVERT);
    checkbox_colour_invert.setState(global_settings.colour_invert);
    checkbox_colour_invert.addItemListener(this);

    checkbox_reverse_fg = new Checkbox(INVERT_FG);
    checkbox_reverse_fg.setState(global_settings.reverse_fg);
    checkbox_reverse_fg.addItemListener(this);

    checkbox_reverse_bg = new Checkbox(INVERT_BG);
    checkbox_reverse_bg.setState(global_settings.reverse_bg);
    checkbox_reverse_bg.addItemListener(this);

    // checkbox_rain = new Checkbox(RAIN);
    // checkbox_rain.setState(true);
    // checkbox_rain.addItemListener(this);

    button_restart = new Button(RESTART);
    button_restart.addActionListener(this);
    button_restart.setForeground(Color.blue);
    button_restart.setFont(std_font);

    button_clear = new Button(CLEAR);
    button_clear.addActionListener(this);
    button_clear.setForeground(Color.blue);
    button_clear.setFont(std_font);

    button_static = new Button(STATIC);
    button_static.addActionListener(this);
    button_static.setForeground(Color.blue);
    button_static.setFont(std_font);

    f_slider = new Scrollbar(0, ReactionDiffusion.i_f, 150, 0, 1650);
    f_slider.addAdjustmentListener(this);
    f_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    showAgentRate();

    k_slider = new Scrollbar(0, ReactionDiffusion.i_k, 1000, 0, 21000);
    k_slider.addAdjustmentListener(this);
    k_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    showInhibitorRate();

    u_slider = new Scrollbar(0, ReactionDiffusion.i_mul_u, 4, 0, 260);
    u_slider.addAdjustmentListener(this);
    u_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    showAgentSpeed();

    v_slider = new Scrollbar(0, ReactionDiffusion.i_mul_v, 4, 0, 260);
    v_slider.addAdjustmentListener(this);
    v_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    showInhibitorSpeed();

    scaleu_slider = new Scrollbar(0, ReactionDiffusion.i_scale_u, 2, 12, 20);
    scaleu_slider.addAdjustmentListener(this);
    scaleu_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    showAgentScale();

    scalev_slider = new Scrollbar(0, ReactionDiffusion.i_scale_v, 2, 12, 20);
    scalev_slider.addAdjustmentListener(this);
    scalev_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    showInhibitorScale();

    // frame frequency...
    //Panel panel_frequency = new Panel();
    Label label_frequency = new Label("Show", Label.RIGHT);

    choose_frequency = new TTChoice(this);
    choose_frequency.add("every frame", _FRAME_1);
    choose_frequency.add("alternate frames", _FRAME_2);
    choose_frequency.add("1 frame in 4", _FRAME_4);
    choose_frequency.add("1 frame in 8", _FRAME_8);
    choose_frequency.add("frames rarely", _FRAME_16);
    choose_frequency.add("few frames", _FRAME_32);
    choose_frequency.add("very few frames", _FRAME_64);
    choose_frequency.choice.select(choose_frequency.num_to_str(frame_frequency));
    choose_frequency.choice.setForeground(Color.black);

    Label label_texture_preset = new Label("Preset:", Label.RIGHT);
    label_texture_preset.setForeground(Color.red);

    choose_texture_preset = new TTChoice(this);
    choose_texture_preset.add("a/", PRESET_TEXTURE_A);
    choose_texture_preset.add("b/", PRESET_TEXTURE_B);
    choose_texture_preset.add("c/", PRESET_TEXTURE_C);
    //if (development_version) {
      choose_texture_preset.add("d/", PRESET_TEXTURE_D);
    choose_texture_preset.add("e/", PRESET_TEXTURE_E);
    choose_texture_preset.add("f/", PRESET_TEXTURE_F);
    //}

    choose_texture_preset.choice.select(choose_texture_preset.num_to_str(texture_preset_number));
    choose_texture_preset.choice.setForeground(Color.black);

    //choose_texture_preset.choice.select(choose_texture_preset.num_to_str(texture_preset_number));
    //choose_texture_preset.choice.setForeground(Color.black);

    Label label_animate = new Label("Animate:", Label.RIGHT);
    label_animate.setForeground(Color.white);
    // label_animate.setFont(std_font);

    choose_animate = new TTChoice(this);
    choose_animate.add("selected ", ANIMATE_SELECTED);
    choose_animate.add("none", ANIMATE_NONE);
    choose_animate.add("inactive ", ANIMATE_INACTIVE);
    choose_animate.add("active", ANIMATE_ACTIVE);
    choose_animate.add("all", ANIMATE_ALL);

    choose_animate.choice.select(choose_animate.num_to_str(animation_type));
    choose_animate.choice.setForeground(Color.black);

    Label label_preset = new Label("Preset:", Label.RIGHT);
    label_preset.setForeground(Color.red);
    label_preset.setFont(std_font);

    choose_preset = new TTChoice(this);
    if (reaction_diffusion) {
      choose_preset.add("pulsar", PRESET_12);
      choose_preset.add("breathing", PRESET_6);
      choose_preset.add("boiling", PRESET_2);
      choose_preset.add("spotty dots", PRESET_3);
      choose_preset.add("fingerprints", PRESET_5);
      choose_preset.add("holly", PRESET_7);
      choose_preset.add("blobs", PRESET_14);
      choose_preset.add("storm", PRESET_8);
      choose_preset.add("circuit pulse", PRESET_11);
      choose_preset.add("ebb and flow", PRESET_18);
      choose_preset.add("continuity", PRESET_13);
      choose_preset.add("strobe", PRESET_16);
      choose_preset.add("small patches", PRESET_1);
      choose_preset.add("make & break", PRESET_18);
      choose_preset.add("what gives", PRESET_9);
      choose_preset.add("explosions", PRESET_10);
      choose_preset.add("materialise", PRESET_4);
      choose_preset.add("network", PRESET_19);
      choose_preset.add("ball bearings", PRESET_20);
      choose_preset.add("mesh maker", PRESET_15);
      choose_preset.add("mesh rings", PRESET_17);
      choose_preset.add("RD2_PRESET_1", RD2_PRESET_1);
      choose_preset.add("RD2_PRESET_2", RD2_PRESET_2);
      choose_preset.add("RD2_PRESET_3", RD2_PRESET_3);
    } else {
      if (interpenetrating) {
        choose_preset.add("water wings", INT_PRESET_1);
        choose_preset.add("cart tracks", INT_PRESET_2);
      } else {
        choose_preset.add("pleasant land", FD_PRESET_1);
        choose_preset.add("seepage", FD_PRESET_2);
        choose_preset.add("freeze and thaw", FD_PRESET_3);
        choose_preset.add("old", FD_PRESET_4);
        choose_preset.add("shallow", FD_PRESET_5);
        choose_preset.add("deep", FD_PRESET_6);
      }
    }

    choose_preset.choice.select(choose_preset.num_to_str(preset_number));
    choose_preset.choice.setForeground(Color.black);

    Label label_colour = new Label("Colour map type:", Label.RIGHT);

    /*
    choose_colour = new TTChoice(this);
    choose_colour.add("yellow/blue"   ,COLOUR_SCHEME_YB);
    choose_colour.add("cyan/red"    ,COLOUR_SCHEME_CR);
    choose_colour.add("magenta/green"  ,COLOUR_SCHEME_MG);
    choose_colour.add("white/black"  ,COLOUR_SCHEME_WB);
    choose_colour.choice.setForeground(Color.black);
    */

    Label label_colour_frequency_u = new Label("Colour freq(agent):", Label.RIGHT);
    choose_colour_frequency_u = new TTChoice(this);
    choose_colour_frequency_u.add("0.5", 0);
    choose_colour_frequency_u.add("1.0", 1);
    choose_colour_frequency_u.add("2.0", 2);
    choose_colour_frequency_u.add("4.0", 3);
    choose_colour_frequency_u.add("8.0", 4);
    choose_colour_frequency_u.choice.select(choose_colour_frequency_u.num_to_str(colour_frequency_u));
    choose_colour_frequency_u.choice.setForeground(Color.black);

    Label label_colour_frequency_v;

    if (diffusion_limited_aggregation) {
      label_colour_frequency_v = new Label("Colour frequency:", Label.RIGHT);
    } else {
      label_colour_frequency_v = new Label("Colour freq(inhibitor):", Label.RIGHT);
    }

    choose_colour_frequency_v = new TTChoice(this);
    choose_colour_frequency_v.add("0.5", 0);
    choose_colour_frequency_v.add("1.0", 1);
    choose_colour_frequency_v.add("2.0", 2);
    choose_colour_frequency_v.add("4.0", 3);
    choose_colour_frequency_v.add("8.0", 4);
    choose_colour_frequency_v.choice.select(choose_colour_frequency_v.num_to_str(colour_frequency_v));
    choose_colour_frequency_v.choice.setForeground(Color.black);

    // 255 = hardwired col value...
    red_slider = new Scrollbar(0, 255, 4, 0, 259);
    red_slider.addAdjustmentListener(this);
    red_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    //showRedSliderValue();

    green_slider = new Scrollbar(0, 255, 4, 0, 259);
    green_slider.addAdjustmentListener(this);
    green_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    //showGreenSliderValue();

    blue_slider = new Scrollbar(0, 255, 4, 0, 259);
    blue_slider.addAdjustmentListener(this);
    blue_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    //showBlueSliderValue();

    fluid_add_rate_slider = new Scrollbar(0, FractalDrainage.add_rate, 100, 1, 1600);
    fluid_add_rate_slider.addAdjustmentListener(this);
    fluid_add_rate_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    //showFluidAddRateValue();

    fluid_seep_rate_slider = new Scrollbar(0, FractalDrainage.seep_rate, 30, 0, 430);
    fluid_seep_rate_slider.addAdjustmentListener(this);
    fluid_seep_rate_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    //showFluidSeepRateValue();	

    upper_threshold_slider = new Scrollbar(0, FractalDrainage.upper_threshold, 1000, 0, 66535);
    upper_threshold_slider.addAdjustmentListener(this);
    upper_threshold_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    //showUpperThresholdValue();

    lower_threshold_slider = new Scrollbar(0, FractalDrainage.lower_threshold, 1000, 0, 66535);
    lower_threshold_slider.addAdjustmentListener(this);
    lower_threshold_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    //showLowerThresholdValue();

    fluid_flow_rate_slider = new Scrollbar(0, FractalDrainage.flow_rate, 2, 0, 11);
    fluid_flow_rate_slider.addAdjustmentListener(this);
    fluid_flow_rate_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    //showFluidFlowRateValue();

    ground_rising_slider = new Scrollbar(0, FractalDrainage.ground_rising, 10, 0, 160);
    ground_rising_slider.addAdjustmentListener(this);
    ground_rising_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    //showGroundRisingValue();

    log_resistance_slider = new Scrollbar(0, FractalDrainage.log_resistance, 2, 1, 12);
    log_resistance_slider.addAdjustmentListener(this);
    log_resistance_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    //showLogResistanceValue();

    dla_threshold_slider = new Scrollbar(0, DiffusionLimitedAggregation.threshold, 30, 2, 285);
    dla_threshold_slider.addAdjustmentListener(this);
    dla_threshold_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    //showDLAThreshold();

    dla_density_slider = new Scrollbar(0, DiffusionLimitedAggregation.density, 10, 0, 110);
    dla_density_slider.addAdjustmentListener(this);
    dla_density_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    //showDLADensity();

    dla_initial_value_slider = new Scrollbar(0, DiffusionLimitedAggregation.initial_value, 30, 2, 285);
    dla_initial_value_slider.addAdjustmentListener(this);
    dla_initial_value_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);
    //showDLAInitialValue();

    speed_slider = new Scrollbar(0, 512, 1000, -3000, 4000);
    speed_slider.addAdjustmentListener(this);
    ani_speed_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);

    mutation_rate_slider = new Scrollbar(0, global_settings.getMutationRate(), 10, 0, 110);
    mutation_rate_slider.addAdjustmentListener(this);
    mutation_rate_label = new Label("XXXXXXXXXXXXXXXXXXXXXXXXXXX", Label.RIGHT);

    Panel panel_south_super = new Panel();
    panel_south_super.setLayout(new BorderLayout());

    // Panel panel_south = new Panel();

    // Panel panel_south = new Panel();

    Panel panel_top_south = new Panel();
    panel_top_south.setLayout(new BorderLayout());

    Panel panel_top_north = new Panel();
    panel_top_north.setLayout(new BorderLayout());

    Panel panel_middle_north = new Panel();
    // panel_middle_north.setLayout(new BorderLayout());

    if (ising_model) {
      panel_east.setLayout(new GridLayout(3, 2));
    } else {
      if (fractal_drainage) {
        panel_east.setLayout(new GridLayout(9, 2));
      } else {
        if (diffusion_limited_aggregation) {
          panel_east.setLayout(new GridLayout(4, 2));
        } else {
          panel_east.setLayout(new GridLayout(8, 2));
        }
      }
    }

    panel_north.setLayout(new GridLayout(0, 4));

    Panel panel_south1 = new Panel();
    Panel panel_south2 = new Panel();
    Panel panel_flip = new Panel();
    // Panel panel_mutation = new Panel();
    Panel panel_invert = new Panel();
    panel_mutation = new Panel();

    if (diffusion_limited_aggregation || ising_model) {
      panel_west.setLayout(new GridLayout(6, 2));
    } else {
      panel_west.setLayout(new GridLayout(10, 2));
    }

    Panel panel_south1c = new Panel();
    // panel_south1.setLayout(new GridLayout(1,4));
    // panel_south1.setLayout(new GridLayout(1,4));
    panel_south1c.setLayout(new GridLayout(1, 3));

    int n_o_s_p = application ? 3 : 2;
    n_o_s_p = 3;
    panel_south.setLayout(new GridLayout(n_o_s_p, 0));

    panel_flip.add(new Label("Flip:", Label.RIGHT));
    panel_flip.add(checkbox_hflip);
    panel_flip.add(checkbox_vflip);

    panel_invert.add(new Label("Invert", Label.RIGHT));
    panel_invert.add(checkbox_invert_r);
    panel_invert.add(checkbox_invert_g);
    panel_invert.add(checkbox_invert_b);
    panel_invert.add(button_col_invert_all);
    panel_invert.add(button_col_invert_none);

    // Panel panel_mutation  = new Panel();
    Panel panel_mutation1 = new Panel();
    Panel panel_mutation1a = new Panel();
    Panel panel_mutation1b = new Panel();
    Panel panel_mutation2 = new Panel();

    panel_mutation.setLayout(new GridLayout(2, 1));
    panel_mutation1.setLayout(new GridLayout(1, 2));
    panel_mutation1a.setLayout(new GridLayout(1, 2));
    // panel_mutation2.setLayout(new GridLayout(1,9));

    panel_mutation1a.add(mutation_rate_label);
    panel_mutation1a.add(mutation_rate_slider);

    panel_mutation1b.add(label_preset);
    panel_mutation1b.add(choose_texture_preset.choice);

    if (!single_texture && texture_modification_allowed) {
      panel_mutation1.add(panel_mutation1a);
    }
    panel_mutation1.add(panel_mutation1b);
   // panel_mutation1.add(panel_invert); // new

    panel_mutation2.add(new Label("Mutate:", Label.RIGHT));
    panel_mutation2.add(checkbox_mut_tex);
    panel_mutation2.add(checkbox_mut_pal);
    panel_mutation2.add(checkbox_mut_hei);
    panel_mutation2.add(checkbox_mut_see);
    panel_mutation2.add(button_mut_invert_all);
    panel_mutation2.add(button_mut_invert_none);

    panel_mutation.add(panel_mutation1);
    if (!single_texture && texture_modification_allowed) {
      panel_mutation.add(panel_mutation2);
    } else {
      panel_mutation.add(panel_invert); // new
    }

    Panel panel_pause_n_step = new Panel();
    panel_pause_n_step.add(checkbox_pause);
    //Panel tp = new Panel();
    panel_pause_n_step.add(button_step);
    //panel_south1c.add(tp);
    panel_south1c.add(panel_pause_n_step);

    Panel panel_ani_speed = new Panel();
    panel_ani_speed.setLayout(new GridLayout(1, 2));
    panel_ani_speed.add(ani_speed_label);
    panel_ani_speed.add(speed_slider);
    panel_south1c.add(panel_ani_speed);

    Panel panel_animate = new Panel();
    panel_animate.add(label_animate);
    panel_animate.add(choose_animate.choice);
    if (!single_texture) {
      panel_south1c.add(panel_animate);
    }

    // Panel tp = new Panel();
    panel_south1.setLayout(new BorderLayout());
    panel_south1.add(panel_south1c, BorderLayout.SOUTH);
    // panel_south1.add(panel_south1c);

    Panel panel_south3 = new Panel();
    Panel panel_south0 = new Panel();

    if (application) {
      if (!single_texture && texture_modification_allowed) {
        panel_south2.add(button_load_text);
        panel_south2.add(button_save_text);
        panel_south2.add(button_save_jpeg);
      }
    }

    panel_north.add(button_texture_garden);
    if (development_version) {
      panel_north.add(button_texture_lab);
      panel_north.add(button_texture_editor);
    }

    panel_north.add(button_texture_viewer);

    if (extra_controls) {
      panel_south2.add(button_clear);
      panel_south2.add(button_static);
      panel_south2.add(button_restart);
    }

    Panel temp_preset_panel = new Panel();

    panel_south2.add(temp_preset_panel);

    if (!single_texture) {
      panel_south0.add(panel_invert); // new
    }

    //panel_south0.add(panel_invert);
    panel_south0.add(checkbox_height_map);
    panel_south0.add(panel_flip);

    panel_south.add(panel_south0);
    panel_south.add(panel_south1);

    if (application || single_texture) {
      panel_south.add(panel_south2);
    }

    /*
    if (extra_controls) {
    Panel panel_top = new Panel();
    
    panel_east.add(label_frequency);
    panel_east.add(choose_frequency.choice);
    
    if (reaction_diffusion) {
    panel_east.add(f_label);
    panel_east.add(f_slider);
    
    panel_east.add(k_label);
    panel_east.add(k_slider);
    
    panel_east.add(u_label);
    panel_east.add(u_slider);
    
    panel_east.add(v_label);
    panel_east.add(v_slider);
    
    panel_east.add(scaleu_label);
    panel_east.add(scaleu_slider);
    
    panel_east.add(scalev_label);
    panel_east.add(scalev_slider);
    }
    
    if (fractal_drainage) {
    panel_east.add(fluid_add_rate_label);
    panel_east.add(fluid_add_rate_slider);
    panel_east.add(fluid_seep_rate_label);
    panel_east.add(fluid_seep_rate_slider);
    panel_east.add(fluid_flow_rate_label);
    panel_east.add(fluid_flow_rate_slider);
    panel_east.add(log_resistance_label);
    panel_east.add(log_resistance_slider);
    panel_east.add(ground_rising_label);
    panel_east.add(ground_rising_slider);
    
    panel_east.add(upper_threshold_label);
    panel_east.add(upper_threshold_slider);
    panel_east.add(lower_threshold_label);
    panel_east.add(lower_threshold_slider);
    }
    
    if (diffusion_limited_aggregation) {
    panel_east.add(dla_threshold_label);
    panel_east.add(dla_threshold_slider);
    }
    
    if (diffusion_limited_aggregation || ising_model) {
    panel_east.add(dla_density_label);
    panel_east.add(dla_density_slider);
    }
    if (diffusion_limited_aggregation) {
    panel_east.add(dla_initial_value_label);
    panel_east.add(dla_initial_value_slider);
    }
    
    if (!diffusion_limited_aggregation && !ising_model) {
    panel_east.add(label_preset);
    panel_east.add(choose_preset.choice);
    }
    
    if (ising_model) {
    panel_east.add(checkbox_reverse);
    }
    
    // WEST...
    if (!bw_colour_map) {
    panel_west.add(label_colour);
    panel_west.add(choose_colour.choice);
    }
    
    panel_west.add(red_label);
    panel_west.add(red_slider);
    
    panel_west.add(green_label);
    panel_west.add(green_slider);
    
    panel_west.add(blue_label);
    panel_west.add(blue_slider);
    
    if (!diffusion_limited_aggregation && !ising_model) {
    panel_west.add(label_colour_frequency_u);
    panel_west.add(choose_colour_frequency_u.choice);
    }
    
    panel_west.add(label_colour_frequency_v);
    panel_west.add(choose_colour_frequency_v.choice);
    
    if (!diffusion_limited_aggregation && !ising_model) {
    panel_west.add(checkbox_normalise);
    panel_west.add(checkbox_height_map);
    }
    
    //if (!diffusion_limited_aggregation) {
    panel_west.add(checkbox_reverse_fg);
    //}
    
    if (!ising_model && !diffusion_limited_aggregation) {
    panel_west.add(checkbox_reverse_bg);
    }
    
    if (!diffusion_limited_aggregation && !ising_model) {
    panel_west.add(checkbox_colour_swap);
    }
    
    panel_west.add(checkbox_colour_invert);
    
    panel_west.add(checkbox_tile_x);
    panel_west.add(checkbox_tile_y);
    
    // panel_top_south.add(panel_south, BorderLayout.NORTH);
    //panel_top_south.add(panel_east, BorderLayout.SOUTH);
    }
    else
    {
    // checkbox_height_map.setFont(std_font);
    
    
    //button_new_cmap.setFont(std_font);
    //panel_south.add(button_new_cmap);
    }
    */

    panel_top_north.add(panel_middle_north, BorderLayout.NORTH);

    panel_south_super.add(panel_south, BorderLayout.SOUTH);
    // panel_south_super.add(panel_top_north, BorderLayout.NORTH);

    // add(panel_south_super, BorderLayout.EAST);
    // add(panel_south_super, BorderLayout.SOUTH);

    texture_array = new TextureArray(applet, TA_X_MAX, TA_Y_MAX);

    tg_canvas = new TGCanvas(applet);
    tg_canvas.addMouseListener(tg_canvas);
    tg_canvas.addMouseMotionListener(tg_canvas);

    tv_canvas = new TVCanvas(applet);
    tv_canvas.addMouseListener(tv_canvas);
    tv_canvas.addMouseMotionListener(tv_canvas);

    addBasicGUIElements();
    addTGGUIElements();
    validate();

    greyButtons();

    showAll(); // sliders...

    current_canvas.repaint();

    thread_finished = false;

    Thread recalc = new Thread(this);
    recalc.start();

    validate(); // why doesn't this work properly...?
  }

  public void stop() {
    thread_finished = true;
  }

  public void run() {
    //  repaint();
    texture_array.texture_array[0].selection_type = TextureContainer.SELECTED_MASK;

    texture_array.renderAll();

    presets.initialPreset("a/");

    do {
      if (!global_settings.paused) {
        if (idle_message.current_message == 0) {
          texture_array.animateAll();
        }

        current_canvas.repaint();
      }

      if (global_settings.stepping) {
        global_settings.paused = true;
        global_settings.stepping = false;
      }

      message.execute();
      idle_message.execute();

      sleepFor(2);

    } while (!thread_finished);
  }

  public void sleepFor(int t) {
    try {
      Thread.sleep(t);
    } catch (Exception e) {}
  }

  public void itemStateChanged(ItemEvent e) {
    String state_changed_string = null;
    try {
      if (e != null) {
        state_changed_string = (String) (e.getItem());
      }

      // pause...
      if (e.getSource() == checkbox_pause) {
        global_settings.paused = !global_settings.paused;
        greyButtons();
      }

      if (e.getSource() == checkbox_invert_r) {
        TextureContainer tc = texture_array.getSelectedTextureContainer();
        tc.texture.setXORColour(tc.texture.xor_colour ^ 0xFF0000);
        updateTextureUIDisplay(texture_array.getSelectedTextureContainer());
      }

      if (e.getSource() == checkbox_invert_g) {
        TextureContainer tc = texture_array.getSelectedTextureContainer();
        tc.texture.setXORColour(tc.texture.xor_colour ^ 0x00FF00);
        updateTextureUIDisplay(texture_array.getSelectedTextureContainer());
      }

      if (e.getSource() == checkbox_invert_b) {
        TextureContainer tc = texture_array.getSelectedTextureContainer();
        tc.texture.setXORColour(tc.texture.xor_colour ^ 0x0000FF);
        updateTextureUIDisplay(texture_array.getSelectedTextureContainer());
      }

      if (e.getSource() == checkbox_normalise) {
        normalise = !normalise;
      }

      if (e.getSource() == checkbox_colour_swap) {
        global_settings.colour_swap = !global_settings.colour_swap;
        texture_array.getSelectedTextureContainer().texture.setColour();
      }

      if (e.getSource() == checkbox_colour_invert) {
        global_settings.colour_invert = !global_settings.colour_invert;
        texture_array.getSelectedTextureContainer().texture.setColour();
      }

      if (e.getSource() == checkbox_reverse_fg) {
        global_settings.reverse_fg = !global_settings.reverse_fg;
        texture_array.getSelectedTextureContainer().texture.setColour();
      }

      if (e.getSource() == checkbox_reverse) {
        // global_settings.reverse_fg = !global_settings.reverse_fg;
        texture_array.getSelectedTextureContainer().texture.generation ^= 1;
      }

      if (e.getSource() == checkbox_reverse_bg) {
        global_settings.reverse_bg = !global_settings.reverse_bg;
        texture_array.getSelectedTextureContainer().texture.setColour();
        // TextureArray.texture.generation ^= 1; // !!!!!! Inverse FG
        // Log.log("REVERSE");
      }

      if (e.getSource() == checkbox_tile_x) {
        texture_array.invertTileXSelected(); // tile_x ^= 3;
        setTileScaleFactors();
      }

      if (e.getSource() == checkbox_tile_y) {
        texture_array.invertTileYSelected();
        // tile_y ^= 3;
        setTileScaleFactors();
      }

      if (e.getSource() == checkbox_hflip) {
        texture_array.getSelectedTextureContainer().texture.invertHFlip(); // tile_x ^= 3;
      }

      if (e.getSource() == checkbox_vflip) {
        texture_array.getSelectedTextureContainer().texture.invertVFlip(); // tile_x ^= 3;
      }

      if (e.getSource() == checkbox_height_map) {
        texture_array.getSelectedTextureContainer().texture.invertHeightmap(); // tile_x ^= 3;
        // texture_array.getSelectedTextureContainer().texture.height_map = !texture_array.getSelectedTextureContainer().texture.height_map;
      }

      if (e.getSource() == checkbox_mut_tex) {
        setMutationSettingsFromCheckboxes(); // global_settings.mut_tex = !global_settings.mut_tex;
      }

      if (e.getSource() == checkbox_mut_pal) {
        setMutationSettingsFromCheckboxes(); // global_settings.mut_pal = !global_settings.mut_pal;
      }

      if (e.getSource() == checkbox_mut_hei) {
        setMutationSettingsFromCheckboxes(); // global_settings.mut_hei = !global_settings.mut_hei;
      }

      if (e.getSource() == checkbox_mut_see) {
        setMutationSettingsFromCheckboxes(); // global_settings.mut_see = !global_settings.mut_see;
      }

      if (e.getSource() == checkbox_rain) {
        texture_array.getSelectedTextureContainer().texture.raining = !texture_array.getSelectedTextureContainer().texture.raining;
      }

      if (choose_frequency.str_to_num(state_changed_string) != -99) {
        frame_frequency = choose_frequency.str_to_num(state_changed_string);
      }

      if (choose_texture_preset.str_to_num(state_changed_string) != -99) {
        texture_preset_number = choose_texture_preset.str_to_num(state_changed_string);
        message.send(Message.MSG_NEW_PRESET, state_changed_string);
        // Presets.setPreset(state_changed_string); // ?!?!
        showAll();
      }

      if (choose_animate.str_to_num(state_changed_string) != -99) {
        animation_type = choose_animate.str_to_num(state_changed_string);
        // message.send(Message.MSG_NEW_PRESET, state_changed_string);
        // Presets.setPreset(state_changed_string); // ?!?!
        // showAll();
      }

      //if (choose_colour.str_to_num(state_changed_string) != -99) {
      //colour_number = choose_colour.str_to_num(state_changed_string);
      //texture_array.getSelectedTextureContainer().texture.setColour();
      //}

      if (e.getSource() == choose_colour_frequency_u.choice) {
        colour_frequency_u = choose_colour_frequency_u.str_to_num(state_changed_string);
        texture_array.getSelectedTextureContainer().texture.setColour();
      }

      if (e.getSource() == choose_colour_frequency_v.choice) {
        colour_frequency_v = choose_colour_frequency_v.str_to_num(state_changed_string);
        texture_array.getSelectedTextureContainer().texture.setColour();
      }
    } catch (java.lang.NullPointerException exc) {
      // do nothing...
    }
  }

  void showAll() {
    showRedSliderValue();
    showGreenSliderValue();
    showBlueSliderValue();
    showUpperThresholdValue();
    showLowerThresholdValue();
    showLogResistanceValue();
    showGroundRisingValue();
    showFluidFlowRateValue();
    showFluidSeepRateValue();
    showFluidAddRateValue();
    showDLAThreshold();
    showDLADensity();
    showDLAInitialValue();
    showAniSpeed();
    showMutationRate();
  }

  void addBasicGUIElements() {
    if (enable_screens) {
      add(panel_north, BorderLayout.NORTH);
    }
    add(panel_south, BorderLayout.SOUTH);
  }

  void addTGGUIElements() {
    Panel tp = new Panel();
    tp.setLayout(new BorderLayout());

    current_canvas = tg_canvas;
    tp.add(current_canvas, BorderLayout.CENTER);
    tp.add(panel_mutation, BorderLayout.SOUTH);

    add(tp, BorderLayout.CENTER);
  }

  void addTVGUIElements() {
    // add(panel_east,  BorderLayout.EAST);
    // add(panel_west,  BorderLayout.WEST);
    // BorderLayout

    current_canvas = tv_canvas;
    add(current_canvas, BorderLayout.CENTER);
  }

  void setTileScaleFactors() {
    plot_scale_factor_x = tile_x ^ 3;
    plot_scale_factor_y = tile_y ^ 3;
  }

  public void actionPerformed(ActionEvent e) {
    // Log.log("BP");
    String arg = e.getActionCommand();
    Object source = e.getSource();

    if (source == button_texture_garden) {
      if (currently_showing != SHOW_TEXTURE_GARDEN) {
        currently_showing = SHOW_TEXTURE_GARDEN;
        buttonBarColourUpdate();
        removeAll();
        addBasicGUIElements();
        addTGGUIElements();
        validate();
      }
    }

    if (source == button_texture_lab) {
      if (currently_showing != SHOW_TEXTURE_LAB) {
        currently_showing = SHOW_TEXTURE_LAB;
        buttonBarColourUpdate();
        removeAll();
        addBasicGUIElements();
        validate();
      }
    }

    if (source == button_texture_editor) {
      if (currently_showing != SHOW_TEXTURE_EDITOR) {
        currently_showing = SHOW_TEXTURE_EDITOR;
        buttonBarColourUpdate();
        removeAll();
        addBasicGUIElements();
        validate();
      }
    }

    if (source == button_texture_viewer) {
      if (currently_showing != SHOW_TEXTURE_VIEWER) {
        currently_showing = SHOW_TEXTURE_VIEWER;
        buttonBarColourUpdate();
        removeAll();
        addBasicGUIElements();
        addTVGUIElements();
        validate();
      }
    }

    if (source == button_col_invert_none) {
      TextureContainer tc = texture_array.getSelectedTextureContainer();
      tc.texture.setXORColour(0); // xor_colour = 0x000000;
      updateTextureUIDisplay(tc);
    }

    if (source == button_col_invert_all) {
      TextureContainer tc = texture_array.getSelectedTextureContainer();
      tc.texture.setXORColour(0xFFFFFF);
      updateTextureUIDisplay(tc);
    }

    if (source == button_mut_invert_all) {
      checkbox_mut_tex.setState(true);
      checkbox_mut_pal.setState(true);
      checkbox_mut_hei.setState(true);
      checkbox_mut_see.setState(true);
      setMutationSettingsFromCheckboxes();
    }

    if (source == button_mut_invert_none) {
      checkbox_mut_tex.setState(false);
      checkbox_mut_pal.setState(false);
      checkbox_mut_hei.setState(false);
      checkbox_mut_see.setState(false);
      setMutationSettingsFromCheckboxes();
    }

    // THE NEXT LOT SHOULD BE DONE SYNCHRONOUSLY, using messages...
    if (arg.equals(RESTART)) {
      message.send(Message.MSG_RESTART);
    }

    if (arg.equals(STATIC)) {
      message.send(Message.MSG_RANDOMISE);
    }

    if (arg.equals(CLEAR)) {
      message.send(Message.MSG_CLEAR);
    }

    if (arg.equals(STEP)) {
      global_settings.paused = false;
      global_settings.stepping = true;
    }

    if (arg.equals(SAVE_JPEG)) {
      saveImage();
    }

    if (arg.equals(SAVE_TEXT)) {
      saveText();
    }

    if (arg.equals(LOAD_TEXT)) {
      loadText();
    }

    if (arg.equals(NEW_CMAP)) {
      texture_array.getSelectedTextureContainer().texture.newColourMap();
      texture_array.getSelectedTextureContainer().texture.makeImage();
      current_canvas.repaint(); // does nothing?
    }
  }

  void setMutationSettingsFromCheckboxes() {
    global_settings.mut_tex = checkbox_mut_tex.getState();
    global_settings.mut_pal = checkbox_mut_pal.getState();
    global_settings.mut_hei = checkbox_mut_hei.getState();
    global_settings.mut_see = checkbox_mut_see.getState();
  }

  void saveImage() {
    if (development_version) {
      FileDialog fd = new FileDialog(frame, "Save JPEG as", FileDialog.SAVE);

      //fd.show();
      fd.setVisible(true);
      String returnedstring = fd.getFile();
      if (returnedstring != "") {
        // R2D.imageDump(returnedstring);
        try {
          FileOutputStream dest = new FileOutputStream(returnedstring);
          com.sun.image.codec.jpeg.JPEGImageEncoder je = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(dest);

          // BufferedImage bi = new BufferedImage(TextureArray.texture.texture_image.i);
          Image im = texture_array.getSelectedTextureContainer().texture.texture_image.i;
          BufferedImage bi = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_RGB);

          Graphics g = bi.getGraphics();
          g.drawImage(im, 0, 0, null);

          je.encode(bi);
          dest.close();
          // System.out.println("Done ImageDump!");

        } catch (Exception e) {
          Log.log(e.toString());
        }
      }
    }
  }

  // synch...
  void loadText() {
    FileDialog fd = new FileDialog(frame, "Load texture description", FileDialog.LOAD);

    fd.setVisible(true);
//    fd.show();
    String returnedstring = fd.getFile();
    if (returnedstring != "") {
      message.send(Message.MSG_LOAD, returnedstring);

      /*
      temp_string = returnedstring;
      
      texture_array.performOperationOnSelectedTexture(
                     new Executor() { 
                        public void execute(Object o) {
                           TextureContainer tc = (TextureContainer)o;
                           tc.loadTexture(temp_string);
                        }
                     }
                  );
      				*/
      //try {
      //FileOutputStream dest = new FileOutputStream(returnedstring);
      //texture_array.getSelectedTextureContainer().texture.program.saveText(dest);
      //dest.close();
      //} 

      //catch (Exception e) {

      //Log.log(e.toString());
      //}
    }
  }

  public void saveText() {
    FileDialog fd = new FileDialog(frame, "Save texture description as", FileDialog.SAVE);

    fd.setVisible(true);
//    fd.show();
    String returnedstring = fd.getFile();
    if (returnedstring != "") {
      try {
        FileOutputStream dest = new FileOutputStream(returnedstring);
        texture_array.getSelectedTextureContainer().texture.program.saveText(dest);
        dest.close();
      } catch (Exception e) {
        Log.log(e.toString());
      }
    }
  }

  public void adjustmentValueChanged(AdjustmentEvent adjustmentevent) {
    if (adjustmentevent.getAdjustable() == f_slider) {
      ReactionDiffusion.setF((int) f_slider.getValue());
      showAgentRate();
    }

    if (adjustmentevent.getAdjustable() == k_slider) {
      ReactionDiffusion.setK((int) k_slider.getValue());
      showInhibitorRate();
    }

    if (adjustmentevent.getAdjustable() == u_slider) {
      ReactionDiffusion.setMultU((int) u_slider.getValue());
      showAgentSpeed();
    }

    if (adjustmentevent.getAdjustable() == v_slider) {
      ReactionDiffusion.setMultV((int) v_slider.getValue());
      showInhibitorSpeed();
    }

    if (adjustmentevent.getAdjustable() == scaleu_slider) {
      ReactionDiffusion.setScaleU((int) scaleu_slider.getValue());
      showAgentScale();
    }

    if (adjustmentevent.getAdjustable() == scalev_slider) {
      ReactionDiffusion.setScaleV((int) scalev_slider.getValue());
      showInhibitorScale();
    }

    /*if (adjustmentevent.getAdjustable() == green_slider) {
    ReactionDiffusion.setGreenValue((int)green_slider.getValue());
    showGreenSliderValue();
    }*/

    if (adjustmentevent.getAdjustable() == red_slider) {
      texture_array.getSelectedTextureContainer().texture.setRedValue((int) red_slider.getValue());
      showRedSliderValue();
      texture_array.getSelectedTextureContainer().texture.setColour();
    }

    if (adjustmentevent.getAdjustable() == green_slider) {
      texture_array.getSelectedTextureContainer().texture.setGreenValue((int) green_slider.getValue());
      showGreenSliderValue();
      texture_array.getSelectedTextureContainer().texture.setColour();
    }

    if (adjustmentevent.getAdjustable() == blue_slider) {
      texture_array.getSelectedTextureContainer().texture.setBlueValue((int) blue_slider.getValue());
      showBlueSliderValue();
      texture_array.getSelectedTextureContainer().texture.setColour();
    }

    if (adjustmentevent.getAdjustable() == fluid_add_rate_slider) {
      FractalDrainage.setAddRate((int) fluid_add_rate_slider.getValue());
      showFluidAddRateValue();
    }

    if (adjustmentevent.getAdjustable() == fluid_seep_rate_slider) {
      FractalDrainage.setSeepRate((int) fluid_seep_rate_slider.getValue());
      showFluidSeepRateValue();
    }

    if (adjustmentevent.getAdjustable() == lower_threshold_slider) {
      FractalDrainage.setLowerThreshold((int) lower_threshold_slider.getValue());
      showLowerThresholdValue();
    }

    if (adjustmentevent.getAdjustable() == upper_threshold_slider) {
      FractalDrainage.setUpperThreshold((int) upper_threshold_slider.getValue());
      showUpperThresholdValue();
    }

    if (adjustmentevent.getAdjustable() == fluid_flow_rate_slider) {
      FractalDrainage.setFlowRate((int) fluid_flow_rate_slider.getValue());
      showFluidFlowRateValue();
    }

    if (adjustmentevent.getAdjustable() == ground_rising_slider) {
      FractalDrainage.setGroundRising((int) ground_rising_slider.getValue());
      showGroundRisingValue();
    }

    if (adjustmentevent.getAdjustable() == log_resistance_slider) {
      FractalDrainage.setLogResistance((int) log_resistance_slider.getValue());
      showLogResistanceValue();
    }

    if (adjustmentevent.getAdjustable() == dla_threshold_slider) {
      DiffusionLimitedAggregation.setThreshold((int) dla_threshold_slider.getValue());
      showDLAThreshold();
    }

    if (adjustmentevent.getAdjustable() == dla_density_slider) {
      DiffusionLimitedAggregation.setDensity((int) dla_density_slider.getValue());
      IsingModel.setDensity((int) dla_density_slider.getValue());
      showDLADensity();
    }

    if (adjustmentevent.getAdjustable() == dla_initial_value_slider) {
      DiffusionLimitedAggregation.setInitialValue((int) dla_initial_value_slider.getValue());
      showDLAInitialValue();
    }

    if (adjustmentevent.getAdjustable() == speed_slider) {
      texture_array.getSelectedTextureContainer().texture.setSpeed((int) speed_slider.getValue());

      showAniSpeed();
    }

    if (adjustmentevent.getAdjustable() == mutation_rate_slider) {
      global_settings.setMutationRate((int) mutation_rate_slider.getValue());

      showMutationRate();
    }
  }

  public void showAgentRate() {
    int temp = ReactionDiffusion.getF();
    f_label.setText("Rate(agent): " + temp);
    f_slider.setValue(temp);
  }

  public void showInhibitorRate() {
    int temp = ReactionDiffusion.getK();
    k_label.setText("Rate(inhibitor): " + temp);
    k_slider.setValue(temp);
  }

  public void showAgentSpeed() {
    int temp = ReactionDiffusion.getMultU();
    u_label.setText("Speed(agent): " + temp);
    u_slider.setValue(temp);
  }

  public void showInhibitorSpeed() {
    int temp = ReactionDiffusion.getMultV();
    v_label.setText("Speed(inhibitor): " + temp);
    v_slider.setValue(temp);
  }

  public void showAgentScale() {
    int temp = ReactionDiffusion.getScaleU();
    scaleu_label.setText("Scale(agent): " + temp);
    scaleu_slider.setValue(temp);
  }

  public void showInhibitorScale() {
    int temp = ReactionDiffusion.getScaleV();
    scalev_label.setText("Scale(inhibitor): " + temp);
    scalev_slider.setValue(temp);
  }

  public void showRedSliderValue() {
    int temp = texture_array.getSelectedTextureContainer().texture.getRedValue();
    red_label.setText("Red:" + temp);
    red_slider.setValue(temp);
  }

  public void showGreenSliderValue() {
    int temp = texture_array.getSelectedTextureContainer().texture.getGreenValue();
    green_label.setText("Green:" + temp);
    green_slider.setValue(temp);
  }

  public void showBlueSliderValue() {
    int temp = texture_array.getSelectedTextureContainer().texture.getBlueValue();
    blue_label.setText("Blue:" + temp);
    blue_slider.setValue(temp);
  }

  public void showFluidAddRateValue() {
    int temp = FractalDrainage.getAddRate();
    fluid_add_rate_label.setText("Add rate:" + temp);
    fluid_add_rate_slider.setValue(temp);
  }

  public void showFluidSeepRateValue() {
    int temp = FractalDrainage.getSeepRate();
    fluid_seep_rate_label.setText("Seep rate:" + temp);
    fluid_seep_rate_slider.setValue(temp);
  }

  public void showLowerThresholdValue() {
    int temp = FractalDrainage.getLowerThreshold();
    lower_threshold_label.setText("Lower threshold:" + temp);
    lower_threshold_slider.setValue(temp);
  }

  public void showUpperThresholdValue() {
    int temp = FractalDrainage.getUpperThreshold();
    upper_threshold_label.setText("Upper threshold:" + temp);
    upper_threshold_slider.setValue(temp);
  }

  public void showFluidFlowRateValue() {
    int temp = FractalDrainage.getFlowRate();
    fluid_flow_rate_label.setText("Flow rate:" + temp);
    fluid_flow_rate_slider.setValue(temp);
  }

  public void showGroundRisingValue() {
    int temp = FractalDrainage.getGroundRising();
    ground_rising_label.setText("Ground rising:" + temp);
    ground_rising_slider.setValue(temp);
  }

  void showLogResistanceValue() {
    int temp = FractalDrainage.getLogResistance();
    log_resistance_label.setText("Log Resistance:" + temp);
    log_resistance_slider.setValue(temp);
  }

  // DLA
  void showDLAThreshold() {
    int temp = DiffusionLimitedAggregation.getThreshold();
    dla_threshold_label.setText("Threshold:" + temp);
    dla_threshold_slider.setValue(temp);
  }

  void showDLADensity() {
    int temp = DiffusionLimitedAggregation.getDensity();
    dla_density_label.setText("Density:" + temp);
    dla_density_slider.setValue(temp);
  }

  void showDLAInitialValue() {
    int temp = DiffusionLimitedAggregation.getInitialValue();
    dla_initial_value_label.setText("Initial value:" + temp);
    dla_initial_value_slider.setValue(temp);
  }

  void showAniSpeed() {
    int temp = texture_array.getSelectedTextureContainer().texture.getSpeed();
    ani_speed_label.setText("Speed:" + temp);
    speed_slider.setValue(temp);
  }

  void showMutationRate() {
    int temp = global_settings.getMutationRate();
    mutation_rate_label.setText("Mutation:" + temp);
    mutation_rate_slider.setValue(temp);
  }

  void greyButtons() {
    //if (paused) {
    button_step.setEnabled(global_settings.paused);
    button_save_jpeg.setEnabled(global_settings.paused);
    button_new_cmap.setEnabled(global_settings.paused);
  }

  public void getDocBase() {
    // get Java version...
    java_version = System.getProperty("java.version");
    directory_separator = System.getProperty("file.separator");
    // Log.log("java_version: " + java_version);
    if (java_version.compareTo("1.2") < 0) {
      if (application) {
        // sound_enabled = false;
        // Log.log("Warning - sound disabled: Java " + java_version + " does not support sound in applications.");
      }
    }

    // application = false;
    if (application) {
      // Log.log("application");
      try {
        document_base = System.getProperty("user.dir");
        // URL temp = new URL("file:/" + document_base + "/");
        base_url = new URL("file:/" + document_base); //  + directory_separator);
        document_base += directory_separator;
        // application = true;
        Log.log("document_base:" + document_base);
      } catch (Exception e2) {
        Log.log("Error2: " + e2.toString());
      }
    } else {
      try {
        base_url = getCodeBase(); // applet
        document_base = base_url.toString();
        String base_url_s = base_url.toString(); // applet

        // trailing "."...?
        if (base_url_s.indexOf("/.") > 0) {
          // Log.log("Warning: slash-fullstop in URL - truncating URL to avoid bug.");
          // document_base = document_base.substring(0, document_base.length() - 2);
          // String .substring(0, document_base.length() - 2);
          base_url = new URL(base_url_s.substring(0, base_url_s.length() - 1));
        }

        // Log.log("Before:" + document_base);
        //if ((!development_version) && ((document_base.indexOf("rockz.co.uk") < 0) && (document_base.indexOf("/Java/Rockz") < 0))) { // applet?
        // crash!
        //for (long l = 0; l++ < Long.MAX_VALUE; ) { }
        //}
        // Log.log("After.");
      } catch (Exception e) {
        Log.log("Error1: " + e.toString()); // application...
      }
    }
  }

  public void updateTextureUIDisplay(TextureContainer tc) {
    Texture t = tc.texture;
    checkbox_hflip.setState(t.hflip);
    checkbox_vflip.setState(t.vflip);
    checkbox_height_map.setState(t.height_map);
    checkbox_invert_r.setState((t.xor_colour & 0xFF0000) != 0);
    checkbox_invert_g.setState((t.xor_colour & 0x00FF00) != 0);
    checkbox_invert_b.setState((t.xor_colour & 0x0000FF) != 0);

    showAniSpeed();
  }

  void buttonBarColourUpdate() {
    button_texture_garden.setForeground((currently_showing == SHOW_TEXTURE_GARDEN) ? Color.yellow : Color.lightGray);
    button_texture_garden.setBackground((currently_showing == SHOW_TEXTURE_GARDEN) ? Color.blue : Color.darkGray);

    button_texture_editor.setForeground((currently_showing == SHOW_TEXTURE_EDITOR) ? Color.yellow : Color.lightGray);
    button_texture_editor.setBackground((currently_showing == SHOW_TEXTURE_EDITOR) ? Color.blue : Color.darkGray);

    button_texture_lab.setForeground((currently_showing == SHOW_TEXTURE_LAB) ? Color.yellow : Color.lightGray);
    button_texture_lab.setBackground((currently_showing == SHOW_TEXTURE_LAB) ? Color.blue : Color.darkGray);

    button_texture_viewer.setForeground((currently_showing == SHOW_TEXTURE_VIEWER) ? Color.yellow : Color.lightGray);
    button_texture_viewer.setBackground((currently_showing == SHOW_TEXTURE_VIEWER) ? Color.blue : Color.darkGray);
  }

  // main method
  public static void main(String args[]) {
    TG _applet = new TG();

    _applet.application = true;

    _applet.applet = _applet;

    _applet.frame = new AppletFrame("TextureGarden", (Applet) _applet, 700, 830);
    _applet.frame.setVisible(true);
  }
}
