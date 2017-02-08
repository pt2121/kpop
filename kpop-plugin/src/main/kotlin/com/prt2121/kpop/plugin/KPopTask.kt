package com.prt2121.kpop.plugin

import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

open class KPopTask: SourceTask() {
  @TaskAction
  fun genKotlin(inputs : IncrementalTaskInputs) {

  }
}
