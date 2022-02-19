/**
 * A class containing static methods which load images -
 * including images within jar files...
 * <p>
 * @author Tim Tyler
 * @version 1.11
 **/

package com.texturegarden.utilities;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.texturegarden.image.ImageWrapper;

public class ImageLoader {
  private static ImageLoader image_loader;
  private static Toolkit toolkit;
  /**
   * ImageLoader, constructor.
   * <p>
   * The constructor is private.
   * There should not be any instances of this
   * class created outside the private one used
   * in the class.
   **/
  private ImageLoader() {}

  /**
   * static initialization.
   * <p>
   * Create an instance of this class for later use...
   **/
  static {
    image_loader = new ImageLoader();
    // debug("IMAGELOADER:" + image_loader); // ok...
  }

  /**
   * Get an image.
   * <p>
   * Loads a specified image, either from the currect directory,
   * Or from inside the relevant jar file, whichever is appropriate.
   **/
  public static ImageWrapper getImage(String name) {
    InputStream in;
    ImageWrapper image;
    boolean ispng;

    //if (!Rockz.directory_separator.equals("/")) {
    //name = StringParser.searchAndReplace(name, "/", Rockz.directory_separator);
    //}

    // debug("Loading: " + name + ".");

    byte[] _array;
    int _array_size;

    toolkit = Toolkit.getDefaultToolkit();
    ispng = (name.indexOf(".png") > 0);

    // debug("Starting to load "+ name + ".");

    try {
      in = image_loader.getClass().getResourceAsStream(name);
      if (in == null) {
        debug("Problem locating image file: " + name);
      }

      if (ispng) {
        //com.sixlegs.image.png.PngImage png = new com.sixlegs.image.png.PngImage(in);
        //image = toolkit.createImage(png);
        // image = com.sun.jimi.core.Jimi.createImage(in);
        image = null;

        //        try {
        //          // base = getCodeBase();
        //          // java.io.InputStream inputstream = (new URL(base, pngPar)).openStream();
        //          PNGReader rdr = new PNGReader(in);
        //          int w = rdr.getWidth();
        //          // debug("111");
        //          int h = rdr.getHeight();
        //          // image = toolkit.createImage(new MemoryImageSource(w, h, rdr.getPixels(), 0, w));
        //          image = new TTImage(rdr.getPixels(), w, h, true);
        //          // debug("111");
        //          // debug("PNG->NULL?:" + image.i);
        //        } catch (IOException ioexception) {
        //          debug("Problem loading image: " + name);
        //          debug("" + ioexception);
        //        }
      } else {
        // Thanks to Karl Schmidt for the followig code...
        ByteArrayOutputStream bytes;

        bytes = new ByteArrayOutputStream();
        _array_size = 1024; // choose a size...
        _array = new byte[_array_size];

        int rb;

        while ((rb = in.read(_array, 0, _array_size)) > -1) {
          bytes.write(_array, 0, rb);
        }

        bytes.close();

        _array = bytes.toByteArray();

        image = new ImageWrapper(toolkit.createImage(_array)); // JPEG...

        // !?!?!?!?
        // do { } while(!toolkit.prepareImage(image.i, -1, -1, null));
        // debug("JPEG->NULL?:" + image.i);
      }

      in.close();

      // debug("Finished loading: " + name + ".");

      return image;
    } catch (IOException e) {
      debug(e.getMessage());
      return null;
    }
  }

  /** 
   * If you want to wait for your images to load, you should
   * seriously consider using the ImageLoadingManager class...
   */
  public static ImageWrapper getImageNow(String name) {
    ImageWrapper temp_image = getImage(name);
    do {} while (!toolkit.prepareImage(temp_image.i, -1, -1, null));
    // debug("Finished loading: " + name + ".");
    return temp_image;
  }

  /**
    * Returns the Toolkit used...
    * <p>
    * Allow access to the toolkit used (i.e. access from outside this class)...
    **/
  public static Toolkit getToolkit() {
    return toolkit;
  }

  final static void debug(String o) {
    System.out.println(o);
  }

}
