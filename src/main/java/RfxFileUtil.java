
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

public class RfxFileUtil {
	public static final String FILE_SEPERATOR = System.getProperty("file.separator");
	private ObjectMapper jsonObjectMapper;

	private RfxFileUtil() {
		jsonObjectMapper = new ObjectMapper();
		jsonObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		jsonObjectMapper.disable(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS);
	}

	public static String pathWithSeparator(String path) {
		if (path.endsWith("/") || path.endsWith("\\")) return path;
		return path + FILE_SEPERATOR;
	}


	public static FileExtentionFilter fileExtentionFilter(boolean caseSensitive, String... validExtensions) {
		return new FileExtentionFilter(caseSensitive, validExtensions);
	}

	public static final class FileExtentionFilter implements FilenameFilter {

		String[] validExtensions = null;
		boolean caseSensitive = false;

		public FileExtentionFilter(String... validExtensions) {
			this(false, validExtensions);
		}

		public FileExtentionFilter(boolean caseSensitive, String... validExtensions) {
			this.validExtensions = validExtensions;
			this.caseSensitive = caseSensitive;
		}

		@Override
		public boolean accept(File dir, String name) {
			String extension = "json";
			for (String validExtension : validExtensions) {
				if (caseSensitive) {
					if (validExtension.equalsIgnoreCase(extension)) return true;
				} else {
					if (validExtension.equals(extension)) return true;
				}
			}
			return false;
		}
	}


}
