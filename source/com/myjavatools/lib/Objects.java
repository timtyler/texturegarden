/**
 * <p>Title: MyJavaTools: Objects Handling</p>
 * <p>Description: Various methods to handle objects that are strangely missing
 * in JDK.
 * Good for Java 1.4 and up.</p>
 * <p>Copyright: This is public domain;
 * The right of people to use, distribute, copy or improve the contents of the
 * following may not be restricted.</p>
 *
 * @version 1.4
 * @author Vlad Patryshev
 */

package com.myjavatools.lib;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.zip.CRC32;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

public class Objects
{
  protected Objects() {}

  /**
   * Maps an array using Map.
   * If Map m is considered as a map from its keys to its values, then
   * for each x in domain m(x) is calculated and stored into resulting array
   *
   * @param m the map
   * @param domain the domain array
   * @return an array with the same number of elements as domain, and with elements
   * being values that correspond to the map's keys.
   *
   * <br><br><b>Example</b>:
   * <p>Suppose we have the following: <code>Map m = new HashMap();<br>
   * m.put("a", "ValueOfA"); m.put("b", "ValueOfB"); m.put("c", "ValueOfC");</code>
   * <p>Then <code>map(m, new String[] {"b", "x", "b"})</code>
   * returns new String[] {"ValueOfB", null, "ValueOfB"}.
   */
  public static final Object[] map(Map m, Object[]domain) {

    if (m == null || domain == null) return null;

    Object[] codomain = new Object[domain.length];

    for (int i = 0; i < domain.length; i++) {
      codomain[i] = m.get(domain[i]);
    }

    return codomain;
  }

  /**
   * Maps a List using Map.
   * If Map m is considered as a map from its keys to its values, then
   * for each x in domain m(x) is calculated and stored into resulting List
   *
   * @param m the map
   * @param domain the domain list
   * @return a list with the same number of elements as domain, and with elements
   * being values that correspond to the map's keys.
   *
   * <br><br><b>Example</b>:
   * <p>Suppose we have the following: <code>Map m = new HashMap();<br>
   * m.put("a", "ValueOfA"); m.put("b", "ValueOfB"); m.put("c", "ValueOfC");</code>
   * <p>Then <code>map(m, Arrays.asList(new String[] {"b", "x", "b"}))</code>
   * returns a List that contains "ValueOfB", null, and "ValueOfB".
   */
  public static final List map(Map m, List domain) {
    if (m == null || domain == null) return null;
    return Arrays.asList(map(m, domain.toArray()));
  }

  /**
   * Maps a Collection using Map.
   * If Map m is considered as a map from its keys to its values, then
   * for each x in domain m(x) is calculated and stored into resulting List
   *
   * @param m the map
   * @param domain the domain list
   * @return a collection with the same number of elements as domain, and with elements
   * being values that correspond to the map's keys.
   *
   * <br><br><b>Example</b>:
   * <p>Suppose we have the following: <code>Map m = new HashMap();<br>
   * m.put("a", "ValueOfA"); m.put("b", "ValueOfB"); m.put("c", "ValueOfC");</code>
   * <p>Then <code>map(m, Arrays.asList(new String[] {"b", "x", "b"}))</code>
   * returns a Collection that contains "ValueOfB", null, and "ValueOfB".
   */
  public static final Collection map(Map m, Collection domain) {
    if (m == null || domain == null) return null;
    return Arrays.asList(map(m, domain.toArray()));
  }

  /**
   * Maps an array using Map, with a default value.
   * If Map m is considered as a map from its keys to its values, then
   * for each x in domain m(x) is calculated and stored into resulting array
   *
   * @param m the map
   * @param domain the domain array
   * @param defaultValue the value that is returned when an element of domain is not
   * in the list of m keys
   * @return an array with the same number of elements as domain, and with elements
   * being values that correspond to the map's keys.
   *
   * <br><br><b>Example</b>:
   * <p>Suppose we have the following: <code>Map m = new HashMap();<br>
   * m.add("a", "ValueOfA"); m.add("b", "ValueOfB", m.add("c", "ValueOfC");</code>
   * <p>Then <code>map(m, new String[] {"b", "x", "b"}, "Johnny Doe")</code><br>
   * returns new String[] {"ValueOfB", "Johnny Doe", "ValueOfB"}.
   */
  public static final Object[] map(Map m, Object[]domain, Object defaultValue) {
    if (m == null || domain == null) return null;

    Object[] codomain = new Object[domain.length];

    for (int i = 0; i < domain.length; i++) {
      Object key = domain[i];

      codomain[i] = m.containsKey(key) ? m.get(key) : defaultValue;
    }

    return codomain;
  }

