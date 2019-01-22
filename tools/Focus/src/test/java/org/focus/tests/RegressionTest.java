package org.focus.tests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.focus.Configuration;
import org.focus.ContextAwareRecommendation;
import org.focus.SimilarityCalculator;
import org.focus.SuccessCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Just a simple regression test to make sure success rates are stable on SH_S
 */
class RegressionTest {
	@Test
	void testSHS11() {
		Configuration conf = Configuration.C1_1;
		String srcDir = "../../dataset/SH_S/";
		int numOfProjects = 200;
		int numOfNeighbours = 2;
		int numOfFolds = 10;
		int step = (int) numOfProjects / numOfFolds;
		List<Integer> ns = Arrays.asList(1, 5, 10, 15, 20);
		Map<Integer, Float> avgSuccess = new HashMap<>();

		for (int i = 0; i < numOfFolds; i++) {
			int trainingStartPos1 = 1;
			int trainingEndPos1 = i * step;
			int trainingStartPos2 = (i + 1) * step + 1;
			int trainingEndPos2 = numOfProjects;
			int testingStartPos = 1 + i * step;
			int testingEndPos = (i + 1) * step;

			String subFolder = "evaluation/round" + (i + 1);

			SimilarityCalculator calculator = new SimilarityCalculator(srcDir, subFolder, conf, trainingStartPos1,
					trainingEndPos1, trainingStartPos2, trainingEndPos2, testingStartPos, testingEndPos);
			calculator.computeProjectSimilarity();

			ContextAwareRecommendation engine = new ContextAwareRecommendation(srcDir, subFolder, numOfNeighbours,
					testingStartPos, testingEndPos);
			engine.recommendation();

			SuccessCalculator calc = new SuccessCalculator(srcDir, subFolder, testingStartPos, testingEndPos);
			for (Integer n : ns) {
				float success = calc.computeSuccessRate(n);

				avgSuccess.put(n, avgSuccess.getOrDefault(n, 0f) + success);
			}
		}

		assertEquals(20.5, avgSuccess.get(1) / numOfFolds);
		assertEquals(35.0, avgSuccess.get(5) / numOfFolds);
		assertEquals(39.0, avgSuccess.get(10) / numOfFolds);
		assertEquals(40.0, avgSuccess.get(15) / numOfFolds);
		assertEquals(43.0, avgSuccess.get(20) / numOfFolds);
	}
	
	@Test
	void testSHS12() {
		Configuration conf = Configuration.C1_2;
		String srcDir = "../../dataset/SH_S/";
		int numOfProjects = 200;
		int numOfNeighbours = 2;
		int numOfFolds = 10;
		int step = (int) numOfProjects / numOfFolds;
		List<Integer> ns = Arrays.asList(1, 5, 10, 15, 20);
		Map<Integer, Float> avgSuccess = new HashMap<>();

		for (int i = 0; i < numOfFolds; i++) {
			int trainingStartPos1 = 1;
			int trainingEndPos1 = i * step;
			int trainingStartPos2 = (i + 1) * step + 1;
			int trainingEndPos2 = numOfProjects;
			int testingStartPos = 1 + i * step;
			int testingEndPos = (i + 1) * step;

			String subFolder = "evaluation/round" + (i + 1);

			SimilarityCalculator calculator = new SimilarityCalculator(srcDir, subFolder, conf, trainingStartPos1,
					trainingEndPos1, trainingStartPos2, trainingEndPos2, testingStartPos, testingEndPos);
			calculator.computeProjectSimilarity();

			ContextAwareRecommendation engine = new ContextAwareRecommendation(srcDir, subFolder, numOfNeighbours,
					testingStartPos, testingEndPos);
			engine.recommendation();

			SuccessCalculator calc = new SuccessCalculator(srcDir, subFolder, testingStartPos, testingEndPos);
			for (Integer n : ns) {
				float success = calc.computeSuccessRate(n);

				avgSuccess.put(n, avgSuccess.getOrDefault(n, 0f) + success);
			}
		}

		assertEquals(20.5, avgSuccess.get(1) / numOfFolds);
		assertEquals(35.0, avgSuccess.get(5) / numOfFolds);
		assertEquals(39.0, avgSuccess.get(10) / numOfFolds);
		assertEquals(40.0, avgSuccess.get(15) / numOfFolds);
		assertEquals(43.0, avgSuccess.get(20) / numOfFolds);
	}
	
