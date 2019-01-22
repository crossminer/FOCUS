module focus::io::File

import IO;
import String;

bool existFileWithName(loc directory, str name) {
	if(isDirectory(directory)) {
		found = false;
		for(f <- directory.ls) {
			if(isDirectory(f)) {
				found = found || existFileWithName(f, name);
				if(found) {
					return true;
				}
			}
			if(isFile(f) && endsWith(f.path, "/<name>")) {
				return true;
			}
		}
	}
	return false;
}

bool existFileWithExtension(loc directory, str extension) {
	for(f <- directory.ls) {
		if(isDirectory(f)) {
			return existFileWithExtension(f, extension);
		}
		if(isFile(f) && endsWith(f.path, ".<extension>")) {
			return true;
		}
	}
	return false;
}

set[loc] fetchFilesByExtension(loc directory, str extension) {
	files = {};
	if(isDirectory(directory)) {
		for(f <- directory.ls) {
			if(isDirectory(f)) {
				files += fetchFilesByExtension(f, extension);
			}
			if(isFile(f) && endsWith(f.path, ".<extension>")) {
				files += f;
			}
		}
	}
	return files;
}

str getFileName(loc file) {
	index = findLast(file.path, "/") + 1;
	return (index < size(file.path)) ? substring(file.path, index, size(file.path)) : "";
}