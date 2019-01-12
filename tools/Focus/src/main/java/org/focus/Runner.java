package org.focus;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ValueComparator implements Comparator<String> {
	Map<String, Float> base;

	public ValueComparator(Map<String, Float> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with equals.
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		}
		// returning 0 would merge keys
	}
}

public class Runner {
	private String srcDir;

	private static final Logger log = LogManager.getFormatterLogger(Runner.class);

	public void loadConfigurations() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("evaluation.properties"));
			this.srcDir = prop.getProperty("sourceDirectory");
		} catch (IOException e) {
			log.error("Couldn't read evaluation.properties", e);
		}
	}

	public void run() {
		log.info("FOCUS: A Context-Aware Recommender System!");
		loadConfigurations();
		tenFoldCrossValidation();
		leaveOneOutValidation();
	}

	/**
	 * Ten-fold cross validation
	 */
	public void tenFoldCrossValidation() {
		int numOfProjects = 610;
		int numOfNeighbours = 2;
		int numOfFolds = 10;
		int step = (int) numOfProjects / 10;

		for (int i = 0; i < numOfFolds; i++) {
			int trainingStartPos1 = 1;
			int trainingEndPos1 = i * step;
			int trainingStartPos2 = (i + 1) * step + 1;
			int trainingEndPos2 = numOfProjects;
			int testingStartPos = 1 + i * step;
			int testingEndPos = (i + 1) * step;

			int k = i + 1;
			String subFolder = "evaluation/round" + Integer.toString(k);

			SimilarityCalculator calculator = new SimilarityCalculator(this.srcDir, subFolder, trainingStartPos1,
					trainingEndPos1, trainingStartPos2, trainingEndPos2, testingStartPos, testingEndPos);
			calculator.computeProjectSimilarity();

			ContextAwareRecommendation engine = new ContextAwareRecommendation(this.srcDir, subFolder, numOfNeighbours,
					testingStartPos, testingEndPos);
			engine.recommendation();

			APIUsagePatternMatcher matcher = new APIUsagePatternMatcher(this.srcDir, subFolder, trainingStartPos1,
					trainingEndPos1, trainingStartPos2, trainingEndPos2, testingStartPos, testingEndPos);
			matcher.searchAPIUsagePatterns();

			APIUsagePatternEvaluation eval = new APIUsagePatternEvaluation(this.srcDir, subFolder, testingStartPos,
					testingEndPos);
			eval.computeSimilarityScore();
		}
	}

	public void leaveOneOutValidation() {
		int numOfProjects = 610;
		int numOfNeighbours = 2;
		int numOfFolds = numOfProjects;
		int step = 1;

		for (int i = 0; i < numOfFolds; i++) {
			int trainingStartPos1 = 1;
			int trainingEndPos1 = i * step;
			int trainingStartPos2 = (i + 1) * step + 1;
			int trainingEndPos2 = numOfProjects;
			int testingStartPos = 1 + i * step;
			int testingEndPos = (i + 1) * step;

			String subFolder = "evaluation/round1";

			SimilarityCalculator calculator = new SimilarityCalculator(this.srcDir, subFolder, trainingStartPos1,
					trainingEndPos1, trainingStartPos2, trainingEndPos2, testingStartPos, testingEndPos);
			calculator.computeProjectSimilarity();

			ContextAwareRecommendation engine = new ContextAwareRecommendation(this.srcDir, subFolder, numOfNeighbours,
					testingStartPos, testingEndPos);
			engine.recommendation();
		}
	}

	public static void main(String[] args) {
		Runner runner = new Runner();
		runner.run();
		return;
	}
}
