package org.dotmesh;

import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.dotmesh.asserters.BooleanAsserter;
import org.dotmesh.asserters.ClassAsserter;
import org.dotmesh.asserters.IntAsserter;
import org.dotmesh.asserters.StringAsserter;
import org.jmock.api.Invocation;
import org.jmock.api.Invokable;
import org.jmock.internal.ReturnDefaultValueAction;
import org.junit.internal.AssumptionViolatedException;

public class Dotmesh {
	public interface InvocationStringAppendable {
		void appendToInvocationString(StringBuilder b);
	}

	public static class TestVerb {		
		private final FailureNotifier notifier;

		public TestVerb(FailureNotifier notifier) {
			this.notifier = notifier;
		}

		public TestVerb not() {
			return new TestVerb(notifier) {
				@SuppressWarnings("unchecked")
				public <T> T that(T target) {
					return super.that((T) notObject(target));
				}

				private <T> Object notObject(final T target) {
					return imposterize(target.getClass(), new Invokable() {
						@Override
						public Object invoke(Invocation invocation) throws Throwable {
							if (invocation.getInvokedMethod().getName().equals("appendToInvocationString")) {
								((StringBuilder) invocation.getParameter(0)).append("not <" + target + ">");
								return null;
							}
							return !(Boolean) invocation.applyTo(target);
						}
					}, InvocationStringAppendable.class);
				}
			};
		};

		public <T> T that(T target) {
			return explodeOnFalse(target, notifier);
		}

		public final StringAsserter that(String target) {
			return that(pun(target, StringAsserter.class));
		}

		public ClassAsserter that(Class<?> target) {
			return that(pun(target, ClassAsserter.class));
		}

		public IntAsserter that(int target) {
			return that(new RealIntAsserter(target));
		}

		public BooleanAsserter that(boolean target) {
			return that(pun(target, BooleanAsserter.class));
		}

		@SuppressWarnings("unchecked")
		private <T> T explodeOnFalse(final T target, final FailureNotifier notifier) {
			Class<T> type = (Class<T>) target.getClass();
			return imposterize(type, notifier.explodeOnFalseInvokable(target));
		}

		private <T> T imposterize(Class<T> idealType, Invokable inv, Class<?>... additionalInterfaces) {
			return ClassImposteriser.INSTANCE.imposterizeIdeally(inv, idealType, additionalInterfaces);
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
	}

	private static abstract class FailureNotifier {
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
			if (target instanceof InvocationStringAppendable) {
				InvocationStringAppendable appendable = (InvocationStringAppendable) target;
				appendable.appendToInvocationString(b);
			} else {
				b.append("<");
				b.append(target);
				b.append(">");
			}
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

	public static TestVerb affirm = new TestVerb(Dotmesh.FAILED);
	public static TestVerb assume = new TestVerb(Dotmesh.ASSUMPTION_VIOLATED);

	private Dotmesh() {
		// prevent construction
	}
}