module focus::corpus::ExtractMetadata

import focus::corpus::Configuration;
import focus::corpus::Download;
import focus::io::File;
import focus::io::FileCSV;
import IO;
import lang::java::m3::AST;
import lang::java::m3::ClassPaths;
import lang::java::m3::Core;
import String;
import ValueIO;
import Set;

/*
 Build M3/FOCUS/PAM models from GitHub repositories.
 If download=false, the GitHub repositories are
 already cloned in 'directory'; otherwise, they'll be
 cloned in 'directory'
 */
void processGithubRepos(loc directory = datasetLocation, loc m3sPath = javaM3sLocation, bool download = true) {
	urls = ();

	if (download) {
		urls = getGithubReposURLs(githubConfigFile);
		downloadGitHubRepos(directory, urls);
	} else {
		urls = (getFileName(f) : f | f <- directory.ls, isDirectory(f));
	}

	extractModels(directory, urls, m3sPath);
}

/*
 Build FOCUS models from the JAR files located in 'directory'
 and writes the output to 'output'
 */
void processJARs(loc directory, loc output = jarsM3sLocation) {
	for (l <- directory.ls)
		buildFocusModelFromJAR(l, output);
}

/*
 Build a FOCUS model from the JAR lcoated at jarFile
 */
void buildFocusModelFromJAR(loc jarFile, loc output) {
	str filename = jarFile.file + ".txt";
	loc outputFile = output + ("g_" + filename);

	println("Building M3/FOCUS models for <jarFile> and writing the output to <outputFile>...");

	writeFile(outputFile, "");
	project = jarFile[path = ""];
	map[str, int] dict = ();

	M3 m3 = createM3FromJar(jarFile);

	for (c <- classes(m3)) {
		methodDecls = [m | m <- m3.containment[c], m.scheme == "java+method"];

		for (m <- methodDecls) {
			if (size(m3.methodInvocation[m]) > 8) {
				int count = 0;

				for (i <- m3.methodInvocation[m]) {
					if (size(m3.declarations[i]) == 0)
						count += 1;
				}

				if (count > 8) {
					for (i <- m3.methodInvocation[m]) {
						if (size(m3.declarations[i]) == 0)
							appendToFile(outputFile, "<m.path[1..]>#<i.path[1..]>\n");
					}
				}
			}
		}
	}
}

/*
 Download the GitHub repos pointed by 'urls' in 'directory'
 */
void downloadGitHubRepos(loc directory, map[str, loc] urls) {
	for (name <- urls) {
		repoPath = directory + name;
		println("Downloading repository <name> in <repoPath>...");

		try {
			downloadGithubRepo(name, urls[name], repoPath, force = false);
		} catch e: {
			println("Couldnt download <name>: <e>");
		}
	}
}

/*
 Extract M3/FOCUS/PAM models for every project pointed by 'urls' and stored in 'directory'.
 Output models are stored in 'm3sPath'
 */
void extractModels(loc directory, map[str, loc] urls, loc m3sPath) {
	for (name <- urls) {
		if (!projectExistsInLogFile(name)) {
			if (!exists(m3sPath + "<toLowerCase(name)>.m3")) {
				println("Building M3 for <name> [<urls[name]>]...");
				repoPath = directory + name;

				try {
					M3 m;
					loc pamFile   = m3sPath + "/PAM/<toLowerCase(name)>.txt";
					loc focusFile = m3sPath + "/Focus/<toLowerCase(name)>.txt";
					loc logFile   = m3sPath + "log.csv";

					// If it's a Maven project
					if (existFileWithName(repoPath, "pom.xml")) {
						classPaths = getClassPath(repoPath, mavenExecutable=mavenLocation);
						m = createM3FromFiles(repoPath,
							fetchFilesByExtension(repoPath, "java"),
							sourcePath = [repoPath],
							classPath = [*classsPaths[cp] | cp <- classPaths]);
						appendToFile(logFile, "<name>,MVN,<checkTypeErrors(m)> \n");
					}
					// Or a plain one
					else {
						m = createM3FromFiles(repoPath,
							fetchFilesByExtension(repoPath, "java"),
							sourcePath = [repoPath]);
						appendToFile(logFile, "<name>,PLAIN,<checkTypeErrors(m)> \n");
					}

					focusContent = getFocusFormat(m);
					pamContent = getPAMFormat(m);

					// Write the output M3/PAM/FOCUS models
					writeFile(focusFile, focusContent);
					writeFile(pamFile, pamContent);
					writeBinaryValueFile(m3sPath + "<toLowerCase(name)>.m3", m);
					println("M3/FOCUS/PAM models serialized for repository <name> in <m3sPath>.");
				} catch e: {
					appendToFile(logFile, "<name>,ERROR, \n");
					println("Couldnt create M3 for repository <name>: <e>");
				}
			} else {
				println("M3 model <toLowerCase(name)>.m3 already exists. Skipping.");
			}
		} else {
			println("Project <name> already exists in log file. Skipping.");
		}
	}
}

/*
 Print '<methodDeclaration>'#'<methodInvocation>' pairs for 'm3'
 */
str getPAMFormat(M3 m3) {
	str result = "@relation drools \n @attribute fqCaller string @attribute fqCalls string \n @data \n";

	for (c <- classes(m3)) {
		methodDecls = [m | m <- m3.containment[c], m.scheme == "java+method"];

		for (m <- methodDecls)
			for (i <- m3.methodInvocation[m])
				result = result + "\'<m.path[1..]>\',\'<i.path[1..]>\'\n";
	}

	return result;
}

/*
 Print <methodDeclaration>#<methodInvocation> pairs for 'm3'
 */
str getFocusFormat(M3 m3) {
	str result = "";

	for (c <- classes(m3)) {
		methodDecls = [m | m <- m3.containment[c], m.scheme == "java+method"];

		for (m <- methodDecls)
			for (i <- m3.methodInvocation[m])
				result = result + "<m.path[1..]>#<i.path[1..]>\n";
	}

	return result;
}

bool checkTypeErrors(M3 m3) {
	if (n <- m3.messages, /error(str s, _) := n)
		if(/.*cannot be resolved.*/:= s)
			return true;

	return false;
}
