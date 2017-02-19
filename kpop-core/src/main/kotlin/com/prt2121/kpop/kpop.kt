package com.prt2121.kpop

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.PackageDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.prt2121.kpop.internal.KMethod
import com.prt2121.kpop.internal.substringUntil
import org.funktionale.either.Either
import org.funktionale.either.flatMap
import org.funktionale.either.toEitherRight
import org.funktionale.option.toOption
import java.io.File
import java.io.FileNotFoundException

/**
 * based off of com.jakewharton.rxbinding.project
 * https://github.com/JakeWharton/RxBinding/blob/master/buildSrc/src/main/kotlin/com/jakewharton/rxbinding/project/KotlinGenTask.kt
 */

private val SLASH = File.separator

fun kotlinMainDir(projectDir: File): File =
    File("$projectDir-kotlin${SLASH}src${SLASH}main${SLASH}kotlin")

fun deleteOutputDir(projectDir: File): Boolean =
    kotlinMainDir(projectDir).deleteRecursively()

// for gradle project only
internal fun makeGradleKotlinDirPath(javaFile: File): Either<Throwable, String> =
    if (!javaFile.exists()) {
      Either.left(FileNotFoundException("${javaFile.absolutePath} doesn't exist"))
    } else if (!javaFile.parentFile.exists()) {
      println("can't create dir for kotlin package. using current directory")
      Either.right(".")
    } else {
      Either.right(javaFile.parent.replace("java", "kotlin")
          .replace("${SLASH}src", "-kotlin${SLASH}src")
          .substringUntil("src${SLASH}main${SLASH}kotlin$SLASH"))
    }

fun generateGradleKotlinDir(javaFile: File): Either<Throwable, File> =
    makeGradleKotlinDirPath(javaFile).toDisjunction().map(::File).toEither()

internal fun outDir(javaFile: File?, outDir: File?): Either<Throwable, File> =
    outDir.toOption()
        .fold({
          javaFile.toOption()
              .toEitherRight { FileNotFoundException() }
              .toDisjunction()
              .flatMap { f -> generateGradleKotlinDir(f).toDisjunction() }
              .toEither()
        }) {
          if (it.exists()) Either.left(FileAlreadyExistsException(it)) else Either.right(it)
        }

fun makeKFile(jFile: File, ignoredImports: List<String> = emptyList()): KFile {
  // Start parsing the java files
  val cu = JavaParser.parse(jFile)

  val kFile = KFile(ignoredImports)
  kFile.fileName = jFile.name.replace(".java", ".kt")

  // Visit the appropriate nodes and extract information
  cu.accept(object : VoidVisitorAdapter<KFile>() {

    override fun visit(n: PackageDeclaration, file: KFile) {
      file.packageName = n.name.toString()
      super.visit(n, file)
    }

    override fun visit(n: ClassOrInterfaceDeclaration, file: KFile) {
      file.bindingClass = n.nameAsString
      file.extendedClass = n.nameAsString
      super.visit(n, file)
    }

    override fun visit(n: MethodDeclaration, file: KFile) {
      if (n.isPublic)
        file.methods.add(KMethod(n))
      // Explicitly avoid going deeper, we only care about top level methods. Otherwise
      // we'd hit anonymous inner classes and whatnot
    }

    override fun visit(n: ImportDeclaration, file: KFile) {
      if (!n.isStatic) {
        file.imports.add(n.name.toString())
      }
      super.visit(n, file)
    }

  }, kFile)

  return kFile
}

