package com.texturegarden.utilities;

public class ExpandingArray {
  private int max_size;
  private int size;
  private Object[] array;

  final int INCREMENT = 10; // final?

  boolean check_bounds = true; // Rockz.DEVELOPMENT_VERSION;

  public ExpandingArray() {
    this.max_size = 0;
    this.size = 0;
    this.array = new Object[this.max_size];
  }

  ExpandingArray(int max_size) {
    this.max_size = max_size;
    this.size = 0;
    this.array = new Object[this.max_size];
  }

  public void add(Object o) {
    makeMoreIfNeeded();

    array[size++] = o;
  }

  public void overwrite(Object o, int index) {
    if (check_bounds) {
      checkBounds(index);
    }

    array[index] = o;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public Object get(int index) {
    if (check_bounds) {
      checkBounds(index);
    }

    return array[index];
  }

  public void deleteQuickly(int index) {
    if (check_bounds) {
      checkBounds(index);
    }
    
    Object temp = array[index];
    array[index] = array[--size];
    array[size] = temp;
  }

  public void delete(int index) {
    if (check_bounds) {
      checkBounds(index);
    }
    
    Object temp = array[index];
    for (int i = index; i < (size - 1); i++) {
      array[i] = array[i + 1];
    }

    array[--size] = temp;
  }

  public void swap(int j, int k) {
    Object temp;
    temp = get(j);
    overwrite(get(k), j);
    overwrite(temp, k);
  }



  private void checkBounds(int index) {
    if (index < 0) {
      throw new ArrayIndexOutOfBoundsException("(" + index + ")");
    }

    if (index >= size) {
      throw new ArrayIndexOutOfBoundsException("(" + index + " / " + size + ")");
    }
  }

  private void makeMoreIfNeeded() {
    if (size >= max_size) {
      makeMore();
    }
  }

  private void makeMore() {
    Object[] new_array = new Object[max_size + INCREMENT];

    System.arraycopy(array, 0, new_array, 0, max_size);

    array = new_array;

    max_size += INCREMENT;
  }
}