	@Test
	void testSHS2_1() {
		Configuration conf = Configuration.C2_1;
		String srcDir = "../../dataset/SH_S/";
		int numOfProjects = 200;
		int numOfNeighbours = 2;
		int numOfFolds = 10;
		int step = (int) numOfProjects / numOfFolds;
		List<Integer> ns = Arrays.asList(1, 5, 10, 15, 20);
		Map<Integer, Float> avgSuccess = new HashMap<>();

		for (int i = 0; i < numOfFolds; i++) {
			int trainingStartPos1 = 1;
			int trainingEndPos1 = i * step;
			int trainingStartPos2 = (i + 1) * step + 1;
			int trainingEndPos2 = numOfProjects;
			int testingStartPos = 1 + i * step;
			int testingEndPos = (i + 1) * step;

			String subFolder = "evaluation/round" + (i + 1);

			SimilarityCalculator calculator = new SimilarityCalculator(srcDir, subFolder, conf, trainingStartPos1,
					trainingEndPos1, trainingStartPos2, trainingEndPos2, testingStartPos, testingEndPos);
			calculator.computeProjectSimilarity();

			ContextAwareRecommendation engine = new ContextAwareRecommendation(srcDir, subFolder, numOfNeighbours,
					testingStartPos, testingEndPos);
			engine.recommendation();

			SuccessCalculator calc = new SuccessCalculator(srcDir, subFolder, testingStartPos, testingEndPos);
			for (Integer n : ns) {
				float success = calc.computeSuccessRate(n);

				avgSuccess.put(n, avgSuccess.getOrDefault(n, 0f) + success);
			}
		}

		assertEquals(20.5, avgSuccess.get(1) / numOfFolds);
		assertEquals(35.0, avgSuccess.get(5) / numOfFolds);
		assertEquals(39.0, avgSuccess.get(10) / numOfFolds);
		assertEquals(40.0, avgSuccess.get(15) / numOfFolds);
		assertEquals(43.0, avgSuccess.get(20) / numOfFolds);
	}
	
	@Test
	void testSHS22() {
		Configuration conf = Configuration.C2_2;
		String srcDir = "../../dataset/SH_S/";
		int numOfProjects = 200;
		int numOfNeighbours = 2;
		int numOfFolds = 10;
		int step = (int) numOfProjects / numOfFolds;
		List<Integer> ns = Arrays.asList(1, 5, 10, 15, 20);
		Map<Integer, Float> avgSuccess = new HashMap<>();

		for (int i = 0; i < numOfFolds; i++) {
			int trainingStartPos1 = 1;
			int trainingEndPos1 = i * step;
			int trainingStartPos2 = (i + 1) * step + 1;
			int trainingEndPos2 = numOfProjects;
			int testingStartPos = 1 + i * step;
			int testingEndPos = (i + 1) * step;

			String subFolder = "evaluation/round" + (i + 1);

			SimilarityCalculator calculator = new SimilarityCalculator(srcDir, subFolder, conf, trainingStartPos1,
					trainingEndPos1, trainingStartPos2, trainingEndPos2, testingStartPos, testingEndPos);
			calculator.computeProjectSimilarity();

			ContextAwareRecommendation engine = new ContextAwareRecommendation(srcDir, subFolder, numOfNeighbours,
					testingStartPos, testingEndPos);
			engine.recommendation();

			SuccessCalculator calc = new SuccessCalculator(srcDir, subFolder, testingStartPos, testingEndPos);
			for (Integer n : ns) {
				float success = calc.computeSuccessRate(n);

				avgSuccess.put(n, avgSuccess.getOrDefault(n, 0f) + success);
			}
		}

		assertEquals(20.5, avgSuccess.get(1) / numOfFolds);
		assertEquals(35.0, avgSuccess.get(5) / numOfFolds);
		assertEquals(39.0, avgSuccess.get(10) / numOfFolds);
		assertEquals(40.0, avgSuccess.get(15) / numOfFolds);
		assertEquals(43.0, avgSuccess.get(20) / numOfFolds);
	}
}
