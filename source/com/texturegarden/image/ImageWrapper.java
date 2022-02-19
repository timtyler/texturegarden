package com.texturegarden.image;

// intended to be used primarily for MemoryImageSource'd images.

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

import com.texturegarden.TG;

// Don't try writing to JPEGs.  It will have no effect...
public class ImageWrapper {
  public Image i;
  public int[] source;
  public int width;
  public int height;
  public MemoryImageSource mis;

  static ColorModel cm = new DirectColorModel(32, 0xFF0000, 0xFF00, 0xFF);

  static Toolkit toolkit;

  static {
    toolkit = Toolkit.getDefaultToolkit();
  }

  // Constructor...
  public ImageWrapper(ImageWrapper _i) { // clone...
    width = _i.getWidth(null);
    height = _i.getHeight(null);
    int[] in_pix = _i.getArray();
    int[] out_pix = new int[in_pix.length];
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        out_pix[i + width * j] = in_pix[i + width * j];
      }
    }

    createImageFromArray(out_pix, width, height);
  }

  // duplicate existing
  public ImageWrapper(Image _i) {
    setImage(_i);
  }

  // boolean?  What is this for?
  public ImageWrapper(int[] a, int w, int h, boolean x) {
    mis = new MemoryImageSource(w, h, a, 0, w);
    mis.setAnimated(true);
    i = toolkit.createImage(mis);
  }

  public ImageWrapper(int[] a, int w, int h) {
    createImageFromArray(a, w, h);
  }

  public void createImageFromArray(int[] a, int w, int h) {
    source = a;
    width = w;
    height = h;

    //ttip = new TTImageProducer(width, height, cm, source);
    //ttip = null; // 
    //i = toolkit.createImage(ttip);
    // mis = new MemoryImageSource(w, h, ColorModel.getRGBdefault(), a, 0, w);

    mis = new MemoryImageSource(width, height, cm, source, 0, width);
    mis.setAnimated(true);
    mis.setFullBufferUpdates(true);
    i = toolkit.createImage(mis);
    // update();
  }

  public void update() {
    mis.newPixels(); // mis.newPixels(0,0,width,height >> 1);

    /// ttip.startProduction();
    // ttip.update();
    // i.flush();

    // doesn't work in M$ JVM....
    // creating a new image is pathetic - but apparently necessary... :-(
    // mis = new MemoryImageSource(width, height, cm, source, 0, width);
    //mis.setAnimated();
    // i = toolkit.createImage(mis);
  }

  // used for JPEGs...
  void setImage(Image _i) {
    i = _i;
    width = _i.getWidth(null);
    height = _i.getHeight(null);
    source = null;
  }

  Image getImage() {
    return i;
  }

  int getWidth() {
    return width;
  }

  int getHeight() {
    return height;
  }

  int getWidth(Object o) {
    return i.getWidth(null);
  }

  int getHeight(Object o) {
    return i.getHeight(null);
  }

  public int[] getSource() {
    return source;
  }

  public int[] getArray() {
    if ((source == null) || (source.length < 1)) {
      width = getWidth(null);
      height = getHeight(null);

      source = new int[width * height];

      //debug("Making new image:" + (width * height));

      PixelGrabber pg = new PixelGrabber(i, 0, 0, width, height, source, 0, width);
      try {
        pg.grabPixels();
      } catch (InterruptedException e) {
        debug(e.toString());
      }
    }

    return source;
  }

  public static void main(String[] args) {
    TG.main(null);
  }

  static void debug(String o) {
    System.out.println(o);
  }
}
