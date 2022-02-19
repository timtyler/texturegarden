/**
 * TTChoice - Tim Tyler 1998 - 1999.
 */

package com.texturegarden.utilities;
import java.awt.Choice;
import java.awt.Color;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

public class TTChoice extends Object {
  public Vector vector;
  public Choice choice;

  public TTChoice(ItemListener il) {
    choice = new Choice();
    choice.addItemListener(il);
    choice.setBackground(Color.white);
    choice.setForeground(Color.black);

    vector = new Vector();
  }

  public void add(String s, int n) {
    choice.addItem(s);

    vector.addElement(new TTNumStr(n, s)); //  = s;
  }

  public void removeAll() {
    choice.removeAll();

    vector.removeAllElements(); //  = s;
  }

  public int str_to_num(String s) {
    int i = 0;
    Enumeration enumeration;
    TTNumStr temp_pair;

    enumeration = vector.elements();

    while (enumeration.hasMoreElements()) {

      temp_pair = (TTNumStr) (enumeration.nextElement());

      if (temp_pair.string == s)
        return temp_pair.number;
    }

    return -99; // -99 = not found...
  }

  public String num_to_str(int j) {
    int i = 0;
    Enumeration enumeration;
    TTNumStr temp_pair;

    enumeration = vector.elements();

    while (enumeration.hasMoreElements()) {
      temp_pair = (TTNumStr) (enumeration.nextElement());

      if (temp_pair.number == j)
        return temp_pair.string;
    }

    return ""; // null marker = not found...
  }
}
