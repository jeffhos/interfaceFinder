import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.filefilter.IOFileFilter;

public class RubyFileFilter implements IOFileFilter {

	private boolean keep(File file, String arg1) {
		String path = file.getAbsolutePath();
		return file.isFile() && path.contains(".rb") && !shouldWeIgnoreThisFileBasedOnItsPath(path);
	}
	
	private boolean shouldWeIgnoreThisFileBasedOnItsPath(String path) {
		ArrayList<String> stuffToIgnore = new ArrayList<String>();
		stuffToIgnore.add("/db/");
		stuffToIgnore.add("/spec/");
		stuffToIgnore.add("/test/");
		stuffToIgnore.add("/features/");
//		doc
//		features
//		log
//		script
//		test
//		vendor
//		config
//		coverage
//		db
//		public
//		spectmp
		
		for(String ignore : stuffToIgnore) {
			if(path.contains(ignore)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean accept(File arg0) {		
		return keep(arg0, null);
	}

	@Override
	public boolean accept(File arg0, String arg1) {
		return keep(arg0, arg1);
	}

}

