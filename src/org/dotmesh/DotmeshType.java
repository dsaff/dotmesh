/**
 * 
 */
package org.dotmesh;

import java.lang.reflect.Modifier;

public class DotmeshType {
	static final int SYNTHETIC = 0x00001000;
	
	Class<?> type;

	public DotmeshType(Class<?> type) {
		this.type = type;
	}

	boolean isMarkedFinal() {
		return Modifier.isFinal(type.getModifiers());
	}

	boolean isSynthetic() {
		return (type.getModifiers() & SYNTHETIC) != 0;
	}

	boolean isPrivate() {
		return Modifier.isPrivate((type).getModifiers());
	}

	boolean isEffectivelyFinal() {
		return isMarkedFinal() || isSynthetic() || isPrivate();
	}

	boolean isAlreadyEnhanced() {
		return type.getSimpleName().contains("EnhancerByCGLIB");
	}

	Class<? extends Object> targetSuperType() {
		return new DotmeshType(type.getSuperclass()).targetClass();
	}

	public Class<? extends Object> targetClass() {
		if (isEffectivelyFinal() || isAlreadyEnhanced()) {
			return targetSuperType();
		}
		return type;
	}
}