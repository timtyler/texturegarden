package com.myjavatools.lib;

/**
 * <p>Title: My Java Tools Library. Big Map Interface</p>
 * <p>Description: This is an unfinished project; will return to this with Java SDK 1.5</p>
 * <p>Copyright: Copyright (c) 2004 Vlad Patryshev</p>
 * @author vlad@myjavatools.com
 * @version 1.0
 */

public interface BigMap extends java.util.Map {
  public Object get(Object x, Object y);
  public Object get(Object x, Object y, Object z);
  public Object get(Object x, Object y, Object z, Object t);
  public Object get(Object[]args);
  public void put(Object x, Object y, Object value);
  public void put(Object x, Object y, Object z, Object value);
  public void put(Object x, Object y, Object z, Object t, Object value);
  public void put(Object[]args);
}
