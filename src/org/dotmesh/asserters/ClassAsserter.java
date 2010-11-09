package org.dotmesh.asserters;

import java.lang.annotation.Annotation;

public interface ClassAsserter {
	public boolean desiredAssertionStatus();
	public boolean isAnnotation();
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);
	public boolean isAnonymousClass();
	public boolean isArray();
	public boolean isAssignableFrom(Class<?> cls);
	public boolean isEnum();
	public boolean isInstance(Object obj);
	public boolean isInterface();
	public boolean isLocalClass();
	public boolean isMemberClass();
	public boolean isPrimitive();
	public boolean isSynthetic();
}
