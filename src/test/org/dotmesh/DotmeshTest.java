package test.org.dotmesh;

import static org.dotmesh.Dotmesh.affirm;
import static org.dotmesh.Dotmesh.assume;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;

public class DotmeshTest {
	@Test
	public void affirmContains() {
		affirm.that(Arrays.asList("a", "b", "c")).contains("a");
	}

	@Test
	public void affirmMatches() {
		affirm.that("abc").matches(".*a.*");
	}

	@Test
	public void affirmNotIsEmpty() {
		affirm.not().that(Arrays.asList("a", "b", "c")).isEmpty();
	}

	@Test
	public void affirmNotString() {
		affirm.not().that("abc").contains("d");
	}

	@Test
	public void affirmNotClass() {
		affirm.not().that(String.class).isInstance(1);
	}

	@Test
	public void affirmNotToString() {
		try {
			affirm.not().that(Arrays.asList("a", "b", "c")).contains("a");
		} catch (AssertionError expected) {
			affirm.that(expected.getMessage()).equals(
					"Failed: not <[a, b, c]>.contains(<a>)");
			return;
		}
		fail("Should have thrown");
	}

	@Test
	public void affirmDoesntContain() {
		try {
			affirm.that(Arrays.asList("a", "b", "c")).contains("d");
		} catch (AssertionError expected) {
			return;
		}
		fail("Should have thrown AssertionError");
	}

	@Test
	public void affirmOnSet() {
		HashSet<String> set = new HashSet<String>(Arrays.asList("a", "b", "c"));
		affirm.that(set).contains("c");
	}

	@Test
	public void affirmThatOnString() {
		affirm.that("abc").contains("c");
	}

	@Test
	public void affirmThatOnStringFails() {
		try {
			affirm.that("abc").contains("d");
		} catch (AssertionError expected) {
			return;
		}
		fail("Should have thrown AssertionError");
	}

	@Test
	public void detailedAffirmErrorMessage() {
		try {
			affirm.that("abc").contains("defgh");
		} catch (AssertionError expected) {
			String message = expected.getMessage();
			affirm.that(message).equals("Failed: <abc>.contains(<defgh>)");
			return;
		}
		fail("Should have thrown AssertionError");
	}

	@Test
	public void detailedAffirmErrorMessageIsEmpty() {
		try {
			affirm.that(Arrays.asList("a")).isEmpty();
		} catch (AssertionError expected) {
			String message = expected.getMessage();
			affirm.that(message).equals("Failed: <[a]>.isEmpty()");
			return;
		}
		fail("Should have thrown AssertionError");
	}

	@Test
	public void detailedAffirmErrorMessageMultipleParams() {
		try {
			affirm.that("abcde").startsWith("ghi", 1);
		} catch (AssertionError expected) {
			String m = expected.getMessage();
			affirm.that(m).equals("Failed: <abcde>.startsWith(<ghi>,<1>)");
			return;
		}
		fail("Should have thrown AssertionError");
	}

	@Test
	public void detailedAssumeErrorMessageMultipleParams() {
		try {
			assume.that("abcde").startsWith("ghi", 1);
		} catch (AssumptionViolatedException expected) {
			String m = expected.getMessage();
			assume.that(m).equals("Failed: <abcde>.startsWith(<ghi>,<1>)");
			return;
		}
		fail("Should have thrown");
	}

	@Test
	public void assumeNot() {
		try {
			assume.not().that("abcde").startsWith("abc");
		} catch (AssumptionViolatedException expected) {
			String m = expected.getMessage();
			assume.that(m).equals("Failed: <abcde>.startsWith(<ghi>,<1>)");
			return;
		}
		fail("Should have thrown");
	}

	@Test
	public void affirmIntNotEquals() {
		affirm.that(1).notEquals(2);
	}

	@Test
	public void detailedAffirmErrorMessageInts() {
		try {
			affirm.that(1).notEquals(1);
		} catch (AssertionError expected) {
			String m = expected.getMessage();
			affirm.that(m).equals("Failed: <1>.notEquals(<1>)");
			return;
		}
		fail("Should have thrown AssertionError");
	}

	@Test
	public void affirmNotOnInteger() {
		affirm.not().that(1).equals(2);
	}
}