  /**
   * Maps an array using Map, with default values.
   * If Map m is considered as a map from its keys to its values, then
   * for each x in domain m(x) is calculated and stored into resulting array
   *
   * @param m the map
   * @param domain the domain array
   * @param defaultValues the values that are returned when an element of domain is not
   * in the list of m keys
   * @return an array with the same number of elements as domain, and with elements
   * being values that correspond to the map's keys.
   *
   * <br><br><b>Example</b>:
   * <p>Suppose we have the following: <code>Map m = new HashMap();<br>
   * m.add("a", "ValueOfA"); m.add("b", "ValueOfB", m.add("c", "ValueOfC");</code>
   * <p>Then <code>map(m, new String[] {"b", "x", "b"}, new String[] {"Johnny A", "Johnny Bee", "Johnny Doe"})</code><br>
   * returns new String[] {"ValueOfB", "Johnny Bee", "ValueOfB"}.
   */

  public static final Object[] map(Map m, Object[]domain, Object[] defaultValues) {
    if (m == null || domain == null) return null;

    Object[] codomain = new Object[domain.length];

    for (int i = 0; i < domain.length; i++) {
      Object key = domain[i];
      codomain[i] = m.containsKey(key)       ? m.get(key) :
                    i < defaultValues.length ? defaultValues[i] :
                                               null;
    }

    return codomain;
  }

  /**
   * composes two Maps (like functions, if you know what I mean)
   *
   * @param f Map first Map
   * @param g Map second Map
   * @return Map composition, x -> g(f(x));
   *
   * <br><br><b>Example</b>:
   * <p>Suppose we have the following: <br>
   * <code>Map f = toMap(new String[] {"1", "one", "2", "two", "3", "three"});<br>
   *       Map g = toMap(new String[] {"one", "uno", "two", "dos", "three", "tres"});</code>
   * <p>Then <code>compose(f, g)</code><br>
   * returns the same map as produced by <br>
   * <code>toMap(new String[] {"1", "uno", "2", "dos", "3", "tres"});</code><br>
   */
  public static Map compose(Map f, Map g) {
    Map result = getMap(f.size());

    for (Iterator i = f.keySet().iterator(); i.hasNext();) {
      Object x = i.next();
      Object y = f.get(x);
      Object z = g.get(y);
      result.put(x, z);
    }

    return result;
  }

  /**
   * Inverses a Map
   *
   * @param f Map to inverse, must be monomorphic (one-to-one)
   * @throws InstantiationException in case f is not one-to-one
   * @return Map inverse to f
   *
   * <br><br><b>Example</b>:
   * <p>Suppose we have the following: <br>
   * <code>Map f = toMap(new String[] {"1", "one", "2", "two", "3", "three"});<br>
   * <p>Then <code>inverse(f)</code><br>
   * returns the same map as produced by <br>
   * <code>toMap(new String[] {"one", "1", "two", "2", "three", "3"});</code><br>
   */

  public static Map inverse(Map f) throws InstantiationException {
    Map result = getMap(f.size());

    for (Iterator i = f.entrySet().iterator(); i.hasNext();) {
      Map.Entry entry = (Map.Entry)i.next();

      if (result.containsKey(entry.getValue())) {
        throw new InstantiationException("irreversible function");
      }
      result.put(entry.getValue(), entry.getKey());
    }

    return result;
  }

  /**
   * Create a Map from an array of key-value pairs
   *
   * @param pairs the source array
   * @return a new Map
   *
   * @deprecated
   * @see #toMap(Object[])
   *
   * <br><br><b>Example</b>:
   * <li><code>asMap(new String[] {"1", "one", "2", "two", "3", "three"})<code>
   * returns a map with three keys ("1", "2", "3"), and guess which values.</li>
   */
  public static final Map asMap(Object[]pairs) {
    return toMap(pairs);
  }

