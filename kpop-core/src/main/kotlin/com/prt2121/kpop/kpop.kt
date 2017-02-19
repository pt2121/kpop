package com.prt2121.kpop

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.PackageDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.AnnotationExpr
import com.github.javaparser.ast.expr.MarkerAnnotationExpr
import com.github.javaparser.ast.expr.Name
import com.github.javaparser.ast.type.*
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.prt2121.kpop.internal.KMethod
import com.prt2121.kpop.internal.substringUntil
import org.funktionale.either.Either
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.io.File
import java.io.FileNotFoundException

/**
 * based off of com.jakewharton.rxbinding.project
 * https://github.com/JakeWharton/RxBinding/blob/master/buildSrc/src/main/kotlin/com/jakewharton/rxbinding/project/KotlinGenTask.kt
 */

private val SLASH = File.separator
private val GenericTypeNullableAnnotation = MarkerAnnotationExpr(Name("GenericTypeNullable"))

private fun resolveKotlinTypeByName(input: String): String =
    when (input) {
      "Object" -> "Any"
      "Void" -> "Unit"
      "Integer" -> "Int"
      "int", "char", "boolean", "long", "float", "short", "byte" -> input.capitalize()
      "List" -> "MutableList"
      else -> input
    }

/** Recursive function for resolving a Type into a Kotlin-friendly String representation */
internal fun resolveKotlinType(inputType: Type, methodAnnotations: List<AnnotationExpr>? = null): String {
  when (inputType) {
    is ArrayType -> return resolveKotlinType(inputType.elementType, methodAnnotations)
    is ClassOrInterfaceType -> {
      val baseType = resolveKotlinTypeByName(inputType.nameAsString)
      if (inputType.typeArguments == null || !inputType.typeArguments.isPresent) {
        return baseType
      }
      return "$baseType<${inputType.typeArguments.get().map { type: Type -> resolveKotlinType(type, methodAnnotations) }.joinToString()}>"
    }
    is PrimitiveType, is VoidType -> return resolveKotlinTypeByName(inputType.toString())
    is WildcardType -> {
      var nullable = ""
      methodAnnotations
          ?.filter { it == GenericTypeNullableAnnotation }
          ?.forEach { nullable = "?" }
      if (inputType.superTypes != null) {
        return "in ${resolveKotlinType(inputType.superTypes.get())}$nullable"
      } else if (inputType.extendedTypes != null) {
        return "out ${resolveKotlinType(inputType.extendedTypes.get())}$nullable"
      } else {
        throw IllegalStateException("Wildcard with no super or extends")
      }
    }
    else -> throw NotImplementedException()
  }
}

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

