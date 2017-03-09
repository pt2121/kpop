## kpop
Gradle plugin for generating Kotlin extensions from Java static methods

#### usage

```
apply plugin: 'kpop'

buildscript{
  repositories {
    mavenCentral()

    dependencies{
      classpath 'com.github.prt2121:kpop-plugin:0.1.2'
    }
  }
}

kpop {
  includePattern '**/*.java'
  excludePattern '**/blah/*'
  ignoreImport 'com.test.prat', 'com.doh.blah'
}
```

##### run generateKotlin task

```
./gradlew generateKotlin
```

or [cli version](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22kpop-cli%22)

```
java -jar kpop-cli.jar -f Some.java
```

#### credit

This is a _"generalized/updated"_ version of 
[RxBinding](https://github.com/JakeWharton/RxBinding) 
[generateKotlin task](https://github.com/JakeWharton/RxBinding/blob/master/buildSrc/src/main/kotlin/com/jakewharton/rxbinding/project/KotlinGenTask.kt) 
by [Jake](https://github.com/JakeWharton) and [Zac](https://github.com/hzsweers) et al.
