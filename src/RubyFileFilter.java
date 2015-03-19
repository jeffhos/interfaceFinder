import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.filefilter.IOFileFilter;

public class RubyFileFilter implements IOFileFilter {

	private boolean keep(File arg0, String arg1) {
//		ArrayList<String> dirsToIgnore = new ArrayList<String>();
//		dirsToIgnore.add("db");
//		dirsToIgnore.add("spec");
//		dirsToIgnore.add("test");
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
		String path = arg0.getAbsolutePath();
		return (arg0.isFile() && path.contains(".rb") && !path.contains("/db/") && !path.contains("/spec/") && !path.contains("/test/") && !path.contains("/features/"));
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

