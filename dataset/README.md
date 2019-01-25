# FOCUS Datasets

This directory contains the raw JAR files and the meta-data of the four datasets used in the paper:

- [SH_L](./SH_L): 610 Java projects randomly selected and retrieved from GitHub via the [Software Heritage](https://www.softwareheritage.org/archive/)  archive
- [SH_S](./SH_S): the 200 smallest (in size) Java projects extracted from SH<sub>L</sub>
- [MV_L](./MV_L): 3,600 JAR files randomly selected and retrieved from the Maven Central repository
- [MV_S](./MV_S): 1,600 JAR files extracted from MV<sub>L</sub>: for every project, only one version is kepy

We also include the results obtained with PAM using the SH<sub>S</sub> dataset in the [PAM](./PAM) directory.

## Meta-data

Naturally, it is not possible to store in this repository the source code of every GitHub project we analyzed. Instead, the four datasets MV<sub>L</sub>, MV<sub>S</sub>, SH<sub>L</sub>, and SH<sub>S</sub> are organized as follows:

- Every dataset contains a file `List.txt` which points to the meta-data extracted from every project it contains (e.g. 200 projects in [SH_S/List.txt](./SH_S/List.txt))
- Each of these files (e.g. [SH_S/2e7aef2f64abd5997e1f2eb5960ea3cf1c072226.txt](./SH_S/2e7aef2f64abd5997e1f2eb5960ea3cf1c072226.txt)) contains the list of method invocations made from every method declaration in the project:
     - For instance, the line `org/appfuse/webapp/components/MenuItem/getUrl()#java/lang/String/startsWith(java.lang.String)` specifies that the method `getUrl()` in the class `org.appfuse.webapp.components.MenuItem` invokes the method `startsWith(String)` of class `java.lang.String`
- These meta-data files are produced using the tools contained in [FocusRascal](../tools/FocusRascal)
