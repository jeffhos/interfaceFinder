import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml3;

public class Main {

	public static void main(String[] args) throws IOException {

		String path = "/Users/lee/upmc/code/engage6400";		

		Collection<File> rubyFiles = getRubyFiles(new File(path));

		ArrayList epicModules = getEpicModules(rubyFiles);
		//log(epicModules);
		
		ArrayList<String> possibleMatches = getPossibleMatches(rubyFiles);

		ArrayList<String> definiteMatches = removeModulesFromPossibleMatches(possibleMatches, epicModules);

		log(definiteMatches);
		
		writeToDisk(definiteMatches);

	}
	
	public static void writeToDisk(ArrayList<String> matches) throws IOException {
		File fout = new File("out.html");
		FileOutputStream fos = new FileOutputStream(fout);
	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	 
		bw.write("<!DOCTYPE html>");
		bw.write("<html lang=\"en\"");
		bw.write("<head>");
		bw.write("<meta charset=\"utf-8\">");
		
		bw.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		bw.write("<title>Interfaces</title>");
		bw.write("<style>body {font-weight:bold; font-size: 1.2em; } div{padding: 8px;} body div:nth-child(even) { background-color:#dddddd;}</style>");
		
		bw.write("</head>");
		bw.write("<body>");
		for (int i=0; i< matches.size(); i++) {
			String match = matches.get(i);
			String headerTag = "<div>";
//			if(i%2==0){
//				headerTag = "<div class=>";
//			}
			bw.write(headerTag+escapeHtml3(match)+"</div>");
			//bw.newLine();
		}
		
		bw.write("</body>");
		bw.write("</html>");
		bw.close();
	}

	private static ArrayList<String> removeModulesFromPossibleMatches(
			ArrayList<String> possibleMatches, ArrayList<String> epicModules) {
		ArrayList<String> matches = new ArrayList<String>();
		for(String possibleMatch : possibleMatches) {
			boolean keep = true;
			if(possibleMatch.contains("GoldenKey::Epic")){
				matches.add(possibleMatch.substring(possibleMatch.indexOf("GoldenKey::Epic")));
			}
			else
			{

				for(String module : epicModules) {		

					String moduleInvocation = "Epic::"+module;

					if(possibleMatch.contains(moduleInvocation)) {
						keep = false;	
						break;
					}

				}

				if(keep && !matches.contains(possibleMatch)) {
					matches.add(possibleMatch.substring(possibleMatch.indexOf("Epic::")));					
				}
			}
		}

		List<String> sub = matches.subList(1, matches.size());
		Collections.sort(sub);

		return new ArrayList<String>(sub);
	}

	private static Collection<File> getRubyFiles(File dir) {
		Collection<File> files = FileUtils.listFiles(
				dir, 
				new RubyFileFilter(),
				TrueFileFilter.INSTANCE
				);

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

	private static ArrayList<String> getPossibleMatches(Collection<File> rubyFiles) throws IOException {
		ArrayList<String> possibleMatches = new ArrayList<String>();		

		String originalPath = rubyFiles.iterator().next().getPath();
		String filePath = originalPath.substring(originalPath.indexOf("engage6400"));
		for(File rubyFile : rubyFiles) {
			List<String> lines = FileUtils.readLines(rubyFile);
			for(int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				if((line.contains("Epic::") || line.contains("GoldenKey::Epic"))  && !line.contains("Epic::Error") && !line.contains("< Epic::") && !line.contains("Epic::Allergy") && !line.contains("Epic::Wrapper") && !line.contains("Epic::Interconnect.new") && !line.contains("Epic::STRUCTURES")){
					possibleMatches.add(line + " line " + (i + 1) + " in file: " + filePath);
				}
			}
		}

		return possibleMatches;
	}

	private static void log(ArrayList<String> list) {

		for(String string : list) {

			System.out.println(string);
		}				
	}
}


