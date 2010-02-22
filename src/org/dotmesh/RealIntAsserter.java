package org.dotmesh;

import org.dotmesh.asserters.IntAsserter;

public class RealIntAsserter implements IntAsserter {
	private final int target;

	public RealIntAsserter(int target) {
		this.target = target;
	}

	@Override
	public boolean notEquals(int other) {
		return target != other;
	}

	@Override
	public String toString() {
		return String.valueOf(target);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Integer) && (((Integer) obj) == target);
	}
}
