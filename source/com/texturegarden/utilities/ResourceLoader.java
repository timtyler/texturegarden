package com.texturegarden.utilities;

import com.texturegarden.TG;

/**
 * ResourceLoader - A class containing static methods which load resources -
 * <p>
 * @author Tim Tyler
 * @version 1.0
 */

public class ResourceLoader {
  private static ResourceLoader resource_loader;

  static boolean resinzip = !TG.development_version;

  /**
   * ResourceLoader, constructor.
   * <p>
   * The constructor is private.
   * There should not be any instances of this
   * class created outside the private one used
   * in the class.
   **/
  private ResourceLoader() {}

  /**
   * static initialization.
   * <p>
   * Create an instance of this class for later use...
   **/
  static {
    resource_loader = new ResourceLoader();
  }

  /**
   * Get an image.
   * <p>
   * Loads a specified image, either from the currect directory,
   * Or from inside the relevant jar file, whichever is appropriate.
   **/
  public static byte[] getByteArray(String archive, String dir, String leaf) {
    //Log.log("dir:" + dir);
    //Log.log("leaf:" + leaf);
    //Log.log("archive:" + archive);

    byte[] _array;
    if (resinzip) { //?
      _array = ZipLoader.getByteArray(archive, leaf);
    } else {
      _array = FileLoader.getByteArray(dir + leaf);
    }

    return _array;
  }
}
