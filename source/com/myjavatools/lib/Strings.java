/**
 *
 * <p>Title: MyJavaTools: String Tools</p>
 * <p>Description: String and CharSequence handling tools.
 * Good for Java 1.4 and up.</p>
 * <p>Copyright: This is public domain;
 * The right of people to use, distribute, copy or improve the contents of the
 * following may not be restricted.</p>
 *
 * @version 1.4
 * @author Vlad Patryshev
 */

package com.myjavatools.lib;
import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;

public class Strings extends Objects
{
  protected Strings() {}

  /**
   * CharSequence version of indexOf
   *
   * @param s CharSequence
   * @param c char
   * @param fromIndex int
   * @return int
   *
   * see java.lang.String#indexOf() for description
   */
  public static int indexOf(CharSequence s, char c, int fromIndex) {
    for (int i = fromIndex; i < s.length(); i++) {
      if (s.charAt(i) == c) return i;
    }

    return -1;
  }

  /**
   * CharSequence version of indexOf
   *
   * @param s CharSequence
   * @param c char

   * @return int
   *
   * see java.lang.String.indexOf(char) for description
   */
  public static int indexOf(CharSequence s, char c) {
    return indexOf(s, c, 0);
  }

  /**
   * CharSequence version of lastIndexOf
   *
   * @param s CharSequence
   * @param c char
   * @return int
   *
   * see java.lang.String.lastIndexOf(char) for description
   */
  public static int lastIndexOf(CharSequence s, char c) {
    for (int i = s.length() - 1; i >= 0; i--) {
      if (s.charAt(i) == c)return i;
    }
    return -1;
  }

  /**
   * CharSequence version of indexOf
   *
   * @param sequence CharSequence
   * @param subsequence CharSequence
   * @param fromIndex int
   * @return int
   *
   * see java.lang.String.indexOf(String) for description
   */
  public static int indexOf(CharSequence sequence,
                            CharSequence subsequence,
                            int fromIndex) {
    if (subsequence.length() == 0) return fromIndex;
    char c0 = subsequence.charAt(0);
    int subLength = subsequence.length();
    int lastIndex = sequence.length() - subsequence.length();
    for (int i = indexOf(sequence, c0, fromIndex);
        0 <= i && i <= lastIndex;
             i = indexOf(sequence, c0, i+1)) {
      if (startsWith(sequence.subSequence(i, i + subLength),
                     subsequence)) {
        return i;
      }
    }
    return -1;
  }

 /**
 * CharSequence version of indexOf
 *
 * @param sequence CharSequence
 * @param subsequence CharSequence
 * @return int
 *
 * see java.lang.String.indexOf(String) for description
 */
  public static int indexOf(CharSequence sequence, CharSequence subsequence) {
    return indexOf(sequence, subsequence, 0);
  }

  /**
   * CharSequence version of startsWith
   *
   * @param sequence CharSequence
   * @param subsequence CharSequence
   * @return boolean
   *
   * see String.starstWith(String) for description
   */
  public static boolean startsWith(CharSequence sequence, CharSequence subsequence) {

    if (sequence.length() < subsequence.length()) {
      return false;
    }

    for (int i = 0; i < subsequence.length(); i++) {
      if (sequence.charAt(i) != subsequence.charAt(i)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Writes CharSequence to Writer (Hello, Sun! Ever heard of CharSequence class?)
   *
   * @param writer Writer
   * @param cs CharSequence
   * @throws IOException
   */
  public static void write(Writer writer, CharSequence cs)
  throws IOException {
    if (cs == null) return;

    for (int i = 0; i < cs.length(); i++) {
      writer.write(cs.charAt(i));
    }
  }

  /**
   * Checks whether a CharSequence does not contain anything except whitespaces and the like.
   *
   * @param s the sequence to check
   * @return true if empty
   *
   * <br><br><b>Examples</b>:
   * <li><code>isAlmostEmpty(""), isAlmostEmpty(null), isAlmostEmpty("\n   \r \n")</code> all return <b>true</b>;</li>
   * <li><code>isAlmostEmpty("."), isAlmostEmpty("Contains data!")</code> returns <b>false</b>.</li>
   */
  public static boolean isAlmostEmpty(CharSequence s) {
    if (isEmpty(s)) return true;

    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) > ' ') return false;
    }
    return true;
  }

  /**
   * Chooses a string representation of a non-empty object out of two objects.
   *
   * @param o1 the first candidate
   * @param o2 the second candidate
   * @return the first one that is not empty, converted to String
   *
   * <br><br><b>Examples</b>:
   * <li><code>oneOf(null, "xyz")</code> returns "xyz";</li>
   * <li><code>oneOf("abc", "xyz")</code> returns "abc";</li>
   * <li><code>oneOf("", null)</code> return null;</li>
   * <li><code>oneOf("abc", null)</code> returns "abc".</li>
   */
  public static String oneOf(Object o1, Object o2) {
    return !isEmpty(o1) ? o1.toString() : o2 == null ? null : o2.toString();
  }

  /**
   * Chooses a string representation of a non-empty object out of three objects.
   *
   * @param o1 the first candidate
   * @param o2 the second candidate
   * @param o3 the third candidate
   * @return the string representation of the first one that is not empty
   *
   * <br><br><b>Examples</b>:
   * <li><code>oneOf(null, "", "xyz")</code> returns "xyz";</li>
   * <li><code>oneOf("abc", null, "xyz")</code> returns "abc";</li>
   * <li><code>oneOf("", "def", null)</code> returns "def";</li>
   * <li><code>oneOf("", null, "")</code> returns "".</li>
   */
  public static String oneOf(Object o1, Object o2, Object o3) {
    return !isEmpty(o1) ? o1.toString() :
           !isEmpty(o2) ? o2.toString() :
           o3   == null ? null : o3.toString();
  }

  /**
   * Chooses a string representation of a non-empty object out of three objects.
   *
   * @param o1 the first candidate
   * @param o2 the second candidate
   * @param o3 the third candidate
   * @param o4 the fourth candidate
   * @return the string representation of the first one that is not empty
   *
   * <br><br><b>Examples</b>:
   * <li><code>oneOf(null, "", null, "xyz")</code> returns "xyz";</li>
   * <li><code>oneOf("abc", null, "pqr", "xyz")</code> returns "abc";</li>
   * <li><code>oneOf("", "def", null, "xyz")</code> returns "def";</li>
   * <li><code>oneOf("", null, "", null)</code> returns null.</li>
   */
  public static String oneOf(Object o1, Object o2, Object o3, Object o4) {
    return !isEmpty(o1) ? o1.toString() :
           !isEmpty(o2) ? o2.toString() :
           !isEmpty(o3) ? o3.toString() :
           o4   == null ? null : o4.toString();
  }

//  /**
//   * The list of alphabetic characters (English only)
//   *
//   * {@value}
//   */
//  public static final String ALPHA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVXYZ";

  /**
   * Checks whether a character is a latin letter.
   *
   * @param c character to check
   * @return true if it is so
   *
   * @deprecated use Character.isJavaIdentifierStart(char)
   *
   * <br><br><b>Examples</b>:
   * <li><code>isAlpha('a'), isAlpha('O'), isAlpha('I'), isAlpha('l')</code> return <b>true</b>;</li>
   * <li><code>isAlpha('+'), isAlpha('0'), isAlpha('|'), isAlpha('1')</code> return <b>false</b>.</li>
   */
  public static boolean isAlpha(char c) {
//    return ALPHA.indexOf(c) >= 0;
    return Character.isJavaIdentifierStart(c);
  }

  /**
   * Checks whether a character is a latin vowel.
   *
   * @param c the char to tests
   * @return true if the character is one of "aeiaouAEIAOU"
   */
  public static boolean isVowel(char c) {
    return "aeiouAEIOU".indexOf(c) >= 0;
  }

