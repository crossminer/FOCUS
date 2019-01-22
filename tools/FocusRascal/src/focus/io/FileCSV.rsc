module focus::io::FileCSV

import lang::csv::IO;
import IO;

bool projectExistsInLogFile(
	str project, 
	loc file=|project://FocusRascal/data/m3/java-projects/log.csv|) {
	
	items = readCSV(#rel[str,str,str], file, header=false);
	if(<name,typ,err> <- items, name == project) {
		return true;
	}
	else {
		return false;
	}
}
