module focus::m3::Snippets

import IO;
import ValueIO;
import lang::java::m3::AST;
import lang::java::m3::Core;
import lang::java::m3::TypeSymbol;
import List;
import Relation;
import Set;
import String;

/**
 * Infer M3 models for every sub-directory path/X
 * and serialize them into path/X.m3
 */
void inferAllM3s(loc path) {
	if (isDirectory(path)) {
		for (f <- listEntries(path), isDirectory(path + f)) {
			loc project = path + f;
			loc dest = path + (project.file + ".m3");
			
			if (!isFile(dest)) {
				print("Creating M3 model for <project.file>... "); 
				M3 m = createM3FromDirectory(project);
				println("Done.");
				print("Serializing <dest>... ");
				writeBinaryValueFile(dest, m);
				println("Done.");
			} else {
				println("<dest> exists. Skipping.");
			}
		};
	} else {
		println("<path> does not exist. Skipping.");
	}
}

// Meant to be invoked from FOCUS
// Inputs are in the form:
// java/lang/StringBuffer/StringBuffer() : g_xmpcore-5.1.3.jar.txt
str retrieveSnippet(str fun, str project) {
	try {
		loc m3file = |file:///run/media/dig/Cioran/Research/dataset/SH_L/| + (project + ".m3");
		
		if (isFile(m3file)) {
			// Assuming this is a method, but could be a constructor or smth else
			// [] should be replaced with %5B%5D
			loc fun = |java+method:///<replaceAll(replaceAll(fun, "[", "%5B"), "]", "%5D")>|;
			M3 m = readBinaryValueFile(#M3, m3file);
			
			println("Retrieving snippet for <fun>");
			println(functionSnippet(fun, m));
		} else {
			println("Couldnt find <m3file>");
		}
	} catch MalFormedURI(str l): {
		println("Couldnt build a location for <fun>");
	};

	return "";
}

// Note: use only with M3 models created from source code.
str functionSnippet(loc fun, M3 m3) {
	srcs = m3.declarations[fun];
	try {
		src = getOneFrom(srcs);
		code = readFileLines(src);
		// Note: begin and end line are not required. Rascal provides it for free.
		//return ("" | it + "<l> \n" | l <- slice(code, src.begin.line - 1 , src.end.line - src.begin.line + 1));
		return ("" | it + "<t>" | t <- code);
	}
	catch EmptySet(): {
		println("There is no declaration related to the corresponding function");
		return "";
	}
}

// Note: use only with M3 models created from source code.
list[str] functionSnippetAsList(loc fun, M3 m3) {
	srcs = m3.declarations[fun];
	try {
		src = getOneFrom(srcs);
		return readFileLines(src);
	}
	catch EmptySet(): {
		throw "There is no declaration related to the corresponding function.";
	}
}

/*
	Retruns a list with the line number where the function inv is called
	in the function fun. 
*/
list[int] invocationInSnippet(loc fun, loc inv, M3 m3) {
	locs = [];
	code = functionSnippetAsList(fun, m3);
	name = functionName(inv);
	for(i <- [0..size(code)]) {
		if(/<r1:<name>()>/ := inv.file && /<r2:.+<name>().?>/ := code[i]) {
			locs += i;
		}
		else if(/<r1:<name>(.+)>/ := inv.file && /<r2:.+<name>(.+).?>/ := code[i]) {
			locs += i;
		}
		else {
			continue;
		}
	}
	return locs;
}

str functionName(loc fun)
	= substring(fun.file, 0, findFirst(fun.file, "("));

