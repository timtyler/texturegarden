package com.texturegarden.xml.driver;
public class StringUtilitiesForSAXDriver {
  static boolean isWhiteSpace(char[] ca, int n) {
    for (int i = 0; i < n; i++) {
      if (ca[i] > ' ') {
        return false;
      }
    }

    return true;
  }

  static boolean isWhiteSpace(String string) {
    final char[] ca = string.toCharArray();

    return isWhiteSpace(ca, ca.length);
  }
  
  static boolean isXMLElementPart(char c) {
    if (c == '-') {
      return true;
    }
    if (c == ':') {
      return true;
    }

    return Character.isJavaIdentifierPart(c);
  }

}
