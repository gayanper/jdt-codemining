package org.eclipse.jdt.experimental.junit.codemining.tester;

import java.util.stream.Stream;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.experimental.JavaCodeMiningPlugin;

public class JUnit3MethodTester implements IJUnitMethodTester {

	public static final IJUnitMethodTester INSTANCE = new JUnit3MethodTester();

	@Override
	public boolean isTestMethod(IMethod method) {
		return IJUnitMethodTester.isMethod(method, true) && isTestClass(method.getDeclaringType())
				&& method.getElementName().startsWith("test");
	}

	private boolean isTestClass(IType type) {
		try {
			return Stream.of(type.newSupertypeHierarchy(new NullProgressMonitor()).getAllSupertypes(type))
					.filter(t -> "junit.framework.TestCase".equals(t.getFullyQualifiedName())).findFirst().isPresent();
		} catch (JavaModelException e) {
			JavaCodeMiningPlugin.getDefault().getLog().error("isTestClass check for JUnit3 failed.", e);
			return false;
		}
	}
}
