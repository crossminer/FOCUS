# FOCUS

This repository contains the source code implementation and the datasets used to get the experimental results of the following paper:

_FOCUS: A Recommender System for Mining API Function Calls and Usage Patterns_

## Introduction

FOCUS is a context-aware collaborative-filtering system that exploits cross relationships among OSS projects to suggest the inclusion of additional API invocations and concrete API usage patterns.

Implementing a collaborative filtering recommendation system requires to assess the similarity of two customers, i.e., two
projects. Existing approaches consider that any two projects using an API of interest are equally valuable sources of knowl-
edge. Instead, we postulate that not all projects are equal when it comes to recommending usage patterns: a project that is
highly similar to the project currently being developed should provide higher quality patterns than a highly dissimilar one.
Our collaborative filtering recommendation system attempts to narrow down the search scope by only considering the projects
that are the most similar to the active project. Therefore, methods that are typically used conjointly by similar projects
in similar contexts tend to be recommended first.

We incorporate these ideas in a new context-aware collaborative filtering recommender system that mines OSS repositories to provide developers with API **F**uncti**O**n **C**alls and **US**age patterns, FOCUS for short. Our approach employs a new model to represent mutual relationships between projects and collaboratively mines API usage from the most similar projects.

## Repository Structure

* tools
	* __Focus__: API Function calls and usage patterns approach.
	* __PAM__: utilities supporting PAM evaluation.
	* __arffExtractor__: Rascal metadata extractors.
* dataset
	* __jars__: parsed jar files;
	* __MV_L__: metadata of the MV<sub>L</sub> dataset (3,600 JAR files);
	* __MH_S__: metadata of the MV<sub>S</sub> dataset (1,600 JAR files);
	* __SH_L__: metadata of the SH<sub>L</sub> dataset (610 GitHub projects);
	* __SH_S__: metadata of the SH<sub>S</sub> dataset (200 GitHub projects);

__Note<sup>1</sup>__: the archive of 5,147 Java projects retrieved from GitHub via the Software Heritage archive is available at `anonymized`.

__Note<sup>2</sup>__: to get the results shown in the paper you must run the `Focus` tool included in this repository. Follow the instructions provided in the corresponding project.
