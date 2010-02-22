package org.dotmesh;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.dotmesh.asserters.BooleanAsserter;
import org.dotmesh.asserters.IntAsserter;
import org.dotmesh.asserters.StringAsserter;
import org.jmock.api.Invocation;
import org.jmock.api.Invokable;
import org.jmock.internal.ReturnDefaultValueAction;
import org.junit.internal.AssumptionViolatedException;

public class Dotmesh {
	static abstract class FailureNotifier {
		public abstract void failed(String invocationString) throws Throwable;

		<T> Invokable explodeOnFalseInvokable(final T target) {
			return new Invokable() {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					if (!(Boolean) redirect(target).invoke(invocation)) {
						failed(invocationString(target, invocation));
					}
					return new ReturnDefaultValueAction().invoke(invocation);
				}
			};
		}

		private <T> Invokable redirect(final T target) {
			return new Invokable() {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					return invocation.applyTo(target);
				}
			};
		}

		private String invocationString(final Object target, Invocation invocation) {
			StringBuilder b = new StringBuilder();
			b.append("Failed: ");
			appendObject(b, target);
			b.append(".");
			b.append(invocation.getInvokedMethod().getName());
			b.append("(");
			boolean first = true;
			Object[] params = invocation.getParametersAsArray();
			for (Object param : params) {
				if (first) {
					first = false;
				} else {
					b.append(",");
				}
				appendObject(b, param);
			}
			b.append(")");
			String string = b.toString();
			return string;
		}

		private void appendObject(StringBuilder b, final Object target) {
			b.append("<");
			b.append(target);
			b.append(">");
		}
	}

	private static FailureNotifier FAILED = new FailureNotifier() {
		@Override
		public void failed(String invocationString) throws Throwable {
			fail(invocationString);
		}
	};

	private static FailureNotifier ASSUMPTION_VIOLATED = new FailureNotifier() {
		@Override
		public void failed(String invocationString) throws Throwable {
			throw new AssumptionViolatedException(invocationString);
		}
	};

	private Dotmesh() {

	}

	private static Dotmesh instance() {
		return new Dotmesh();
	}

	// GENERAL PUBLIC API
	public static <T> T assertThat(final T target) {
		return (T) instance().explodeOnFalse(target, Dotmesh.FAILED);
	}

	public static <T> T assumeThat(final T target) {
		return (T) instance().explodeOnFalse(target, Dotmesh.ASSUMPTION_VIOLATED);
	}

	@SuppressWarnings("unchecked")
	public static <T> T not(T target) {
		return (T) instance().notObject(target);
	}

	// BUILT-IN PUNS

	public static StringAsserter string(String target) {
		return instance().pun(target, StringAsserter.class);
	}

	public static StringAsserter assertThat(String target) {
		assertNotNull(target);
		return assertThat(string(target));
	}

	public static StringAsserter not(String target) {
		return not(string(target));
	}

	public static IntAsserter integer(int target) {
		return new RealIntAsserter(target);
	}

	public static IntAsserter assertThat(int target) {
		return assertThat(integer(target));
	}

	public static IntAsserter not(int target) {
		return not(integer(target));
	}

	public static BooleanAsserter bool(boolean target) {
		return instance().pun(target, BooleanAsserter.class);
	}

	public static BooleanAsserter assertThat(boolean target) {
		return assertThat(bool(target));
	}

	public static BooleanAsserter not(boolean target) {
		return not(bool(target));
	}

	// PRIVATE IMPLEMENTATION

	private <T> T imposterize(Class<T> idealType, Invokable inv) {
		return ClassImposteriser.INSTANCE.imposterizeIdeally(inv, idealType);
	}

	@SuppressWarnings("unchecked")
	private <T> T explodeOnFalse(final T target, final FailureNotifier notifier) {
		Class<T> type = (Class<T>) target.getClass();
		return imposterize(type, notifier.explodeOnFalseInvokable(target));
	}

	private <T> T pun(final Object target, Class<T> targetType) {
		return imposterize(targetType, new Invokable() {
			@Override
			public Object invoke(Invocation invocation) throws Throwable {
				Method invokedMethod = invocation.getInvokedMethod();
				Method proxyMethod = target.getClass().getMethod(
						invokedMethod.getName(),
						invokedMethod.getParameterTypes());
				return proxyMethod.invoke(target, invocation
						.getParametersAsArray());
			}
		});
	}

	private <T> Object notObject(final T target) {
		return imposterize(target.getClass(), new Invokable() {
			@Override
			public Object invoke(Invocation invocation) throws Throwable {
				if (invocation.getInvokedMethod().getName().equals("toString"))
					return "not(" + target + ")";
				return !(Boolean) invocation.applyTo(target);
			}
		});
	}

	// copied from Modifier
	static final int SYNTHETIC = 0x00001000;
}