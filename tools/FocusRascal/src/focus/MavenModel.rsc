module focus

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

public void createM3sFromJar(loc jarFile,
                                loc graphName =
|file:///home/juri/Scrivania/phuong|) {
     str fileName = jarFile.file + ".txt";
     graphName = graphName + ("g_" + fileName);
     println(graphName);
     writeFile(graphName,"");
     project = jarFile[path=""];
     map[str,int] dict = ();

             model = createM3FromJar(jarFile);
             println(model);
              clazzes = classes(model);
      for(loc clazz <- clazzes){
          methodDecls = [e | e <- model.containment[clazz], e.scheme ==
"java+method"];
          for (loc l <- methodDecls){
              if( size(model.methodInvocation[l]) > 8){
                 int count = 0;
                 for (loc l1 <-  model.methodInvocation[l]){
                     if(size(model.declarations[l1]) == 0)
                         count += 1;
             }
                 if(count > 8){
                     for (loc l1 <-  model.methodInvocation[l])
                         if(size(model.declarations[l1]) == 0)
                             appendToFile(graphName,
"<l.path[1..]>#<l1.path[1..]>\n");
                 }
             }
         }
     }
}

public void jarM3(loc folder){
     for(loc l <- folder.ls) {
         createM3sFromJar(l);
     }
}


void mavenM3(bool githubDownload=true,
     //loc directory=|file:///Users/ochoa/tmp/github|,
     loc directory=|file:///home/juri/Documenti/dataset|,
     loc
mavenExecutable=|file:///home/juri/development/apache-maven-3.5.4/bin/mvn|,
     loc m3sPath=|project://FocusRascal/data/m3/java-projects|) {
     println(directory);
     urls = ();
     if(githubDownload) {
         urls =
getGithubReposURLs(|project://FocusRascal/config/github-repos.properties|);
         downloadGitHubRepos(directory, urls);
     }
     else {
         urls = (getFileName(f) : f | f <- directory.ls, isDirectory(f));
     }
     createM3s(directory, urls, m3sPath, mavenExecutable);
}

void downloadGitHubRepos(loc directory, map[str,loc] urls) {
     for(name <- urls) {
         println("Downloading <name> repository...");
         repoPath = directory + name;

         try {
             // Download GitHub repository if needed
             downloadGithubRepo(name, urls[name], repoPath, force=false);
         }
         catch e: {
             println("Could not download <name> repository. Error: <e>" );
             continue;
         }
     }
}

void createM3s(loc directory, map[str,loc] urls, loc m3sPath, loc
mavenExecutable) {
     for(name <- urls) {
         if(!projectExistsInLogFile(name)){
             if(!exists(m3sPath + "<toLowerCase(name)>.m3")){
                 println("Creating M3 for <name> repository
<urls[name]>...");
                 repoPath = directory + name;
                 try {
                     M3 m;
                     loc pamFile = m3sPath + "/PAM/<toLowerCase(name)>.txt";
                     loc focusFile = m3sPath +
"/Focus/<toLowerCase(name)>.txt";
                     if(existFileWithName(repoPath, "pom.xml")) {
                         m3Path = m3sPath + toLowerCase(name);
                         if(!exists(m3Path)) {
                             // Gather classpath and create M3
                             classPaths = getClassPath(repoPath,
mavenExecutable=mavenExecutable);
                             m = createM3FromFiles(repoPath,
                                 fetchFilesByExtension(repoPath, "java"),
                                 sourcePath=[repoPath],
                                 classPath=[*classPaths[cp] | cp <-
classPaths]);
                             appendToFile(m3sPath + "log.csv",
"<name>,MVN,<checkTypeErrors(m)> \n");
                         }
                         else {
                             println("<name> M3 already exists.
Skipping..");
                         }
                     }
                     else {
                         m = createM3FromFiles(repoPath,
                             fetchFilesByExtension(repoPath, "java"),
                             sourcePath=[repoPath]);
                         appendToFile(m3sPath + "log.csv",
"<name>,PLAIN,<checkTypeErrors(m)> \n");
                     }

                     content = getFocusFormat(m);
                     content2 = getPAMFormat(m);
                     writeFile(focusFile, content);
                     writeFile(pamFile, content2);
                     writeBinaryValueFile(m3sPath +
"<toLowerCase(name)>.m3", m);
                     println("M3 was created for <name> repository.");
                 }
                 catch e: {
                     appendToFile(m3sPath + "log.csv", "<name>,ERROR, \n");
                     println("Could not create M3 for <name> repository.
Error: <e>");
                     continue;
                 }
             }
         }
         else println("\t already extracted " + name);
     }
}
public str getPAMFormat(M3 ast){
     str    result = "@relation drools \n @attribute fqCaller string
@attribute fqCalls string \n @data \n";
     listClass=classes(ast);
     for(loc c <- listClass){
          methodDecls = [e | e <- ast.containment[c], e.scheme ==
"java+method"];
          for (loc l <- methodDecls)
             for (loc l1 <-  ast.methodInvocation[l])
                 result = result + "
\'<l.path[1..]>\',\'<l1.path[1..]>\'\n";
     }
     return result;
}

public str getFocusFormat(M3 ast){
     str    result = "";
     listClass=classes(ast);
     for(loc c <- listClass){
          methodDecls = [e | e <- ast.containment[c], e.scheme ==
"java+method"];
          for (loc l <- methodDecls)
             for (loc l1 <-  ast.methodInvocation[l])
                 result = result + "<l.path[1..]>#<l1.path[1..]>\n";
     }
     return result;
}

bool checkTypeErrors(M3 m) {
     if(n <- m.messages, /error(str s,_) := n) {
         if(/.*cannot be resolved.*/:= s) {
             return true;
         }
     }
     return false;
}