  /**
   * Checks whether a CharSequence contains any latin letters.
   *
   * @param s CharSequence to check
   * @return true if it is so
   *
   * <br><br><b>Examples</b>:
   * <li><code>hasAlpha("a"), hasAlpha("2OO2"), hasAlpha("This is a string")</code> return <b>true</b>;</li>
   * <li><code>hasAlpha("+"), hasAlpha("1900"), hasAlpha("|1!*")</code> return <b>false</b>.</li>
   */
  public static boolean hasAlpha(CharSequence s) {
    for (int i = 0; i < s.length(); i++) {
      if (Character.isJavaIdentifierStart(s.charAt(i))) return true;
    }
    return false;
  }

  /**
   * Counts the number of occurrences of char c in CharSequence s.
   *
   * @param s the string to scan
   * @param c the character to count
   * @return the number of occurrences
   *
   * <br><br><b>Example</b>:
   * <li><code>countChar("Goodness me, the clock has struck", 'o')</code> returns 3.</li>
   */
  public static int countChar(CharSequence s, char c) {
    int n = 0;
    for (int i = indexOf(s, c); i >= 0; i=indexOf(s, c, i+1)) {
      n++;
    }
    return n;
  }

  /**
   * Calculates how many lines the text contains.
   * Lines are supposed to be separated by '\n' character.
   *
   * @param s the CharSequence with text
   * @return number of lines in the string (separated by '\n')
   *
   * <br><br><b>Examples</b>:
   * <li><code>textHeight("One\nTwo\nThree")</code> returns 3;</li>
   * <li><code>textHeight("\nOne\nTwo\nThree\n")</code> returns 5.</li>
   */
  public static int textHeight(CharSequence s) {
    return countChar(s, '\n') + 1;
  }

  /**
   * Calculates how many horizontal lines will the text take in a textarea.
   * This is the maximum line length for all lines in the text.
   * Lines are separated by '\n' character.
   *
   * @param s the CharSequence with text
   * @return maximum line length in the text
   *
   * <br><br><b>Example</b>:
   * <li><code>textWidth("One\nTwo\nThree")</code> returns 5.</li>
   */
  public static int textWidth(CharSequence s) {
    int n = 1;
    int curPos = 0;
    while (curPos < s.length()) {
      int nextPos = indexOf(s, '\n', curPos);
      if (nextPos < 0) nextPos = s.length();
      if (n < nextPos - curPos) n = nextPos - curPos;
      curPos = nextPos+1;
    }
    return n;
  }

  /**
   * Calculates the number of words in the CharSequence.
   * This is just the number of tokens separated by default separators.
   *
   * @param s the CharSequence to analyze
   * @return number of words
   *
   * <br><br><b>Examples</b>:
   * <li><code>wordCount("This is life!")</code> returns 3;</li>
   * <li><code>wordCount("C'est la vie !")</code> returns 4, but for a wrong reason.</li>
   */
  public static int wordCount(CharSequence s) {
    return (new StringTokenizer(s.toString())).countTokens();
  }

  /**
   * Counts leading spaces in a char sequence
   *
   * @param s
   * @return number of leading spaces
   *
   * <br><br><b>Example</b>:
   * <li><code>countLeadingSpaces(" this is a string   ")</code> returns 1.</li>
   */
  public static int countLeadingSpaces(CharSequence s) {
    int l = s.length();
    int n = 0;
    while (n < l && s.charAt(n) == ' ') n++;
    return n;
  }

  /**
   * Counts trailing spaces in a char sequence
   * @param s
   * @return number of trailing spaces
   *
   * <br><br><b>Example</b>:
   * <li><code>countTrailingSpaces(" this is a string   ")</code> returns 3.</li>
   */
  public static int countTrailingSpaces(CharSequence s) {
    int l = s.length();
    int n = 0;
    while (n < l && s.charAt(l - n - 1) == ' ') n++;
    return n == s.length() ? 0 : n;
  }

  /**
   * Fills a string with a character
   *
   * @param c
   * @param n
   * @return a new string consisting of character c repeated n times
   *
   * <br><br><b>Example</b>:
   * <li><code>fill("*", 10)</code> returns "**********".</li>
   */
  public static String fill(char c, int n) {
    char[] data = new char[n];
    int i;
    for (i = 0; i < n; i++) {
      data[i] = c;
    }
    return new String(data);
  }

  /**
   * "fast int to string hex" conversion array
   */
  private static final String[] HEX = {
      "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0a", "0b", "0c", "0d", "0e", "0f",
      "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1a", "1b", "1c", "1d", "1e", "1f",
      "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2a", "2b", "2c", "2d", "2e", "2f",
      "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3a", "3b", "3c", "3d", "3e", "3f",
      "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4a", "4b", "4c", "4d", "4e", "4f",
      "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "5a", "5b", "5c", "5d", "5e", "5f",
      "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6a", "6b", "6c", "6d", "6e", "6f",
      "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "7a", "7b", "7c", "7d", "7e", "7f",
      "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "8a", "8b", "8c", "8d", "8e", "8f",
      "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9a", "9b", "9c", "9d", "9e", "9f",
      "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "aa", "ab", "ac", "ad", "ae", "af",
      "b0", "b1", "b2", "b3", "b4", "b5", "b6", "b7", "b8", "b9", "ba", "bb", "bc", "bd", "be", "bf",
      "c0", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "ca", "cb", "cc", "cd", "ce", "cf",
      "d0", "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "d9", "da", "db", "dc", "dd", "de", "df",
      "e0", "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8", "e9", "ea", "eb", "ec", "ed", "ee", "ef",
      "f0", "f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "fa", "fb", "fc", "fd", "fe", "ff",
  };

  /**
   * Converts a byte to hex string.
   *
   * @param b the byte
   * @return b representation as a two-character hex string
   *
   * <br><br><b>Example</b>:
   * <li><code>toHex(155)</code> returns "9b".</li>
   */
  public static String toHex(byte b) {
    int i = b;
    return HEX[(i&255)];
  }

  /**
   * Converts an integer to hex string. It is the same as Integer.toHexString().
   *
   * @param i the integer
   * @return i representation as a hex string
   *
   * <br><br><b>Example</b>:
   * <li><code>toHex(1234)</code> returns "4d2".</li>
   */
  public static String toHex(int i) {
    return Integer.toHexString(i);
  }

  /**
   * Converts a char to hex string
   *
   * @param ch the char
   * @param up if true, use upper case, otherwise lower
   * @return i representation as a four-character hex string
   *
   * <br><br><b>Examples</b>:
   * <li><code>toHex('\u005cu12bc', true)</code> returns "12BC";</li>
   * <li><code>toHex('\u005cu00af', false)</code> returns "00af".</li>
   */
  public static String toHex(char ch, boolean up) {
    String hex = "0000" + Integer.toHexString(ch);
    if (up) hex = hex.toUpperCase();
    return hex.substring(hex.length() - 4);
  }

  /**
   * Converts a char to hex string
   *
   * @param ch the char
   * @return a four-character string (lower case)
   *
   * <br><br><b>Example</b>:
   * <li><code>toHex('\u005cu00af')</code> returns "00af".</li>
   */
  public static String toHex(char ch) {
    return toHex(ch, false);
  }

  /**
   * Converts a CharSequence to hex string (character by character)
   *
   * @param s the CharSequence
   * @param up if true, use upper case, otherwise lower
   * @return s representation as a hex string
   *
   * <br><br><b>Examples</b>:
   * <li><code>toHex("kl\u005cu12bc", true)</code> returns "006B006C12BC";</li>
   * <li><code>toHex("kl\u005cu12bc", true)</code> returns "006b006c12bc".</li>
   */
  public static String toHex(CharSequence s, boolean up) {
    StringBuffer b = new StringBuffer();
    for (int i = 0; i < s.length(); i++) {
      b.append(toHex(s.charAt(i), up));
    }
    return b.toString();
  }


