This repository contains the source code implementation of FOCUS, the dataset as well as the experimental results for the following papers:

FOCUS: A Recommender System for Mining API Function Calls and Usage Patterns

Introduction to FOCUS
===================
FOCUS is a context-aware collaborative-filtering system that exploits cross relationships among OSS projects to suggest the inclusion of additional API invocations and concrete API usage patterns.

Implementing a collaborative filtering recommendation system requires to assess the similarity of two customers, i.e., two
projects. Existing approaches consider that any two projects using an API of interest are equally valuable sources of knowl-
edge. Instead, we postulate that not all projects are equal when it comes to recommending usage patterns: a project that is
highly similar to the project currently being developed should provide higher quality patterns than a highly dissimilar one.
Our collaborative filtering recommendation system attempts to narrow down the search scope by only considering the projects
that are the most similar to the active project. Therefore, methods that are typically used conjointly by similar projects
in similar contexts tend to be recommended first.

We incorporate these ideas in a new context-aware collaborative filtering recommender system that mines OSS repositories to provide developers with API **F**uncti**O**n** **C**alls and **US**age patterns: FOCUS. FOCUS employs a new model to represent mutual relationships between projects and collaboratively mines API usage from the most similar projects.

# Repository structure #

* tool
	* Focus - API FunctiOn Calls and USage patterns;
	* Pam scripts - utilities supporting PAM evaluation;
	* arffExtractor - Rascal metadata extractors;
* Dataset
	* Jars_Dataset
		* __jars__ contains parsed jar files;
		* __MVL__ contains FOCUS's metadata of 3600 jar files;
		* __MHL__ contains FOCUS's metadata of 1600 jar files;
	* Repositories_Dataset: Archive of 5,147 Java projects retrieved from GitHub via the Software
Heritage archive is available at http://vps.xxx.org/vault.tar.gz.
		* __SHL__ contains both FOCUS and PAM metadata of 610 projects;
		* __SHS__ contains both FOCUS and PAM metadata of 200 projects;
* Evaluation
	* Jars_Dataset
		* evaluation files 
		* Repositories_Dataset
	* PAM 
		* 10-folds valitazione result files 
	* Focus 
		* 10-folds valitazione result files			
