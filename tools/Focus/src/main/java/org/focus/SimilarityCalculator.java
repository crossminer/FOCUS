package org.focus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;

/**
 * Compute similarity between every testing project and all training projects
 * using cosine similarity with weight
 */
public class SimilarityCalculator {
	private DataReader reader = new DataReader();

	private String srcDir;
	private String simDir;
	private String subFolder;
	private Configuration configuration;

	private int trainingStartPos1;
	private int trainingEndPos1;
	private int trainingStartPos2;
	private int trainingEndPos2;
	private int testingStartPos;
	private int testingEndPos;

	private static final Logger log = LogManager.getFormatterLogger(SimilarityCalculator.class);

	public SimilarityCalculator(String srcDir) {
		this.srcDir = srcDir;
	}

	public SimilarityCalculator(String srcDir, String subFolder, Configuration conf, int trainingStartPos1,
			int trainingEndPos1, int trainingStartPos2, int trainingEndPos2, int testingStartPos, int testingEndPos) {
		this.srcDir = srcDir;
		this.subFolder = subFolder;
		this.configuration = conf;
		this.simDir = this.srcDir + this.subFolder + "/" + "Similarities" + "/";

		this.trainingStartPos1 = trainingStartPos1;
		this.trainingEndPos1 = trainingEndPos1;
		this.trainingStartPos2 = trainingStartPos2;
		this.trainingEndPos2 = trainingEndPos2;
		this.testingStartPos = testingStartPos;
		this.testingEndPos = testingEndPos;
	}

	/**
	 * Compute the similarity between the project testingPro and all the projects in
	 * the supplied list and serialize the results.
	 */
	public void computeSimilarity(String testingPro, Map<String, Map<String, Integer>> projects) {
		Map<String, Integer> termFrequency = computeTermFrequency(projects);
		Map<String, Float> testingProjectVector = new HashMap<>();
		Map<String, Float> projectSimilarities = new HashMap<>();

//		log.info("Computing similarity between %s and all other projects", testingPro);

		// Computes the feature vector of the testing project,
		// ie. the TF-IDF for all its invocations
		Map<String, Integer> terms = projects.get(testingPro);
		for (String term : terms.keySet()) {
			float tfIdf = computeTF_IDF(terms.get(term), projects.size(), termFrequency.get(term));
			testingProjectVector.put(term, tfIdf);
		}

		// Compute the feature vector of all training projects in the corpus and
		// store their similarity with the testing project in the similarity vector
		for (String trainingProject : projects.keySet()) {
			if (!trainingProject.equals(testingPro)) {
				Map<String, Float> trainingProjectVector = new HashMap<>();
				terms = projects.get(trainingProject);

				for (String term : terms.keySet()) {
					float tfIdf = computeTF_IDF(terms.get(term), projects.size(), termFrequency.get(term));
					trainingProjectVector.put(term, tfIdf);
				}

				float similarity = computeCosineSimilarity(testingProjectVector, trainingProjectVector);
				projectSimilarities.put(trainingProject, similarity);
			}
		}

		// Order projects by similarity in a sortedMap
		ValueComparator bvc = new ValueComparator(projectSimilarities);
		TreeMap<String, Float> sortedMap = new TreeMap<>(bvc);
		sortedMap.putAll(projectSimilarities);

		// Store similarities in the evaluation directory
		reader.writeSimilarityScores(simDir, testingPro, sortedMap);
	}

	/**
	 * Compute the similarity between two vectors using Jaccard Similarity
	 */
	public float computeJaccardSimilarity(byte[] vector1, byte[] vector2) {
		int count = 0;
		int length = vector1.length;

		for (int i = 0; i < length; i++)
			if (vector1[i] == 1.0 && vector2[i] == 1.0)
				count++;

		return (float) count / (2 * length - count);
	}

	/**
	 * Compute the similarity between every testing project and all training
	 * projects
	 */
	public void computeProjectSimilarity() {
		Map<String, Map<String, Integer>> trainingProjects = new HashMap<>();
		Map<Integer, String> trainingProjectsID = new HashMap<>();

		// Read all training project IDs in trainingProjectsID
		if (trainingStartPos1 < trainingEndPos1)
			trainingProjectsID.putAll(reader.readProjectList(srcDir + "List.txt", trainingStartPos1, trainingEndPos1));

		if (trainingStartPos2 < trainingEndPos2)
			trainingProjectsID.putAll(reader.readProjectList(srcDir + "List.txt", trainingStartPos2, trainingEndPos2));

		// Read all training projects in trainingProjects
		for (String trainingID : trainingProjectsID.values())
			trainingProjects.putAll(reader.getProjectInvocations(srcDir, trainingID));

		// Read all testing project IDs in testingProjectsID
		Map<Integer, String> testingProjectsID = reader.readProjectList(srcDir + "List.txt", testingStartPos,
				testingEndPos);

		int numOfTestingInvocations = 0;
		boolean removeHalf = false;

		switch (configuration) {
			case C1_1:
				numOfTestingInvocations = 1;
				removeHalf = true;
			break;
			case C1_2:
				numOfTestingInvocations = 4;
				removeHalf = true;
			break;
			case C2_1:
				numOfTestingInvocations = 1;
				removeHalf = false;
			break;
			case C2_2:
				numOfTestingInvocations = 4;
				removeHalf = false;
			break;
		}

		for (Integer testingID : testingProjectsID.keySet()) {
			String testingProjectID = testingProjectsID.get(testingID);

			// Get half of all declarations and used for similarity computation
			Map<String, Map<String, Integer>> testingProject = reader.getTestingProjectInvocations(srcDir, subFolder,
					testingProjectID, numOfTestingInvocations, removeHalf);

			trainingProjects.putAll(testingProject);
			computeSimilarity(testingProjectID, trainingProjects);
			trainingProjects.remove(testingProjectID);
		}
	}

	/**
	 * Compute the cosine similarity between two project vectors
	 */
	private float computeCosineSimilarity(Map<String, Float> v1, Map<String, Float> v2) {
		Set<String> both = Sets.intersection(v1.keySet(), v2.keySet());
		double scalar = 0, norm1 = 0, norm2 = 0;

		// Only perform cosine similarity on words that exist in both lists
		if (both.size() > 0) {
			for (Float f : v1.values())
				norm1 += f * f;

			for (Float f : v2.values())
				norm2 += f * f;

			for (String k : both)
				scalar += v1.get(k) * v2.get(k);

			if (scalar == 0)
				return 0f;
			else
				return (float) (scalar / Math.sqrt(norm1 * norm2));
		} else {
			return 0f;
		}
	}

	/**
	 * Compute a term-frequency map which stores, for every invocation, how many
	 * projects in the supplied list invoke it
	 * 
	 * java/util/ArrayList/ArrayList()=131 java/util/List/add(E)=129
	 * java/io/PrintStream/println(java.lang.String)=128
	 */
	private Map<String, Integer> computeTermFrequency(Map<String, Map<String, Integer>> projects) {
		Map<String, Integer> termFrequency = new HashMap<>();

		for (Map<String, Integer> terms : projects.values()) {
			for (String term : terms.keySet()) {
				termFrequency.put(term, termFrequency.getOrDefault(term, 0) + 1);
			}
		}

		return termFrequency;
	}

	/**
	 * Standard term-frequency inverse document frequency calculation
	 */
	private float computeTF_IDF(int count, int total, int freq) {
		return (float) (count * Math.log(total / freq));
	}
}
