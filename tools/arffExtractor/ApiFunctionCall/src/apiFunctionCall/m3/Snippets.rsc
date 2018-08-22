module apiFunctionCall::Snippets

import IO;
import lang::java::m3::AST;
import lang::java::m3::Core;
import lang::java::m3::TypeSymbol;
import List;
import Relation;
import Set;
import String;

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
		throw "There is no declaration related to the corresponding function.";
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

