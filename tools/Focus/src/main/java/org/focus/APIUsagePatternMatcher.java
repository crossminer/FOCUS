package org.focus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;

public class APIUsagePatternMatcher {

	private String srcDir;
	private String recDir;
	private String patternDir;
	private String subFolder;
	private String tiDir;

	private int trainingStartPos1;
	private int trainingEndPos1;
	private int trainingStartPos2;
	private int trainingEndPos2;
	private int testingStartPos;
	private int testingEndPos;

	private static final Logger log = LogManager.getFormatterLogger(APIUsagePatternMatcher.class);

	public APIUsagePatternMatcher(String sourceDir, String subFolder, int trStartPos1, int trEndPos1, int trStartPos2,
			int trEndPos2, int teStartPos, int teEndPos) {
		this.srcDir = sourceDir;
		this.subFolder = subFolder;
		this.recDir = this.srcDir + this.subFolder + "/" + "Recommendations" + "/";
		this.patternDir = this.srcDir + this.subFolder + "/" + "APIUsagePatterns" + "/";
		this.tiDir = this.srcDir + this.subFolder + "/" + "TestingInvocations" + "/";

		this.trainingStartPos1 = trStartPos1;
		this.trainingEndPos1 = trEndPos1;
		this.trainingStartPos2 = trStartPos2;
		this.trainingEndPos2 = trEndPos2;
		this.testingStartPos = teStartPos;
		this.testingEndPos = teEndPos;
	}

	public void searchAPIUsagePatterns() {
		DataReader reader = new DataReader();
		Map<Integer, String> trainingProjects = new HashMap<>();
		Map<Integer, String> testingProjects = reader.readProjectList(srcDir + "List.txt",
				testingStartPos, testingEndPos);

		// Read training projects information
		if (trainingStartPos1 < trainingEndPos1)
			trainingProjects.putAll(reader.readProjectList(srcDir + "List.txt",
					trainingStartPos1, trainingEndPos1));

		if (trainingStartPos2 < trainingEndPos2)
			trainingProjects.putAll(reader.readProjectList(srcDir + "List.txt",
					trainingStartPos2, trainingEndPos2));

		// Collect in allProjects the method invocations for every training project
		Map<String, Map<String, Set<String>>> allProjects = new HashMap<>();
		for (String trainingProject : trainingProjects.values()) {
			Map<String, Set<String>> projectMIs = reader.getProjectDetails(srcDir, trainingProject);
			allProjects.put(trainingProject, projectMIs);
		}

		// For every testingPro, collect the Jaccard distance
		// between the recommendations and the actual invocations
		for (String testingPro : testingProjects.values()) {
			Map<String, Float> results = new HashMap<>();
			Map<Integer, String> recommendations = reader.readRecommendationFile(recDir + testingPro);
			Map<Integer, String> testingInvocations = reader.getTestingInvocations(tiDir + testingPro);

			int index = recommendations.size() + 1;

			log.info("Searching API usage pattern for " + testingPro);

			// add also the testing invocation(s)
			for (String invocation : testingInvocations.values()) {
				recommendations.put(index, invocation);
				index++;
			}

			for (String project : allProjects.keySet()) {
				Map<String, Set<String>> methodInvocations = allProjects.get(project);

				for (Map.Entry<String, Set<String>> entry : methodInvocations.entrySet()) {
					String declaration = entry.getKey();
					Set<String> invocations = entry.getValue();
					Set<String> allMIs = new HashSet<>();

					int size = invocations.size();
					int s = recommendations.size();

					if (s < size)
						size = s;

					for (int i = 1; i <= size; i++)
						allMIs.add(recommendations.get(i));

					int size1 = Sets.intersection(invocations, allMIs).size();
					int size2 = Sets.union(invocations, allMIs).size();
					float jaccard = (float) size1 / size2;
					results.put(project + "#" + declaration, jaccard);
				}
			}

			ValueComparator bvc = new ValueComparator(results);
			TreeMap<String, Float> sortedMap = new TreeMap<>(bvc);
			sortedMap.putAll(results);

			int numOfRecs = 20;
			reader.writeUsagePatterns(patternDir + testingPro, sortedMap, numOfRecs);
		}
	}
}
