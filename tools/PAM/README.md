# APIMaterials

Parsing PAM: Python code

The Python scripts that support ten-fold validation. In particular, parsingPAM.py provides the following functions:

* splitFiles: it takes as argument the folder in which there are all the files that represent the project, keeps2/3 of each and discards 1/3;
* combineMethodsInv: creates an ARFF file with the project combined with the 3 invocations extracted.
* combineAll: merges one project with the other 304 to create a single ARFF file to be analyzed by PAM.
* fromFocusToPam: converts the format used for FOCUS to the format used by PAM. It takes as argument the folder containing the projects in the FOCUS format and the destination folder.
* getGroundTruth: takes out the first 3 invocations and discards the others which are then stored in the GroundTuth folder.

ConvertArffFile.py converts metadata FOCUS2PAM. 

The complete evaluation process involves the following sequence of functions to call:
1. fromFocusToPam
2. convertArffFile
3. splitFiles
4. getGroundTruth
5. combineMethodsInv
6. combineAll
