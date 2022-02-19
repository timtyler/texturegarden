/**
 * A class containing static methods which load images -
 * including images within jar files...
 * <p>
 * @author Tim Tyler
 * @version 1.0
 **/

package com.texturegarden.utilities;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipLoader {
  private static ZipLoader zip_loader;

  /**
   * ZipLoader, constructor.
   * <p>
   * The constructor is private.
   * There should not be any instances of this
   * class created outside the private one used
   * in the class.
   **/
  private ZipLoader() {}

  /**
   * static initialization.
   * <p>
   * Create an instance of this class for later use...
   **/
  static {
    zip_loader = new ZipLoader();
  }

  /**
   * Get an image.
   * <p>
   * Loads a specified image, either from the currect directory,
   * Or from inside the relevant jar file, whichever is appropriate.
   **/
  public static byte[] getByteArray(String archive, String name) {
    // Log.log("IN:" + archive + " ... " + name);
    InputStream in;

    byte[] _array;
    byte[] _array2;
    // byte[] _new_array;
    // byte[] _second_array;

    int _array_size;
    int _array2_size;
    //int _second_array_size;
    // int _new_array_size;
    //int _bytes_read;
    //int _more_bytes_read;

    ByteArrayOutputStream bytes;
    ByteArrayOutputStream bytes2;

    ZipEntry entry;

    ByteArrayInputStream bais;
    ZipInputStream zis;

    try {
      // only need to load file once...
      // debug("Starting to load "+ name + ".");
      in = zip_loader.getClass().getResourceAsStream(archive);

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

    try {
      bais = new ByteArrayInputStream(_array);
      zis = new ZipInputStream(bais);
      do {
        entry = zis.getNextEntry();
      } while (!entry.getName().equals(name));

      bytes2 = new ByteArrayOutputStream();
      _array2_size = 1024; // choose a size...
      _array2 = new byte[_array2_size];

      int rb;

      while ((rb = zis.read(_array2, 0, _array2_size)) > -1) {
        bytes2.write(_array2, 0, rb);
      }

      bytes2.close();

      _array2 = bytes2.toByteArray();
    } catch (IOException e) {
      Log.log(e.getMessage());
      return null;
    }

    return _array2;
  }
}
