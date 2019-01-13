package org.focus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContextAwareRecommendation {
	private DataReader reader = new DataReader();

	private String srcDir;
	private String groundTruth;
	private String simDir;
	private String recDir;
	private String subFolder;

	private int testingStartPos;
	private int testingEndPos;

	private int numOfNeighbours;
	private int numOfRows;
	private int numOfCols;
	private int numOfSlices;

	private static final Logger log = LogManager.getFormatterLogger(ContextAwareRecommendation.class);

	public ContextAwareRecommendation(String sourceDir, String suFolder, int numOfNeighbours, int teStartPos,
			int teEndPos) {
		this.srcDir = sourceDir;
		this.subFolder = suFolder;
		this.numOfNeighbours = numOfNeighbours;
		this.groundTruth = this.srcDir + subFolder + "/" + "GroundTruth" + "/";
		this.recDir = this.srcDir + subFolder + "/" + "Recommendations" + "/";
		this.simDir = this.srcDir + subFolder + "/" + "Similarities" + "/";
		this.testingStartPos = teStartPos;
		this.testingEndPos = teEndPos;
	}

	/**
	 * Build a 3-D user-item-context ratings matrix
	 */
	public byte[][][] buildUserItemContextMatrix(String testingPro, List<String> listOfProjects,
			List<String> listOfMethodInvocations) {
		Map<Integer, String> simProjects = reader.getMostSimilarProjects(simDir + testingPro, numOfNeighbours);
		Map<String, Map<String, Set<String>>> allProjects = new HashMap<>();

		List<String> listOfPRs = new ArrayList<String>();
		Set<String> allMDs = new HashSet<>();
		Set<String> allMIs = new HashSet<>();

		int sz = simProjects.keySet().size();
		for (int key = 0; key < sz; key++) {
			String project = simProjects.get(key);
			Map<String, Set<String>> tmpMIs = reader.getProjectDetails(srcDir, project);

			// All method declarations of a project
			allMDs.addAll(tmpMIs.keySet());
			for (Set<String> mis : tmpMIs.values())
				allMIs.addAll(mis);

			allProjects.put(project, tmpMIs);
			listOfPRs.add(project);
		}

		// The slice for the testing project is located at the end of the matrix
		listOfPRs.add(testingPro);

		// Read the corresponding ground-truth file
		Set<String> groundTruthMIs = reader.getGroundTruthInvocations(groundTruth, testingPro);
		Map<String, Set<String>> testingMIs = new HashMap<>();

		// Add the testing project, excluding ground-truth method invocations
		Map<String, Set<String>> tmpMIs = reader.getTestingProjectDetails(srcDir, testingPro, groundTruthMIs,
				testingMIs);

		allMDs.addAll(tmpMIs.keySet());
		for (Set<String> s : tmpMIs.values())
			allMIs.addAll(s);

		// FIXME: ???
		String testingMD = testingMIs.keySet().iterator().next();
		Set<String> tmpMISet = testingMIs.get(testingMD);

		// Add testing data to the list of invocations/projects
		tmpMIs.putAll(testingMIs);
		allProjects.put(testingPro, tmpMIs);

		// Convert to an ordered list of all method declarations to make sure
		// that the testing method declaration locates at the end of the list
		List<String> listOfMDs = new ArrayList<>(allMDs);
		if (listOfMDs.contains(testingMD))
			listOfMDs.remove(listOfMDs.indexOf(testingMD));
		listOfMDs.add(testingMD);

		// Convert to an ordered list of all method invocations to make sure
		// that all testing method invocations locate at the end of the list
		List<String> listOfMIs = new ArrayList<>(allMIs);
		for (String testingMI : tmpMISet)
			if (listOfMIs.contains(testingMI))
				listOfMIs.remove(listOfMIs.indexOf(testingMI));
		for (String testingMI : tmpMISet)
			listOfMIs.add(testingMI);

		numOfSlices = listOfPRs.size();
		numOfRows = listOfMDs.size();
		numOfCols = listOfMIs.size();

		byte[][][] matrix = new byte[numOfSlices][numOfRows][numOfCols];

		// Populate all cells in the user-item-context ratings matrix using 1s and 0s
		for (int i = 0; i < numOfSlices - 1; i++) {
			String currentPro = listOfPRs.get(i);
			Map<String, Set<String>> myMDs = allProjects.get(currentPro);

			for (int j = 0; j < numOfRows; j++) {
				String currentMD = listOfMDs.get(j);

				if (myMDs.containsKey(currentMD)) {
					Set<String> myMIs = myMDs.get(currentMD);

					for (int k = 0; k < numOfCols; k++) {
						String currentMI = listOfMIs.get(k);

						if (myMIs.contains(currentMI))
							matrix[i][j][k] = (byte) 1;
						else
							matrix[i][j][k] = (byte) 0;
					}
				} else {
					for (int k = 0; k < numOfCols; k++)
						matrix[i][j][k] = (byte) 0;
				}
			}
		}

		// This is the testing project, ie. the last slice of the 3-D matrix
		Map<String, Set<String>> myMDs = allProjects.get(testingPro);
		for (int j = 0; j < numOfRows - 1; j++) {
			String currentMD = listOfMDs.get(j);

			if (myMDs.containsKey(currentMD)) {
				Set<String> myMIs = myMDs.get(currentMD);

				for (int k = 0; k < numOfCols; k++) {
					String currentMI = listOfMIs.get(k);

					if (myMIs.contains(currentMI))
						matrix[numOfSlices - 1][j][k] = (byte) 1;
					else
						matrix[numOfSlices - 1][j][k] = (byte) 0;
				}
			} else {
				for (int k = 0; k < numOfCols; k++)
					matrix[numOfSlices - 1][j][k] = (byte) 0;
			}
		}

		String currentMD = listOfMDs.get(numOfRows - 1);
		Set<String> myMIs = myMDs.get(currentMD);
		for (int k = 0; k < numOfCols; k++) {
			String currentMI = listOfMIs.get(k);

			if (myMIs.contains(currentMI))
				matrix[numOfSlices - 1][numOfRows - 1][k] = (byte) 1;
			else
				matrix[numOfSlices - 1][numOfRows - 1][k] = (byte) -1;
		}

		for (String l : listOfPRs)
			listOfProjects.add(l);

		for (String l : listOfMIs)
			listOfMethodInvocations.add(l);

		return matrix;
	}

	/**
	 * Recommend new invocations for every testing project using the
	 * collaborative-filtering technique
	 */
	public void recommendation() {
		Map<Integer, String> testingProjects = reader.readProjectList(srcDir + "List.txt", testingStartPos,
				testingEndPos);

		for (String testingPro : testingProjects.values()) {
			Map<String, Float> recommendations = new HashMap<>();
			List<String> listOfPRs = new ArrayList<>();
			List<String> listOfMIs = new ArrayList<>();

			Map<String, Float> simScores = reader.getSimilarityScores(simDir + testingPro, numOfNeighbours);

			byte matrix[][][] = buildUserItemContextMatrix(testingPro, listOfPRs, listOfMIs);

			// The testingMethodVector represents the invocations made
			// from the testing method
			byte[] testingMethodVector = matrix[numOfSlices - 1][numOfRows - 1];

			Map<String, Float> mdSimScores = new HashMap<String, Float>();

			// Compute the jaccard similarity between the testingMethod and every other
			// method
			// and store the results in mdSimScores
			for (int i = 0; i < numOfSlices - 1; i++) {
				for (int j = 0; j < numOfRows; j++) {
					byte[] otherMethodVector = matrix[i][j];

					SimilarityCalculator simCalculator = new SimilarityCalculator(srcDir);
					float sim = simCalculator.computeJaccardSimilarity(testingMethodVector, otherMethodVector);
					String key = Integer.toString(i) + "#" + Integer.toString(j);
					mdSimScores.put(key, sim);
				}
			}

			// Sort the results
			ValueComparator bvc = new ValueComparator(mdSimScores);
			TreeMap<String, Float> simSortedMap = new TreeMap<>(bvc);
			simSortedMap.putAll(mdSimScores);

			// Compute the top-3 most similar methods
			Map<String, Float> top3Sim = new HashMap<>();
			int count = 0;
			for (String key : simSortedMap.keySet()) {
				top3Sim.put(key, mdSimScores.get(key));
				count++;
				if (count > 3)
					break;
			}

			float[] ratings = new float[numOfCols - 1];

			// For every '?' cell (-1.0), compute a rating
			for (int k = 0; k < numOfCols; k++) {
				if (matrix[numOfSlices - 1][numOfRows - 1][k] == -1) {
					double totalSim = 0;

					// Iterate over the top-3 most similar methods
					for (String key : top3Sim.keySet()) {
						String line = key.trim();

						String parts[] = line.split("#");
						int slice = Integer.parseInt(parts[0]);
						int row = Integer.parseInt(parts[1]);

						// Compute the average rating of the method declaration
						double avgMDRating = 0;
						for (int m = 0; m < numOfCols; m++)
							avgMDRating += matrix[slice][row][m];
						avgMDRating /= numOfCols;

						String project = listOfPRs.get(slice);
						double projectSim = simScores.get(project);
						double val = projectSim * matrix[slice][row][k];
						double methodSim = top3Sim.get(key);
						totalSim += methodSim;
						ratings[k] += (val - avgMDRating) * methodSim;
					}

					if (totalSim != 0)
						ratings[k] /= totalSim;

					double activeMDrating = 0.8;
					ratings[k] += activeMDrating;
					String methodInvocation = listOfMIs.get(k);
					recommendations.put(methodInvocation, ratings[k]);
				}
			}

			ValueComparator bvc2 = new ValueComparator(recommendations);
			TreeMap<String, Float> recSortedMap = new TreeMap<>(bvc2);
			recSortedMap.putAll(recommendations);

			reader.writeRecommendations(recDir + testingPro, recSortedMap, recommendations);
		}
	}
}
