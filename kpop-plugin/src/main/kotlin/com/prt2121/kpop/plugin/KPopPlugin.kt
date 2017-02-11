package com.prt2121.kpop.plugin

import com.prt2121.kpop.KPopExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer

class KPopPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val kpop = project.extensions.create("kpop", KPopExtension::class.java)

    project.afterEvaluate {

      val container = project.properties["sourceSets"] as SourceSetContainer
      val fs = container.flatMap { src ->
        src.allJava.sourceDirectories.files
      }

      val genTask = project.tasks.create("generateKotlin", GenKotlinTask::class.java).apply {
        source(fs)
        include(kpop.includePattern)
        exclude(kpop.excludePattern)
      }

      genTask.outputs.upToDateWhen { false }
      project.tasks.add(genTask)
    }
  }
}