  /**
   * In a "creative" way checks whether a string or a container is empty.
   * <br>Accepts a <code>Collection</code>, a <code>Map</code>, an array, a <code>String</code>.
   *
   * @param data a Collection or a Map or an array or a string to check
   * @return true if data is empty
   *
   * <br><br><b>Examples</b>:
   * <li><code>isEmpty(""), isEmpty(null), isEmpty(new HashMap())</code> all return <b>true</b>;</li>
   * <li><code>isEmpty(" "), isEmpty(new int[] {1})</code> returns <b>false</b>.</li>
   */
  public static final boolean isEmpty(Object data) {
    if (data == null) return true;
    if (data instanceof Collection) return ((Collection)data).isEmpty();
    if (data instanceof Map)        return ((Map)       data).isEmpty();
    if (data instanceof Object[])   return ((Object[])  data).length == 0;
    return (data.toString().length() == 0) ||
           "null".equals(data.toString());
  }

  /**
   * Converts char array to byte array (per-element casting)
   *
   * @param from char array
   * @return byte array
   *
   * <br><br><b>Example</b>:
   * <li><code>toBytes(new char[] {0x0123, 0x4567, 0x89ab, 0xcdef})</code>
   * returns {0x23, 0x67, (byte)0xab, (byte)0xef}.</li>
   */
  public static final byte[] toBytes(char[] from) {
    byte[] result = new byte[from.length];

    for (int i = 0; i < from.length; i++) {
      result[i] = (byte)from[i];
    }

    return result;
  }

  /**
   * Converts byte array to char array (per-element casting)
   * @param from byte array
   * @return char array
   *
   * <br><br><b>Example</b>:
   * <li><code>toChars(new byte[] {0x23, 0x67, (byte)0xab, (byte)0xef})</code>
   * returns new char[] {0x23, 0x67, 0xab, 0xef}.</li>
   */
  public static final char[] toChars(byte[] from) {
    char[] result = new char[from.length];

    for (int i = 0; i < from.length; i++) {
      result[i] = (char)(0xff & from[i]);
    }

    return result;
  }

  /**
   * Calculates crc32 on a byte array
   *
   * @param data source bytes
   * @return its crc32
   *
   * <br><br><b>Example</b>:
   * <li><code>crc32(new byte[] {1, 2, 3})</code>
   * returns 1438416925.</li>
   */
  public static final long crc32(byte[] data) {
    CRC32 crc32 = new CRC32();
    crc32.update(data);
    return crc32.getValue();
  }

  /**
   * Calculates crc32 on a byte array
   *
   * @param data source bytes
   * @param off offset in the array
   * @param len length of the area to crc
   * @return its crc32
   *
   * <br><br><b>Example</b>:
   * <li><code>crc32(new byte[] {0, 1, 2, 3, 4}, 1, 3)</code>
   * returns 1438416925.</li>
   */
  public static final long crc32(byte[] data, int off, int len) {
    CRC32 crc32 = new CRC32();
    crc32.update(data, off, len);
    return crc32.getValue();
  }

  /**
   * Converts long to byte array (lower bytes first)
   *
   * @param from the long value
   * @return byte array
   *
   * <br><br><b>Example</b>:
   * <li><code>toBytes(0x0123456789abcdefl)</code>
   * returns {(byte)0xef, (byte)0xcd, (byte)0xab, (byte)0x89, 0x67, 0x45, 0x23, 0x01}.</li>
   */
  public static final byte[] toBytes(long from) {
    java.lang.Long l;
    byte[] result = new byte[8];

    for (int i = 0; i < 8; i++) {
      result[i] = (byte)from;
      from >>= 8;
    }

    return result;
  }

  /**
   * Gets the index of the first element of an array that equals to specified object
   *
   * @param what the object to look for in array
   * @param array array of objects to look for what
   * @return index of the object in array, or -1 if none found
   *
   * <br><br><b>Examples</b>:
   * <li><code>indexOf("abc", new String[] {"123", "abc", "xyz"}) </code>
   * returns 1;</li>
   *
   * <li><code>indexOf(null, new String[] {"123", "abc", null}) </code>
   * returns 2;</li>
   *
   */
  public static int indexOf(Object what, Object[] array) {
    for (int i = 0; i < array.length; i++) {
      if ((what == null && array[i] == null) ||
          (what != null && what.equals(array[i]))) return i;
    }
    return -1;
  }

