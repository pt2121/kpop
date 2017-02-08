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
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.io.File

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

internal fun deleteOutputDir(projectDir: String) {
  val outputDir = File("$projectDir-kotlin${SLASH}src${SLASH}main${SLASH}kotlin")
  outputDir.deleteRecursively()
}

internal fun generateKotlinDir(javaFile: File): File {
  val kotlinSrc = javaFile.parent.replace("java", "kotlin")
      .replace("${SLASH}src", "-kotlin${SLASH}src")
      .substringUntil("src${SLASH}main${SLASH}kotlin${SLASH}")
  return File(kotlinSrc)
}

fun makeKFile(jFile: File): KFile {
  // Start parsing the java files
  val cu = JavaParser.parse(jFile)

  val kFile = KFile()
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

