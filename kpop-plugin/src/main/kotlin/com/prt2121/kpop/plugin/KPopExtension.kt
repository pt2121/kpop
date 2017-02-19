package com.prt2121.kpop.plugin

/**
 * Gradle extension that holds properties for KPop
 */
open class KPopExtension {
  open var includePattern: String = ""
  open var excludePattern: String = ""
  var ignoreImports = mutableListOf<String>()

  open fun ignoreImport(vararg imports: String) {
    for (i in imports)
      ignoreImports.add(i)
  }
}