  /**
   * Gets the index of the next element of an array that equals to specified object
   *
   * @param what the object to look for in array
   * @param array array of objects to look for what
   * @param fromIndex start search from this position
   * @return index of the object in array, or -1 if none found
   *
   * <br><br><b>Examples</b>:
   * <li><code>indexOf("abc", new String[] {"abc", "abc", "xyz", 1}) </code>
   * returns 1;</li>
   *
   * <li><code>indexOf(null, new String[] {"123", "abc", null}}, 1) </code>
   * returns 2;</li>
   *
   */
  public static int indexOf(Object what, Object[] array, int fromIndex) {
    for (int i = fromIndex; i < array.length; i++) {
      if ((what == null && array[i] == null) ||
          (what != null && what.equals(array[i]))) return i;
    }
    return -1;
  }

  /**
   * Gets the index of the first element of an list that equals to specified object
   *
   * @param what the object to look for in list
   * @param list list of objects to look for what
   * @return index of the object in list, or -1 if none found
   *
   * <br><br><b>Examples</b>:
   * <li><code>List l = new ArrayList();
   * l.add("123"); l.add("abc"), l.add("xyz");
   * indexOf("abc", list, 1} </code>
   * returns 1;</li>
   *
   * <li><code>List l = new ArrayList();
   * l.add("123"); l.add("abc"), l.add(null);
   * <li><code>indexOf(null, list) </code>
   * returns 2;</li>
   *
   */
  public static int indexOf(Object what, List list) {
    for (int i = 0; i < list.size(); i++) {
      Object current = list.get(i);
      if ((what == null && current == null) ||
          (what != null && what.equals(current))) return i;
    }
    return -1;
  }

  /**
   * Gets the index of the next element of a list that equals to specified object
   *
   * @param what the object to look for in list
   * @param list list of objects to look for what
   * @param fromIndex start search from this position
   * @return index of the object in list, or -1 if none found
   *
   * <br><br><b>Examples</b>:
   * <li><code>List l = new ArrayList();
   * l.add("abc"); l.add("abc"), l.add("xyz");
   * indexOf("abc", list, 1} </code>
   * returns 1;</li>
   *
   * <li><code>List l = new ArrayList();
   * l.add("abc"); l.add("abc"), l.add(null);
   * indexOf(null, list, 1} </code>
   * returns 2;</li>
   *
   */
  public static int indexOf(Object what, List list, int fromIndex) {
    for (int i = fromIndex; i < list.size(); i++) {
      Object current = list.get(i);
      if ((what == null && current == null) ||
          (what != null && what.equals(current))) return i;
    }
    return -1;
  }

  /**
   * Map factory
   *
   * @return Map - a new empty Map
   */
  public static Map getMap() {
    return new HashMap();
  }

  /**
   * Map factory
   * @param size the number of keys
   * @return Map - a new empty Map
   */
  public static Map getMap(int size) {
    return new HashMap(size * 4 / 3);
  }


  /**
   * Set factory
   * @param size the number of keys
   * @return Set - a new empty Set
   */
  public static Set getSet(int size) {
    return new HashSet(size);
  }

  /**
   * makes a singleton Map from a key-value pair
   *
   * @param key Object
   * @param value Object
   * @return Map that has just one key and its value
   *
   * <br><br><b>Example</b>:
   * <li><code>toMap("the key", "This is the value").get("the key");</code>
   * returns "This is the value";</li>
   *
   */
  public static Map toMap(Object key, Object value) {
    Map map = getMap(1);
    map.put(key, value);
    return map;
  }

