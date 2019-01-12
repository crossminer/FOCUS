package org.focus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;

/**
 * Compute similarity between every testing project and all training projects using Cosine Similarity with Weight
 */
public class SimilarityCalculator {

	private String srcDir;
	private String simDir;
	private String subFolder;

	private int trainingStartPos1;
	private int trainingEndPos1; 
	private int trainingStartPos2;
	private int trainingEndPos2; 
	private int testingStartPos;
	private int testingEndPos;
	
	private static final Logger log = LogManager.getFormatterLogger(SimilarityCalculator.class);

	public SimilarityCalculator(String sourceDir) {
		this.srcDir = sourceDir;
	}

	public SimilarityCalculator(String sourceDir, String subFolder, int trStartPos1, int trEndPos1, 
			int trStartPos2, int trEndPos2, 
			int teStartPos, int teEndPos) {			
		this.srcDir = sourceDir;		
		this.subFolder = subFolder;				
		this.simDir = this.srcDir + this.subFolder + "/" + "Similarities" + "/";

		this.trainingStartPos1 = trStartPos1;
		this.trainingEndPos1 = trEndPos1;
		this.trainingStartPos2 = trStartPos2;
		this.trainingEndPos2 = trEndPos2;
		this.testingStartPos = teStartPos;
		this.testingEndPos = teEndPos;	
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
			int freq = 0;
			for (String term : terms.keySet()) {
				if (termFrequency.containsKey(term))
					freq = termFrequency.get(term) + 1;
				else
					freq = 1;
				termFrequency.put(term, freq);
			}
		}

		return termFrequency;
	}

	/**
	 * Standard term-frequency inverse document frequency calculation
	 */
	private float computeTF_IDF(int count, int total, int freq) {
		return (float) count * (float) Math.log(total / freq);
	}

	/**
	 * Compute the similarity between the project testingPro and all the projects in
	 * the supplied list and serialize the results.
	 */
	public void computeSimilarity(String testingPro, Map<String, Map<String, Integer>> projects) {
		Map<String, Integer> termFrequency = computeTermFrequency(projects);
		Map<String, Float> testingProjectVector = new HashMap<>();
		Map<String, Float> projectSimilarities = new HashMap<>();

		// Computes the feature vector of the testing project,
		// ie. the TF-IDF for all its invocations
		Map<String, Integer> terms = projects.get(testingPro);
		for (String term : terms.keySet()) {
			float tfIdf = computeTF_IDF(terms.get(term), projects.size(), termFrequency.get(term));
			testingProjectVector.put(term, tfIdf);
		}

		BufferedWriter writer = null;
		try {
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

			// Store similarities in the dataset/X/evaluation/roundN/Similarities/* files
			writer = new BufferedWriter(new FileWriter(simDir + testingPro));
			for (Map.Entry<String, Float> entry : sortedMap.entrySet()) {
				writer.append(testingPro + "\t" + entry.getKey() + "\t" + entry.getValue());
				writer.newLine();
				writer.flush();
			}
		} catch (IOException e) {
			log.error("Couldn't write file " + simDir + testingPro, e);
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				log.error("Couldn't close file " + simDir + testingPro, e);
			}
		}
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
				val = computeCosineSimilarity(vector1, vector2);					
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
			computeSimilarity(project, projects);
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


	private float computeCosineSimilarity(Map<String, Float> v1, Map<String, Float> v2) {
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
