package com.prt2121.blah;

public class Yo {

  private StringUtils() {
    throw new AssertionError("No instances.");
  }

  public static boolean isEmpty(final CharSequence cs) {
    return cs == null || cs.length() == 0;
  }
}
