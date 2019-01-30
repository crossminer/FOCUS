# PAM Support Tools

Parsing PAM: Python code

The Python scripts that support ten-fold validation. We already parsed the same data used
by FOCUS to provide as input for PAM. All you have to do to get the recommendation outcomes by PAM is to run the following commands:
```
$ cd <FOCUS_root>/tools/Pam
$ mvn clean compile exec:java -Dexec.mainClass=org.focus.Runner -Dexec.args=confs/shs12.properties
```
The results are stored using a similar directory structure used by FOCUS
as given below:
* ```GroundTruth```: the folder contains the ground-truth invocations.
* ```Recommendations```: the list of recommended invocations for each project
is stored in this folder.
* ```TestingInvocations```: the folder stores the invocations used as query.

***NOTE:*** The following instructions are dedicated for those who want to run
PAM from the beginning. Thus, the execution presented in the rest of this
section is purely optional.

For your convenience, we provide a Python script named ```parsingPAM```.
py to convert the input data used by FOCUS to be fed to PAM. Run the
following commands (The execution of the commands necessitates python3 which is available at https://www.python.org/download/releases/3.0/): 
```
$ cd <FOCUS_root>/tools/PAM
$ python3 parsingPAM.py <FOCUS_root>/dataset/SH_S/ <FOCUS_root>/dataset/PAM/SH_S/
```
Then execute PAM on the given input data as follows:
```
$ git clone git@github.com:mast-group/api-mining.git <PAM_root>
$ cd <PAM_root>
$ mvn package
$ for f in <FOCUS_root>/dataset/PAM/SH_S/ *; do java -jar api-mining/target/api-mining-1.0.jar -f $f; done
```
where <PAM_root> is the directory where you have cloned the repository.
In our case, we set it to ```/home/admin/Desktop/PAM/```.

These python scripts are developed by [@claudioDsi](https://github.com/claudioDsi) during his thesis work.
