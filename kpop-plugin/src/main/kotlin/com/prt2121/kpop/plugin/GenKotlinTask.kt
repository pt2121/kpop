package com.prt2121.kpop.plugin

import com.prt2121.kpop.kotlinMainDir
import com.prt2121.kpop.makeKFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File

open class GenKotlinTask : SourceTask() {

  private val projectDir: File
    get() = project.projectDir

  @get:OutputDirectory
  val outputDir: File by lazy {
    kotlinMainDir(projectDir)
  }

  @TaskAction
  fun genKotlin(inputs : IncrementalTaskInputs) {
    outputDir.deleteRecursively()

    getSource().forEach {
      val kFile = makeKFile(it)
      kFile.generate(outputDir)
    }
  }
}
