This repository contains the source code implementation of FOCUS, the datasets as well as the experimental results for the following paper:

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

We incorporate these ideas in a new context-aware collaborative filtering recommender system that mines OSS repositories to provide developers with API **F**uncti**O**n **C**alls and **US**age patterns, FOCUS for short. Our approach employs a new model to represent mutual relationships between projects and collaboratively mines API usage from the most similar projects.

# Repository structure #

* tool
	* Focus - API Function calls and usage patterns;
	* Pam scripts - utilities supporting PAM evaluation;
	* arffExtractor - Rascal metadata extractors;
* Dataset
	* Jars_Dataset
		* __jars__: parsed jar files;
		* __MVL__: metadata of the MV<sub>L</sub> dataset (3,600 jar files);
		* __MHL__: metadata of the MV<sub>S</sub> dataset (1,600 jar files);
	* Repositories_Dataset: The archive of 5,147 Java projects retrieved from GitHub via the Software
Heritage archive is available at http://vps.xxx.org/vault.tar.gz.
		* __SHL__: metadata of the SH<sub>L</sub> dataset (610 GitHub projects);
		* __SHS__: metadata of the SH<sub>S</sub> dataset (200 GitHub projects);
* Evaluation
	* Jars_Dataset
		* evaluation files 
		* Repositories_Dataset
	* PAM 
		* leave-one-out cross validation result files 
	* Focus 
		* ten-fold cross validation result files			
