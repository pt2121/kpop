package com.prt2121.kpop

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

class kpop {
  private val SLASH = File.separator

  @Test fun kotlinMainDir_exists() {
    val file = mock<File> {
      on { toString() } doReturn "dir"
    }

    assertEquals(File("dir-kotlin${SLASH}src${SLASH}main${SLASH}kotlin"), kotlinMainDir(file))
  }

  @Test fun kotlinMainDir_notExist() {
    val file = mock<File> {
      on { exists() } doReturn false
    }

    assertNull(kotlinMainDir(file))
  }

  @Test fun makeGradleKotlinDirPath_notExist() {
    val file = mock<File> {
      on { exists() } doReturn false
      on { absolutePath } doReturn "foo"
    }

    val ex = makeGradleKotlinDirPath(file).left().get()
    assert(ex is FileNotFoundException)
    assertEquals("foo doesn't exist", ex.message)
  }

  @Test fun makeGradleKotlinDirPath_parentAlreadyExists() {
    val p = mock<File> {
      on { exists() } doReturn true
    }

    val file = mock<File> {
      on { exists() } doReturn true
      on { parentFile } doReturn p
    }

    assert(makeGradleKotlinDirPath(file).left().get() is FileAlreadyExistsException)
  }

  @Test fun makeGradleKotlinDirPath_cool() {
    val p = mock<File> {
      on { exists() } doReturn false
    }

    val file = mock<File> {
      on { exists() } doReturn true
      on { parentFile } doReturn p
      on { parent } doReturn "hi${SLASH}src${SLASH}main${SLASH}java${SLASH}com${SLASH}foo${SLASH}bar"
    }

    assertEquals("hi-kotlin${SLASH}src${SLASH}main${SLASH}kotlin$SLASH", makeGradleKotlinDirPath(file).right().get())
  }
}
