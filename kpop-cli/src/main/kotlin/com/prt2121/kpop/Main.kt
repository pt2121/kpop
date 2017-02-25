package com.prt2121.kpop

object Main {

  /**
   * gradle clean build
   * java -jar kpop-core/build/libs/kpop-core.jar -f Some.java -ig com.test.prat,com.ignore.imports
   */
  @JvmStatic
  fun main(args: Array<String>) {
    val cli = makeCli(args)
    val ignoreImports = cli?.let(::ignoreImports)
    val outDir = cli?.let(::outputDir)
    val javaFile = cli?.let(::javaFile)

    val o = outDir(javaFile, outDir)

    if (o.isRight()) {
      val dir = o.right().get()
      val kFile = makeKFile(javaFile!!, ignoreImports.orEmpty())
      kFile.generate(dir)
    } else {
      println("error: ${o.left().get()}")
    }
  }
}
