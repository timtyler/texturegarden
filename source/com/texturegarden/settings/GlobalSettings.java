package com.texturegarden.settings;
/**
 *  GlobalSettings - Tim Tyler 2000-2003
 */

/*
 * ToDo:
 *
 * MutationRate...
 *
 */

public class GlobalSettings {
  public int mutation_rate = 50;

  public boolean mut_tex = true;
  public boolean mut_hei = true;
  public boolean mut_pal = true;
  public boolean mut_see = true;

  public boolean paused = false;
  public boolean stepping = false;
  public boolean colour_swap = true;
  public boolean colour_invert = false;
  public boolean reverse_fg = false;
  public boolean reverse_bg = false;

  public int getMutationRate() {
    return mutation_rate;
  }

  public void setMutationRate(int mr) {
    mutation_rate = mr;
  }
}
