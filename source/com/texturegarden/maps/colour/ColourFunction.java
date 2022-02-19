package com.texturegarden.maps.colour;

public class ColourFunction {
  public int xor;
  public ColourMap colour_map;

  public ColourFunction(ColourMap colour_map, int xor) {
    this.xor = xor;
    this.colour_map = colour_map;
  }

  public int getColour(int v) {
    return v & 0xFFFF;
  }
}
