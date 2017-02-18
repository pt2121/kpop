package com.prt2121.kpop

import com.prt2121.kpop.internal.javaFile
import com.prt2121.kpop.internal.makeCli

object Main {

  /**
   * gradle clean build
   * java -jar kpop-app/build/libs/kpop-app.jar -f "Some.java"
   */
  @JvmStatic
  fun main(args: Array<String>) {
    makeCli(args)
        ?.let(::javaFile)
        ?.let {
          val out = generateKotlinDir(it)
          println("writing to ${out.absoluteFile}")
          val kFile = makeKFile(it)
          kFile.generate(out)
        }
  }
}
