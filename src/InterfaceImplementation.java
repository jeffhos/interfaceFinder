import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml3;

import java.util.ArrayList;

public class InterfaceImplementation implements Comparable<InterfaceImplementation>{
	public String Usage;
	public String FileName;
	public int LineNumber;
	public String CleanName = "not set";

	private static String mGkString = "GoldenKey::Epic";
	private static String mEpicString = "Epic::";

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
			CleanName = CleanName.substring(0, parenIndex);
			
		}

		return CleanName;
	}
	public boolean isGk() {
		return mIsGk(Usage);
	}
	public boolean isEpic() {
		return mIsEpic(Usage);
	}
	private static boolean mIsGk(String Usage) {
		return Usage.indexOf(mGkString) >= 0;
	}
	private static boolean mIsEpic(String Usage) {
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
		int first = this.getCleanName().compareTo(other.getCleanName());
		if(first==0) {
			int second = this.FileName.compareTo(other.FileName);
			if(second==0) {
				return this.LineNumber == other.LineNumber ? 0 : (this.LineNumber > other.LineNumber ? 1 : -1);				
			}
			else {
				return second;
			}
		}
		else {
			return first;
		}
	}
	public static boolean lineIsAPossibleInterfaceImplementation(String line) {
		ArrayList<String> stuffToIgnore = new ArrayList<String>();
		stuffToIgnore.add("Epic::Error");
		stuffToIgnore.add("< Epic::");
		stuffToIgnore.add("Epic::Wrapper");
		stuffToIgnore.add("Epic::Interconnect.new");
		stuffToIgnore.add("Epic::STRUCTURES");
		stuffToIgnore.add("module_eval");
		stuffToIgnore.add("respond_to");
		//stuffToIgnore.add("Epic.send");
		stuffToIgnore.add("Epic::<Something>");
		stuffToIgnore.add("Epic::<Something>");		
		
		if(!(mIsGk(line) || mIsEpic(line))) {
			return false;
		}
		
		for(String ignore : stuffToIgnore) {
			if(line.contains(ignore)){
				return false;
			}
		}
		
		return true;
	}
}



