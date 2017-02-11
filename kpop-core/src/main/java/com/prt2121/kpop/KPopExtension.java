package com.prt2121.kpop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Gradle extension that holds properties for KPop
 */
public class KPopExtension {
  private File javaFile = null;
  private File javaDir = null;
  private String includePattern = "";
  private String excludePattern = "";
  private List<String> ignoreImports = new ArrayList<>();

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

  public String getIncludePattern() {
    return includePattern;
  }

  public void setIncludePattern(String includePattern) {
    this.includePattern = includePattern;
  }

  public String getExcludePattern() {
    return excludePattern;
  }

  public void setExcludePattern(String excludePattern) {
    this.excludePattern = excludePattern;
  }

  public List<String> getIgnoreImports() {
    return ignoreImports;
  }

  public void setIgnoreImports(List<String> ignoreImports) {
    this.ignoreImports = ignoreImports;
  }
}
