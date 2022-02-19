package com.texturegarden.utilities;
public class StringParser {
  /*
  final static boolean debug = true;
  
  static String getSandwichedString(String s, String s_start, String s_end) {
    int i_start = s.indexOf(s_start);
  
  // debug("start:" + i_start);
  
    if (i_start < 0) {
       return "";
    }
  
    i_start += s_st'art.length();
  
    int i_end = s.indexOf(s_end, i_start);
  
  // debug("end:" + i_end);
  
    if (i_end < 0) {
       return "";
    }
  
    return s.substring(i_start,i_end);
  }
  */

  /*
     static boolean isStringPresent(String s, String ss) {
        int i_start = s.indexOf(ss);
     
        return (i_start >= 0);
     }
  	*/

  /*
     static String byteArrayToString(byte[] ba) {
        int len = ba.length;
        char c;
     
     // debug("LEN:" + len);
     
        StringBuffer sb = new StringBuffer();
     
        for (int i = 0; i < len; i++ ) {
           c = (char)(ba[i] & 255);
           if (c != 13) { // CRLF issues dealt with here :-(
              sb.append(c);
           }
        }
     
        return sb.toString(); // .substring(i_start,i_end);
     }
  */

  /**
   * Takes a list of objects and concatenates the toString()
   * representation of each object and returns the result.
   *
   * @param objects an array of objects
   *
   * @returns a string formed by concatenating string
   * representations of the objects in the array.
   */
  /*
     static public String concat(Object[] objects) {
        StringBuffer buffer = new StringBuffer ();
        for (int i=0; i < objects.length; i++) {
           buffer.append (objects[i].toString());
        }
     
        return buffer.toString();
     }
  	*/

  /**
   * Return a String with all occurrences of the "from" String
   * within "original" replaced with the "to" String.
   * If the "original" string contains no occurrences of "from",
   * "original" is itself returned, rather than a copy.
   *
   * @param original the original String
   * @param from the String to replace within "original"
   * @param to the String to replace "from" with
   *
   * @returns a version of "original" with all occurrences of
   * the "from" parameter being replaced with the "to" parameter.
   */
  /*
     public static String replace(String original, String from, String to) {
        int from_length = from.length();
     
        if (from_length != to.length()) {
           int start = original.indexOf(from);
        
           if (start == -1) {
              return original;
           }
        
           char[] original_chars = original.toCharArray();
        
           StringBuffer buffer = new StringBuffer(from_length);
        
           int copy_from = 0;
           while (start != -1) {
              buffer.append(original_chars, copy_from, start - copy_from);
              buffer.append(to);
              copy_from = start + from_length;
              start = original.indexOf(from, copy_from);
           }
        
           buffer.append(original_chars, copy_from, original_chars.length - copy_from);
        
           return buffer.toString();
        }
        else
        {
           if (from.equals(to)) {
              return original;
           }
        
           int start = original.indexOf(from);
        
           if (start == -1) {
              return original;
           }
        
           char[] main_char_array = original.toCharArray();
           char[] to_chars = to.toCharArray();
        
           int copy_from = 0;
           while (start != -1) {
              System.arraycopy(to_chars, 0, main_char_array, start, from_length);
              copy_from = start + from_length;
              start = original.indexOf(from, copy_from);
           }
        
           return new String(main_char_array);
        }
     }
  
  
     public static void main(String[] args) {
        // Rockz.main(null);
     
        debug(replace("1223456789-123456789-123456789", "12", "1"));
        do { } while (true);
     }
  
  
     final static void debug(String o) {
        System.out.println(o);
     }
  */

}