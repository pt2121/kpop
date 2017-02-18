package com.prt2121.kpop.internal

fun String.substringUntil(delimiter: String, missingDelimiterValue: String = this): String {
  val index = indexOf(delimiter)
  return if (index == -1) missingDelimiterValue else substring(0, index + delimiter.length)
}
