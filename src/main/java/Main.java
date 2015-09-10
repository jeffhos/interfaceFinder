import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml3;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

//git push https://github.com/ICEDLEE337/interfaceFinder.git master


public class Main {

	public static void main(String[] args) throws IOException {
	  if (args.length < 1) {
	    System.err.println("Usage:");
	    System.err.println("  interfaceFinder <source_dir>");
	    System.exit(1);
	  }
		basePath = args[0];
		Collection<File> rubyFiles = getRubyFiles(new File(basePath));
		ArrayList<String> epicModules = getEpicModules(rubyFiles);
		ArrayList<InterfaceImplementation> possibleMatches = getPossibleMatches(rubyFiles);
		ArrayList<InterfaceImplementation> definiteMatches = removeModulesFromPossibleMatches(possibleMatches, epicModules);
		writeToDisk(definiteMatches);
	}

	private static Collection<File> getRubyFiles(File dir) {
		Collection<File> files = FileUtils.listFiles(
				dir, 
				new RubyFileFilter(),
				TrueFileFilter.INSTANCE);

		return files;
	}

	private static ArrayList<String> getEpicModules(Collection<File> rubyFiles) throws IOException {
		ArrayList<String> modules = new ArrayList<String>();
		for(File rubyFile : rubyFiles) {
			List<String> lines = FileUtils.readLines(rubyFile);
			for(int i=0; i < lines.size()-1; i++) {
				String line = lines.get(i);
				String next = lines.get(i+1);
				if(i+1 < lines.size() && line.contains("module Epic")  && (next.contains("class ") || next.contains("module "))){
					String module = next.replace("class ", "").replace("module ", "").replace(" < Epic::Wrapper", "").trim();
					modules.add(module);					
				}
			}
		}
		return modules;
	}

	private static ArrayList<InterfaceImplementation> getPossibleMatches(Collection<File> rubyFiles) throws IOException {
		ArrayList<InterfaceImplementation> matches = new ArrayList<InterfaceImplementation>();		

		for(File rubyFile : rubyFiles) {
			String originalPath = rubyFile.getPath();
			String filePath = originalPath.replaceFirst(basePath, "");
			List<String> lines = FileUtils.readLines(rubyFile);
			for(int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);

				if(InterfaceImplementation.lineIsAPossibleInterfaceImplementation(line)){					
					InterfaceImplementation ii = new InterfaceImplementation();
					ii.LineNumber = i+1;
					ii.FileName = filePath;
					ii.Usage = line;					
					matches.add(ii);
				}
			}
		}

		return matches;
	}

	private static ArrayList<InterfaceImplementation> removeModulesFromPossibleMatches(
			ArrayList<InterfaceImplementation> possibleMatches, 
			ArrayList<String> epicModules) {

		ArrayList<InterfaceImplementation> matches = new ArrayList<InterfaceImplementation>();

		for(InterfaceImplementation possibleMatch : possibleMatches) {
			boolean keep = true;
			if(possibleMatch.isGk()){
				matches.add(possibleMatch);
			}
			else
			{
				if(possibleMatch.isEpic()){ 
					//make sure it's not a class or module definition
					for(String module : epicModules) {		

						String moduleInvocation = "Epic::"+module;

						if(possibleMatch.Usage.contains(moduleInvocation)) {
							keep = false;	
							break;
						}
					}

					if(keep && !matches.contains(possibleMatch)) {
						matches.add(possibleMatch);											
					}
				}				
			}	
		}

		List<InterfaceImplementation> sub = matches.subList(1, matches.size());
		Collections.sort(sub);

		return new ArrayList<InterfaceImplementation>(sub);
	}


	public static void writeToDisk(ArrayList<InterfaceImplementation> matches) throws IOException {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String formattedDate = sdf.format(date);
		
		File outputDir = new File("output");
		outputDir.mkdir();
		String filename = "output/interfaces-"+formattedDate+".csv";
		File fout = new File(filename);
		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		bw.write("Interface,File,Line");
		bw.newLine();
		for (int i=0; i< matches.size(); i++) {
			InterfaceImplementation match = matches.get(i);
			bw.write(match.getCsv());
			bw.newLine();
		}

		bw.close();
	}


	private static void log(ArrayList<String> list) {

		for(String string : list) {

			System.out.println(string);
		}				
	}
	
	private static String basePath;
}



