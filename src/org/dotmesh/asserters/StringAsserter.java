/**
 * 
 */
package org.dotmesh.asserters;

public interface StringAsserter {
	public boolean isEmpty();

	public boolean contentEquals(StringBuffer sb);

	public boolean contentEquals(CharSequence cs);

	public boolean equalsIgnoreCase(String anotherString);

	public boolean regionMatches(int toffset, String other, int ooffset, int len);

	public boolean regionMatches(boolean ignoreCase, int toffset, String other,
			int ooffset, int len);

	public boolean startsWith(String prefix, int toffset);
	
	public boolean startsWith(String prefix);

	public boolean endsWith(String suffix);
	
	public boolean matches(String regex);
	
	public boolean contains(CharSequence s);
}