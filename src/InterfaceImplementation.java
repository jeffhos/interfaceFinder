import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml3;

import java.util.ArrayList;

public class InterfaceImplementation implements Comparable<InterfaceImplementation>{
	public String Usage;
	public String FileName;
	public int LineNumber;
	public String CleanName = "not set";

	private String mGkString = "GoldenKey::Epic";
	private String mEpicString = "Epic::";

	public String getUsage() {
		return Usage;
	}
	public void setUsage(String usage) {
		Usage = usage;
	}
	public String getFileName() {
		return FileName;
	}
	public void setFileName(String fileName) {
		FileName = fileName;
	}
	public int getLineNumber() {
		return LineNumber;
	}
	public void setLineNumber(int lineNumber) {
		LineNumber = lineNumber;
	}
	public String getCleanName() {
		CleanName = Usage;
		int startIndex = -1;

		if(isGk()) {
			startIndex = Usage.indexOf(mGkString);
		}
		else {
			startIndex = Usage.indexOf(mEpicString);
		}

		if(startIndex>=0) {
			CleanName = Usage.substring(startIndex);
		}		

		int parenIndex = CleanName.indexOf("(");
		if(parenIndex >= 0) {
			System.out.println(parenIndex);
			System.out.println(CleanName);
			CleanName = CleanName.substring(0, parenIndex);
			System.out.println(CleanName);
		}

		return CleanName;
	}
	public boolean isGk() {
		return Usage.indexOf(mGkString) >= 0;
	}
	public boolean isEpic() {
		return Usage.indexOf(mEpicString) >= 0;
	}
	public String getHtml() {
		return getRow(getCol(getCleanName())+getCol(FileName)+getCol(LineNumber));
	}
	public String getRow(String content) {		
		return "<tr>"+content+"</tr>"; 
	}
	public String getCol(Object content) {		
		return "<td>"+escapeHtml3(content.toString())+"</td>"; 
	}
	@Override
	public int compareTo(InterfaceImplementation other) {
		return this.getCleanName().compareTo(other.getCleanName());
	}
	public static boolean lineIsAnInterfaceImplementation(String line) {
		ArrayList<String> stuffToIgnore = new ArrayList<String>();
		stuffToIgnore.add("Epic::Error");
		stuffToIgnore.add("< Epic::");
		stuffToIgnore.add("Epic::Allergy");
		stuffToIgnore.add("Epic::Wrapper");
		stuffToIgnore.add("Epic::Interconnect.new");
		stuffToIgnore.add("Epic::STRUCTURES");
		//return !line.contains("Epic::Error") && !line.contains("< Epic::") && !line.contains("Epic::Allergy") && !line.contains("Epic::Wrapper") && !line.contains("Epic::Interconnect.new") && !line.contains("Epic::STRUCTURES");
		
		for(String ignore : stuffToIgnore) {
			if(line.contains(ignore)){
				return false;
			}
		}
		return true;
	}
}


