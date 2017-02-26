package com.prt2121.kpop.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import java.io.File

class KPopPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val kpop = project.extensions.create("kpop", KPopExtension::class.java)

    project.afterEvaluate {
      val javaSrc = project.convention.getPlugin(JavaPluginConvention::class.java).sourceSets

      val fs = javaSrc.flatMap { src ->
        src.allJava.sourceDirectories.files
      }

      val genTask = project.tasks.create("generateKotlin", GenKotlinTask::class.java).apply {
        source(fs + androidLibJavaSources(project) + androidJavaSources(project))
        include(kpop.includePattern)
        exclude(kpop.excludePattern)
        ignoreImports(kpop.ignoreImports)
      }

      genTask.outputs.upToDateWhen { false }
      project.tasks.add(genTask)
    }
  }

  private fun androidLibJavaSources(project: Project): List<Collection<File>> {
    val variants = project.extensions.findByType(LibraryExtension::class.java)?.libraryVariants
    val variant = variants?.first { it.name == "release" }
    return variant?.sourceSets.orEmpty().map { it.javaDirectories }
  }

  private fun androidJavaSources(project: Project): List<Collection<File>> {
    val variants = project.extensions.findByType(AppExtension::class.java)?.applicationVariants
    val variant = variants?.first { it.name == "release" }
    return variant?.sourceSets.orEmpty().map { it.javaDirectories }
  }
}
