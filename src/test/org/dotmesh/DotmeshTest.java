package test.org.dotmesh;

import static org.dotmesh.Dotmesh.assertThat;
import static org.dotmesh.Dotmesh.integer;
import static org.dotmesh.Dotmesh.not;
import static org.dotmesh.Dotmesh.string;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

public class DotmeshTest {
	@Test
	public void assertContains() {
		assertThat(Arrays.asList("a", "b", "c")).contains("a");
	}

	@Test
	public void assertMatches() {
		assertThat("abc").matches(".*a.*");
	}

	@Test
	public void assertNotIsEmpty() {
		assertThat(not(Arrays.asList("a", "b", "c"))).isEmpty();
	}

	@Test
	public void notToString() {
		assertThat(string(not(Arrays.asList("a", "b", "c")).toString()))
				.equals("not([a, b, c])");
	}

	@Test
	public void assertDoesntContain() {
		try {
			assertThat(Arrays.asList("a", "b", "c")).contains("d");
		} catch (AssertionError expected) {
			return;
		}
		fail("Should have thrown AssertionError");
	}

	@Test
	public void assertOnSet() {
		assertThat(new HashSet<String>(Arrays.asList("a", "b", "c"))).contains(
				"c");
	}

	@Test
	public void assertThatOnString() {
		assertThat("abc").contains("c");
	}

	@Test
	public void assertThatOnStringFails() {
		try {
			assertThat("abc").contains("d");
		} catch (AssertionError expected) {
			return;
		}
		fail("Should have thrown AssertionError");
	}

	@Test
	public void detailedErrorMessage() {
		try {
			assertThat("abc").contains("defgh");
		} catch (AssertionError expected) {
			assertThat(expected.getMessage()).equals(
					"Failed: <abc>.contains(<defgh>)");
			return;
		}
		fail("Should have thrown AssertionError");
	}

	@Test
	public void detailedErrorMessageIsEmpty() {
		try {
			assertThat(Arrays.asList("a")).isEmpty();
		} catch (AssertionError expected) {
			assertThat(expected.getMessage()).equals("Failed: <[a]>.isEmpty()");
			return;
		}
		fail("Should have thrown AssertionError");
	}

	@Test
	public void detailedErrorMessageMultipleParams() {
		try {
			assertThat("abcde").startsWith("ghi", 1);
		} catch (AssertionError expected) {
			assertThat(expected.getMessage()).equals(
					"Failed: <abcde>.startsWith(<ghi>,<1>)");
			return;
		}
		fail("Should have thrown AssertionError");
	}

	@Test
	public void intNotEquals() {
		assertThat(1).notEquals(2);
	}

	@Test
	public void realIntAsserterNotEquals() {
		assertThat(integer(2).notEquals(2)).equals(false);
	}

	@Test
	public void notOnInteger() {
		assertThat(not(1)).equals(2);
	}
}
