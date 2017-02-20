package com.prt2121.kpop.internal

import org.junit.Assert.assertEquals
import org.junit.Test

class KMethodTest {

  @Test fun resolveKotlinTypeByName() {
    assertEquals("Any", KMethod.resolveKotlinTypeByName("Object"))
    assertEquals("Unit", KMethod.resolveKotlinTypeByName("Void"))
    assertEquals("Int", KMethod.resolveKotlinTypeByName("Integer"))
    assertEquals("Int", KMethod.resolveKotlinTypeByName("int"))
    assertEquals("Char", KMethod.resolveKotlinTypeByName("char"))
    assertEquals("Boolean", KMethod.resolveKotlinTypeByName("boolean"))
    assertEquals("Long", KMethod.resolveKotlinTypeByName("long"))
    assertEquals("Float", KMethod.resolveKotlinTypeByName("float"))
    assertEquals("Short", KMethod.resolveKotlinTypeByName("short"))
    assertEquals("Byte", KMethod.resolveKotlinTypeByName("byte"))
    assertEquals("MutableList", KMethod.resolveKotlinTypeByName("List"))
  }

  @Test fun cleanUpDoc_em() {
    assertEquals("*", KMethod.cleanUpDoc("<em>"))
    assertEquals("**", KMethod.cleanUpDoc("<em></em>"))
    assertEquals("*doc*", KMethod.cleanUpDoc("<em>doc</em>"))
    assertEquals("*", KMethod.cleanUpDoc("</em>"))
  }

  @Test fun cleanUpDoc_p() {
    assertEquals("", KMethod.cleanUpDoc("<p>"))
  }

  @Test fun cleanUpDoc_code() {
    assertEquals("`doc`", KMethod.cleanUpDoc("{@code doc}"))
  }

  @Test fun cleanUpDoc_link() {
    assertEquals("[Foo]", KMethod.cleanUpDoc("{@link Foo}"))
  }

  @Test fun cleanUpDoc_linkMethod() {
    assertEquals("[Foo.bar]", KMethod.cleanUpDoc("{@link Foo#bar}"))
  }

  @Test fun cleanUpDoc_linkplain() {
    assertEquals("[baz][Foo]", KMethod.cleanUpDoc("{@linkplain Foo baz}"))
  }

  @Test fun cleanUpDoc_linkplainMethod() {
    assertEquals("[baz][Foo.bar]", KMethod.cleanUpDoc("{@linkplain Foo#bar baz}"))
  }
}
