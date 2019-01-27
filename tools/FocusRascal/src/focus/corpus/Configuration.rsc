module focus::corpus::Configuration

import Boolean;
import IO;
import List;
import Relation;
import Set;
import String;

// Pointer to mvn executable, used to build the classpath of Maven projects
public loc mavenLocation = |file:///usr/bin/mvn|;

// Where the GitHub repositories are stored and/or cloned
public loc datasetLocation = |file:///home/dig/repositories/FOCUS/tools/FocusRascal/dataset/|;

// Where the M3, PAM, and FOCUS models are stored
public loc javaM3sLocation = |project://FocusRascal/data/m3/java-projects|;

// Where the FOCUS models built from JARs are stored
public loc jarsM3sLocation = |project://FocusRascal/data/m3/jar-projects|;

// Where information about the GitHub projects to analyze is stored
public loc githubConfigFile = |project://FocusRascal/config/github-repos.properties|;
