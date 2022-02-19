/**
 * A class containing static methods which load images -
 * including images within jar files...
 * <p>
 * @author Tim Tyler
 * @version 1.0
 **/

package com.texturegarden.utilities;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileLoader {
  private static FileLoader file_loader;

  /**
   * FileLoader, constructor.
   * <p>
   * The constructor is private.
   * There should not be any instances of this
   * class created outside the private one used
   * in the class.
   **/
  private FileLoader() {}

  /**
   * static initialization.
   * <p>
   * Create an instance of this class for later use...
   **/
  static {
    file_loader = new FileLoader();
  }

  /**
   * Get an image.
   * <p>
   * Loads a specified image, either from the currect directory,
   * Or from inside the relevant jar file, whichever is appropriate.
   **/
  public static byte[] getByteArray(String name) {
    InputStream in;

    byte[] _array;
    // byte[] _new_array;
    // byte[] _second_array;

    int _array_size;
    // int _second_array_size;
    //int _new_array_size;
    //int _bytes_read;
    //int _more_bytes_read;

    try {
      //Log.log("Starting to load " + name);
      in = file_loader.getClass().getResourceAsStream(name);
      // Log.log("in: "+ in);

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

      in.close();
    } catch (IOException e) {
      Log.log(e.getMessage());
      return null;
    }

    return _array;
  }
}
