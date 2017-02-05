package com.prt2121.kpop

import java.io.File
import java.nio.file.Files
import kotlin.properties.Delegates

/**
 * Represents a kotlin file that corresponds to a Java file/class in an RxBinding module
 */
class KFile {
  /**
   * These are imports of classes that Kotlin advises against using and are replaced in
   * {@link #resolveKotlinTypeByName}
   */
  private val IGNORED_IMPORTS = listOf(
      "java.util.List",
      "android.support.annotation.CheckResult",
      "android.support.annotation.NonNull",
      "android.support.annotation.RequiresApi"
  )

  var fileName: String by Delegates.notNull<String>()
  var packageName: String by Delegates.notNull<String>()
  var bindingClass: String by Delegates.notNull<String>()
  var extendedClass: String by Delegates.notNull<String>()
  val methods = mutableListOf<KMethod>()
  val imports = mutableListOf<String>()

  /** Generates the code and writes it to the desired directory */
  fun generate(directory: File) {
    var directoryPath = directory.absolutePath
    var finalDir: File? = null
    if (!packageName.isEmpty()) {
      packageName.split('.').forEach {
        directoryPath += File.separator + it
      }
      finalDir = File(directoryPath)
      Files.createDirectories(finalDir.toPath())
    }

    File(finalDir, fileName).bufferedWriter().use { writer ->
      writer.append("package $packageName\n\n")

      imports.filter { !IGNORED_IMPORTS.contains(it) }
          .forEach { im ->
            writer.append("import $im\n")
          }

      methods.forEach { m ->
        writer.append("\n${m.generate(bindingClass)}\n")
      }
    }
  }

  override fun toString(): String =
      "KFile(fileName=$fileName, packageName=$packageName, bindingClass=$bindingClass, extendedClass=$extendedClass methods=$methods, imports=$imports)"
}