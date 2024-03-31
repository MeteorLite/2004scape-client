package jagex2.datastruct;

public class JString {

    private static final char[] builder = new char[12];

    private static final char[] BASE37_LOOKUP = new char[] {
		'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
		'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
		't', 'u', 'v', 'w', 'x', 'y', 'z',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
	};

    public static long toBase37( String str) {
		long hash = 0L;

		for ( int i = 0; i < str.length() && i < 12; i++) {
			char c = str.charAt(i);
			hash *= 37L;

			if (c >= 'A' && c <= 'Z') {
				hash += c + 1 - 65;
			} else if (c >= 'a' && c <= 'z') {
				hash += c + 1 - 97;
			} else if (c >= '0' && c <= '9') {
				hash += c + 27 - 48;
			}
		}

		while (hash % 37L == 0L && hash != 0L) {
			hash /= 37L;
		}

		return hash;
	}

    public static String fromBase37( long username) {
		// >= 37 to the 12th power
		if (username <= 0L || username >= 6582952005840035281L) {
			return "invalid_name";
		}

		if (username % 37L == 0L) {
			return "invalid_name";
		}

		int len = 0;
		while (username != 0L) {
			long last = username;
			username /= 37L;
			builder[11 - len++] = BASE37_LOOKUP[(int) (last - username * 37L)];
		}

		return new String(builder, 12 - len, len);
	}

    public static long hashCode( String str) {
		String upper = str.toUpperCase();
		long hash = 0L;

		for ( int i = 0; i < upper.length(); i++) {
			hash = hash * 61L + (long) upper.charAt(i) - 32L;
			hash = hash + (hash >> 56) & 0xFFFFFFFFFFFFFFL;
		}

		return hash;
	}

    public static String formatIPv4( int ip) {
		return (ip >> 24 & 0xFF) + "." + (ip >> 16 & 0xFF) + "." + (ip >> 8 & 0xFF) + "." + (ip & 0xFF);
	}

    public static String formatName( String str) {
		if (str.length() == 0) {
			return str;
		}

		char[] chars = str.toCharArray();
		for ( int i = 0; i < chars.length; i++) {
			if (chars[i] == '_') {
				chars[i] = ' ';

				if (i + 1 < chars.length && chars[i + 1] >= 'a' && chars[i + 1] <= 'z') {
					chars[i + 1] = (char) (chars[i + 1] + 'A' - 97);
				}
			}
		}

		if (chars[0] >= 'a' && chars[0] <= 'z') {
			chars[0] = (char) (chars[0] + 'A' - 97);
		}

		return new String(chars);
	}

    public static String toSentenceCase( String str) {
		String lower = str.toLowerCase();
		char[] chars = lower.toCharArray();
		int length = chars.length;
		boolean capitalize = true;

		for ( int i = 0; i < length; i++) {
			char c = chars[i];

			if (capitalize && c >= 'a' && c <= 'z') {
				chars[i] = (char) (chars[i] - 32);
				capitalize = false;
			}

			if (c == '.' || c == '!') {
				capitalize = true;
			}
		}

		return new String(chars);
	}

    public static String toAsterisks( String str) {
		String temp = "";
		for ( int i = 0; i < str.length(); i++) {
			temp = temp + "*";
		}
		return temp;
	}
}
