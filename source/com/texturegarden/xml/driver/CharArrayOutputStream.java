package com.texturegarden.xml.driver;

public class CharArrayOutputStream {
 private int max_size;
  private int size;
  private char[] array;

  final int INCREMENT = 10; // final?

  boolean check_bounds = true; // DEVELOPMENT_VERSION;

  public CharArrayOutputStream() {
    this.max_size = 0;
    this.size = 0;
    this.array = new char[this.max_size];
  }

  CharArrayOutputStream(int max_size) {
    this.max_size = max_size;
    this.size = 0;
    this.array = new char[this.max_size];
  }

  public void add(char c) {
    makeMoreIfNeeded();

    array[size++] = c;
  }

  public void overwrite(char c, int index) {
    if (check_bounds) {
      checkBounds(index);
    }

    array[index] = c;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public char get(int index) {
    if (check_bounds) {
      checkBounds(index);
    }

    return array[index];
  }

  public void deleteQuickly(int index) {
    if (check_bounds) {
      checkBounds(index);
    }
    
    char temp = array[index];
    array[index] = array[--size];
    array[size] = temp;
  }

  public void delete(int index) {
    if (check_bounds) {
      checkBounds(index);
    }
    
    char temp = array[index];
    for (int i = index; i < (size - 1); i++) {
      array[i] = array[i + 1];
    }

    array[--size] = temp;
  }

  public void swap(int j, int k) {
    char temp = get(j);
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
    char[] new_array = new char[max_size + INCREMENT];

    System.arraycopy(array, 0, new_array, 0, max_size);

    array = new_array;

    max_size += INCREMENT;
  }

  // Danger, Will Robinson!
  // Low-level access is permitted here for the sake of performance...
  public char[] getRawArray() {
    return array;
  }
}
