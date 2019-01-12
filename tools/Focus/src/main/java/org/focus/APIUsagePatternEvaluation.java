package org.focus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class APIUsagePatternEvaluation {

	private String srcDir;
	private String groundTruth;
	private String patternDir;
	private String subFolder;
	private String levenshteinDir;

	private int testingStartPos;
	private int testingEndPos;
	
	private static final Logger log = LogManager.getFormatterLogger(APIUsagePatternEvaluation.class);

	public APIUsagePatternEvaluation(String sourceDir, String subFolder, int teStartPos, int teEndPos) {
		this.srcDir = sourceDir;		
		this.subFolder = subFolder;				
		this.groundTruth = this.srcDir + this.subFolder + "/" + "GroundTruth" + "/";
		this.patternDir = this.srcDir + this.subFolder + "/" + "APIUsagePatterns" + "/";
		this.levenshteinDir = this.srcDir + this.subFolder + "/" + "LevenshteinDistance" + "/";
		this.testingStartPos = teStartPos;
		this.testingEndPos = teEndPos;			
	}


	/**
	 * Generate a set of characters used to represent method invocations.
	 */
	public List<String> generateTerms(){
		List<String> ret = new ArrayList<String>();		
		for(char alphabet = 'A'; alphabet <='Z'; alphabet++ ) {
			ret.add(String.valueOf(alphabet));	        
		}	
		for(char alphabet = 'a'; alphabet <='z'; alphabet++ ) {
			ret.add(String.valueOf(alphabet));	        
		}		

		for(int i=0;i<10;i++)ret.add(Integer.toString(i));
		ret.add("+");
		ret.add("-");
		ret.add("*");
		ret.add("/");
		ret.add("!");
		ret.add("@");
		ret.add("§");
		ret.add("$");
		ret.add("%");
		ret.add("&");
		ret.add("(");
		ret.add(")");
		ret.add("=");
		ret.add("?");
		ret.add(";");
		ret.add(".");
		ret.add(",");
		ret.add(":");
		ret.add("°");
		ret.add("′");
		ret.add("¹");
		ret.add("²");
		ret.add("³");
		ret.add("¼");
		ret.add("½");
		ret.add("¬");
		ret.add("{");
		ret.add("[");
		ret.add("]");
		ret.add("}");
		ret.add("¸");
		ret.add("€");
		ret.add("¶");
		ret.add("ŧ");
		ret.add("←");
		ret.add("↓");
		ret.add("→");
		ret.add("ø");
		ret.add("þ");
		ret.add("¨");
		ret.add("~");
		ret.add("æ");
		ret.add("ſ");
		ret.add("ð");
		ret.add("đ");
		ret.add("ŋ");
		ret.add("ħ");
		ret.add("ĸ");
		ret.add("ł");
		ret.add("˝");
		ret.add("^");
		ret.add("’");
		ret.add("»");
		ret.add("«");
		ret.add("¢");
		ret.add("„");
		ret.add("“");
		ret.add("”");
		ret.add("µ");
		ret.add("·");
		ret.add("…");
		ret.add("–");

		return ret;
	}


	public void computeSimilarityScore() {
		DataReader reader = new DataReader();						
		Map<Integer,String> testingProjects = reader.readProjectList(this.srcDir + "List.txt",testingStartPos,testingEndPos);
		Set<Integer> keyTestingProjects = testingProjects.keySet();

		for(Integer keyTesting:keyTestingProjects){		
			String project = testingProjects.get(keyTesting);			
			String patternFile = this.patternDir + project;
			String gtFile = this.groundTruth + project;

			log.info("Computing Levenshtein score for: " + project);

			String gtDeclaration = reader.readGroundTruthDeclaration(gtFile);						
			// Read all method invocations of the ground-truth declaration			
			Map<Integer,String> groundTruthInvocations = reader.getDeclarationDetails(this.srcDir,project,gtDeclaration);
			//System.out.println("Size: " + terms.size());

			Map<Integer,String> patterns = reader.readPatternFile(patternFile);
			Set<Integer> keySet = patterns.keySet();			
			String levenshteinFile = this.levenshteinDir + project;

			try {				
				BufferedWriter writer = new BufferedWriter(new FileWriter(levenshteinFile));

				for(Integer key:keySet) {

					Set<Integer> keySet2 = groundTruthInvocations.keySet();
					List<String> terms = generateTerms();			
					Map<String,String> dictionary = new HashMap<String,String>();

					String s1="",iv="";

					// Build a string that represents the ground-truth declaration
					for(Integer key2:keySet2) {				
						iv = groundTruthInvocations.get(key2);	

						if(dictionary.isEmpty()) {						
							String alias = terms.get(0);
							dictionary.put(iv, alias);
							terms.remove(terms.indexOf(alias));
							s1 += alias;
						} 
						else {						
							if(dictionary.containsKey(iv)) {
								String alias = dictionary.get(iv);
								s1 += alias;					
							} 
							else {
								String alias = terms.get(0);
								dictionary.put(iv, alias);
								terms.remove(terms.indexOf(alias));
								s1 += alias;
							}						
						}			
					}

					String line = patterns.get(key);
					String[] parts = line.split("#");  
					String pro = parts[0];
					String md = parts[1];
					Map<Integer,String> invocations = reader.getDeclarationDetails(this.srcDir, pro, md);
					Set<Integer> keySet3 = invocations.keySet();

					// if(terms.size()<5) System.out.println("Size: " + terms.size());
					//System.out.println(project + " \t" + keySet3.size() + "\t" + terms.size());

					String s2 ="";

					for(Integer key3:keySet3) {
						iv = invocations.get(key3);										
						if(dictionary.containsKey(iv)) {
							String alias = dictionary.get(iv);
							s2 += alias;					
						} 
						else {
							if(terms.size()<5) {
								log.info(project + terms.size());
							}
							if(terms.size()==0) {
								log.error("terms.size() == 0 for " + project);
								continue;
							}
							String alias = terms.get(0);
							dictionary.put(iv, alias);
							terms.remove(terms.indexOf(alias));
							s2 += alias;
						}				
					}

					// System.out.println(project);
					// System.out.println(s1);
					// System.out.println(s2);
					// System.out.println("===================");
					int distance = levenshteinDistance(s1,s2);

					String content = line + "\t" + distance;				
					writer.append(content);							
					writer.newLine();
					writer.flush();					
				}				
				writer.close();							
			} 
			catch (IOException e) {
				log.error("Failed to compute similarity scores", e);
			}			
		}				
	}


	public int levenshteinDistance(String string1, String string2) {                          
		int len0 = string1.length() + 1;                                                     
		int len1 = string2.length() + 1;                                                     

		// the array of distances                                                       
		int[] cost = new int[len0];                                                     
		int[] newcost = new int[len0];                                                  

		// initial cost of skipping prefix in String s0                                 
		for (int i = 0; i < len0; i++) cost[i] = i;                                     

		// dynamically computing the array of distances                                  

		// transformation cost for each letter in s1                                    
		for (int j = 1; j < len1; j++) {                                                
			// initial cost of skipping prefix in String s1                             
			newcost[0] = j;                                                             

			// transformation cost for each letter in s0                                
			for(int i = 1; i < len0; i++) {                                             
				// matching current letters in both strings                             
				int match = (string1.charAt(i - 1) == string2.charAt(j - 1)) ? 0 : 1;
				// computing cost for each transformation                               
				int cost_replace = cost[i - 1] + match;                                 
				int cost_insert  = cost[i] + 1;                                         
				int cost_delete  = newcost[i - 1] + 1;                             
				// keep minimum cost                                                    
				newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
			}                                                                           
			// swap cost/newcost arrays                                                 
			int[] swap = cost; cost = newcost; newcost = swap;                          
		}                                                                               
		// the distance is the cost for transforming all letters in both strings        
		return cost[len0 - 1];                                                          
	}

}
