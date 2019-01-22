package org.focus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Runner {
	private String srcDir;
	private int numOfProjects = 0;

	private static final Logger log = LogManager.getFormatterLogger(Runner.class);

	public void loadConfigurations() {
		try (InputStream in = new FileInputStream("evaluation.properties")) {
			Properties prop = new Properties();
			prop.load(in);
			srcDir = prop.getProperty("sourceDirectory");

			String projectList = srcDir + "/List.txt";
			BufferedReader reader = new BufferedReader(new FileReader(projectList));
			while (reader.readLine() != null)
				numOfProjects++;
			reader.close();
		} catch (IOException e) {
			log.error("Couldn't read evaluation.properties", e);
		}
	}

	public void run() {
		log.info("FOCUS: A Context-Aware Recommender System!");
		loadConfigurations();

		log.info("Running ten-fold cross validation on " + srcDir);
		tenFoldCrossValidation();

		// log.info("Running leave-one-out cross validation on " + srcDir);
		// leaveOneOutValidation();
	}

	/**
	 * Ten-fold cross validation
	 */
	public void tenFoldCrossValidation() {
		// FIXME: Values should be extracted from the dataset
		int numOfNeighbours = 2;
		int numOfFolds = 10;
		int step = (int) numOfProjects / 10;
		List<Integer> ns = Arrays.asList(1, 5, 10, 15, 20);
		Map<Integer, Float> avgSuccess = new HashMap<>();
		Map<Integer, Float> avgPrecision = new HashMap<>();
		Map<Integer, Float> avgRecall = new HashMap<>();

		for (int i = 0; i < numOfFolds; i++) {
			int trainingStartPos1 = 1;
			int trainingEndPos1 = i * step;
			int trainingStartPos2 = (i + 1) * step + 1;
			int trainingEndPos2 = numOfProjects;
			int testingStartPos = 1 + i * step;
			int testingEndPos = (i + 1) * step;

			int k = i + 1;
			String subFolder = "evaluation/round" + Integer.toString(k);

			SimilarityCalculator calculator = new SimilarityCalculator(srcDir, subFolder, trainingStartPos1,
					trainingEndPos1, trainingStartPos2, trainingEndPos2, testingStartPos, testingEndPos);
			calculator.computeProjectSimilarity();

			ContextAwareRecommendation engine = new ContextAwareRecommendation(srcDir, subFolder, numOfNeighbours,
					testingStartPos, testingEndPos);
			engine.recommendation();

//			APIUsagePatternMatcher matcher = new APIUsagePatternMatcher(srcDir, subFolder, trainingStartPos1,
//					trainingEndPos1, trainingStartPos2, trainingEndPos2, testingStartPos, testingEndPos);
//			matcher.searchAPIUsagePatterns();

			SuccessCalculator calc = new SuccessCalculator(srcDir, subFolder, testingStartPos, testingEndPos);
			for (Integer n : ns) {
				float success = calc.computeSuccessRate(n);
				float precision = calc.computePrecision(n);
				float recall = calc.computeRecall(n);

				avgSuccess.put(n, avgSuccess.getOrDefault(n, 0f) + success);
				avgPrecision.put(n, avgPrecision.getOrDefault(n, 0f) + precision);
				avgRecall.put(n, avgRecall.getOrDefault(n, 0f) + recall);

				// log.info("successRate@" + n + " = " + success);
				// log.info("precision@" + n + " = " + precision);
				// log.info("recall@" + n + " = " + recall);
			}
		}

		log.info("### RESULTS ###");
		for (Integer n : ns) {
			log.info("successRate@" + n + " = " + avgSuccess.get(n) / numOfFolds);
			log.info("precision@" + n + "   = " + avgPrecision.get(n) / numOfFolds);
			log.info("recall@" + n + "      = " + avgRecall.get(n) / numOfFolds);
		}
	}

	public void leaveOneOutValidation() {
		int numOfNeighbours = 2;
		int numOfFolds = numOfProjects;
		int step = 1;
		List<Integer> ns = Arrays.asList(1, 5, 10, 15, 20);
		Map<Integer, Float> avgSuccess = new HashMap<>();
		Map<Integer, Float> avgPrecision = new HashMap<>();
		Map<Integer, Float> avgRecall = new HashMap<>();

		for (int i = 0; i < numOfFolds; i++) {
			int trainingStartPos1 = 1;
			int trainingEndPos1 = i * step;
			int trainingStartPos2 = (i + 1) * step + 1;
			int trainingEndPos2 = numOfProjects;
			int testingStartPos = 1 + i * step;
			int testingEndPos = (i + 1) * step;

			String subFolder = "evaluation/round1";

			SimilarityCalculator calculator = new SimilarityCalculator(srcDir, subFolder, trainingStartPos1,
					trainingEndPos1, trainingStartPos2, trainingEndPos2, testingStartPos, testingEndPos);
			calculator.computeProjectSimilarity();

			ContextAwareRecommendation engine = new ContextAwareRecommendation(srcDir, subFolder, numOfNeighbours,
					testingStartPos, testingEndPos);
			engine.recommendation();

			SuccessCalculator calc = new SuccessCalculator(srcDir, subFolder, testingStartPos, testingEndPos);
			for (Integer n : ns) {
				float success = calc.computeSuccessRate(n);
				float precision = calc.computePrecision(n);
				float recall = calc.computeRecall(n);

				avgSuccess.put(n, avgSuccess.getOrDefault(n, 0f) + success);
				avgPrecision.put(n, avgPrecision.getOrDefault(n, 0f) + precision);
				avgRecall.put(n, avgRecall.getOrDefault(n, 0f) + recall);

				// log.info("successRate@" + n + " = " + success);
				// log.info("precision@" + n + " = " + precision);
				// log.info("recall@" + n + " = " + recall);
			}
		}

		log.info("### RESULTS ###");
		for (Integer n : ns) {
			log.info("successRate@" + n + " = " + avgSuccess.get(n) / numOfFolds);
			log.info("precision@" + n + "   = " + avgPrecision.get(n) / numOfFolds);
			log.info("recall@" + n + "      = " + avgRecall.get(n) / numOfFolds);
		}
	}

	public static void main(String[] args) {
		new Runner().run();
	}
}
