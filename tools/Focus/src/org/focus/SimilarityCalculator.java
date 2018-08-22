package org.focus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.Sets;

/**
 * Compute similarity between every testing project and all training projects using Cosine Similarity with Weight
 */
public class SimilarityCalculator {

	private String srcDir;
	private String groundTruth;
	private String simDir;
	private String subFolder;

	private int trainingStartPos1;
	private int trainingEndPos1; 
	private int trainingStartPos2;
	private int trainingEndPos2; 
	private int testingStartPos;
	private int testingEndPos;

	public SimilarityCalculator(String sourceDir) {
		this.srcDir = sourceDir;
	}

	public SimilarityCalculator(String sourceDir, String subFolder, int trStartPos1, int trEndPos1, 
			int trStartPos2, int trEndPos2, 
			int teStartPos, int teEndPos) {			
		this.srcDir = sourceDir;		
		this.subFolder = subFolder;				
		this.groundTruth = this.srcDir + this.subFolder + "/" + "GroundTruth" + "/";		
		this.simDir = this.srcDir + this.subFolder + "/" + "Similarities" + "/";

		this.trainingStartPos1 = trStartPos1;
		this.trainingEndPos1 = trEndPos1;
		this.trainingStartPos2 = trStartPos2;
		this.trainingEndPos2 = trEndPos2;
		this.testingStartPos = teStartPos;
		this.testingEndPos = teEndPos;	
	}


	public void ComputeSimilarity(String testingPro, Map<String,Map<String,Integer>> projects) {	
		Set<String> keySet = projects.keySet();		
		// the number of projects in the corpus
		int numOfProjects = projects.size();		

		Map<String,Integer> termFrequency = new HashMap<String,Integer>();		
		Map<String,Integer> terms = new HashMap<String,Integer>(); 
		Set<String> keySet2 = null;

		for(String pro:keySet) {
			terms = projects.get(pro);			
			keySet2 = terms.keySet();
			int freq = 0;
			for(String term:keySet2) {
				if(termFrequency.containsKey(term))freq=termFrequency.get(term)+1;
				else freq=1;
				termFrequency.put(term, freq);
			}			
		}

		// termFrequency: term -- number of projects that contain the term
		Map<String,Float> vector1 = new HashMap<String,Float>();
		Map<String, Float> sim = new HashMap<String, Float>();

		// the input project		
		terms = projects.get(testingPro);
		keySet2 = terms.keySet();	
		float tmp,val;

		// tf-idf: term frequency - inverse document frequency
		for(String term:keySet2) {
			tmp = numOfProjects/termFrequency.get(term);
			val = (float) (terms.get(term)*Math.log(tmp));			
			vector1.put(term, val);
		}

		String content="";	
		keySet = projects.keySet();

		//		System.out.println("size of the training set is: " + keySet.size());

		// compute the similarities between the input project and all other projects in the corpus
		// and save to files
		try {					
			for(String trainingPro:keySet) {	

				if(!trainingPro.equals(testingPro)) {
					Map<String,Float> vector2 = new HashMap<String,Float>();
					terms = projects.get(trainingPro);
					keySet2 = terms.keySet();

					for(String term:keySet2) {
						tmp = numOfProjects/termFrequency.get(term);
						val = (float) (terms.get(term)*Math.log(tmp));			
						vector2.put(term, val);
					}			
					val = CosineSimilarity(vector1, vector2);					
					sim.put(trainingPro, val);								
				}													
			}					

			ValueComparator bvc =  new ValueComparator(sim);        
			TreeMap<String,Float> sorted_map = new TreeMap<String,Float>(bvc);
			sorted_map.putAll(sim);				
			keySet2 = sorted_map.keySet();				

			BufferedWriter writer = new BufferedWriter(new FileWriter(this.simDir+testingPro));												
			for(String key:keySet2){				
				content = testingPro + "\t" + key + "\t" + sim.get(key);
				System.out.println("The similarity between " + testingPro + " and " + key + " is: " + sim.get(key));
				writer.append(content);							
				writer.newLine();
				writer.flush();						
			}				
			writer.close();

		} 
		catch (IOException e) {
			e.printStackTrace();
		}

		return;		
	}


	/**
	 * Compute the similarity between two vectors using Jaccard Similarity
	 */
	public float JaccardSimilarity(int vector1[], int vector2[]) {
		float ret = 0;						
		int count = 0;
		int length = vector1.length;	

		for(int i=0;i<length;i++)if(vector1[i]==1.0 && vector2[i]==1.0)count++;											
		float size1 = count;		
		float size2 = 2*length - size1;	
		ret = (float)size1/size2;
		//		System.out.println(vector1.size() + "\t"+ vector2.size() + "\t" +size1 + "\t" + size2);
		return ret;		
	}


