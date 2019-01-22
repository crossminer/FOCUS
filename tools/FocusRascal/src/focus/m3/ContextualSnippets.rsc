module focus::m3::ContextualSnippets

import IO;
import lang::java::m3::AST;
import lang::java::m3::Core;
import lang::java::m3::TypeSymbol;
import List;
import Relation;
import Set;
import String;

import focus::m3::Snippets;


/* 
	fun: logical location of the actual function. 
	inv: logical location of the suggested invocation;
	ctx: M3 model of the project where the suggested function is declared;
	m3: M3 model of the actual project.
*/
str contextualFunctionSnippet(loc fun, loc inv, M3 ctx, M3 m3) {
	// Check relevant modifiers
	isStatic = false;
	isAbstract = false;
	for(m <- ctx.modifiers[inv]) {
		if(m := static()) {
			isStatic = true;
		}
		else if(m := abstract()) {
			isAbstract = true;
		}
		else {
			continue;
		}
	}
	
	// Check return type
	returnType = getOneFrom(ctx.types[inv]).returnType;
	// Check parameters
	params = getOneFrom(ctx.types[inv]).parameters;
	
	// Build snippet 
	// Check when multiple classes are retrieved
	clazz = getOneFrom({p | p <- invert(ctx.containment+)[inv], isClass(p)});
	// This wont be like this always (e.g. Factories, Subclasses).
	snippet = if(!isStatic) "<clazz.file> my<clazz.file> = new <clazz.file>(); \n"; else "";
	snippet += "<returnTypeForSnippet(returnType)>";
	snippet += 	if(isStatic) "<clazz.file>.<functionName(inv)>"; 
				else "my<clazz.file>.<functionName(inv)>";
	// What about suggesting parameters according to method params, local variables and fields?
	snippet += "(<for(p <- params){><paramForSnippet(p)><;}>);";
	return snippet;
}

private str paramForSnippet(TypeSymbol param) {
	typ = typeAsStr(param);
	return (contains(typ, "\<")) ? 
			"p<substring(typ, 0, findFirst(typ, "\<"))>" : 
			"p<replaceFirst(typ, "<substring(typ, 0, 1)>", toUpperCase("<substring(typ, 0, 1)>"))>" ;
}

private str returnTypeForSnippet(TypeSymbol returnType) 
	= (\void() := returnType) ? "" : "<typeAsStr(returnType)> genResult = ";

// Types taken from lang::java::m3::TypeSymbol module.
private str typeAsStr(TypeSymbol typ) {
	result = "";
	switch(typ) {
		case t:array(_,_) : result = typeAsStr(t.component);
		case t:class(_,_) : {
			prev = ("" | it + ",<typeAsStr(c)>" | c <- t.typeParameters);
			result = "<t.decl.file><(isEmpty(prev) ? "" : "\<<substring(prev,1)>\>")>";
		}
		case t:interface(_,_) : {
			prev = ("" | it + ",<typeAsStr(c)>" | c <- t.typeParameters);
			result = "<t.decl.file><(isEmpty(prev) ? "" : "\<<substring(prev,1)>\>")>";
		}
		case t:enum(_) : result = "<t.decl.file>";
		case \boolean() : result = "boolean";
		case \byte() : result = "byte";
		case \double() : result = "double";
		case \char() : result = "char";
		case \float() : result = "float";
		case \int() : result = "int";
		case \long() : result = "long";
		case \short() : result = "short";
		case \void() : result = "void";
	}
	return result;
}
