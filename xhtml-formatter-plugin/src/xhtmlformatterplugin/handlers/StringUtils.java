package xhtmlformatterplugin.handlers;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
	
	public static int compare(final String str1, final String str2) {
        return compare(str1, str2, true);
    }
	
	public static int compare(final String str1, final String str2, final boolean nullIsLess) {
        if (str1 == str2) { // NOSONARLINT this intentionally uses == to allow for both null
            return 0;
        }
        if (str1 == null) {
            return nullIsLess ? -1 : 1;
        }
        if (str2 == null) {
            return nullIsLess ? 1 : - 1;
        }
        return str1.compareTo(str2);
    }
	
	
	public static int compareLengthAndThenContent(final String str1, final String str2) {
		if (!!!StringUtils.isAllBlank(str1, str2)) {
			final int delta = str1.length() - str2.length();
			if (delta == 0) {
				return compare(str1, str2);
			}
			else {
				return delta;
			}
		} else {
			return compare(str1, str2);
		}
	}
}
