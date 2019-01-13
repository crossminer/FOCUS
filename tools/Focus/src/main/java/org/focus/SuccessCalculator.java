package org.focus;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SuccessCalculator {
	private DataReader reader = new DataReader();
	private String srcDir;
	private String subFolder;
	private String recDir;
	private String gtDir;
	private int testingStartPos;
	private int testingEndPos;

	public SuccessCalculator(String srcDir, String subFolder, int testingStartPos, int testingEndPos) {
		this.srcDir = srcDir;
		this.subFolder = subFolder;
		this.recDir = this.srcDir + this.subFolder + "/" + "Recommendations" + "/";
		this.gtDir = this.srcDir + this.subFolder + "/" + "GroundTruth" + "/";
		this.testingStartPos = testingStartPos;
		this.testingEndPos = testingEndPos;
	}

	public float computeSuccessRate(int n) {
		Map<Integer, String> testingProjectsID = reader.readProjectList(srcDir + "List.txt", testingStartPos,
				testingEndPos);

		int numberOfMatches = 0;
		for (String project : testingProjectsID.values()) {
			Set<String> topRec = reader.readRecommendationFile(this.recDir + project, n);
			Set<String> groundTruth = reader.readGroundTruthInvocations(this.gtDir + project);

			Set<String> intersection = new HashSet<String>(groundTruth);
			intersection.retainAll(topRec);

			if (intersection.size() > 0)
				numberOfMatches++;
		}

		return (float) numberOfMatches / testingProjectsID.size() * 100;
	}

	public float computePrecision(int n) {
		Map<Integer, String> testingProjectsID = reader.readProjectList(srcDir + "List.txt", testingStartPos,
				testingEndPos);

		float precision = 0;
		for (String project : testingProjectsID.values()) {
			Set<String> topRec = reader.readRecommendationFile(this.recDir + project, n);
			Set<String> groundTruth = reader.readGroundTruthInvocations(this.gtDir + project);

			Set<String> intersection = new HashSet<String>(groundTruth);
			intersection.retainAll(topRec);

			precision += (float) intersection.size() / n;
		}

		return (float) precision / testingProjectsID.size();
	}

	public float computeRecall(int n) {
		Map<Integer, String> testingProjectsID = reader.readProjectList(srcDir + "List.txt", testingStartPos,
				testingEndPos);

		float recall = 0;
		for (String project : testingProjectsID.values()) {
			Set<String> topRec = reader.readRecommendationFile(this.recDir + project, n);
			Set<String> groundTruth = reader.readGroundTruthInvocations(this.gtDir + project);

			Set<String> intersection = new HashSet<String>(groundTruth);
			intersection.retainAll(topRec);

			recall += (float) intersection.size() / groundTruth.size();
		}

		return (float) recall / testingProjectsID.size();
	}
}
