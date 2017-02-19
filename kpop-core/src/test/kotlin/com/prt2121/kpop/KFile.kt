package com.prt2121.kpop

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.File

class KFile {
  @Test fun finalDir_noPackage() {
    assertNull(finalDir(File(""), ""))
  }

  @Test fun finalDir_cool() {
    val file = mock<File> {
      on { absolutePath } doReturn "path"
    }

    val path = finalDir(file, "com.prt2121.kpop")!!.path
    assertEquals("path/com/prt2121/kpop", path)
  }
}
