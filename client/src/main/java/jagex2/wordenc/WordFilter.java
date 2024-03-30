package jagex2.wordenc;

import jagex2.io.Jagfile;
import jagex2.io.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public class WordFilter {

    private static int[] fragments;

    private static char[][] badWords;

    private static byte[][][] badCombinations;

    private static char[][] domains;

    private static char[][] tlds;

    private static int[] tldType;

    private static final String[] ALLOWLIST = new String[] { "cook", "cook's", "cooks", "seeks", "sheet" };

    public static void unpack( Jagfile jag) {
		Packet fragments = new Packet(jag.read("fragmentsenc.txt", null));
		Packet bad = new Packet(jag.read("badenc.txt", null));
		Packet domain = new Packet(jag.read("domainenc.txt", null));
		Packet tld = new Packet(jag.read("tldlist.txt", null));
		read(bad, domain, fragments, tld);
	}

    private static void read( Packet bad, Packet domain, Packet fragments, Packet tld) {
		readBadWords(bad);
		readDomains(domain);
		readFragments(fragments);
		readTld(tld);
	}

    private static void readTld( Packet buf) {
		int count = buf.g4();
		tlds = new char[count][];
		tldType = new int[count];
		for ( int i = 0; i < count; i++) {
			tldType[i] = buf.g1();
			char[] tld = new char[buf.g1()];
			for ( int j = 0; j < tld.length; j++) {
				tld[j] = (char) buf.g1();
			}
			tlds[i] = tld;
		}
	}

    private static void readBadWords( Packet buf) {
		int count = buf.g4();
		badWords = new char[count][];
		badCombinations = new byte[count][][];
		readBadCombinations(buf, badWords, badCombinations);
	}

    private static void readDomains( Packet buf) {
		int count = buf.g4();
		domains = new char[count][];
		readDomain(buf, domains);
	}

    private static void readFragments( Packet buf) {
		fragments = new int[buf.g4()];
		for ( int i = 0; i < fragments.length; i++) {
			fragments[i] = buf.g2();
		}
	}

    private static void readBadCombinations( Packet buf, char[][] badwords, byte[][][] badCombinations) {
		for ( int i = 0; i < badwords.length; i++) {
			char[] badword = new char[buf.g1()];
			for ( int j = 0; j < badword.length; j++) {
				badword[j] = (char) buf.g1();
			}
			badwords[i] = badword;
			byte[][] combination = new byte[buf.g1()][2];
			for ( int j = 0; j < combination.length; j++) {
				combination[j][0] = (byte) buf.g1();
				combination[j][1] = (byte) buf.g1();
			}
			if (combination.length > 0) {
				badCombinations[i] = combination;
			}
		}
	}

    private static void readDomain( Packet buf, char[][] domains) {
		for ( int i = 0; i < domains.length; i++) {
			char[] domain = new char[buf.g1()];
			for ( int j = 0; j < domain.length; j++) {
				domain[j] = (char) buf.g1();
			}
			domains[i] = domain;
		}
	}

    private static void filterCharacters( char[] in) {
		int pos = 0;
		for ( int i = 0; i < in.length; i++) {
			if (allowCharacter(in[i])) {
				in[pos] = in[i];
			} else {
				in[pos] = ' ';
			}
			if (pos == 0 || in[pos] != ' ' || in[pos - 1] != ' ') {
				pos++;
			}
		}
		for ( int i = pos; i < in.length; i++) {
			in[i] = ' ';
		}
	}

    private static boolean allowCharacter( char c) {
		return c >= ' ' && c <= '\u007f' || c == ' ' || c == '\n' || c == '\t' || c == '£' || c == '€';
	}

    public static String filter( String input) {
		long start = System.currentTimeMillis();
		char[] outputPre = input.toCharArray();
		filterCharacters(outputPre);
		String trimmed = (new String(outputPre)).trim();
		char[] output = trimmed.toLowerCase().toCharArray();
		String lowercase = trimmed.toLowerCase();
		filterTld(output);
		filterBad(output);
		filterDomains(output);
		filterFragments(output);
		int j;
		for ( int i = 0; i < ALLOWLIST.length; i++) {
			j = -1;
			while ((j = lowercase.indexOf(ALLOWLIST[i], j + 1)) != -1) {
				char[] allowed = ALLOWLIST[i].toCharArray();
				System.arraycopy(allowed, 0, output, 0 + j, allowed.length);
			}
		}
		replaceUpperCases(output, trimmed.toCharArray());
		formatUpperCases(output);
		long end = System.currentTimeMillis();
		return (new String(output)).trim();
	}

    private static void replaceUpperCases( char[] in, char[] unfiltered) {
		for ( int i = 0; i < unfiltered.length; i++) {
			if (in[i] != '*' && isUpperCase(unfiltered[i])) {
				in[i] = unfiltered[i];
			}
		}
	}

    private static void formatUpperCases( char[] in) {
		boolean upper = true;
		for ( int i = 0; i < in.length; i++) {
			char c = in[i];
			if (!isAlpha(c)) {
				upper = true;
			} else if (upper) {
				if (isLowerCase(c)) {
					upper = false;
				}
			} else if (isUpperCase(c)) {
				in[i] = (char) (c + 'a' - 65);
			}
		}
	}

    private static void filterBad( char[] in) {
		for ( int passes = 0; passes < 2; passes++) {
			for ( int i = badWords.length - 1; i >= 0; i--) {
				filter(badCombinations[i], in, badWords[i]);
			}
		}
	}

    private static void filterDomains( char[] in) {
		char[] filteredAt = (char[]) in.clone();
		char[] at = new char[] { '(', 'a', ')' };
		filter(null, filteredAt, at);
		char[] filteredDot = (char[]) in.clone();
		char[] dot = new char[] { 'd', 'o', 't' };
		filter(null, filteredDot, dot);
		for ( int i = domains.length - 1; i >= 0; i--) {
			filterDomain(filteredDot, filteredAt, domains[i], in);
		}
	}

    private static void filterDomain( char[] filteredDot, char[] filteredAt, char[] domain, char[] in) {
		if (domain.length <= in.length) {
			int stride;
			for ( int start = 0; start <= in.length - domain.length; start += stride) {
				int end = start;
				int offset = 0;
				stride = 1;
				boolean match;
				filter_pass:
				while (true) {
					while (true) {
						if (end >= in.length) {
							break filter_pass;
						}
						match = false;
						char b = in[end];
						char c = 0;
						if (end + 1 < in.length) {
							c = in[end + 1];
						}
						int charSize;
						if (offset < domain.length && (charSize = getEmulatedDomainCharSize(c, domain[offset], b)) > 0) {
							end += charSize;
							offset++;
						} else {
							if (offset == 0) {
								break filter_pass;
							}
							int charSize2;
							if ((charSize2 = getEmulatedDomainCharSize(c, domain[offset - 1], b)) > 0) {
								end += charSize2;
								if (offset == 1) {
									stride++;
								}
							} else {
								if (offset >= domain.length || !isSymbol(b)) {
									break filter_pass;
								}
								end++;
							}
						}
					}
				}
				if (offset >= domain.length) {
					match = false;
					int atFilter = getDomainAtFilterStatus(start, in, filteredAt);
					int dotFilter = getDomainDotFilterStatus(in, filteredDot, end - 1);
					if (atFilter > 2 || dotFilter > 2) {
						match = true;
					}
					if (match) {
						for ( int i = start; i < end; i++) {
							in[i] = '*';
						}
					}
				}
			}
		}
	}

    private static int getDomainAtFilterStatus( int end, char[] a, char[] b) {
		if (end == 0) {
			return 2;
		}
		for ( int i = end - 1; i >= 0 && isSymbol(a[i]); i--) {
			if (a[i] == '@') {
				return 3;
			}
		}
		int asteriskCount = 0;
		for ( int i = end - 1; i >= 0 && isSymbol(b[i]); i--) {
			if (b[i] == '*') {
				asteriskCount++;
			}
		}
		if (asteriskCount >= 3) {
			return 4;
		} else if (isSymbol(a[end - 1])) {
			return 1;
		} else {
			return 0;
		}
	}

    private static int getDomainDotFilterStatus( char[] a, char[] b, int start) {
		if (start + 1 == a.length) {
			return 2;
		} else {
			int i = start + 1;
			while (true) {
				if (i < a.length && isSymbol(a[i])) {
					if (a[i] != '.' && a[i] != ',') {
						i++;
						continue;
					}
					return 3;
				}
				int asteriskCount = 0;
				for ( int j = start + 1; j < a.length && isSymbol(b[j]); j++) {
					if (b[j] == '*') {
						asteriskCount++;
					}
				}
				if (asteriskCount >= 3) {
					return 4;
				}
				if (isSymbol(a[start + 1])) {
					return 1;
				}
				return 0;
			}
		}
	}

    private static void filterTld( char[] in) {
		char[] filteredDot = (char[]) in.clone();
		char[] dot = new char[] { 'd', 'o', 't' };
		filter(null, filteredDot, dot);
		char[] filteredSlash = (char[]) in.clone();
		char[] slash = new char[] { 's', 'l', 'a', 's', 'h' };
		filter(null, filteredSlash, slash);
		for ( int i = 0; i < tlds.length; i++) {
			filterTld(filteredSlash, tldType[i], in, tlds[i], filteredDot);
		}
	}

    private static void filterTld( char[] filteredSlash, int type, char[] chars, char[] tld, char[] filteredDot) {
		int stride;
		if (tld.length <= chars.length) {
			boolean compare = true;
			for ( int start = 0; start <= chars.length - tld.length; start += stride) {
				int end = start;
				int offset = 0;
				stride = 1;
				boolean match;
				filter_pass:
				while (true) {
					while (true) {
						if (end >= chars.length) {
							break filter_pass;
						}
						match = false;
						char b = chars[end];
						char c = 0;
						if (end + 1 < chars.length) {
							c = chars[end + 1];
						}
						int charLen;
						if (offset < tld.length && (charLen = getEmulatedDomainCharSize(c, tld[offset], b)) > 0) {
							end += charLen;
							offset++;
						} else {
							if (offset == 0) {
								break filter_pass;
							}
							int charLen2;
							if ((charLen2 = getEmulatedDomainCharSize(c, tld[offset - 1], b)) > 0) {
								end += charLen2;
								if (offset == 1) {
									stride++;
								}
							} else {
								if (offset >= tld.length || !isSymbol(b)) {
									break filter_pass;
								}
								end++;
							}
						}
					}
				}
				if (offset >= tld.length) {
					match = false;
					int status0 = getTldDotFilterStatus(chars, filteredDot, start);
					int status1 = getTldSlashFilterStatus(filteredSlash, end - 1, chars);
					if (type == 1 && status0 > 0 && status1 > 0) {
						match = true;
					}
					if (type == 2 && (status0 > 2 && status1 > 0 || status0 > 0 && status1 > 2)) {
						match = true;
					}
					if (type == 3 && status0 > 0 && status1 > 2) {
						match = true;
					}
					boolean lastCheck = type == 3 && status0 > 2 && status1 > 0;
					if (match) {
						int first = start;
						int last = end - 1;
						boolean findStart;
						int i;
						if (status0 > 2) {
							if (status0 == 4) {
								findStart = false;
								for (i = start - 1; i >= 0; i--) {
									if (findStart) {
										if (filteredDot[i] != '*') {
											break;
										}
										first = i;
									} else if (filteredDot[i] == '*') {
										first = i;
										findStart = true;
									}
								}
							}
							findStart = false;
							for (i = first - 1; i >= 0; i--) {
								if (findStart) {
									if (isSymbol(chars[i])) {
										break;
									}
									first = i;
								} else if (!isSymbol(chars[i])) {
									findStart = true;
									first = i;
								}
							}
						}
						if (status1 > 2) {
							if (status1 == 4) {
								findStart = false;
								for (i = last + 1; i < chars.length; i++) {
									if (findStart) {
										if (filteredSlash[i] != '*') {
											break;
										}
										last = i;
									} else if (filteredSlash[i] == '*') {
										last = i;
										findStart = true;
									}
								}
							}
							findStart = false;
							for (i = last + 1; i < chars.length; i++) {
								if (findStart) {
									if (isSymbol(chars[i])) {
										break;
									}
									last = i;
								} else if (!isSymbol(chars[i])) {
									findStart = true;
									last = i;
								}
							}
						}
						for ( int j = first; j <= last; j++) {
							chars[j] = '*';
						}
					}
				}
			}
		}
	}

    private static int getTldDotFilterStatus( char[] a, char[] b, int start) {
		if (start == 0) {
			return 2;
		}
		int i = start - 1;
		while (true) {
			if (i >= 0 && isSymbol(a[i])) {
				if (a[i] != ',' && a[i] != '.') {
					i--;
					continue;
				}
				return 3;
			}
			int asteriskCount = 0;
			int j;
			for (j = start - 1; j >= 0 && isSymbol(b[j]); j--) {
				if (b[j] == '*') {
					asteriskCount++;
				}
			}
			if (asteriskCount >= 3) {
				return 4;
			}
			if (isSymbol(a[start - 1])) {
				return 1;
			}
			return 0;
		}
	}

    private static int getTldSlashFilterStatus( char[] b, int end, char[] a) {
		if (end + 1 == a.length) {
			return 2;
		}
		int i = end + 1;
		while (true) {
			if (i < a.length && isSymbol(a[i])) {
				if (a[i] != '\\' && a[i] != '/') {
					i++;
					continue;
				}
				return 3;
			}
			int asteriskCount = 0;
			for ( int j = end + 1; j < a.length && isSymbol(b[j]); j++) {
				if (b[j] == '*') {
					asteriskCount++;
				}
			}
			if (asteriskCount >= 5) {
				return 4;
			}
			if (isSymbol(a[end + 1])) {
				return 1;
			}
			return 0;
		}
	}

    private static void filter( byte[][] badCombinations, char[] chars, char[] fragment) {
		if (fragment.length <= chars.length) {
			boolean compare = true;
			int stride;
			for ( int start = 0; start <= chars.length - fragment.length; start += stride) {
				int end = start;
				int fragOff = 0;
				int iterations = 0;
				stride = 1;
				boolean isSymbol = false;
				boolean isEmulated = false;
				boolean isNumeral = false;
				boolean bad;
				char b;
				char c;
				label163:
				while (true) {
					while (true) {
						if (end >= chars.length || isEmulated && isNumeral) {
							break label163;
						}
						bad = false;
						b = chars[end];
						c = '\0';
						if (end + 1 < chars.length) {
							c = chars[end + 1];
						}
						int charLen;
						if (fragOff < fragment.length && (charLen = getEmulatedSize(c, fragment[fragOff], b)) > 0) {
							if (charLen == 1 && isNumber(b)) {
								isEmulated = true;
							}
							if (charLen == 2 && (isNumber(b) || isNumber(c))) {
								isEmulated = true;
							}
							end += charLen;
							fragOff++;
						} else {
							if (fragOff == 0) {
								break label163;
							}
							int charLen2;
							if ((charLen2 = getEmulatedSize(c, fragment[fragOff - 1], b)) > 0) {
								end += charLen2;
								if (fragOff == 1) {
									stride++;
								}
							} else {
								if (fragOff >= fragment.length || !isLowerCaseAlpha(b)) {
									break label163;
								}
								if (isSymbol(b) && b != '\'') {
									isSymbol = true;
								}
								if (isNumber(b)) {
									isNumeral = true;
								}
								end++;
								iterations++;
								if (iterations * 100 / (end - start) > 90) {
									break label163;
								}
							}
						}
					}
				}
				if (fragOff >= fragment.length && (!isEmulated || !isNumeral)) {
					bad = true;
					int cur;
					if (isSymbol) {
						boolean badCurrent = false;
						boolean badNext = false;
						if (start - 1 < 0 || isSymbol(chars[start - 1]) && chars[start - 1] != '\'') {
							badCurrent = true;
						}
						if (end >= chars.length || isSymbol(chars[end]) && chars[end] != '\'') {
							badNext = true;
						}
						if (!badCurrent || !badNext) {
							boolean good = false;
							cur = start - 2;
							if (badCurrent) {
								cur = start;
							}
							while (!good && cur < end) {
								if (cur >= 0 && (!isSymbol(chars[cur]) || chars[cur] == '\'')) {
									char[] frag = new char[3];
									int off;
									for (off = 0; off < 3 && cur + off < chars.length && (!isSymbol(chars[cur + off]) || chars[cur + off] == '\''); off++) {
										frag[off] = chars[cur + off];
									}
									boolean valid = off != 0;
									if (off < 3 && cur - 1 >= 0 && (!isSymbol(chars[cur - 1]) || chars[cur - 1] == '\'')) {
										valid = false;
									}
									if (valid && !isBadFragment(frag)) {
										good = true;
									}
								}
								cur++;
							}
							if (!good) {
								bad = false;
							}
						}
					} else {
						b = ' ';
						if (start - 1 >= 0) {
							b = chars[start - 1];
						}
						c = ' ';
						if (end < chars.length) {
							c = chars[end];
						}
						byte bIndex = getIndex(b);
						byte cIndex = getIndex(c);
						if (badCombinations != null && comboMatches(bIndex, badCombinations, cIndex)) {
							bad = false;
						}
					}
					if (bad) {
						int numeralCount = 0;
						int alphaCount = 0;
						for ( int n = start; n < end; n++) {
							if (isNumber(chars[n])) {
								numeralCount++;
							} else if (isAlpha(chars[n])) {
								alphaCount++;
							}
						}
						if (numeralCount <= alphaCount) {
							for (cur = start; cur < end; cur++) {
								chars[cur] = '*';
							}
						}
					}
				}
			}
		}
	}

    private static boolean comboMatches( byte a, byte[][] combos, byte b) {
		int first = 0;
		if (combos[first][0] == a && combos[first][1] == b) {
			return true;
		}
		int last = combos.length - 1;
		if (combos[last][0] == a && combos[last][1] == b) {
			return true;
		}
		do {
			int middle = (first + last) / 2;
			if (combos[middle][0] == a && combos[middle][1] == b) {
				return true;
			}
			if (a < combos[middle][0] || a == combos[middle][0] && b < combos[middle][1]) {
				last = middle;
			} else {
				first = middle;
			}
		} while (first != last && first + 1 != last);
		return false;
	}

    private static int getEmulatedDomainCharSize( char c, char a, char b) {
		if (a == b) {
			return 1;
		} else if (a == 'o' && b == '0') {
			return 1;
		} else if (a == 'o' && b == '(' && c == ')') {
			return 2;
		} else if (a == 'c' && (b == '(' || b == '<' || b == '[')) {
			return 1;
		} else if (a == 'e' && b == '€') {
			return 1;
		} else if (a == 's' && b == '$') {
			return 1;
		} else if (a == 'l' && b == 'i') {
			return 1;
		} else {
			return 0;
		}
	}

    private static int getEmulatedSize( char c, char a, char b) {
		if (a == b) {
			return 1;
		}
		if (a >= 'a' && a <= 'm') {
			if (a == 'a') {
				if (b != '4' && b != '@' && b != '^') {
					if (b == '/' && c == '\\') {
						return 2;
					}
					return 0;
				}
				return 1;
			}
			if (a == 'b') {
				if (b != '6' && b != '8') {
					if (b == '1' && c == '3') {
						return 2;
					}
					return 0;
				}
				return 1;
			}
			if (a == 'c') {
				if (b != '(' && b != '<' && b != '{' && b != '[') {
					return 0;
				}
				return 1;
			}
			if (a == 'd') {
				if (b == '[' && c == ')') {
					return 2;
				}
				return 0;
			}
			if (a == 'e') {
				if (b != '3' && b != '€') {
					return 0;
				}
				return 1;
			}
			if (a == 'f') {
				if (b == 'p' && c == 'h') {
					return 2;
				}
				if (b == '£') {
					return 1;
				}
				return 0;
			}
			if (a == 'g') {
				if (b != '9' && b != '6') {
					return 0;
				}
				return 1;
			}
			if (a == 'h') {
				if (b == '#') {
					return 1;
				}
				return 0;
			}
			if (a == 'i') {
				if (b != 'y' && b != 'l' && b != 'j' && b != '1' && b != '!' && b != ':' && b != ';' && b != '|') {
					return 0;
				}
				return 1;
			}
			if (a == 'j') {
				return 0;
			}
			if (a == 'k') {
				return 0;
			}
			if (a == 'l') {
				if (b != '1' && b != '|' && b != 'i') {
					return 0;
				}
				return 1;
			}
			if (a == 'm') {
				return 0;
			}
		}
		if (a >= 'n' && a <= 'z') {
			if (a == 'n') {
				return 0;
			}
			if (a == 'o') {
				if (b != '0' && b != '*') {
					if ((b != '(' || c != ')') && (b != '[' || c != ']') && (b != '{' || c != '}') && (b != '<' || c != '>')) {
						return 0;
					}
					return 2;
				}
				return 1;
			}
			if (a == 'p') {
				return 0;
			}
			if (a == 'q') {
				return 0;
			}
			if (a == 'r') {
				return 0;
			}
			if (a == 's') {
				if (b != '5' && b != 'z' && b != '$' && b != '2') {
					return 0;
				}
				return 1;
			}
			if (a == 't') {
				if (b != '7' && b != '+') {
					return 0;
				}
				return 1;
			}
			if (a == 'u') {
				if (b == 'v') {
					return 1;
				}
				if (b == '\\' && c == '/' || b == '\\' && c == '|' || b == '|' && c == '/') {
					return 2;
				}
				return 0;
			}
			if (a == 'v') {
				if ((b != '\\' || c != '/') && (b != '\\' || c != '|') && (b != '|' || c != '/')) {
					return 0;
				}
				return 2;
			}
			if (a == 'w') {
				if (b == 'v' && c == 'v') {
					return 2;
				}
				return 0;
			}
			if (a == 'x') {
				if ((b != ')' || c != '(') && (b != '}' || c != '{') && (b != ']' || c != '[') && (b != '>' || c != '<')) {
					return 0;
				}
				return 2;
			}
			if (a == 'y') {
				return 0;
			}
			if (a == 'z') {
				return 0;
			}
		}
		if (a >= '0' && a <= '9') {
			if (a == '0') {
				if (b == 'o' || b == 'O') {
					return 1;
				} else if ((b != '(' || c != ')') && (b != '{' || c != '}') && (b != '[' || c != ']')) {
					return 0;
				} else {
					return 2;
				}
			} else if (a == '1') {
				return b == 'l' ? 1 : 0;
			} else {
				return 0;
			}
		} else if (a == ',') {
			return b == '.' ? 1 : 0;
		} else if (a == '.') {
			return b == ',' ? 1 : 0;
		} else if (a == '!') {
			return b == 'i' ? 1 : 0;
		} else {
			return 0;
		}
	}

    private static byte getIndex( char c) {
		if (c >= 'a' && c <= 'z') {
			return (byte) (c + 1 - 'a');
		} else if (c == '\'') {
			return 28;
		} else if (c >= '0' && c <= '9') {
			return (byte) (c + 29 - '0');
		} else {
			return 27;
		}
	}

    private static void filterFragments( char[] chars) {
		boolean compare = false;
		int end = 0;
		int count = 0;
		int start = 0;
		while (true) {
			do {
				int index;
				if ((index = indexOfNumber(chars, end)) == -1) {
					return;
				}
				boolean foundLowercase = false;
				for ( int i = end; i >= 0 && i < index && !foundLowercase; i++) {
					if (!isSymbol(chars[i]) && !isLowerCaseAlpha(chars[i])) {
						foundLowercase = true;
					}
				}
				if (foundLowercase) {
					count = 0;
				}
				if (count == 0) {
					start = index;
				}
				end = indexOfNonNumber(index, chars);
				int value = 0;
				for ( int i = index; i < end; i++) {
					value = value * 10 + chars[i] - 48;
				}
				if (value <= 255 && end - index <= 8) {
					count++;
				} else {
					count = 0;
				}
			} while (count != 4);
			for ( int i = start; i < end; i++) {
				chars[i] = '*';
			}
			count = 0;
		}
	}

    private static int indexOfNumber( char[] input, int off) {
		for ( int i = off; i < input.length && i >= 0; i++) {
			if (input[i] >= '0' && input[i] <= '9') {
				return i;
			}
		}
		return -1;
	}

    private static int indexOfNonNumber( int off, char[] input) {
		int i = off;
		while (true) {
			if (i < input.length && i >= 0) {
				if (input[i] >= '0' && input[i] <= '9') {
					i++;
					continue;
				}
				return i;
			}
			return input.length;
		}
	}

    private static boolean isSymbol( char c) {
		return !isAlpha(c) && !isNumber(c);
	}

    private static boolean isLowerCaseAlpha( char c) {
		if (c >= 'a' && c <= 'z') {
			return c == 'v' || c == 'x' || c == 'j' || c == 'q' || c == 'z';
		} else {
			return true;
		}
	}

    private static boolean isAlpha( char c) {
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
	}

    private static boolean isNumber( char c) {
		return c >= '0' && c <= '9';
	}

    private static boolean isLowerCase( char c) {
		return c >= 'a' && c <= 'z';
	}

    private static boolean isUpperCase( char c) {
		return c >= 'A' && c <= 'Z';
	}

    private static boolean isBadFragment( char[] input) {
		boolean skip = true;
		for ( int i = 0; i < input.length; i++) {
			if (!isNumber(input[i]) && input[i] != '\0') {
				skip = false;
				break;
			}
		}
		if (skip) {
			return true;
		}
		int i = firstFragmentId(input);
		int start = 0;
		int end;
		end = fragments.length - 1;
		if (i == fragments[start] || i == fragments[end]) {
			return true;
		}
		do {
			int middle = (start + end) / 2;
			if (i == fragments[middle]) {
				return true;
			}
			if (i < fragments[middle]) {
				end = middle;
			} else {
				start = middle;
			}
		} while (start != end && start + 1 != end);
		return false;
	}

    private static int firstFragmentId( char[] chars) {
		if (chars.length > 6) {
			return 0;
		}
		int value = 0;
		for ( int i = 0; i < chars.length; i++) {
			char c = chars[chars.length - i - 1];
			if (c >= 'a' && c <= 'z') {
				value = value * 38 + c + 1 - 'a';
			} else if (c == '\'') {
				value = value * 38 + 27;
			} else if (c >= '0' && c <= '9') {
				value = value * 38 + c + 28 - '0';
			} else if (c != '\0') {
				return 0;
			}
		}
		return value;
	}
}
