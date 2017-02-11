package com.prt2121.kpop.plugin

import com.prt2121.kpop.KPopExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer

class KPopPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.afterEvaluate {

      val container = project.properties["sourceSets"] as SourceSetContainer
      val fs = container.flatMap { src ->
        src.allJava.sourceDirectories.files
      }

      project.extensions.create("kpop", KPopExtension::class.java)

      val genTask = project.tasks.create("generateKotlin", GenKotlinTask::class.java).apply {
        source(fs)
//        include("")
//        exclude("")
      }

      genTask.outputs.upToDateWhen { false }
      project.tasks.add(genTask)
    }
  }
}
