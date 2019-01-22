module focus::m3::MethodInvocations

import lang::java::m3::Core;
import List;
import ListRelation;
import Set;

import focus::m3::Snippets;

// Returns a ListRelation where for a tuple <a,b>:
// a: invoked method; b: line where it is invoked
lrel[loc,int] orderedMethodInvocations(loc fun, M3 m3) {
	ordered = [];
	meths = m3.methodInvocation[fun];
	for(m <- meths) {
		ordered += [<n, m> | n <- invocationInSnippet(fun, m, m3)];
	}
	return invert(sort(ordered));
}
