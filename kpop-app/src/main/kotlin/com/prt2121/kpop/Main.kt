package com.prt2121.kpop

object Main {

  /**
   * gradle clean build
   * java -jar kpop-app/build/libs/kpop-app.jar -f "Some.java"
   */
  @JvmStatic fun main(args: Array<String>) {
    makeCli(args)
        ?.let(::javaFile)
        ?.let {
          val out = generateKotlinDir(it)
          println("--> ${out.absoluteFile}")
          val kFile = makeKFile(it)
          kFile.generate(out)
        }
  }
}
