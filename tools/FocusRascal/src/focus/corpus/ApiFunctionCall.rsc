module focus::corpus::ApiFunctionCall

import focus::corpus::Configuration;
import IO;
import lang::java::m3::Core;
import lang::java::jdt::m3::AST;
import lang::java::jdt::m3::Core;
import lang::csv::IO;
import Set;
import String;
import lang::java::m3::AST;
import util::Math;
import org::eclipse::scava::dependency::model::maven::Maven;
import org::eclipse::scava::dependency::model::maven::model::MavenModelBuilder;


/* 
	ast: M3 model of the actual project.
*/
public str getFocusFormat(M3 ast){
	str	result = "";
	listClass=classes(ast);
	for(loc c <- listClass){
	 	methodDecls = [e | e <- ast.containment[c], e.scheme == "java+method"];
	 	for (loc l <- methodDecls)	
			for (loc l1 <-  ast.methodInvocation[l])					
				result = result + "<l.path[1..]>#<l1.path[1..]>\n";
	}
	return result;
}
/* 
	ast: M3 model of the actual project.
*/
public str getPAMFormat(M3 ast){
	str	result = "@relation drools \n @attribute fqCaller string @attribute fqCalls string \n @data \n";
	listClass=classes(ast);
	for(loc c <- listClass){
	 	methodDecls = [e | e <- ast.containment[c], e.scheme == "java+method"];
	 	for (loc l <- methodDecls)	
			for (loc l1 <-  ast.methodInvocation[l])						
				result = result + " \'<l.path[1..]>\',\'<l1.path[1..]>\'\n";
	}
	return result;
}

/*
	root: root location of repository folders
*/
public void iterate(loc root){
	int count = 0;
	for(loc l <- root.ls){
		exportProjectFiles(l);
		count = count +1;
		print("-" + toString(count));
	}
}
/*
	project: project to be analyzed
*/
public void exportProjectFiles(loc project){
	try {
		str projectName = getProjectName(project);
		projectName = projectName + ".txt";
		loc focus = resultPath + "Focus/" + projectName;
		loc PAM = resultPath + "PAM/"  + projectName;
		println(project);
		if(!exists(focus)){
			print("\t pre");
			model = createM3FromDirectory(project);
			print("\t PAM");
			str PAMContent = getPAMFormat(model);
			print("\t Focus");
			str focusContent = getFocusFormat(model);
			writeFile(focus, focusContent);
			writeFile(PAM, PAMContent);
		}
	}
	catch :
		println("\t###" + project.path);
}

str getProjectName(loc project){
	str projectName = project.path;
	int i = findLast(projectName, "/");
	if (size(projectName)-1 == i){
		projectName = projectName[..-1];
		int i = findLast(projectName, "/");
	}
	projectName = projectName[findLast(projectName, "/")+1..];
	return projectName;
}