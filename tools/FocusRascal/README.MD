# FocusRascal

FocusRascal is a [Rascal](https://www.rascal-mpl.org/) project used to mine Java projects (in source code or binary JAR form) to extract method invocations and declarations information used by the FOCUS recommender system. FocusRascal extracts so-called M<sup>3</sup> models from source code and JARs, which are then turned into FOCUS- and PAM-compatible models.

## Installation
In order to install FocusRascal, please follow these steps:

1. Install an appropriate version of **Eclipse RCP and RAP** according to your platform/distribution from this link: [Eclipse RCP and RAP](https://www.eclipse.org/downloads/packages/release/2018-12/r/eclipse-ide-rcp-and-rap-developers).
2. In Eclipse, navigate to `Help -> Install New Software...` and use the update site URL [https://update.rascal-mpl.org/unstable/](https://update.rascal-mpl.org/unstable/) to install Rascal.
3. Restart Eclipse to complete the installation.
4. Clone locally the `rascal-java-build-manager` project which is a required dependency of FocusRascal:
    - e.g. `$ git clone https://github.com/cwi-swat/rascal-java-build-manager`
5. In Eclipse, import the two projects `FocusRascal` and `rascal-java-build-manager` by navigating to `File -> Import -> Existing Projects into Workspace` and pointing the root directory to, respectively, `FocusRascal` and `rascal-java-build-manager`. Both projects should compile without any issue.

## Configuration

Make sure to edit the file `FocusRascal/src/focus/corpus/Configuration.rsc`.

The `mavenLocation` should point to your local `mvn` executable (which, on Linux, can be found with the command `which mvn`). The `datasetLocation` should be updated with an absolute path to the `dataset` directory in the `FocusRascal` project (e.g. `|file:///path/to/FOCUS/tools/FocusRascal/dataset/|;`, note the mandatory `|` around the path). The other relative paths can be left as is.

```
// Pointer to mvn executable, used to build the classpath of Maven projects
public loc mavenLocation = |file:///usr/bin/mvn|;

// Where the GitHub repositories are stored and/or cloned
public loc datasetLocation = |file:///FOCUS_ROOT/tools/FocusRascal/dataset/|;

// Where the M3, PAM, and FOCUS models are stored
public loc javaM3sLocation = |project://FocusRascal/data/m3/java-projects|;

// Where the FOCUS models built from JARs are stored
public loc jarsM3sLocation = |project://FocusRascal/data/m3/jar-projects|;

// Where information about the GitHub projects to analyze is stored
public loc githubConfigFile = |project://FocusRascal/config/github-repos.properties|;
```

## Quick Start

All manipulations must be done in Eclipse:

1. First, open the Rascal perspective: `Window -> Perspective -> Open Perspective -> Rascal`
2. Right-click on the `FocusRascal` project and select `Rascal Console`, this opens a new Rascal console in the lower part of the IDE
3. In the console, import the ExtractMetadata module by typing `import focus::corpus::ExtractMetadata;`

From here, the two main functions to invoke from the console are:

- `processGithubRepos()`, which reads the GitHub repositories stored in `FocusRascal/config/github-repos.properties`, clone all repositories, build their M<sup>3</sup> models, and write the output PAM and FOCUS files in `FocusRascal/data/m3/java-projects/`. By default, the `github-repos.properties` file points to the 17 libraries depicted in Table 1 of the ICSE'19 paper.  **Please bear in mind that it takes a long time to complete**.
    - To use it, simply type `processGithubRepos();` in the Rascal console.
- `processJARs(loc directory)` which takes as input the path to a directory containing a list of JARs, and writes as output the FOCUS models of those JARs in `FocusRascal/data/m3/jar-projects/`. For instance, to parse, analyse, and compute the M<sup>3</sup> and FOCUS models of the 3,600 JARs extracted from Maven Central (dataset MV<sub>L</sub> in the ICSE'19 paper), type the following in the Rascal console (replacing `/path/to/FOCUS` with the path to your clone of the FOCUS repository):
    - `processJARs(|file:///path/to/FOCUS/dataset/jars|);`
