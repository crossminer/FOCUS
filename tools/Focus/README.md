# FOCUS

This folder contains the implementation of FOCUS. It allows to run the evaluation on all four datasets (SH<sub>L</sub>, SH<sub>S</sub>, MV<sub>L</sub>, MV<sub>S</sub>) using all four possible configurations (C1.1, C1.2, C2.1, C2.2) described in the ICSE'19 paper.

## Requirements

  - Apache Maven >= 3.0
  - Java >= 1.8
  - (optional) Bash for running all evaluations automatically

## Running the tool
To start the evaluation of FOCUS using the default `evaluation.properties` file (see below), run the following command:

```
mvn clean compile exec:java -Dexec.mainClass=org.focus.Runner
```

To use a different `.properties` file, use the `-Dexec.args` argument:

```
mvn clean compile exec:java -Dexec.mainClass=org.focus.Runner -Dexec.args=confs/shs12.properties
```

Results (success rate, precision, recall) for different cut-off values are displayed in the console directly as follows:

```
FOCUS: A Context-Aware Recommender System!
Running ten-fold cross validation on ../../dataset/MV_L/ with configuration C1_1
Fold [9/10]: SimilarityCalculator took 454s
Fold [8/10]: SimilarityCalculator took 455s
Fold [2/10]: SimilarityCalculator took 466s
Fold [7/10]: SimilarityCalculator took 467s
Fold [9/10]: ContextAwareRecommendation took 17s
Fold [8/10]: ContextAwareRecommendation took 17s
Fold [0/10]: SimilarityCalculator took 476s
Fold [1/10]: SimilarityCalculator took 476s
Fold [6/10]: SimilarityCalculator took 477s
Fold [4/10]: SimilarityCalculator took 477s
Fold [7/10]: ContextAwareRecommendation took 13s
Fold [2/10]: ContextAwareRecommendation took 15s
Fold [1/10]: ContextAwareRecommendation took 7s
Fold [0/10]: ContextAwareRecommendation took 9s
Fold [6/10]: ContextAwareRecommendation took 9s
Fold [4/10]: ContextAwareRecommendation took 11s
Fold [3/10]: SimilarityCalculator took 231s
Fold [5/10]: SimilarityCalculator took 234s
Fold [3/10]: ContextAwareRecommendation took 6s
Fold [5/10]: ContextAwareRecommendation took 9s
### 10-FOLDS RESULTS ###
successRate@1 = 73.30556
precision@1   = 0.73305553
recall@1      = 0.06737014
successRate@5 = 82.66667
precision@5   = 0.6486667
recall@5      = 0.2956125
successRate@10 = 86.69446
precision@10   = 0.5511667
recall@10      = 0.49201664
successRate@15 = 88.22222
precision@15   = 0.4440371
recall@15      = 0.57449764
successRate@20 = 89.13888
precision@20   = 0.3645417
recall@20      = 0.6169203
10-fold took 716s
```

 Intermediate results for every fold (recommended invocations, groundtruth invocations, usage patterns, etc.) are stored in the `evaluation` folder of the corresponding dataset (e.g. `../../dataset/SH_S/evaluation/`).

Please bear in mind that the evaluation takes time and resources. The table below gives reference time for 10-fold cross-validation on a Linux 4.20.3 with Intel Core i7-6700HQ CPU @ 2.60GHz and 16GB of RAM.

| Dataset  | Configuration | Time (seconds) |
| :--- | :--- | ---: |
SH<sub>S</sub> | C1.1 | 4 |
SH<sub>S</sub> | C1.2 | 4 |
SH<sub>S</sub> | C2.1 | 4 |
SH<sub>S</sub> | C2.2 | 5 |
SH<sub>L</sub> | C1.1 | 314 |
SH<sub>L</sub> | C1.2 | 312 |
SH<sub>L</sub> | C2.1 | 298 |
SH<sub>L</sub> | C2.2 | 345 |
MV<sub>S</sub> | C1.1 | 135 |
MV<sub>S</sub> | C1.2 | 124 |
MV<sub>S</sub> | C2.1 | 139 |
MV<sub>S</sub> | C2.2 | 135 |
MV<sub>L</sub> | C1.1 | 716 |
MV<sub>L</sub> | C1.2 | 732 |
MV<sub>L</sub> | C2.1 | 741 |
MV<sub>L</sub> | C2.2 | 768 |

## The `evaluation.properties` file
The evaluation of FOCUS is configured with a `.properties` file that specifies (i) the dataset (ii) the configuration and (iii) the validation technique to be used. For instance, the default `evaluation.properties` runs 10-fold cross-validation on SH<sub>S</sub> using the C1.1 configuration:

```
# Dataset directory (SH_L, SH_S, MV_L, MV_S)
sourceDirectory=../../dataset/SH_S/

# Configuration (C1.1, C1.2, C2.1, C2.2)
configuration=C1.1

# Validation type (ten-fold, leave-one-out)
validation=ten-fold
```

This file can be edited to point to a different dataset in the `../../dataset/` directory, to select a different configuration, or to switch between 10-fold and leave-one-out cross-validation.

The [confs/](./confs) directory contains predefined `.properties` file for every dataset/configuration pair, which can be selected using the method described above.

## Running 10-fold cross-validation on all datasets using all configurations

The (Bash) script `run-all.sh` automatically runs the evaluation on every dataset using all possible configurations stored in the [confs/](./confs) folder and stores the results in the [results/](./results) folder:

```
# !/bin/bash

JAR=$PWD/target/focus-0.0.1-SNAPSHOT-jar-with-dependencies.jar

for f in $(ls confs);
	do java -Xmx8g -jar $JAR confs/$f > $PWD/results/$f.txt
done

```

To use this script, FOCUS must first be packed in a single JAR `focus-0.0.1-SNAPSHOT-jar-with-dependencies.jar` using the following command:

```
mvn clean compile assembly:single 
```

## Running the evaluation of PAM

The recommendations computed using the PAM tool are stored in [dataset/PAM/](../../dataset/PAM/). To compute success rate, precision, and recall for the cut-off values `N = {1, 5, 10, 15, 20)`, as presented in the paper, run the following command:

```
mvn compile exec:java -Dexec.mainClass=org.focus.Runner -Dexec.args=PAM
```

Output should be as follows:

```
FOCUS: A Context-Aware Recommender System!
Computing PAM results from ../../dataset/PAM/SH_S-results/ (leave-one-out cross-validation)
successRate@1 = 8.0
precision@1 = 0.08
recall@1 = 0.012586491
successRate@5 = 15.000001
precision@5 = 0.029999994
recall@5 = 0.027643414
successRate@10 = 27.5
precision@10 = 0.032999985
recall@10 = 0.056715574
successRate@15 = 29.499998
precision@15 = 0.023999996
recall@15 = 0.060281053
successRate@20 = 33.5
precision@20 = 0.021999996
recall@20 = 0.07093916
```
