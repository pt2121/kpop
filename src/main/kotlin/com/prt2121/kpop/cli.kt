package com.prt2121.kpop

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import java.io.File

fun makeCli(args: Array<String>): CommandLine? {
  val fileOpt = Option.builder("f")
      .longOpt("java-file")
      .hasArg()
      .desc("input java file")
      .build()

  val dirOpt = Option.builder("d")
      .longOpt("java-dir")
      .hasArg()
      .desc("input java directory")
      .build()

  val includeOpt = Option.builder("i")
      .longOpt("include-pattern")
      .hasArg()
      .desc("include regex pattern")
      .build()

  val excludeOpt = Option.builder("e")
      .longOpt("exclude-pattern")
      .hasArg()
      .desc("exclude regex pattern")
      .build()

  val options = Options()
      .addOption(fileOpt)
      .addOption(dirOpt)
      .addOption(includeOpt)
      .addOption(excludeOpt)

  try {
    args.forEach(::println)
    return DefaultParser().parse(options, args)
  } catch (e: ParseException) {
    println(e)
    return null
  }
}

fun javaFile(command: CommandLine): File? =
    if (command.hasOption("f"))
      File(command.getOptionValue('f'))
    else
      null

fun javaDir(command: CommandLine): File? =
    if (command.hasOption("d"))
      File(command.getOptionValue('d'))
    else
      null

fun includePattern(command: CommandLine): Regex? =
    if (command.hasOption("i"))
      Regex(command.getOptionValue("i"))
    else
      null

fun excludePattern(command: CommandLine): Regex? =
    if (command.hasOption("e"))
      Regex(command.getOptionValue("e"))
    else
      null
