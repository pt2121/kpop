package com.prt2121.kpop

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.File

class KFileTest {
  @Test fun finalDir_noPackage() {
    assertNull(KFile.finalDir(File(""), ""))
  }

  @Test fun finalDir_cool() {
    val file = mock<File> {
      on { absolutePath } doReturn "path"
    }

    val path = KFile.finalDir(file, "com.prt2121.kpop")!!.path
    assertEquals("path/com/prt2121/kpop", path)
  }
}
