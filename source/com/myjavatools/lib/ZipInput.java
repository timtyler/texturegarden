package com.myjavatools.lib;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * <p>Title: MyJavaTools: ZippedInput</p>
 * <p>Description: Extends ZipInputStream functionality.
 * Good for Java 1.4 and up.</p>
 * <p>Copyright: This is public domain;
 * The right of people to use, distribute, copy or improve the contents of the
 * following may not be restricted.</p>
 *
 * @version 1.4
 * @author Vlad Patryshev
 */

public class ZipInput {
  ZipInputStream zis = null;
  ZipEntry ze = null;

  /**
   * initializes ZipInput with InputStream
   * @param is InputStream the source of data
   */
  public ZipInput(InputStream is) {
    zis = new ZipInputStream(is);
  }

  /**
   * initializes ZipInput with data
   * @param data byte[] the source data represented as bytes
   */
  public ZipInput(byte[] data) {
    this(new ByteArrayInputStream(data));
//    System.out.println("crc is " + Long.toHexString(Tools.crc32(data)));
  }

  /**
   * initializes ZipInput with data
   * @param data char[] the source data represented as chars (a byte per char)
   */
  public ZipInput(char[] data) {
    this(Tools.toBytes(data));
//    System.out.println("crc is " + Long.toHexString(Tools.crc32(Tools.toBytes(data))));
  }

  /**
   * moves to next ZipEntry
   * @throws IOException when there are problems reading the input
   * @return boolean true if there is a next entry; false otherwise
   */
  public boolean next()
    throws IOException {
    return (ze = zis.getNextEntry()) != null;
  }

  /**
   * gets current ZipEntry
   * @return ZipEntry current entry
   */
  public ZipEntry getEntry() {
    return ze;
  }

  /**
   * gets the name of current ZipEntry
   * @return String the entry name
   */
  public String getEntryName() {
    return ze.getName();
  }

  /**
   * gets timestamp of current zip entry
   * @return long the entry timestamp (ms since 1/1/70)
   */
  public long getEntryTime() {
  return ze.getTime();
  }

  /**
   * gets the size of current entry data
   * @return long the number of bytes in current entry data
   */
  public long getEntrySize() {
  return ze.getSize();
  }

  /**
   * checks whether current ZipEntry is a directory
   * @return boolean true if it is directory, false otherwise
   */
  public boolean isEntryDirectory() {
  return ze.isDirectory();
  }

  /**
   * retrieves the contents of current ZipEntry data
   * @throws IOException if could not read the data
   * @return byte[] the contents of current entry data
   */
  public byte[] getBytes()
  throws IOException {
    if (ze == null || ze.isDirectory()) return new byte[0];
    int size = (int)getEntrySize();
    if (size < 0) { // Yeah, right... unknown length. How can we treat it, I wonder...
      List chunkList = new ArrayList();
      int bcount = 0;
      int total  = 0;
      final int bufsize = 65500;
      while (bcount >= 0) {
        byte[] buffer = new byte[bufsize];
        chunkList.add(buffer);
        int pos = 0;
        while ( (bcount = zis.read(buffer, pos, buffer.length - pos)) >
0) {
//      System.out.print("-" + bcount + "- ");
          pos += bcount;
        }
        total += pos;
      }
      byte[] buffer = new byte[total];
      int pos = 0;
      for (Iterator i = chunkList.iterator(); i.hasNext();) {
        System.arraycopy(i.next(), 0, buffer, pos, Math.min(bufsize, total - pos));
        pos += bufsize;
      }
      return buffer;
    }
    byte[] buffer = new byte[(int)getEntrySize()];
    int bcount = 0;
    int pos = 0;
    while((bcount = zis.read(buffer, pos, buffer.length - pos)) > 0) {
      pos += bcount;
    }

    return buffer;
  }

  /**
   * gets the contents of current ZipEntry data as characters
   * @param encoding String encoding to use
   * @throws IOException when could not read the data
   * @return String unzipped and decoded data
   */
  public String getChars(String encoding)
    throws IOException {
    return Tools.decode(getBytes(), encoding);
  }
}
