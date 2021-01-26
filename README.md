[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.2550379.svg)](https://doi.org/10.5281/zenodo.2550379)

# FOCUS

This repository contains the source code implementation of FOCUS and the datasets used to replicate the experimental results of our paper submitted to the IEEE Transactions on Software Engineering:

_Recommending API Function Calls and Code Snippets to Support Software Development_

Authors: Phuong T. Nguyen, Juri Di Rocco, Claudio Di Sipio, Davide Di Ruscio, and Massimiliano Di Penta

which is an extension of the following paper, which has been accepted and published in the Proceedings of the 41st International Conference on Software Engineering (ICSE 2019):

_FOCUS: A Recommender System for Mining API Function Calls and Usage Patterns_

Authors: Phuong T. Nguyen, Juri Di Rocco, Davide Di Ruscio, Lina Ochoa, Thomas Degueule, and Massimiliano Di Penta

A pre-print version of the paper is available [here](https://hal.archives-ouvertes.fr/hal-02023023/document).

Our paper has been awarded two badges by the ICSE 2019 Artifact Evaluation Track, namely "Artifacts Available" and "Artifacts Evaluated." This means that all the related artifacts have been properly documented, and they are consistent, complete, and reproducible. Furthermore, they include appropriate evidence to facilitate future reuse and reproduction. We also strictly
adhere to norms and standards of the research community for artifacts of this type.

<p align="center">
<img src="https://github.com/crossminer/FOCUS/blob/master/Badges.png" width="250">
</p>

A detailed instruction on how to experiment the artifacts is provided [here](https://github.com/crossminer/FOCUS/blob/master/README.pdf).

Furthermore, FOCUS has been successfully integrated into the Eclipse IDE, allowing one to invoke API recommendations while they are programming. You can find a detailed instruction on how to install the IDE in the following [link](https://mdegroup.github.io/FOCUS-Appendix/install.html).


## Introduction

FOCUS is a context-aware collaborative-filtering recommendation system that exploits cross relationships among OSS projects to suggest the inclusion of additional API invocations and concrete API usage patterns. The current implementation targets Java code specifically.

Implementing a collaborative-filtering recommendation system requires to assess the similarity of two customers, i.e., two software projects. Existing approaches consider that any two projects using an API of interest are equally valuable sources of knowledge. Instead, we postulate that not all projects are equal when it comes to recommending usage patterns: a project that is highly similar to the project currently being developed should provide higher quality patterns than a highly dissimilar one.

Our collaborative-filtering recommendation system attempts to narrow down the search scope by only considering the projects that are the most similar to the active project. Therefore, methods that are typically used conjointly by similar projects in similar contexts tend to be recommended first.

We incorporate these ideas in a new context-aware collaborative filtering recommender system that mines OSS repositories to provide developers with API **F**uncti**O**n **C**alls and **US**age patterns: FOCUS. Our approach employs a new model to represent mutual relationships between projects and collaboratively mines API usage from the most similar projects.

## Examples of FOCUS recommendations

A list of FOCUS recommendation instances is available at [https://mdegroup.github.io/FOCUS-Appendix/tasks.html](https://mdegroup.github.io/FOCUS-Appendix/tasks.html)
## Extension in the TSE submission

We designed and implemented FOCUS as a novel approach to provide developers with API calls and soure code while they are programming. The system works on the basis of a context-aware collaborative filtering technique to extract API usages from OSS projects. In the TSE submission, we demonstrate the suitability of FOCUS for Android programming by evaluating it on a dataset of 2,600 apps. The experimental results demonstrate that our approach outperforms the state-of-the-art approach PAM concerning success rate, accuracy, and execution time. More importantly, we show that FOCUS efficiently recommends source code to a method declaration being developed. We also find out that there is no subtle relationship between the categories for apps defined in Google Play and their API usages.

The metadata parsed for a dataset consisting of 2,600 Android apps is stored in the [following folder](./dataset/TSE). We acknowledge the original data collected from the AndroidTimeMachine [platform](https://androidtimemachine.github.io/).




## Repository Structure

This repository is organized as follows:

* The [tools](./tools) directory contains the implementation of the different tools we developed:
	* [Focus](./tools/Focus): The Java implementation of FOCUS
	* [FocusRascal](./tools/FocusRascal): A set of tools written in [Rascal](https://www.rascal-mpl.org/) that are used to (i) transform raw Java source and binary code into FOCUS-processable and PAM-processable data (ii) retrieve concrete Java usage patterns
	* [PAM](./tools/PAM): A set of Python scripts allowing to compare our approach to PAM
* The [dataset](./dataset) directory contains the datasets described in the paper that we use to evaluate FOCUS:
	* [jars](./dataset/jars): 3,600 JAR files extracted from Maven Central (the raw MV<sub>L</sub> dataset)
	* [MV_L](./dataset/MV_L): meta-data of the MV<sub>L</sub> dataset (extracted from 3,600 JAR files)
	* [MV_S](./dataset/MV_S): meta-data of the MV<sub>S</sub> dataset (extracted from 1,600 JAR files)
	* [SH_L](./dataset/SH_L): meta-data of the SH<sub>L</sub> dataset (extracted from the source code of 610 GitHub projects)
	* [SH_S](./dataset/SH_S): meta-data of the SH<sub>S</sub> dataset (extracted from the source code of 200 GitHub projects)

__Note<sup>1</sup>__: the archive of 5,147 Java projects retrieved from GitHub via the [Software Heritage](https://www.softwareheritage.org/) archive is available at [this url](https://annex.softwareheritage.org/public/dataset/vault-crossminer/856749_done_with_origins.txt.gz).

__Note<sup>2</sup>__: The results presented in the paper can be reproduced following the instructions contained in the [Focus](./tools/Focus) directory.


## How to cite
If you find our work useful for your research, please cite the paper using the following BibTex entry:

```
@inproceedings{Nguyen:2019:FRS:3339505.3339636,
 author = {Nguyen, Phuong T. and Di Rocco, Juri and Di Ruscio, Davide and Ochoa, Lina and Degueule, Thomas and Di Penta, Massimiliano},
 title = {{FOCUS: A Recommender System for Mining API Function Calls and Usage Patterns}},
 booktitle = {Proceedings of the 41st International Conference on Software Engineering},
 series = {ICSE '19},
 year = {2019},
 location = {Montreal, Quebec, Canada},
 pages = {1050--1060},
 numpages = {11},
 url = {https://doi.org/10.1109/ICSE.2019.00109},
 doi = {10.1109/ICSE.2019.00109},
 acmid = {3339636},
 publisher = {IEEE Press},
 address = {Piscataway, NJ, USA},
} 

```

## Troubleshooting

If you encounter any difficulties in working with the tool or the datasets, please do not hesitate to contact us at one of the following emails: phuong.nguyen@univaq.it, juri.dirocco@univaq.it. We will try our best to answer you as soon as possible.
