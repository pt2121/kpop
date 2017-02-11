package com.prt2121.kpop;

import java.io.File;
import java.util.List;
import kotlin.text.Regex;

public class KPopExtension {
  private File javaFile = null;
  private File javaDir = null;
  private Regex includePattern = null;
  private Regex excludePattern = null;
  private List<String> ignoreImport = null;

  public File getJavaFile() {
    return javaFile;
  }

  public void setJavaFile(File javaFile) {
    this.javaFile = javaFile;
  }

  public File getJavaDir() {
    return javaDir;
  }

  public void setJavaDir(File javaDir) {
    this.javaDir = javaDir;
  }

  public Regex getIncludePattern() {
    return includePattern;
  }

  public void setIncludePattern(Regex includePattern) {
    this.includePattern = includePattern;
  }

  public Regex getExcludePattern() {
    return excludePattern;
  }

  public void setExcludePattern(Regex excludePattern) {
    this.excludePattern = excludePattern;
  }

  public List<String> getIgnoreImport() {
    return ignoreImport;
  }

  public void setIgnoreImport(List<String> ignoreImport) {
    this.ignoreImport = ignoreImport;
  }
}
