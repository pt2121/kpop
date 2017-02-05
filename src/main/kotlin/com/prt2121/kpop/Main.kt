package com.prt2121.kpop

import java.io.File

object Main {
  @JvmStatic fun main(args: Array<String>) {
    deleteOutputDir("RxBinding/rxbinding")
    val f = File("RxBinding/rxbinding/src/main/java/com/jakewharton/rxbinding2/view/RxView.java")
    val out = generateKotlinDir(f)
    val kFile = makeKFile(f)
    kFile.generate(out)
  }
}