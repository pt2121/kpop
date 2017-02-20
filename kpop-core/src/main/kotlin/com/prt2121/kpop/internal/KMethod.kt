package com.prt2121.kpop.internal

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.AnnotationExpr
import com.github.javaparser.ast.expr.MarkerAnnotationExpr
import com.github.javaparser.ast.expr.Name
import com.github.javaparser.ast.type.*
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
 * Represents a method implementation that needs to be wired up in Kotlin
 */
class KMethod(declaration: MethodDeclaration) {
  private val name = declaration.name
  private val annotations: List<AnnotationExpr> = declaration.annotations
  private val comment = declaration.comment?.toString()?.let { cleanUpDoc(it) }
  private val extendedClass = declaration.parameters[0].type.toString()
  private val parameters = declaration.parameters.subList(1, declaration.parameters.size)
  private val returnType = declaration.type
  private val typeParameters = typeParams(declaration.typeParameters)
  private val GenericTypeNullableAnnotation = MarkerAnnotationExpr(Name("GenericTypeNullable"))

  /** Generates method level type parameters */
  private fun typeParams(params: List<TypeParameter>?): String? {
    if (params == null || params.isEmpty()) {
      return null
    }

    return params.joinToString(prefix = "<", postfix = ">") { p ->
      if (p.typeBound.isNotEmpty())
        "${p.name} : ${resolveKotlinType(p.typeBound.first())}"
      else
        "${p.name}"
    }
  }

  /**
   * Generates parameters in a kotlin-style format
   *
   * @param specifyType boolean indicating whether or not to specify the type (i.e. we don't
   *        need the type when we're passing params into the underlying Java implementation)
   */
  private fun kParams(specifyType: Boolean): String {
    val builder = StringBuilder()
    parameters.forEach { p -> builder.append("${p.name}${if (specifyType) ": " + resolveKotlinType(p.type) else ""}") }
    return builder.toString()
  }

  /**
   * Generates the kotlin code for this method
   *
   * @param bindingClass name of the RxBinding class this is tied to
   */
  internal fun generate(bindingClass: String): String {
    ///////////////
    // STRUCTURE //
    ///////////////
    // Javadoc
    // public inline fun DrawerLayout.drawerOpen(): Observable<Boolean> = RxDrawerLayout.drawerOpen(this)
    // <access specifier> inline fun <extendedClass>.<name>(params): <type> = <bindingClass>.name(this, params)

    val fParams = kParams(true)
    val jParams = kParams(false)

    val builder = StringBuilder();

    // doc
    builder.append("${comment ?: ""}\n")

    // signature boilerplate
    builder.append("inline fun ")

    // type params
    builder.append(if (typeParameters != null) typeParameters + " " else "")

    // return type
    val kotlinType = resolveKotlinType(returnType, annotations)
    builder.append("$extendedClass.$name($fParams): $kotlinType")

    builder.append(" = ")

    // target method call
    builder.append("$bindingClass.$name(${if (jParams.isNotEmpty()) "this, $jParams" else "this"})")

    // Void --> Unit mapping
    if (kotlinType == "Observable<Unit>") {
      builder.append(".map { Unit }")
    }

    return builder.toString()
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

  companion object {
    internal val DOC_LINK_REGEX = "[0-9A-Za-z._]*"

    internal fun resolveKotlinTypeByName(input: String): String =
        when (input) {
          "Object" -> "Any"
          "Void" -> "Unit"
          "Integer" -> "Int"
          "int", "char", "boolean", "long", "float", "short", "byte" -> input.capitalize()
          "List" -> "MutableList"
          else -> input
        }

    /** Cleans up the generated doc and translates some html to equivalent markdown for Kotlin docs */
    internal fun cleanUpDoc(doc: String): String {
      return doc.replace("<em>", "*")
          .replace("</em>", "*")
          .replace("<p>", "")
          // JavaParser adds a couple spaces to the beginning of these for some reason
          .replace("    *", "*")
          // {@code view} -> `view`
          .replace("\\{@code ($DOC_LINK_REGEX)\\}".toRegex()) { result: MatchResult ->
            val codeName = result.destructured
            "`${codeName.component1()}`"
          }
          // {@link Foo} -> [Foo]
          .replace("\\{@link ($DOC_LINK_REGEX)\\}".toRegex()) { result: MatchResult ->
            val foo = result.destructured
            "[${foo.component1()}]"
          }
          // {@link Foo#bar} -> [Foo.bar]
          .replace("\\{@link ($DOC_LINK_REGEX)#($DOC_LINK_REGEX)\\}".toRegex()) { result: MatchResult ->
            val (foo, bar) = result.destructured
            "[$foo.$bar]"
          }
          // {@linkplain Foo baz} -> [baz][Foo]
          .replace("\\{@linkplain ($DOC_LINK_REGEX) ($DOC_LINK_REGEX)\\}".toRegex()) { result: MatchResult ->
            val (foo, baz) = result.destructured
            "[$baz][$foo]"
          }
          //{@linkplain Foo#bar baz} -> [baz][Foo.bar]
          .replace("\\{@linkplain ($DOC_LINK_REGEX)#($DOC_LINK_REGEX) ($DOC_LINK_REGEX)\\}".toRegex()) { result: MatchResult ->
            val (foo, bar, baz) = result.destructured
            "[$baz][$foo.$bar]"
          }
          // Remove any trailing whitespace
          .replace("(?m)\\s+$".toRegex(), "")
          .trim()
    }
  }
}
