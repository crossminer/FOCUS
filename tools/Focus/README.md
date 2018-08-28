# FOCUS Tool

This folder contains the source code implementation of FOCUS. To execute the runner on the four presented datesets (i.e. SH<sub>L</sub>, SH<sub>S</sub>, MV<sub>L</sub>, MV<sub>S</sub>):

1. Go to `FOCUS/tools/Focus/evaluation.properties` and point the `sourceDirectory` property to the local path where one of the four datasets is located. For example, `sourceDirectory=<local-path>/FOCUS/dataset/SH_L/`. 

2. Run the following maven command:
```
mvn exec:java -Dexec.mainClass="it.focus.Runner" 
```

3. Results will be stored in the `evaluation` folder of the selected dataset directory. For instance, `<local-path>/FOCUS/dataset/SH_L/evaluation/`. 
