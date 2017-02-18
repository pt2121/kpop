package com.prt2121.kpop

import com.prt2121.kpop.internal.ignoreImports
import com.prt2121.kpop.internal.javaFile
import com.prt2121.kpop.internal.makeCli

object Main {

  /**
   * gradle clean build
   * java -jar kpop-core/build/libs/kpop-core.jar -f Some.java -ig com.test.prat,com.ignore.imports
   */
  @JvmStatic
  fun main(args: Array<String>) {
    val cli = makeCli(args)
    val ignoreImports = cli?.let(::ignoreImports)

    cli?.let(::javaFile)?.let {
      val out = generateKotlinDir(it)
      println("writing to ${out.absoluteFile}")
      val kFile = makeKFile(it, ignoreImports.orEmpty())
      kFile.generate(out)
    }
  }
}
