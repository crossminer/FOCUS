# !/bin/bash

JAR=$PWD/target/focus-0.0.1-SNAPSHOT-jar-with-dependencies.jar

for f in $(ls confs); do
	CONF=$PWD/confs/$f
	OUT=$PWD/results/$f.txt

	echo "Running FOCUS on configuration $CONF. Outputs are written to $OUT."
	java -Xmx8g -jar $JAR $CONF > $OUT
	echo "Done"
done

