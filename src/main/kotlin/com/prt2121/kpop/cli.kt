package com.prt2121.kpop

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import java.io.File

fun makeCli(args: Array<String>): CommandLine? {
  val jOption = Option.builder("j")
      .longOpt("javafile")
      .hasArg()
      .desc("the input java file")
      .build()

  val options = Options()
      .addOption(jOption)

  try {
    args.forEach(::println)
    return DefaultParser().parse(options, args)
  } catch (e: ParseException) {
    println(e)
    return null
  }
}

fun javaFile(command: CommandLine):File? =
    if (command.hasOption("j"))
      File(command.getOptionValue('j'))
    else
      null