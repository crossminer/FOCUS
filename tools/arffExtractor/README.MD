# API Function Call

API Function Call is a Rascal project developed by `anonymized` in the context of the `anonymized` project. This project is a prototype that aims at mining Java projects to extract facts used for API recommendation. Source code facts are gathered in M3 models that are then use to retrieve the information required by a recommender system.

## Requirements
The following are the requirements to successfully use the current project:

1. **Eclipse RCP and RAP:** download and use [Eclipse RCP and RAP](https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/photon/R/eclipse-rcp-photon-R-macosx-cocoa-x86_64.dmg).
2. **Rascal:** install the unstable version of Rascal Eclipse in your IDE from the update site https://update.rascal-mpl.org/unstable/.
3. **Rascal Java Build Manager:** project developed at CWI to extract the classpath of Maven and OSGi-based Java projects. This project is required to compute M3 models from source code without losing information from Java projects' dependencies. The Rascal Java Build Manager project must be cloned from https://github.com/cwi-swat/rascal-java-build-manager and imported to your current workspace.

## Quick Start
The main module to access the project functionality corresponds to `ExtractMetadata.rsc`. To start interacting with the implemented functionality:

1. Right click on the root of the API Function Call project and select the option `Rascal Console`.
2. Go to the `Rascal Console` and import the main module: 
```
import apiFunctionCall::ExtractMetadata;
```
3. Invoke the ```mavenM3``` function to download target Java projects from GitHub (if mandatory parameter is set to `true`) and create their corresponding M3 models:
```
mavenM3(githubDownload=<true|false>, directory=<projects-path>, mavenExecutable=<mvn-exec-path>, 
  m3sPath=<m3s-path>);
```
**Note:** main information regarding Java projects to be downloaded from GitHub is specified at `config/github-repos.properties`. The following is a brief description of its content:
```
repos=<number of repositories to be downloaded>
repos.<i>=<repository name>;<repository URL>
```
or invoke the ```jarM3``` function create their corresponding M3 models from jar files:
```
jarM3(directory=<jar-folder-path>);
```

## API

### apiFunctionCall::ExtractMetadata

1. `void mavenM3(bool githubDownload, loc directory, loc directory, loc mavenExecutable, loc m3sPath)`:
- **githubDownload**: true if projects must be downloaded, false otherwise. Default: `true`.
- **directory**: path to the folder where all Java projects will be downloaded or are available.
- **mavenExecutable**: path to the Maven executable file.
- **m3sPath**: path to the place where M3 models will be stored. Default: `|project://ApiFunctionCall/data/m3/java-projects|`
2. `void mavenM3(bool githubDownload, loc directory, loc directory, loc mavenExecutable, loc m3sPath)`:
- **folder**: path to the folder where all jar files are available.`
           
3. `void downloadGitHubRepos(loc directory, map[str,loc] urls)`:
Downloads GitHub repositories specified in the configuration file.
- **directory**: path to the folder where all Java projects will be downloaded or are available.
- **urls**: map with the URLs of the GitHub repositories (with repository name as key - `str`; and repository URL as value - `loc`).

4. `void createM3s(loc directory, map[str,loc] urls, loc m3sPath, loc mavenExecutable)`:
Uses the `lang::java::m3::Core` Rascal module to create M3 models from source code. 
- **directory**: path to the folder with all target Java projects.
- **urls**: map with the URLs of the GitHub repositories (with repository name as key - `str`; and repository URL as value - `loc`).
- **m3sPath**: path to the place where M3 models will be stored.
- **mavenExecutable**: path to the Maven executable file.

5. `bool checkTypeErrors(M3 m)`:
Checks if the M3 model passed as parameter has type resolution errors (which may mean that some dependencies have not been resolved). Take care with false positives!
