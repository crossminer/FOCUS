# !/bin/bash

JAR=$PWD/target/focus-0.0.1-SNAPSHOT-jar-with-dependencies.jar

for f in $(ls confs);
	do java -Xmx8g -jar $JAR confs/$f > $PWD/results/$f.txt
done

