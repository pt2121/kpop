package com.prt2121.kpop

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

class fileHelper {
  private val SLASH = File.separator

  @Test fun kotlinMainDir_exists() {
    val file = mock<File> {
      on { toString() } doReturn "dir"
      on { exists() } doReturn true
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

  @Test fun outDir_nullDir_nullFile() {
    assert(outDir(null, null).left().get() is IllegalArgumentException)
  }

  @Test fun outDir_dirExists() {
    val dir = mock<File> {
      on { exists() } doReturn true
    }

    assert(outDir(null, dir).left().get() is FileAlreadyExistsException)
  }

  @Test fun outDir_dirNotExists() {
    val dir = mock<File> {
      on { exists() } doReturn false
    }

    assertEquals(dir, outDir(null, dir).right().get())
  }
}
