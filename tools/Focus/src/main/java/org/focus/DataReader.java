package org.focus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataReader {

	private static final Logger log = LogManager.getFormatterLogger(DataReader.class);

	public Set<String> getPCmembers(String filename) {
		Set<String> list = new HashSet<String>();
		String line = null;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				String name = parts[0].trim();
				list.add(name);
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		return list;
	}

	public Map<String, String> getTestingInvocations2(String filename) {
		Map<String, String> ret = new HashMap<>();
		String line = null;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				String[] vals = line.split("#");
				ret.put(vals[0].trim(), vals[1].trim());
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		return ret;
	}

	public Map<String, Set<String>> getProjectDetailsFromARFF2(String path, String filename) {
		Map<String, Set<String>> methodInvocations = new HashMap<String, Set<String>>();
		Set<String> vector = null;
		String line = null;
		filename = path + filename;
		int count = 0;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				count++;
				if (count > 6) {
					String[] parts = line.split("#");
					String md = parts[0].replace("'", "").trim();
					String temp = parts[1].replace("'", "").trim();

					String[] invocations = temp.split(" ");
					int len = invocations.length;

					if (methodInvocations.containsKey(md))
						vector = methodInvocations.get(md);
					else
						vector = new HashSet<String>();

					for (int i = 0; i < len; i++) {
						String mi = invocations[i].trim();
						vector.add(mi);
					}
					methodInvocations.put(md, vector);
				}
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		return methodInvocations;
	}

	public Map<Integer, String> readProjectList(String filename, int startPos, int endPos) {
		Map<Integer, String> ret = new HashMap<Integer, String>();
		String line = "", repo = "";
		int count = 1;
		int id = startPos;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while (count < startPos) {
				line = reader.readLine();
				count++;
			}
			while (((line = reader.readLine()) != null)) {
				repo = line.trim();
				ret.put(id, repo);
				id++;
				count++;
				if (count > endPos)
					break;
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		return ret;
	}

	/**
	 * Get all method invocations that do not belong to the ground-truth data
	 */
	public Map<String, Set<String>> getTestingProjectDetails(String path, String filename, Set<String> gtInvocations,
			Map<String, Set<String>> testingMIs) {
		Map<String, Set<String>> methodInvocations = new HashMap<String, Set<String>>();
		String testingMD = "", testingMI = "";
		// get the testing method declaration*/
		for (String s : gtInvocations) {
			String[] parts = s.split("#");
			testingMD = parts[0].trim();
			break;
		}

		Set<String> vector = null;
		String line = null;
		filename = path + filename;
		Set<String> tmp = new HashSet<String>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				if (line.contains(testingMD)) {
					// get the testing method invocations
					if (!gtInvocations.contains(line)) {
						String[] parts = line.split("#");
						testingMI = parts[1];
						tmp.add(testingMI);
					}

				} else {
					// other method invocations, we get them all
					String[] parts = line.split("#");
					String md = parts[0].trim();
					String mi = parts[1].trim();
					if (methodInvocations.containsKey(md))
						vector = methodInvocations.get(md);
					else
						vector = new HashSet<String>();
					vector.add(mi);
					methodInvocations.put(md, vector);
				}
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		testingMIs.put(testingMD, tmp);
		return methodInvocations;
	}

	/**
	 * We need to retain the sequence of both declarations and invocations, so a
	 * LinkedHashMap and a List are used
	 */
	public LinkedHashMap<String, List<String>> getProjectDetails2(String path, String filename) {
		LinkedHashMap<String, List<String>> methodInvocations = new LinkedHashMap<String, List<String>>();
		List<String> vector = null;
		String line = null;
		filename = path + filename;
		String prevMD = "", currentMD = "";
		boolean start = true;
		Set<String> doneDeclarations = new HashSet<String>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("#");
				String md = parts[0].trim();
				String mi = parts[1].trim();

				// to avoid a project with two identical method declarations
				if (start) {
					prevMD = md;
					start = false;
				}
				currentMD = md;

				if (!currentMD.equals(prevMD)) {
					doneDeclarations.add(prevMD);
					prevMD = currentMD;
				}

				if (!doneDeclarations.contains(currentMD)) {
					if (methodInvocations.containsKey(md))
						vector = methodInvocations.get(md);
					else
						vector = new ArrayList<String>();
					vector.add(mi);
					methodInvocations.put(md, vector);
				}
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		// remove all declarations with less than 5 invocations from the data
		Set<String> keySet = methodInvocations.keySet();
		Set<String> temp = new HashSet<String>();

		for (String key : keySet) {
			List<String> list = methodInvocations.get(key);
			if (list.size() < 5)
				temp.add(key);
		}

		for (String key : temp)
			methodInvocations.remove(key);
		return methodInvocations;
	}

	public Map<Integer, String> getDeclarationDetails(String path, String project, String declaration) {
		Map<Integer, String> invocations = new HashMap<Integer, String>();
		String line = null;
		String filename = path + project;
		int id = 1;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("#");
				String md = parts[0].trim();
				String mi = parts[1].trim();

				if (md.equals(declaration)) {
					invocations.put(id, mi);
					id++;
				}
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}
		return invocations;
	}

	public Map<String, Set<String>> getProjectDetails(String path, String filename) {
		Map<String, Set<String>> methodInvocations = new HashMap<String, Set<String>>();
		Set<String> vector = null;
		String line = null;
		filename = path + filename;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("#");
				String md = parts[0].trim();
				String mi = parts[1].trim();

				if (methodInvocations.containsKey(md))
					vector = methodInvocations.get(md);
				else
					vector = new HashSet<String>();

				vector.add(mi);
				methodInvocations.put(md, vector);
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		return methodInvocations;
	}

	public Set<String> getGroundTruthInvocations(String path, String filename) {
		Set<String> gtInvocations = new HashSet<String>();
		String line = null;
		filename = path + filename;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				gtInvocations.add(line);
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}
		return gtInvocations;
	}

	public Map<String, Map<String, Integer>> getProjectInvocations(String path, String name) {
		Map<String, Map<String, Integer>> methodInvocations = new HashMap<String, Map<String, Integer>>();
		Map<String, Integer> terms = new HashMap<String, Integer>();
		String line = null;
		String filename = path + name;

		int freq = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("#");
				String mi = parts[1].trim();

				if (terms.containsKey(mi))
					freq = terms.get(mi) + 1;
				else
					freq = 1;

				terms.put(mi, freq);
			}
			reader.close();
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		methodInvocations.put(name, terms);
		return methodInvocations;
	}

	public Map<String, Map<String, Integer>> getTestingProjectInvocations(String path, String subFolder,
			String filename, int numOfInvocations, boolean removeHalf) {
		LinkedHashMap<String, List<String>> methodInvocations = getProjectDetails2(path, filename);

		Set<String> keySet = methodInvocations.keySet();
		List<String> list = null;
		int size = 0, index = 0;
		Set<String> removedKey = new HashSet<String>();

		// remove the last half of the method declarations, only when there are more
		// than 5 declarations
		if (keySet.size() < 6)
			removeHalf = false;

		if (removeHalf) {
			size = methodInvocations.size();
			int half = Math.round(size / 3);
			int count = 0;
			for (String key : keySet) {
				count++;
				if (count > half)
					removedKey.add(key);
			}
		}

		// remove the last declarations
		for (String key : removedKey)
			methodInvocations.remove(key);

		keySet = methodInvocations.keySet();
		list = new ArrayList<String>(keySet);
		size = list.size();

		// select a method that contains at least some invocations: this is our future
		// work
		// randomly select a method declaration as testing
		// System.out.println("size is: "+size + " " + filename);
		index = size - 1; // randomGenerator.nextInt(size);

		// the method invocation that is used as query
		String testingDeclaration = list.get(index);
		Set<String> groundTruthMIs = new HashSet<String>();
		list = methodInvocations.get(testingDeclaration);
		size = list.size();

		Set<String> set = new HashSet<String>();
		for (int i = 0; i < size; i++)
			set.add(list.get(i));

		// remove from the end to the beginning

		// for(String key:set) {
		// groundTruthMIs.add(key);
		// /*remove it from the testing data*/
		// list.remove(list.indexOf(key));
		// count++;
		// val = size - count;
		// if(val==numOfInvocations)break;
		// }

		List<String> query = new ArrayList<String>();

		// this is the query, it is the first rows of the declaration
		for (int i = 0; i < numOfInvocations; i++)
			query.add(list.get(i));

		// this is the ground-truth data
		for (int i = numOfInvocations; i < size; i++)
			groundTruthMIs.add(list.get(i));

		// remove the testing declaration from the set of method invocations
		methodInvocations.remove(testingDeclaration);
		// save back the testing declaration, however only with the selected testing
		// invocations
		Map<String, List<String>> tmpMethodInvocations = new HashMap<String, List<String>>();
		tmpMethodInvocations.put(testingDeclaration, list);
		methodInvocations.putAll(tmpMethodInvocations);

		String testingInvocationLocation = path + subFolder + "/" + "TestingInvocations" + "/";
		// save the testing method invocations to an external file for future usage

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(testingInvocationLocation + filename))) {
			size = query.size();
			for (int i = 0; i < size; i++) {
				String content = testingDeclaration + "#" + query.get(i);
				writer.append(content);
				writer.newLine();
				writer.flush();
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + testingInvocationLocation + filename, e);
		}

		String groundTruthPath = path + subFolder + "/" + "GroundTruth" + "/";
		// save the ground-truth method invocations to an external file for future
		// comparison
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(groundTruthPath + filename))) {
			for (String s : groundTruthMIs) {
				String content = testingDeclaration + "#" + s;
				writer.append(content);
				writer.newLine();
				writer.flush();
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + groundTruthPath + filename, e);
		}

		Map<String, Map<String, Integer>> ret = new HashMap<String, Map<String, Integer>>();
		keySet = methodInvocations.keySet();

		Map<String, Integer> map = new HashMap<String, Integer>();
		List<String> terms = null;
		int freq = 0;

		// get all method invocations and their corresponding frequency
		for (String key : keySet) {
			terms = methodInvocations.get(key);
			for (String term : terms) {
				if (map.containsKey(term))
					freq = map.get(term) + 1;
				else
					freq = 1;
				map.put(term, freq);
			}
		}

		ret.put(filename, map);
		return ret;
	}

	/**
	 * Using all method declarations
	 */
	public Map<String, Map<String, Integer>> getAllTestingProjectInvocations(String path, String subFolder,
			String filename, int numOfInvocations) {
		Map<String, List<String>> methodInvocations = getProjectDetails2(path, filename);
		Set<String> keySet = methodInvocations.keySet();
		List<String> list = null;

		// remove a half of the method declarations

		// int size = methodInvocations.size();
		// int half = Math.round(size/2);
		// int index = 0;
		//
		// for(int i=0;i<half;i++) {
		// keySet = methodInvocations.keySet();
		// list = new ArrayList<String>(keySet);
		// size = list.size();
		// index = randomGenerator.nextInt(size);
		// String md = list.get(index);
		// methodInvocations.remove(md);
		// }

		keySet = methodInvocations.keySet();
		list = new ArrayList<String>(keySet);
		int size = list.size();

		// randomly select a method declaration as testing
		int index = size - 1; // randomGenerator.nextInt(size);

		// the method invocation that is used as testing data
		String testingDeclaration = list.get(index);

		Set<String> groundTruthMIs = new HashSet<String>();
		list = methodInvocations.get(testingDeclaration);
		size = list.size();
		int count = 0;

		Set<String> set = new HashSet<String>();
		for (int i = 0; i < size; i++)
			set.add(list.get(i));
		int val = 0;

		for (String key : set) {
			groundTruthMIs.add(key);
			// remove it from the testing data
			list.remove(list.indexOf(key));
			count++;
			val = size - count;
			if (val == numOfInvocations)
				break;
		}

		// remove the testing declaration from the set of method invocations
		methodInvocations.remove(testingDeclaration);
		// save back the testing declaration, however only with the selected testing
		// invocations
		Map<String, List<String>> tmpMethodInvocations = new HashMap<String, List<String>>();
		tmpMethodInvocations.put(testingDeclaration, list);
		methodInvocations.putAll(tmpMethodInvocations);

		String testingInvocationLocation = path + subFolder + "/" + "TestingInvocations" + "/";
		// save the testing method invocations to an external file for future usage

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(testingInvocationLocation + filename))) {
			size = list.size();
			for (int i = 0; i < size; i++) {
				String content = testingDeclaration + "#" + list.get(i);
				writer.append(content);
				writer.newLine();
				writer.flush();
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + testingInvocationLocation + filename, e);
		}

		String groundTruthPath = path + subFolder + "/" + "GroundTruth" + "/";
		// save the ground-truth method invocations to an external file for future
		// comparison
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(groundTruthPath + filename))) {
			for (String s : groundTruthMIs) {
				String content = testingDeclaration + "#" + s;
				writer.append(content);
				writer.newLine();
				writer.flush();
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + groundTruthPath + filename, e);
		}

		Map<String, Map<String, Integer>> ret = new HashMap<String, Map<String, Integer>>();
		keySet = methodInvocations.keySet();
		Map<String, Integer> map = new HashMap<String, Integer>();
		List<String> terms = null;
		int freq = 0;

		// get all method invocations and their corresponding frequency
		for (String key : keySet) {
			terms = methodInvocations.get(key);

			for (String term : terms) {
				if (map.containsKey(term))
					freq = map.get(term) + 1;
				else
					freq = 1;
				map.put(term, freq);
			}
		}

		ret.put(filename, map);
		return ret;
	}

	// get a half of method invocations and save the other half into ground-truth
	// file
	// public Map<String,Map<String,Integer>> getTestingProjectInvocations(String
	// path, String filename, String groundTruthPath) {
	//
	// Map<String,List<String>> methodInvocations = new
	// HashMap<String,List<String>>();
	// methodInvocations = getProjectDetails2(path, filename);
	//
	// Set<String> keySet = methodInvocations.keySet();
	// List<String> list = new ArrayList<String>(keySet);
	// int size = list.size();
	// Random randomGenerator = new Random();
	// /*randomly select a method declaration as testing*/
	//
	// int index = randomGenerator.nextInt(size);
	// /*the method invocation that is used as test data*/
	// String testingDeclaration = list.get(index);
	//
	// list = methodInvocations.get(testingDeclaration);
	// size = list.size();
	// index = randomGenerator.nextInt(size);
	// String testingInvocation = list.get(index);
	// list.remove(index);
	// size = list.size();
	//
	// /*save the ground-truth method invocations to an external file for future
	// comparison*/
	//
	// try{
	// BufferedWriter writer = new BufferedWriter(new FileWriter(groundTruthPath +
	// filename));
	// for(int i=0;i<size;i++) {
	// String content = testingDeclaration + "#" + list.get(i);
	// writer.append(content);
	// writer.newLine();
	// writer.flush();
	// }
	// writer.close();
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	//
	//
	//
	// /*remove the testing declaration from the set of method invocations*/
	// methodInvocations.remove(testingDeclaration);
	//
	// /*save back the testing declaration, however with only one invocation, the
	// testing invocation*/
	// Map<String,List<String>> tmpMethodInvocations = new
	// HashMap<String,List<String>>();
	// List<String> temp = new ArrayList<String>();
	// temp.add(testingInvocation);
	// tmpMethodInvocations.put(testingDeclaration, temp);
	// methodInvocations.putAll(tmpMethodInvocations);
	//
	// Map<String,Map<String,Integer>> ret = new
	// HashMap<String,Map<String,Integer>>();
	// keySet = methodInvocations.keySet();
	//
	// Map<String,Integer> map = new HashMap<String,Integer>();
	// List<String> terms = null;
	// int freq=0;
	//
	// /*get all method invocations and their corresponding frequency*/
	// for(String key:keySet) {
	// terms = methodInvocations.get(key);
	// for(String term:terms) {
	// if(map.containsKey(term))freq = map.get(term)+1;
	// else freq = 1;
	// map.put(term, freq);
	// }
	// }
	//
	// ret.put(filename, map);
	// return ret;
	// }

	/**
	 * Read the whole file
	 */
	public Map<Integer, String> readRecommendationFile(String filename) {
		Map<Integer, String> ret = new HashMap<Integer, String>();
		String line = null;
		String[] vals = null;
		int id = 1;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				vals = line.split("\t");
				String invocation = vals[0].trim();
				ret.put(id, invocation);
				id++;
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		return ret;
	}

	public Map<Integer, String> getTestingInvocations(String filename) {
		Map<Integer, String> ret = new HashMap<Integer, String>();
		String line = null;
		String[] vals = null;
		int id = 1;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				vals = line.split("#");
				String invocation = vals[1].trim();
				ret.put(id, invocation);
				id++;
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		return ret;
	}

	/**
	 * Read the recommended pattern for a given project
	 */
	public Map<Integer, String> readPatternFile(String filename) {
		Map<Integer, String> ret = new HashMap<Integer, String>();
		String line = null;
		String[] vals = null;
		int id = 1;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				vals = line.split("\t");
				String invocation = vals[0].trim();
				ret.put(id, invocation);
				id++;
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		return ret;
	}

	/**
	 * Read the ground-truth invocations for a given project
	 */
	public Set<String> readGroundTruthInvocations(String filename) {
		Set<String> ret = new HashSet<>();
		String line = null;
		String[] vals = null;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				vals = line.split("#");
				String invocation = vals[1].trim();
				ret.add(invocation);
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		return ret;
	}

	/**
	 * Read the ground-truth declaration for a given project
	 */
	public String readGroundTruthDeclaration(String filename) {
		String declaration = null;
		String line = null;
		String[] vals = null;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				vals = line.split("#");
				declaration = vals[0].trim();
				break;
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		return declaration;
	}

	/**
	 * Read a specific number of lines from the file
	 */
	public Set<String> readRecommendationFile(String filename, int size) {
		Set<String> ret = new HashSet<String>();
		String line = null;
		String[] vals = null;
		int count = 0;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				vals = line.split("\t");
				String library = vals[0].trim();
				ret.add(library);
				count++;
				if (count == size)
					break;
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		return ret;
	}

	public Set<String> readGroundTruthFile(String filename) {
		Set<String> ret = new HashSet<String>();
		String line = null;
		String[] vals = null;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				vals = line.split("\t");
				String library = vals[1].trim();
				ret.add(library);
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}
		return ret;
	}

	public Map<Integer, String> getMostSimilarProjects(String filename, int size) {
		Map<Integer, String> projects = new HashMap<Integer, String>();
		String line = null;
		String[] vals = null;
		int count = 0;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				vals = line.split("\t");
				String URI = vals[1].trim();
				projects.put(count, URI);
				count++;
				if (count == size)
					break;
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		return projects;
	}

	public Map<String, Float> getSimilarityScores(String filename, int size) {
		Map<String, Float> projects = new HashMap<>();
		String line = null;
		String[] vals = null;
		int count = 0;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while ((line = reader.readLine()) != null) {
				vals = line.split("\t");
				String URI = vals[1].trim();
				float score = Float.parseFloat(vals[2].trim());
				projects.put(URI, score);
				count++;
				if (count == size)
					break;
			}
		} catch (IOException e) {
			log.error("Couldn't read file " + filename, e);
		}

		return projects;
	}

	public void writeSimilarityScores(String simDir, String project, Map<String, Float> similarities) {
		String filename = simDir + project;

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			for (Map.Entry<String, Float> entry : similarities.entrySet()) {
				writer.append(project + "\t" + entry.getKey() + "\t" + entry.getValue());
				writer.newLine();
				writer.flush();
			}
		} catch (IOException e) {
			log.error("Couldn't write file " + filename, e);
		}
	}

	public void writeRecommendations(String filename, TreeMap<String, Float> sortedMap,
			Map<String, Float> recommendations) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			for (String key : sortedMap.keySet()) {
				writer.append(key + "\t" + recommendations.get(key));
				writer.newLine();
				writer.flush();
			}
			writer.close();
		} catch (IOException e) {
			log.error("Couldn't write file " + filename, e);
		}
	}

	public void writeUsagePatterns(String filename, Map<String, Float> patterns, int numOfRecs) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			int count = 0;
			for (Map.Entry<String, Float> pattern : patterns.entrySet()) {
				writer.append(pattern.getKey() + "\t" + pattern.getValue());
				writer.newLine();
				writer.flush();
				count++;
				if (count > numOfRecs)
					break;
			}
		} catch (IOException e) {
			log.error("Couldn't write file " + filename, e);
		}
	}
}