	public Map<String, Float> ComputeSimilarity2(String testingPro, Map<String,Map<String,Integer>> projects) {	
		Set<String> keySet = projects.keySet();		
		// the number of projects in the corpus
		int numOfProjects = projects.size();		

		Map<String,Integer> termFrequency = new HashMap<String,Integer>();		
		Map<String,Integer> terms = new HashMap<String,Integer>(); 
		Set<String> keySet2 = null;

		for(String pro:keySet) {
			terms = projects.get(pro);			
			keySet2 = terms.keySet();
			int freq = 0;

			for(String term:keySet2) {
				if(termFrequency.containsKey(term))freq=termFrequency.get(term)+1;
				else freq=1;
				termFrequency.put(term, freq);
			}			
		}

		// termFrequency: term -- number of projects that contain the term
		Map<String,Float> vector1 = new HashMap<String,Float>();
		Map<String, Float> sim = new HashMap<String, Float>();

		// the input project
		terms = projects.get(testingPro);
		keySet2 = terms.keySet();

		float tmp,val;

		// tf-idf: term frequency - inverse document frequency
		for(String term:keySet2) {
			tmp = numOfProjects/termFrequency.get(term);
			val = (float) (terms.get(term)*Math.log(tmp));			
			vector1.put(term, val);
		}

		keySet = projects.keySet();

		// compute the similarities between the input project and all other projects in the corpus		
		for(String trainingPro:keySet) {			
			if(!trainingPro.equals(testingPro)) {
				Map<String,Float> vector2 = new HashMap<String,Float>();
				terms = projects.get(trainingPro);
				keySet2 = terms.keySet();

				for(String term:keySet2) {
					tmp = numOfProjects/termFrequency.get(term);
					val = (float) (terms.get(term)*Math.log(tmp));			
					vector2.put(term, val);
				}			
				val = CosineSimilarity(vector1, vector2);					
				sim.put(trainingPro, val);								
			}													
		}					

		return sim;		
	}


	/**
	 * Compute similarity between every testing project and all training projects using Cosine Similarity with Weight
	 */
	public void computeProjectSimilarity(){
		DataReader reader = new DataReader();			

		Map<Integer,String> trainingProjects = new HashMap<Integer,String>();		
		if(trainingStartPos1<trainingEndPos1) trainingProjects = reader.readProjectList(this.srcDir + "List.txt",trainingStartPos1,trainingEndPos1);		
		if(trainingStartPos2 < trainingEndPos2) {
			Map<Integer,String> tempoProjects = reader.readProjectList(this.srcDir + "List.txt",trainingStartPos2,trainingEndPos2);		
			trainingProjects.putAll(tempoProjects);
		}

		Map<String,Map<String,Integer>> projects = new HashMap<String,Map<String,Integer>>();				
		Set<Integer> keyTrainingProjects = trainingProjects.keySet();		

		for(Integer keyTraining:keyTrainingProjects){			
			String project = trainingProjects.get(keyTraining);
			//			System.out.println(project);
			projects.putAll(reader.getProjectInvocations(this.srcDir,project));			
		}

		Map<String,Map<String,Integer>> testingPro = new HashMap<String,Map<String,Integer>>();					
		Map<Integer,String> testingProjects = reader.readProjectList(this.srcDir + "List.txt",testingStartPos,testingEndPos);
		Set<Integer> keyTestingProjects = testingProjects.keySet();

		int numOfTestingInvocations = 3;
		// to specify if we completely remove a half of the method declarations
		boolean removeHalf = true;

		for(Integer keyTesting:keyTestingProjects){		
			String project = testingProjects.get(keyTesting);	

			// get a half of all declarations and used for similarity computation
			testingPro = reader.getTestingProjectInvocations(this.srcDir, this.subFolder, project, numOfTestingInvocations, removeHalf);

			// get all declarations and used for similarity computation	
			//			testingPro = reader.getAllTestingProjectInvocations(this.srcDir, this.subFolder, project, numOfTestingInvocations);			
			projects.putAll(testingPro);		
			ComputeSimilarity(project, projects);
			projects.remove(project);
		}

		return;
	}


	public Map<String,Float> getProjectSimilarity(String pro1, Set<String> projects){
		Map<String,Float> ret = new HashMap<String,Float>();	
		DataReader reader = new DataReader();			
		Map<String,Map<String,Integer>> projects2 = new HashMap<String,Map<String,Integer>>();	

		for(String project:projects){						
			projects2.putAll(reader.getProjectInvocations(this.srcDir,project));			
		}

		projects2.putAll(reader.getProjectInvocations(this.srcDir, pro1));						
		ret = ComputeSimilarity2(pro1, projects2);
		return ret;
	}


	private float CosineSimilarity(Map<String, Float> v1, Map<String, Float> v2) {
		Set<String> both = Sets.intersection(v1.keySet(), v2.keySet());
		// Set<String> both=v1.keySet();
		// both.retainAll(v2.keySet());
		double sclar = 0, norm1 = 0, norm2 = 0;

		// we need to perform cosine similarity only on words that exist in both lists
		if (both.size() > 0) {
			for (Float f : v1.values()) {
				norm1 += f * f;
			}
			for (Float f : v2.values()) {
				norm2 += f * f;
			}
			for (String k : both) {
				sclar += v1.get(k) * v2.get(k);
			}
			if (sclar == 0)
				return 0f;
			else
				return (float) (sclar / Math.sqrt(norm1 * norm2));
		} 
		else {
			return 0f;
		}
	}
}
