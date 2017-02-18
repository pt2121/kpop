package com.prt2121.sample;

import com.test.prat;
import com.ignore.imports;

public class StringUtils {

  private StringUtils() {
    throw new AssertionError("No instances.");
  }

  public static boolean isEmpty(final CharSequence cs) {
    return cs == null || cs.length() == 0;
  }
}
