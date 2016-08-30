package com.blade.aop;

import java.util.HashSet;
import java.util.Set;

public final class AopConfig {

	private Set<String> aopPackages = new HashSet<String>();

	public AopConfig() {

	}

	public void addPackage(String packageName) {
		aopPackages.add(packageName);
	}

	public Set<String> getPackages() {
		return aopPackages;
	}

}
