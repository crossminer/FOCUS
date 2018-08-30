# APIMaterials

Parsing PAM: Python code

Python scripts support ten folder validation. parsingPAM.py provides the following functions:

* splitFiles: it takes as argument the folder in which there are all the files that represent the project and takes 2/3 of each and discard 1/3;
* combineMethodsInv: It creates the arff file with the project combined with the 3 invocations extracted. It takes the folder of the invocations, the folder of the projects and the folder of destination in which there are stored all the arff file ;
* combineAll: It merges one project with the other 304 in order to create the arff file to be analyzed by PAM. It takes the folder that contains the projects, the folder that contains all the dataset (the 305 projects) and the destination folder;
* fromFocusToPam: It converts the format used for Focus to the format used by PAM. It takes as argument the folder where are stored the projects in the Focus format and the destination folder.
* getGroundTruth: It takes the first 3 invocations and discard the others, that are inserted in the ground truth folder. It takes the path in which there are the file to split and the destination folders, one for the testing invocations and for the ground truth.


ConvertArffFile.py converts metadatas Focus2PAM. 

The complete evaluation process involves the following sequence of functions to call:
1. fromFocusToPam
2. convertArffFile
3. splitFiles
4. getGroundTruth
5. combineMethodsInv
6. combineAll