  /**
   * Converts a character to its Java octal encoding format: \\o[o][o]
   *
   * @param c the character
   * @return a string
   *
   * <br><br><b>Example</b>:
   * <li><code>toJavaOctalEncoding('\n')</code> returns "\\12".</li>
   */
  public static String toJavaOctalEncoding(char c) {
    return "\\" + Integer.toString(c, 8);
  }

  /**
   * Converts a character to its Java hex encoding format: \\uxxxx
   *
   * @param c the character
   * @return a string, encoded c representation
   *
   * <br><br><b>Example</b>:
   * <li><code>toJavaHexEncoding('\u005cu00af')</code> returns "\\u00af".</li>
   */
  public static String toJavaHexEncoding(char c) {
    return "\\u" + toHex(c);
  }

  /**
   * Converts a character to its Java hex encoding format: \\uxxxx
   *
   * @param c the character
   * @param up if true, use upper case, otherwise lower
   * @return a string, encoded c representation
   *
   * <br><br><b>Example</b>:
   * <li><code>toJavaHexEncoding('\u005cu00af', false)</code> returns "\\u00af".</li>
   */
  public static String toJavaHexEncoding(char c, boolean up) {
    return "\\u" + toHex(c, up);
  }

  /**
   * Converts a character to how it should be represented in properties files
   *
   * @param c the character
   * @param up if true, use upper case, otherwise lower
   * @return a string, encoded c representation
   *
   * <br><br><b>Examples</b>:
   * <li><code>toPropertiesEncoding('\u005cu00af', false)</code> returns "\\u00af";</li>
   * <li><code>toPropertiesEncoding('\u005cu00af', true)</code> returns "\\u00AF";</li>
   * <li><code>toPropertiesEncoding('a', false)</code> returns "a".</li>
   */
  public static String toPropertiesEncoding(char c, boolean up) {
    return (c > 0x7f || c < ' ') ? toJavaHexEncoding(c, up) : ("" + c);
  }

  /**
   * Converts a character to how it should be represented in properties files
   *
   * @param c the character
   * @return a string, encoded c representation
   *
   * <br><br><b>Examples</b>:
   * <li><code>toPropertiesEncoding('\u005cu00af')</code> returns "\\u00af";</li>
   * <li><code>toPropertiesEncoding('a')</code> returns "a".</li>
   */
  public static String toPropertiesEncoding(char c) {
    return toPropertiesEncoding(c, false);
  }

  /**
   * Characters that should be escaped in Java or C code
   *
   * {@value}
   */
  public static final String ESCAPEE = "\\\"\'\n\r\t\f\b";
  /**
   * Characters used in escapes
   *
   * {@value}
   */
  public static final String ESCAPED = "\\\"'nrtfb";

  /**
   * Checks whether a character needs encoding in Java
   *
   * @param c the character
   * @return true if so
   *
   * <br><br><b>Examples</b>:
   * <li><code>needsEncoding('\u005cu00af')</code> returns <b>true</b>;</li>
   * <li><code>needsEncoding('a')</code> returns <b>false</b>.</li>
   */
  public static boolean needsEncoding(char c) {
    return ESCAPEE.indexOf(c) >= 0 || c < ' ' || c > 0x7f;
  }

  /**
   * Converts a character to its Java encoding (hex or escaped or intact)
   *
   * @param c the character
   * @param up if true, use upper case, otherwise lower
   * @param escape if true, escape escapable characters
   * @return a string with a proper Java representation of the character c
   *
   * <br><br><b>Examples</b>:
   * <li><code>toJavaEncoding('\u005cu00af', false, false)</code> returns "\\u00af";</li>
   * <li><code>toJavaEncoding('\u005cu000a', true, true)</code> returns "\\n";</li>
   * <li><code>toJavaEncoding('\u005cu000e', true, true)</code> returns "\\16";</li>
   * <li><code>toJavaEncoding('a', true, true)</code> returns "a".</li>
   */
  public static String toJavaEncoding(char c, boolean up, boolean escape) {
    int i = escape ? ESCAPEE.indexOf(c) : -1;
    return (i >= 0)    ? ("\\" + ESCAPED.charAt(i)) :
           (c < ' ')   ? toJavaOctalEncoding(c)     :
           (c > 0x7f)  ? toJavaHexEncoding(c, up)   :
                         ("" + c);
  }

  /**
   * Converts a character to its Java encoding (hex or escaped or intact)
   *
   * @param c the character
   * @param up if true, use upper case, otherwise lower
   * @return a string with a proper Java representation of the character c
   *
   * <br><br><b>Examples</b>:
   * <li><code>toJavaEncoding('\u005cu00af', false)</code> returns "\\u00af";</li>
   * <li><code>toJavaEncoding('\u005cu00af', true)</code> returns "\\u00AF";</li>
   * <li><code>toJavaEncoding('\u005cu000a', true)</code> returns "\\n";</li>
   * <li><code>toJavaEncoding('\u005cu000e', true)</code> returns "\\16";</li>
   * <li><code>toJavaEncoding('a', true)</code> returns "a".</li>
   */
  public static String toJavaEncoding(char c, boolean up) {
    return toJavaEncoding(c, up, true);
  }

  /**
   * Converts a character to its Java encoding (hex or escaped or intact)
   *
   * @param c the character
   * @return a string with a proper Java representation of the character c
   *
   * <br><br><b>Examples</b>:
   * <li><code>toJavaEncoding('\u005cu00af')</code> returns "\\u00af";</li>
   * <li><code>toJavaEncoding('\u005cu000a')</code> returns "\\n";</li>
   * <li><code>toJavaEncoding('\u005cu000e')</code> returns "\\16";</li>
   * <li><code>toJavaEncoding('a')</code> returns "a".</li>
   */
  public static String toJavaEncoding(char c) {
    return toJavaEncoding(c, false);
  }

  /**
   * Converts a character to its C encoding (hex or escaped or intact)
   *
   * @param c the character
   * @return a string with a proper C language representation of the character c
   *
   * <br><br><b>Examples</b>:
   * <li><code>toCEncoding('\u005cuabcd')</code> returns "\\xabcd";</li>
   * <li><code>toCEncoding('\u005cu00af')</code> returns "\\xaf";</li>
   * <li><code>toCEncoding('\u005cu000a')</code> returns "\\n";</li>
   * <li><code>toCEncoding('a')</code> returns "a".</li>
   */
  public static String toCEncoding(char c) {
    int i = ESCAPEE.indexOf(c);
    return (i >= 0)    ? ("\\" + ESCAPED.charAt(i)) :
           (c < ' ' || c > 0x7f) ? ("\\x" + Long.toHexString(c)) :
           ("" + c);
  }

  /**
   * Checks whether a CharSequence needs encoding in Java
   *
   * @param s the CharSequence
   * @return true if so
   *
   * <br><br><b>Examples</b>:
   * <li><code>needsEncoding("Feliz Año Nuevo")</code> returns <b>true</b>;</li>
   * <li><code>needsEncoding("Feliz Navedad")</code> returns <b>false</b>.</li>
   */
  public static boolean needsEncoding(CharSequence s) {
    if (s == null) return false;
    for (int i = 0; i < s.length(); i++) {
      if (needsEncoding(s.charAt(i))) return true;
    }
    return false;
  }