  /**
   * makes a Map from two key-value pairs
   *
   * @param key1 Object first key
   * @param value1 Object first value
   * @param key2 Object second key
   * @param value2 Object second value
   * @return Map that has these two keys and values
   *
   * <br><br><b>Example</b>:
   * <li><code>toMap("2", "kaksi", "3", "kolmi").get("3");</code>
   * returns "kolmi";</li>
   */
  public static Map toMap(Object key1, Object value1, Object key2, Object value2) {
    Map map = toMap(key1, value1);
    map.put(key2, value2);
    return map;
  }
  /**
   * makes a Map from three key-value pairs
   *
   * @param key1 Object first key
   * @param value1 Object first value
   * @param key2 Object second key
   * @param value2 Object second value
   * @param key3 Object third key
   * @param value3 Object third value
   * @return Map the map that contains these three keys and values
   *
   * <br><br><b>Example</b>:
   * <li><code>toMap("1", "un", "2", "deux", "3", "troix").get("2");</code>
   * returns "deux";</li>
   */
  public static Map toMap(Object key1, Object value1,
                          Object key2, Object value2,
                          Object key3, Object value3) {
    Map map = toMap(key1, value1, key2, value2);
    map.put(key3, value3);
    return map;
  }

  /**
   * makes a Map from key-value pairs
   *
   * @param pairs Object[] odd elements of the array are keys, and even elements are values
   * @return Map the map that maps odd elements from <code>pairs</code> array to even elements;
   * if pairs is null, returns null
   *
   * <br><br><b>Example</b>:
   * <li><code>toMap(new String[] {"1", "un", "2", "deux", "3", "troix"}).get("2");</code>
   * returns "deux";</li>
   */
  public static Map toMap(Object[] pairs) {
    if (pairs == null) return null;

    Map map = getMap(pairs.length / 2);

    for (int i = 0; i < pairs.length; i+=2) {
      map.put(pairs[i], pairs[i+1]);
    }

    return map;
  }

  /**
   * makes a Set from an array of objects
   *
   * @param objects Object[] objects to fill the collection
   * @return Set the set that contains all the objects from array
   * if objects array is null, returns null
   *
   * <br><br><b>Example</b>:
   * <li><code>toSet(new String[] {"1", "2", "3").size();</code>
   * returns 3;</li>
   */
  public static Set toSet(Object[] objects) {
    if (objects == null) return null;

    Set set = getSet(objects.length);

    for (int i = 0; i < objects.length; i++) {
      set.add(objects[i]);
    }

    return set;
  }

  private static final int[] goodPrimes = {
      3, 5, 11, 17, 29, 47, 79, 127, 211,
      347, 563, 911, 1481, 2393, 3877, 6271, 10151, 16427, 26591,
      43019, 69623,
  };

//  private static int[] oddprimes = new int[20000];
//  private static int[] someprimes = new int[100];
//  static {
//    oddprimes[0] = 3;
//    for (int i = 1; i < oddprimes.length; i++) {
//      int candidate = (oddprimes[i-1] + 1) / 2 * 2 + 1;
//
//      for (boolean foundPrime = false; !foundPrime;) {
//        boolean gotDivisor = false;
//        for (int j = 0;
//             !gotDivisor && j < i && oddprimes[j] * oddprimes[j] <= candidate;
//             j++) {
//          gotDivisor = candidate % oddprimes[j] == 0;
//        }
//        if (gotDivisor) {
//          candidate += 2;
//        } else {
//          foundPrime = !gotDivisor;
//        }
//      }
//      oddprimes[i] = candidate;
//    }
//    someprimes[0] = 3;
//    someprimes[1] = 5;
//    int pos = 2;
//    for (int i = 0; i < oddprimes.length; i++) {
//      if (oddprimes[i] >= someprimes[pos-2] + someprimes[pos-1]) {
//        someprimes[pos++] = oddprimes[i];
//        if (pos % 10 == 0) System.out.println("\n    ");
//        System.out.print("" + oddprimes[i] + ", ");
//      }
//    }
//  }

  /**
   * Reallocates Map to a new HashMap, to improve hash map access.
   * Unfortunately, in 1.4.2 the hashmaps created all have 2^n size - have to wait
   * for 1.5 to do something about it. Prime numbers would be much better.
   * @param map Map
   * @return HashMap
   */
  public static HashMap reallocate(Map map) {
//    int size = 3;
//    for (int i = 0; i < goodPrimes.length && map.size() > goodPrimes[i]; i++) {
//      size = goodPrimes[i];
//    }
//
//    while (size < map.size()) {
//      size = 2 * size + 1;
//    }
    int size = map.size() * 4 / 3;

    HashMap result = new HashMap(size);
    result.putAll(map);
    return result;
  }
}

