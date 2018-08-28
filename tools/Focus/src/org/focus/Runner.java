package org.focus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


class ValueComparator implements Comparator<String> {

	Map<String, Float> base;
	public ValueComparator(Map<String, Float> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with equals.    
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} 
		else {
			return 1;
		} 
		// returning 0 would merge keys
	}
}

public class Runner {

	private String srcDir;	
	private String subFolder;
	private int numOfProjects;
	private int numOfNeighbours;
	private int numOfFolds;


	public void loadConfigurations(){		
		Properties prop = new Properties();				
		try {
			prop.load(new FileInputStream("evaluation.properties"));		
			this.srcDir=prop.getProperty("sourceDirectory");
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}	
		return;
	}


	public void run(){		
		System.out.println("FOCUS: A Context-Aware Recommender System!");
		loadConfigurations();
		tenFoldCrossValidation();

		//		leaveOneOutValidation();
		//		System.out.println(System.currentTimeMillis());		
		//		testLinkedHashMap();
	}

	/**
	 * Ten-fold cross validation
	 */	
	public void tenFoldCrossValidation() {
		numOfProjects = 610;		
		numOfNeighbours = 2;
		numOfFolds = 10;
		int step = (int)numOfProjects/10;								

		for(int i=0;i<numOfFolds;i++) {
			int trainingStartPos1 = 1;			
			int trainingEndPos1 = i*step;			
			int trainingStartPos2 = (i+1)*step+1;
			int trainingEndPos2 = numOfProjects;
			int testingStartPos = 1+i*step;
			int testingEndPos =   (i+1)*step;

			int k=i+1;
			subFolder = "evaluation/round" + Integer.toString(k);

			SimilarityCalculator calculator = new SimilarityCalculator(this.srcDir,this.subFolder,
					trainingStartPos1,
					trainingEndPos1,
					trainingStartPos2,
					trainingEndPos2,
					testingStartPos,
					testingEndPos);
			calculator.computeProjectSimilarity();

			ContextAwareRecommendation engine = new ContextAwareRecommendation(this.srcDir,this.subFolder,numOfNeighbours,testingStartPos,testingEndPos);
			engine.recommendation();

			APIUsagePatternMatcher matcher = new APIUsagePatternMatcher(this.srcDir,this.subFolder,
					trainingStartPos1,
					trainingEndPos1,
					trainingStartPos2,
					trainingEndPos2,
					testingStartPos,
					testingEndPos);

			//			matcher.searchAPIUsagePatterns();

			APIUsagePatternEvaluation eval = new APIUsagePatternEvaluation(this.srcDir, this.subFolder, testingStartPos, testingEndPos);
			//			eval.ComputeSimilarityScore();
		}
	}


	public void leaveOneOutValidation() {		
		numOfProjects = 610;		
		numOfNeighbours = 2;
		numOfFolds = numOfProjects;		
		int step = 1;

		for(int i=0;i<numOfFolds;i++) {			
			int trainingStartPos1 = 1;			
			int trainingEndPos1 = i*step;			
			int trainingStartPos2 = (i+1)*step+1;
			int trainingEndPos2 = numOfProjects;
			int testingStartPos = 1+i*step;
			int testingEndPos =  (i+1)*step;

			int k=i+1;
			subFolder = "round1";			

			SimilarityCalculator calculator = new SimilarityCalculator(this.srcDir,this.subFolder,
					trainingStartPos1,
					trainingEndPos1,
					trainingStartPos2,
					trainingEndPos2,
					testingStartPos,
					testingEndPos);
			calculator.computeProjectSimilarity();	

			ContextAwareRecommendation engine = new ContextAwareRecommendation(this.srcDir,this.subFolder,numOfNeighbours,testingStartPos,testingEndPos);
			engine.recommendation();			
		}		
	}


	public static void main(String[] args) {	
		Runner runner = new Runner();			
		runner.run();				    		    
		return;
	}	
}