  /**
   * Converts a CharSequence to its Java encoding (hex or escaped or intact, per char)
   *
   * @param s the CharSequence
   * @param up if true, use upper case, otherwise lower
   * @param escape if true, escape escapable characters
   * @return the encoded string
   *
   * <br><br><b>Examples</b>:
   * <li><code>toJavaEncoding("\nFeliz Año Nuevo\n", true, false)</code>
   * returns "\u005cu000AFeliz \u005cu00A4o Nuevo\u005cu000A";</li>
   * <li><code>toJavaEncoding("\nFeliz Año Nuevo\n", true, true)</code>
   * returns "\\nFeliz \u005cu00A4o Nuevo\\n";</li>
   * <li><code>toJavaEncoding("\nFeliz Año Nuevo\n\0", false, true)</code>
   * returns "\\nFeliz \u005cu00a4o Nuevo\\n\\0".</li>
   */
  public static String toJavaEncoding(CharSequence s, boolean up, boolean escape) {
    if (!needsEncoding(s)) return s.toString();

    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < s.length(); i++) {
      buf.append(toJavaEncoding(s.charAt(i), up, escape));
    }

    return buf.toString();
  }

  /**
   * Converts a CharSequence to its Java encoding (hex or escaped or intact, per char)
   *
   * @param s the CharSequence
   * @param up if true, use upper case, otherwise lower
   * @return the encoded string
   *
   * <br><br><b>Examples</b>:
   * <li><code>toJavaEncoding("\nFeliz Año Nuevo\n", true)</code>
   * returns "\nFeliz \u005cu00A4o Nuevo\n";</li>
   * <li><code>toJavaEncoding("\nFeliz Año Nuevo\n\0", false)</code>
   * returns "\\nFeliz \u005cu00a4o Nuevo\\n\\0".</li>
   */
  public static String toJavaEncoding(CharSequence s, boolean up) {
    return toJavaEncoding(s, up, true);
  }

  /**
   * Converts a CharSequence to its Java encoding (hex or escaped or intact, per char)
   *
   * @param s the CharSequence
   * @return the encoded string
   *
   * <br><br><b>Example</b>:
   * <li><code>toJavaEncoding("\nFeliz Año Nuevo\n\0")</code>
   * returns "\\nFeliz A\u005cu00f1o Nuevo\\n\\0".</li>
   */
  public static String toJavaEncoding(CharSequence s) { return toJavaEncoding(s, false); }

  /**
   * Converts a CharSequence to its C encoding
   *
   * @param s the CharSequence
   * @return the encoded string
   *
   * <br><br><b>Example</b>:
   * <li><code>toCEncoding("\nFeliz Año Nuevo\n")</code>
   * returns "\\nFeliz A\\x00f1o Nuevo\\n".</li>
   */
  public static String toCEncoding(CharSequence s) {
    if (!needsEncoding(s)) return s.toString();

    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < s.length(); i++) {
      buf.append(toCEncoding(s.charAt(i)));
    }

    return buf.toString();
  }

  /**
   * Converts a character to its SGML numeric encoding
   *
   * @param c the character
   * @return a string with the representation of c as
   * "Numeric Character Reference" in SGMLese
   *
   * <br><br><b>Example</b>:
   * <li><code>toSgmlEncoding('\n')</code>
   * returns "&amp;#10;".</li>
   */
  public static String toSgmlEncoding(char c) {
  return c > 0x20 || c == 0x9 || c == 0xa || c == 0xd ? "&#" + (int)c + ";" : "?";
  }

  /**
   * Encodes a character by SGML rules
   * It can be a hex representation
   * @param c the character
   * @return the string with either Predefined Entity, Numeric Character Reference,
   * or null if no entity could be found
   *
   * <br><br><b>Examples</b>:
   * <li><code>sgmlEntity('\u005c60ab')</code> returns "&amp;#24747;" (that is, Numeric Character Reference);</li>
   * <li><code>sgmlEntity('<')</code> returns "&amp;lt;" (that is, Predefined Entity);</li>
   * <li><code>sgmlEntity('&')</code> returns "&amp;lt;" (that is, Predefined Entity);</li>
   * <li><code>sgmlEntity('X')</code> returns <b>null</b>";</li>
   * <li><code>sgmlEntity('\n')</code> returns <b>null</b>".</li>
   */
  public static String sgmlEntity(char c) {
    return (c == '<') ? "&lt;" :
           (c == '>') ? "&gt;" :
           (c == '\'') ? "&apos;" :
           (c == '\"') ? "&quot;" :
           (c == '&') ? "&amp;" :
           (c == ']') ? "&#93;" :
           (c < '\u0020' && c != '\n' && c != '\r' && c != '\t') || c > '\u0080' ?
              toSgmlEncoding(c) :
           null;
  }

  /**
   * Encodes a CharSequence by SGML rules
   * (using predefined entities and numeric character encodings when necessary)
   * @param s the original CharSequence
   * @return the encoded string
   *
   * <br><br><b>Example</b>:
   * <li><code>toSgmlEncoding("<i>Feliz Año Nuevo</i>\n")</code>
   * returns "&amp;lt;i&amp;gt;Feliz A&amp;#164;o Nuevo&amp;lt;/i&amp;gt;\n".</li>
   */
  public static String toSgmlEncoding(CharSequence s) {
    if (isEmpty(s)) return s.toString();

    StringBuffer buffer = new StringBuffer(s.length() * 2);

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      String entity = sgmlEntity(c);
      if (entity == null) {
        buffer.append(c);
      } else {
        buffer.append(entity);
      }
    }
    return buffer.toString();
  }

