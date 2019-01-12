package org.focus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

	public APIUsagePatternMatcher(String sourceDir, String subFolder, int trStartPos1, int trEndPos1, 
			int trStartPos2, int trEndPos2, 
			int teStartPos, int teEndPos) {			
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
		Map<Integer,String> testingProjects = reader.readProjectList(this.srcDir + "List.txt",this.testingStartPos,this.testingEndPos);	
		Set<Integer> keyTestingProjects = testingProjects.keySet();						
		String testingPro = "", filename="";		

		Map<Integer,String> trainingProjects = new HashMap<Integer,String>();		
		if(trainingStartPos1 < trainingEndPos1) trainingProjects = reader.readProjectList(this.srcDir + "List.txt",trainingStartPos1,trainingEndPos1);		
		if(trainingStartPos2 < trainingEndPos2) {
			Map<Integer,String> tempoProjects = reader.readProjectList(this.srcDir + "List.txt",trainingStartPos2,trainingEndPos2);		
			trainingProjects.putAll(tempoProjects);
		}

		Map<String,Map<String,Set<String>>> allProjects = new HashMap<String,Map<String,Set<String>>>();
		Map<String,Set<String>> tmpMethodInvocations = new HashMap<String,Set<String>>();

		Set<Integer> keyTrainingProjects = trainingProjects.keySet();		

		for(Integer keyTraining:keyTrainingProjects){			
			String project = trainingProjects.get(keyTraining);		
			filename = project;
			tmpMethodInvocations =  reader.getProjectDetails(this.srcDir,filename);
			allProjects.put(project, tmpMethodInvocations);	
		}

		Set<String> keySet = null;
		Map<String,Set<String>> methodInvocations = new HashMap<String,Set<String>>();	
		Map<String, Float> results = null;

		for(Integer keyTesting:keyTestingProjects){		
			testingPro = testingProjects.get(keyTesting);		
			filename = testingPro;
			log.info("Searching API usage pattern for " + testingPro);
			String tmp = this.recDir + filename;
			Map<Integer, String> recommendations = reader.readRecommendationFile(tmp);
			String tmp2 = this.tiDir + filename;
			Map<Integer, String> testingInvocations = reader.getTestingInvocations(tmp2);

			int index = recommendations.size()+1;
			Set<Integer> ks = testingInvocations.keySet();

			// add also the testing invocation(s)
			for(Integer i:ks) {
				String invocation = testingInvocations.get(i);
				recommendations.put(index, invocation);
				index++;				
			}

			keySet = allProjects.keySet();
			results = new HashMap<String, Float>();

			for(String project:keySet) {		
				methodInvocations = allProjects.get(project);
				Set<String> keySet2 = methodInvocations.keySet();

				for(String md:keySet2) {
					Set<String> invocations = methodInvocations.get(md);
					int size = invocations.size();
					int s = recommendations.size();
					if(s<size)size = s;
					Set<String> allMIs = new HashSet<String>();
					for(int i=1;i<=size;i++)allMIs.add(recommendations.get(i));
					int size1 = Sets.intersection(invocations, allMIs).size();
					int size2 = Sets.union(invocations, allMIs).size();
					float jaccard = (float)size1/size2;
					String key = project+"#"+md;
					results.put(key, jaccard);					
				}
			}

			ValueComparator bvc =  new ValueComparator(results);        
			TreeMap<String,Float> sorted_map = new TreeMap<String,Float>(bvc);
			sorted_map.putAll(results);
			Set<String> keySet2 = sorted_map.keySet();

			filename = testingPro;
			int numOfRecs = 20;

			try {
				String tmp3 = this.patternDir + filename;
				BufferedWriter writer = new BufferedWriter(new FileWriter(tmp3));
				int count = 0;
				for(String key:keySet2){					
					String content = key + "\t" + results.get(key);				
					writer.append(content);							
					writer.newLine();
					writer.flush();
					count++;
					if(count>numOfRecs)break;
				}
				writer.close();							
			} 
			catch (IOException e) {
				log.error("Couldn't write file " + this.patternDir + filename, e);
			}	

		}
		return;
	}
}
