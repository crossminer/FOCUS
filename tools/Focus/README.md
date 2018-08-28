This folder contains the source code implementation of FOCUS. To execute the runner on supporting datesets:

1. set ```sourceDirectory``` properties to ```FOCUS/tools/Focus/evaluation.properties``` as selected dataset (i.e. SHL, SHS, MVL, MVS)
2. run the following maven command:

```
mvn exec:java -Dexec.mainClass="it.focus.Runner" 
```