//****************************************************
// html-encode all non-ascii chars

   /**
    * encodes a CharSequence into an HTML-acceptable format
    *
    * @param s CharSequence original CharSequence
    * @return String encoded string
    *
    * All non-ascii characters are replaced with their &amp;# representation; other
    * characters are left intact.
    *
    * <br><br><b>Example</b>:
    * <li><code>htmlEncode("<i>Feliz Año Nuevo</i>\n")</code>
    * returns "<i>Feliz A&amp;#164;o Nuevo<i>\n".</li>
    */
   public static String htmlEncode(CharSequence s) {
    if (isEmpty(s)) return "";

    StringBuffer out = new StringBuffer();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      int k = c;
      if (c > 0x100) {
        out.append("&#" + k + ";");
      } else {
        out.append(c);
      }
    }

    return out.toString();
  }

  /**
   * Converts a char array to a readable string.
   * (By Replacing potentially unreadables characters with '.')
   *
   * @param data original char array
   * @param beginIndex where to start
   * @param endIndex where to end (before this position)
   * @return the string with unreadable chars replaced
   *
   * <br><br><b>Example</b>:
   * <li><code>toReadable("\t¡Hola señor!\n".toCharArray(), 2, 12)</code>
   * will return "..Hola se.or!.".</li>
   */
  public static String toReadable(char[] data, int beginIndex, int endIndex) {
    StringBuffer buf = new StringBuffer(data.length);
    for (int i = beginIndex; i < data.length && i < endIndex; i++) {
      char c = data[i];
      if (c < ' ' || c > 127) c = '.';
      buf.append(c);
    }
    return buf.toString();
  }

    /**
     * Converts a CharSequence to a readable string.
     * (By Replacing potentially unreadables characters with '.')
     *
     * @param s original CharSequence
     * @return the string with unreadable chars replaced
     *
     * <br><br><b>Example</b>:
     * <li><code>toReadable("\t¡Hola señor!\n")</code> will return "..Hola se.or!.".</li>
     */
    public static String toReadable(CharSequence s) {
      return toReadable(s.toString().toCharArray(), 0, s.length());
    }

  /**
   * Hexadecimal dump of a byte array.
   * Produces neatly arranged lines of bot hex and ascii representation of bytes
   * from the array.
   *
   * @param data the data array
   * @return the hex dump string
   *
   * <br><br><b>Example</b>:
   * <li><code>hexDump(new byte[] {1, 'a', 'b', '\n', 'c'})</code> will return
   * "\r\n01 61 62 0a 63             | . a b . c\r\n".</li>
   */
  public static String hexDump(byte[] data) {
    if (data == null || data.length == 0) return "";
    StringBuffer out = new StringBuffer();

    for (int i = 0; i < data.length; i+= 16) {
      out.append("\r\n");
      out.append(toHex((byte)(i >> 8)));
      out.append(toHex((byte)(i     )));
      out.append(": ");

      for (int j = i; j < i + 16; j++) {
        out.append(j < data.length ? toHex(data[j]) : "  ");
        out.append(" ");
      }
      out.append("| ");

      for (int j = i; j < i + 16 && j < data.length; j++) {
        byte b = data[j];
        out.append(b >= ' ' ? (char)b : '\u00b7');
        out.append(" ");
      }
    }
    out.append("\r\n");

    return out.toString();
  }

  /**
   * Hexadecimal dump of a char array
   * Produces neatly arranged lines of bot hex and ascii representation of bytes
   * from the array.
   *
   * @param data the data array
   * @return a string containing both hex and ascii representation of the data
   *
   * <br><br><b>Example</b>:
   * <li><code>hexDump(new char[] {1, 'a', 'b', '\n', 'c'})</code> will return<br>
   * "\r\n0000: 0001 0061 0062 000a 0063                                                        | .ab.c\r\n".</li>
   */
  public static String hexDump(char[] data) {
    if (data == null || data.length == 0) return "";
    StringBuffer out = new StringBuffer();

    for (int i = 0; i < data.length; i+= 16) {
      out.append("\r\n");
      out.append(i < 16 ? "000" : i < 256 ? "00" : i < 4096 ? "0" : "");
      out.append(toHex(i));
      out.append(": ");

      for (int j = i; j < i + 16; j++) {
        out.append(j >= data.length ? "    " : toHex(data[j]));
        out.append(" ");
      }
      out.append("| ");
      out.append(toReadable(data, i, Math.min(i+16, data.length)));
    }
    out.append("\r\n");

    return out.toString();
  }

  /**
   * Hexadecimal dump of a CharSequence
   * Produces neatly arranged lines of bot hex and ascii representation of bytes
   * from the array.
   *
   * @param data the CharSequence
   * @return a string containing both hex and ascii representation of the data
   *
   * <br><br><b>Example</b>:
   * <li><code>hexDump("\u0001ab\nc")</code> will return
   * "\r\n0001 0061 0062 000a 0063             | .ab.c".</li>
   */
  public static String hexDump(CharSequence data) {
    return hexDump(data.toString().toCharArray());
  }

  /**
   * Converts an array of chars to a readable hexadecimal form
   *
   * @param data the data array
   * @return a string that is basically a hex dump of the data
   *
   * <br><br><b>Example</b>:
   * <li><code>toHexReadable(new char[] {1, 'a', 'b', '\n', 'c'})</code> will return
   * "0001 0061 0062 000a 0063 \r\n".</li>
   */
  public static String toHexReadable(char[] data) {
    if (data == null || data.length == 0) return "";
    StringBuffer out = new StringBuffer();

    for (int i = 0; i < data.length; i+=16) {
      for (int j = i; j < i + 16 && j < data.length; j++) {
        out.append(toHex(data[j]));
        out.append(" ");
      }

      out.append("\r\n");
    }
    return out.toString();
  }

  /**
   * Converts an array of chars to a readable hexadecimal form
   *
   * @param data the data array
   * @param from beginning index
   * @param to ending index (not included)
   * @return a string that is basically a hex dump of the data
   *
   * <br><br><b>Example</b>:
   * <li><code>toHexReadable(new byte[] {1, 2, 48}, 1, 3)</code> will return
   * "02 30 \r\n".</li>
   */
  public static String toHexReadable(byte[] data, int from, int to) {
    if (data == null || data.length == 0) return "";
    StringBuffer out = new StringBuffer();

    for (int i = from; i < Math.min(to, data.length); i+=16) {
      for (int j = i; j < i + 16 && j < Math.min(to, data.length); j++) {
        out.append(toHex(data[j]));
        out.append(" ");
      }

      out.append("\r\n");
    }
    return out.toString();
  }

  /**
   * Converts an array of bytes to a readable hexadecimal form
   *
   * @param data the data array
   * @return a string that is basically a hex dump of the data
   *
   * <br><br><b>Example</b>:
   * <li><code>toHexReadable(new byte[] {1, 2, 48})</code> will return
   * "01 02 30 ".</li>
   */
  public static String toHexReadable(byte[] data) {
    if (data == null || data.length == 0) return "";
    StringBuffer out = new StringBuffer();

    for (int i = 0; i < data.length; i+=16) {
      for (int j = i; j < i + 16 && j < data.length; j++) {
        out.append(toHex(data[j]));
        out.append(" ");
      }

      out.append("\r\n");
    }
    return out.toString();
  }

  /**
   * Converts a CharSequence to a readable hexadecimal string
   *
   * @param s the data CharSequence
   * @return a string that is basically a hex dump of the data
   *
   * <br><br><b>Example</b>:
   * <li><code>toHexReadable("\u0001ab\nc")</code> will return
   * "0001 0061 0062 000a 0063 \r\n".</li>
   */
  public static String toHexReadable(CharSequence s) {
    if (isEmpty(s)) return "";
    StringBuffer out = new StringBuffer();

    for (int i = 0; i < s.length(); i+=16) {
      CharSequence chunk = s.subSequence(i, Math.min(i + 16, s.length()));

      for (int j = 0; j < chunk.length(); j++) {
        out.append(toHex(chunk.charAt(j)));
        out.append(" ");
      }

      out.append("\r\n");
    }
    return out.toString();
  }

  /**
   * Perl operation join.
   *   Concatenates a collection of (string representations of) objects
   *   using a separator string to separate
   *
   * @param separator the separator CharSequence   * @param collection the collection of objects to join
   * @return resulting string
   *
   * <br><br><b>Examples</b>:<br>
   * <code>
   * HashSet   a = new HashSet();<br>
   * List b = new ArrayList(); b.add("entry1"); b.add("entry2");<br>
   * <li><code>join(", ", a)</code> returns "";</li>
   * <li><code>join(", ", b)</code> returns "entry1, entry2".</li>
   */
  public static String join(CharSequence separator, Collection collection) {
    if (separator == null || collection == null) return "";

    StringBuffer buf = new StringBuffer();

    for (Iterator i = collection.iterator(); i.hasNext();) {
      Object ith = i.next();
      if (ith != null) buf.append(ith);

      if (i.hasNext()) {
        buf.append(separator);
      }
    }

    return buf.toString();
  }

  /**
   * Perl operation join.
   *   Concatenates an array of (string representations of) objects
   *   using a separator to separate
   *
   * @param separator the separator
   * @param what the array of objects to join
   * @return resulting string
   *
   * <br><br><b>Examples</b>:<br>
   * <li><code>join(", ", new Long[] {new Long(1), new Long(555)})</code>
   * returns "1, 555";</li>
   * <li><code>join(" and ", new String[] {"Here", "there", "everywhere"})</code>
   * returns "Here and there and everywhere".</li>
   */
  public static String join(CharSequence separator, Object[] what) {
    if (separator == null || what == null) return "";

    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < what.length; i++) {
      Object ith = what[i];
      if (ith != null) buf.append(ith.toString());

      if (i <what.length - 1) {
        buf.append(separator);
      }
    }

    return buf.toString();
  }

  /**
   * Perl operation split.
   *   splits a source string into a collection of strings
   *
   * @param separator separator character sequence
   * @param source source character sequence
   * @return a collection of extracted character sequencess
   *
   * @see #join(CharSequence, Object[])
   * see java.lang.String.split(String)
   *
   * <br><br><b>Example</b>:<br>
   * <code>
   * <li><code>split(":", "a:ab:abcde:")</code>
   * returns a list containing four elements, "a", "ab", "abcde", "".</li>
   */
  public static List split(CharSequence separator, CharSequence source) {

    ArrayList stringList = new ArrayList();
    boolean found = false;
    for (int pos = 0;
         source != null && separator != null && pos < source.length();) {
      int last = indexOf(source, separator, pos);
      if (last < 0) {
        last = source.length();
      } else {
        found = true;
      }
      stringList.add(source.subSequence(pos, last));
      pos = last + separator.length();
    }
    if (found) stringList.add("");
    stringList.trimToSize();
    return stringList;
  }

  /**
   * Perl operation grep.
   *   Extracts from a string array the strings that match a regexp
   *
   * @param source source array
   * @param regexp expression to match
   * @return a collection of matching character sequences from source
   *
   * <br><br><b>Example</b>:<br>
   * <code>
   * <li><code>grep(new String[] {"good", "bad", "ugly"}, "g."))</code>
   * returns a list containing two elements: "good", "ugly".</li>
   */
  public static List grep(CharSequence[] source, CharSequence regexp)
    throws PatternSyntaxException {
    return grep(source, Pattern.compile(regexp.toString()));
  }

 /**
  * Perl operation grep.
  *   Extracts from an array the char sequences that match a regexp
  *
  * @param source source array
  * @param regexp expression to match
  * @return a collection of matching character sequences
  *
  * <br><br><b>Example</b>:<br>
  * <code>
  * <li><code>grep(new String[] {"good", "bad", "ugly"}, Pattern.compile("g.")))</code>
  * returns a list containing two elements: "good", "ugly".</li>
  */
 public static List grep(CharSequence[] source, Pattern regexp) {
   ArrayList result = new ArrayList();
   for (int i = 0; i < source.length; i++) {
     Matcher matcher = regexp.matcher(source[i]);
     if (matcher.find()) {
       result.add(source[i]);
     }
   }
   result.trimToSize();
   return result;
 }

  /**
   * Replaces a subsequence in a char sequence with another subsequence
   *
   * @param where the string containing the substrings to replace
   * @param oldSubstring what to replace
   * @param newSubstring with what to replace
   * @param all if true, all (nonintersecting) substrings are replaced,
   *        otherwise only one
   * @return resulting string
   *
   * see java.lang.String.replaceAll(String,String) and java.lang.String.replaceFirst(String, String)
   *
   * <br><br><b>Examples</b>:
   * <li><code>replace("God loves you", "love", "hate", true)</code>
   * returns "God hates you";</li>
   * <li><code>replace("All you need is love, love!", "me", false)</code>
   * returns "All you need is me, love!".</li>
   */
  public static String replace(CharSequence where,
                                     CharSequence oldSubstring,
                                     CharSequence newSubstring,
                                     boolean all) {
    if (where == null) return null;

    if (oldSubstring == null || newSubstring == null)
      return where.toString();

    StringBuffer out = new StringBuffer();
    int pos = 0;
    do {
      int newPos = indexOf(where, oldSubstring, pos);

      if (newPos < 0) break;
      out.append(where.subSequence(pos, newPos));
      out.append(newSubstring);

      pos = newPos + oldSubstring.length();
    } while (all);

    out.append(where.subSequence(pos, where.length()));
    return out.toString();
  }

  /**
   * Extracts value from a char sequence of format NAME="VALUE"
   *
   * @param input sequence of the aforementioned format
   * @param name the name on the left side of '='
   * @return the string value inside the quotes (quotes omitted) if the string
   *         has the specified format; null utherwise
   *
   * <br><br><b>Examples</b>:
   * <li><code>extractValue("java.home=\"c:\\java\\jdk1.4.1\"\nx=\"abcd\"", "x")</code>
   * returns "abcd";</li>
   * <li><code>extractValue("java.home=\|c:\\java\\jdk1.4.1\"\nx=\"abcd\"", "java.home")</code>
   * returns "c:\\java\\jdk1.4.1".</li>
   */
  public static String extractValue(CharSequence input, CharSequence name) {
    int iname = indexOf(input, name + "=\"");
    if (iname < 0) return null;

    int ivalue = iname + name.length() + 2;
    int ievalu = indexOf(input, '"', ivalue);
    if (ievalu < 0) return null;

    return input.subSequence(ivalue, ievalu).toString();
  }

  /**
   * Packs bytes into a string
   *
   * @param from byte array
   * @return a string that consists of the same bytes, packed two per character
   *
   * <br><br><b>Example</b>:
   * <li><code>pack(new byte[] {0x23, 0x67, (byte)0xab, (byte)0xef})</code>
   * returns "\u2367\uabef".</li>
   */
  public static String pack(byte[] from) {
    StringBuffer buffer = new StringBuffer((from.length + 1) / 2);
    char previous = 0;

    for (int i = 0; i < from.length; i++) {
      char byteValue = (char)(0xff & from[i]);

      if (i % 2 != 0) {
        buffer.append((char)(previous + byteValue));
      } else {
        previous = (char)(byteValue << 8);
      }
    }

    if (from.length % 2 != 0) {
      buffer.append(previous);
    }
    return buffer.toString();
  }

  /**
   * Unpacks bytes packed in the char sequence
   *
   * @see #pack(byte[])
   * @param data the packed data
   * @return the unpacked data
   *
   * <br><br><b>Example</b>:
   * <li><code>unpack("\u2367\uabef")</code>
   * returns new byte[] {0x23, 0x67, (byte)0xab, (byte)0xef}.</li>
   */
  public static byte [] unpack(CharSequence data) {
// The following method does not work for JDK 1.4; it seems like it replaces
// "bad" characters with ff fe sequences.
//    return encode(string, "UTF-16BE");
    byte[] result = new byte[data.length() * 2];
    for (int i = 0; i < data.length(); i++) {
      int c = data.charAt(i);
      result[i * 2]     = (byte)(c >> 8);
      result[i * 2 + 1] = (byte) c;
    }
    return result;
  }

  /**
   * Decodes (and unescapes) a Java string.
   * Replaces escape sequences with their corresponding character values
   *
   * @param string as presented in the source code
   * @return decoded string in "internal form"
   *
   * <br><br><b>Examples</b>:
   * <li><code>decodeJavaString("This is a string")</code> returns "This is a string";</li>
   * <li><code>decodeJavaString("\\nFeliz \\u00A4o Nuevo\\n")</code>
   * returns "\nFeliz Año Nuevo\n".</li>
   */
  public static String decodeJavaString(CharSequence string) {
    StringBuffer output = new StringBuffer(string.length() * 2);

    if (indexOf(string, '\\') < 0) {
      return string.toString();
    }

    for (int i=0; i < string.length(); i++) {
      char ch = string.charAt(i);
      if (ch == '\\' && i < string.length() - 1) { // So, we have a backslash...
        int escapeIdx = ESCAPED.indexOf(string.charAt(i+1));
        if (escapeIdx >= 0) {
          i++;
          ch = ESCAPEE.charAt(escapeIdx);
        } else
        if (i < string.length() - 5 &&
            string.charAt(i + 1) == 'u') { //possible escape
          try {
            ch = (char)Integer.parseInt(string.subSequence(i+2, i+6).toString(), 16);
            i += 5;
          } catch (NumberFormatException nfe) {
//          Okay, okay, not a hex number...
          }
        }
      }
      output.append(ch);
    }
    return output.toString();
  }

  /**
   * Encodes a char sequence using specified encoding
   *
   * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/intl/encoding.doc.html">list of Java encodings</a>
   *
   * @param s char sequence to encode
   * @param encoding the name of encoding
   * @return encoded array of bytes
   * @throws IOException when something goes wrong with bytearray streams
   * @throws UnsupportedEncodingException when encoding is unknown
   *
   * <br><br><b>Examples</b>:
   * <li><code>encode("Año Nuevo", "UTF8")</code>
   * returns new byte[] {0x41, (byte)0xc3, (byte)0xb1, 0x6f, 0x20, 0x4e, 0x75, 0x65, 0x76, 0x6f};</li>
   * <li><code>encode("Año Nuevo", "MacRoman")</code>
   * returns new byte[] {0x41, (byte)0x96, 0x6f, 0x20, 0x4e, 0x75, 0x65, 0x76, 0x6f}.</li>
   */
  public static byte[] encode(CharSequence s, String encoding)
               throws IOException, UnsupportedEncodingException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Writer osw = new OutputStreamWriter(bos, encoding);
    write(osw, s);
    osw.close();
    return bos.toByteArray();
  }

  /**
   * Decodes a stream using specified encoding
   *
   * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/intl/encoding.doc.html">list of Java encodings</a>
   *
   * @param is stream to decode
   * @param encoding the name of encoding
   * @return data decoded from the stream
   * @throws IOException when something goes wrong with bytearray streams
   * @throws UnsupportedEncodingException when encoding is unknown
   *
   */
  public static String decode(InputStream is, String encoding)
               throws IOException, UnsupportedEncodingException {
    Reader isr               = new InputStreamReader(is, encoding);
    StringBuffer result      = new StringBuffer();
    char[] readBuffer        = new char[4096];

    while (isr.ready()) {
      int l = isr.read(readBuffer);
      result.append(readBuffer, 0, l);
    }

    return result.toString();
  }

  /**
   * Decodes an array of bytes using specified encoding
   *
   * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/intl/encoding.doc.html">list of Java encodings</a>
   *
   * @param bytes byte array
   * @param encoding the name of encoding
   * @return decoded string
   * @throws IOException when something goes wrong with bytearray streams
   * @throws UnsupportedEncodingException when encoding is unknown
   *
   * <br><br><b>Examples</b>:
   * <li><code>decode(new byte[] {0x41, (byte)0xc3, (byte)0xb1, 0x6f, 0x20, 0x4e, 0x75, 0x65, 0x76, 0x6f}, "UTF8")</code>
   * returns "Año Nuevo";</li>
   * <li><code>encode( new byte[] {0x41, (byte)0x96, 0x6f, 0x20, 0x4e, 0x75, 0x65, 0x76, 0x6f}, "MacRoman")</code>
   * returns "Año Nuevo".</li>
   */
  public static String decode(byte[] bytes, String encoding)
               throws IOException, UnsupportedEncodingException {
    return decode(new ByteArrayInputStream(bytes), encoding);
  }

  /**
   * zip (like in zip files) a string producing an array of bytes
   *
   * @param source the string to zip
   * @return bytes representing zipped data
   * @throws IOException when something goes wrong with streams
   * @throws UnsupportedEncodingException when JDK forgets that it knows UTF8
   *
   * <br><br><b>Example</b>:
   * <li><code>zip2bytes("Hello World")</code>
   * returns new byte[] {0x78, (byte)0xda, (byte)0xf3, 0x48, (byte)0xcd, (byte)0xc9, (byte)0xc9, 0x57, (byte)0x08, (byte)0xcf, 0x2f, (byte)0xca, 0x49, 0x01, 0x00, 0x18, 0x0b, 0x04, 0x1d, 0x00}.</li>
   */
  public static byte[] zip2bytes(CharSequence source)
               throws IOException, UnsupportedEncodingException {
    if (source == null) return null;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DeflaterOutputStream dos  = new DeflaterOutputStream(bos,
                                new Deflater(Deflater.BEST_COMPRESSION));
    Writer osw                = new OutputStreamWriter(dos, "UTF-8");
    write(osw, source);
    osw.flush();
    dos.finish();
    bos.write(0); // Just to pad in case of, you know, odd number of bytes.
    osw.close();
    byte[] result = bos.toByteArray();
    return result;
  }

  /**
   * zips a char sequence to a string of lower-byte chars.
   * Does this: CharSequence -> UTF8 bytes -> zip -> bytes -> String
   * @param source char sequence to zip
   * @return string with zipped data
   * @throws IOException when something goes wrong with streams
   * @throws UnsupportedEncodingException when JDK forgets that it knows UTF8
   *
   * <br><br><b>Example</b>:
   * <li><code>zip8bit("Hello World")</code>
   * returns "x\u00da\u00f3H\u00cd\u00c9\u00c9W\b\u00cf/\u00caI\u0001\u0000\u0018\u000b\u0004\u001d\u0000".</li>
   */

   public static String zip8bit(CharSequence source)
               throws IOException, UnsupportedEncodingException {
    return new String(zip2bytes(source));
  }

  /**
   * zips a char sequence to a string.
   * Does this: CharSequence -> UTF8 bytes -> zip -> bytes -> High Unicode -> String
   *
   * @param source char sequence to zip
   * @return string with zipped data
   * @throws IOException when something goes wrong with streams
   * @throws UnsupportedEncodingException when JDK forgets that it knows UTF8
   *
   * <br><br><b>Example</b>:
   * <li><code>zip("Hello World")</code>
   * returns "\u78da\uf348\ucdc9\uc957\u08cf\u2fca\u4901\u0018\u0b04\u1d00".</li>
   */
  public static String zip(CharSequence source)
               throws IOException, UnsupportedEncodingException {
    return pack(zip2bytes(source));
  }

  /**
   * Unzips a stream.
   * Does this: stream -> unzip -> bytes -> UTF8 -> String
   *
   * @param zippedStream
   * @return string with unzipped data
   * @throws IOException when something goes fishy
   * @throws UnsupportedEncodingException when JDK forgets that it knows UTF8
   */

      public static String unzip(InputStream zippedStream)
               throws IOException, UnsupportedEncodingException {
    return decode(new InflaterInputStream(zippedStream), "UTF-8");
  }

  /**
   * Unzips an array of bytes.
   * Does this: bytes -> unzip -> bytes -> UTF8 -> String
   *
   * @param zippedBytes
   * @return string with unzipped data
   * @throws IOException when something goes fishy
   * @throws UnsupportedEncodingException when JDK forgets that it knows UTF8
   *
   * <br><br><b>Example</b>:
   * <li><code>unzip(new byte[] {0x78, (byte)0xda, (byte)0xf3, 0x48, (byte)0xcd, (byte)0xc9, (byte)0xc9, 0x57, 0x08, (byte)0xcf, 0x2f, (byte)0xca, 0x49, 0x01, 0x00, 0x18, 0x0b, 0x04, 0x1d, 0x00})</code>
   * returns "Hello World".</li>
   */
  public static String unzip(byte[] zippedBytes)
               throws IOException, UnsupportedEncodingException {
    return unzip(new ByteArrayInputStream(zippedBytes));
  }

  /**
   * Unzips a char sequence
   * Does this: CharSequence -> High Unicode bytes -> unzip -> bytes -> UTF8 -> String
   *
   * @param zipped
   * @return string with unzipped data
   * @throws IOException when something goes fishy
   * @throws UnsupportedEncodingException when JDK forgets that it knows UTF8
   *
   * <br><br><b>Example</b>:
   * <li><code>unzip("\u78da\uf348\ucdc9\uc957\u08cf\u2fca\u4901\u0018\u0b04\u1d00")</code>
   * returns "Hello World".</li>
   */
  public static String unzip(CharSequence zipped)
               throws IOException, UnsupportedEncodingException {
    return unzip(unpack(zipped));
  }

  /**
   * Calculates crc32 on a char sequence
   * @param data source char sequence
   * @return its crc32
   * @throws IOException when something goes wrong with streams
   * @throws UnsupportedEncodingException when JDK forgets that it knows UTF8
   *
   * <br><br><b>Example</b>:
   * <li><code>crc32("Hello World")</code>
   * returns 2178450716l.</li>
   */
  public static long crc32(CharSequence data)
      throws IOException, UnsupportedEncodingException {
    return crc32(unpack(data));
  }

  /**
   * Returns a crc report on a byte array: a set of partial crc on chunks of data
   *
   * @param data source bytes
   * @param off offset in the array
   * @param len length of the area to crc
   * @param step chunk size
   * @return formatted report
   */
  public static String crcreport(byte[] data, int off, int len, int step) {
    StringBuffer buffer = new StringBuffer();
    int lastplus1 = Math.min(data.length, off + len);
    for (int i = off; i <= lastplus1; i+= step) {
      buffer.append("[" + i + "]" + Long.toHexString(crc32(data, i, Math.min(lastplus1 - i, step))) + " ");
    }
    return buffer.toString();
  }

  /**
   * Returns a crc report on a byte array: a set of partial crc on chunks of data
   *
   * @param data source bytes
   * @param step chunk size
   * @return formatted report
   */
  public static String crcreport(byte[] data, int step) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i <= data.length; i+= step) {
      buffer.append("[" + i + "]" + Long.toHexString(crc32(data, i, Math.min(data.length - i, step))) + " ");
    }
    return buffer.toString();
  }

  /**
   * Converts an array to string array, per element
   *
   * @param object the expected array
   * @return array of strings, or null if input is not what expected
   *
   * <br><br><b>Example</b>:
   * <li><code>toStrings(new Object[] { new Integer(22), new Boolean(false), "wow"})</code>
   * returns new String[] {"22", "false", "wow"}.</li>
   *
   */
  public static String[] toStrings(Object object) {
    if (object == null) return null;
    if (!(object instanceof Object[])) return null;
    Object[] array = (Object[])object;
    String[] result = new String[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i].toString();
    }
    return result;
  }

  /**
   * Stringifies a Throwable, together with is stack trace.
   * @param e the throwable to convert to string
   * @return the string representation
   *
   * <br><br><b>Example</b>:
   * <p><code>
   * try {<br>
   * &nbsp;&nbsp;String s = null;<br>
   * &nbsp;&nbsp;s.toString();<br>
   * } catch (Exception e) {<br>
   * &nbsp;&nbsp;System.out.println(toString(e));<br>
   * }<br></code> prints
   * <p><code>java.lang.NullPointerException<br>
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at com.myjavatools.util.TestStrings</code>
   */
  public static String toString(Throwable e) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    e.printStackTrace(ps);
    return baos.toString();
  }

  /**
   * Formats string with one parameter
   *
   * @param fmtString
   * @param param1
   * @return formatted string
   *
   * <br><br><b>Example</b>:
   * <li><code>format("{0} Monkeys", new Long(12))</code> returns "12 Monkeys".</li>
   *
   */
  public static String format(String fmtString, Object param1)
  {
    return( MessageFormat.format(fmtString, new Object[] {param1}));
  }

  /**
   * Formats string with two parameters
   *
   * @param fmtString
   * @param param1
   * @param param2
   * @return formatted string
   *
   * <br><br><b>Example</b>:
   * <li><code>format("{0} is {1}", "Life", "struggle")</code> returns "Life is struggle".</li>
   *
   */
  public static String format(String fmtString, Object param1, Object param2)
  {
    return(MessageFormat.format(fmtString, new Object[]{param1, param2}));
  }

  /**
   * Formats string with three parameters
   * @param fmtString
   * @param param1
   * @param param2
   * @param param3
   * @return formatted string
   *
   * <br><br><b>Example</b>:
   * <li><code>format("{0} + {1} = {2}", new Byte(2), new Byte(2), new Long(5))</code> returns "2 + 2 = 5".</li>
   *
   */
  public static String format(String fmtString,
                              Object param1,
                              Object param2,
                              Object param3)
  {
    return(MessageFormat.format(fmtString, new Object[]{param1, param2, param3}));
  }


  /**
   * Create Properties from an array of key-value pairs
   *
   * @param pairs the source array
   * @return new Properties
   *
   * <br><br><b>Example</b>:
   * <li><code>asProperties(new String[] {"1", "one", "2", "two", "3", "three"})<code>
   * returns properties with three keys ("1", "2", "3"), and guess which values.</li>
   */
  public static Properties asProperties(String[]pairs) {
    if (pairs == null) return null;
    Properties result = new Properties();
    for (int i = 0; i < pairs.length-1; i+=2) {
      result.setProperty(pairs[i], pairs[i+1]);
    }
    return result;
  }

  /**
   * Finds index of the first difference between two char sequences
   *
   * @param s1
   * @param s2
   * @return the first index at which sequences differ, -1 if the sequences are equal
   *
   * <br><br><b>Examples</b>:
   * <li><code>findDiff("abcd", "abec")</code> returns 2;</li>
   * <li><code>findDiff("abc", "abc")</code> returns -1;</li>
   * <li><code>findDiff("ab", null)</code> returns 0.</li>
   * <li><code>findDiff("", " ")</code> returns 0.</li>
   */
  public static int findDiff(CharSequence s1, CharSequence s2) {
    if (isEmpty(s1) && isEmpty(s2)) return -1;
    if (isEmpty(s1) || isEmpty(s2)) return 0;
    int i;
    for (i = 0; i < Math.min(s1.length(), s2.length()); i++) {
      if (s1.charAt(i) != s2.charAt(i)) return i;
    }
    return s1.length() == s2.length() ? -1 : i;
  }

  /**
   * Three "fuzzy logical" values, _TRUE_, _FALSE_, _UNDEF_
   * ("Intuitionistic" would be a more correct scientific term for these).
   *
   */
  public static final int _TRUE_  = 3;
  public static final int _FALSE_ = 0;
  public static final int _UNDEF_ = 1;

  /**
   * Extracts logical value from a string
   *
   * @param string
   * @return _TRUE_  if it is 'true', 'yes', '1' or '+' (case-insensitive),
   *         _FALSE_ if it is 'false', 'no', '-' or '-' (case-insensitive),
   *         _UNDEF_ in all other cases.
   *
   * <br><br><b>Examples</b>:
   * <li><code>isTrue("YeS")</code> returns _TRUE_;</li>
   * <li><code>isTrue("false")</code> returns _FALSE_;</li>
   * <li><code>isTrue(null)</code> returns _UNDEF_.</li>
   *
   */

  public int isTrue(String string) {
    return ("true" .equalsIgnoreCase(string) ||
            "yes"  .equalsIgnoreCase(string) ||
            "1"    .equals          (string) ||
            "+"    .equals          (string))    ? _TRUE_ :

           ("false".equalsIgnoreCase(string) ||
            "no"   .equalsIgnoreCase(string) ||
            "not"  .equalsIgnoreCase(string) ||
            "0"    .equals          (string) ||
            "-"    .equals          (string))    ? _FALSE_
                                                : _UNDEF_;
  }

  /**
   * Extracts Boolean value from a string
   *
   * @param string
   * @param defaultValue
   * @return <b>true</b>  if it is 'true', 'yes', '1' or '+' (case-insensitive),
   *         <b>false</b> if it is 'false', 'no', '-' or '-' (case-insensitive),
   *         defaultValue in all other cases.
   *
   * <br><br><b>Examples</b>:
   * <li><code>toBoolean("YeS", false)</code> returns true;</li>
   * <li><code>toBoolean("false", false)</code> returns false;</li>
   * <li><code>toBoolean(null, true)</code> returns true.</li>
   */

  public boolean toBoolean(String string, boolean defaultValue) {
    int result = isTrue(string);
    return result == _UNDEF_ ? defaultValue : result == _TRUE_;
  }
}
