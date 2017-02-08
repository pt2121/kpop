package com.prt2121.kpop

import java.io.File

data class GenConfig(
    val javaFile: File?,
    val javaDir: File?,
    val includePattern: Regex?,
    val excludePattern: Regex?,
    val ignoreImport: List<String>?
)
