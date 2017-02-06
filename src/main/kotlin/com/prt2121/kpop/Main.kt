package com.prt2121.kpop

object Main {

  /**
   * java -jar build/libs/KPop.jar -javafile "Some.java"
   */
  @JvmStatic fun main(args: Array<String>) {
    makeCli(args)
        ?.let(::javaFile)
        ?.let {
          println("it $it")
          val out = generateKotlinDir(it)
          println("--> ${out.absoluteFile}")
          val kFile = makeKFile(it)
          kFile.generate(out)
        }
  }
}