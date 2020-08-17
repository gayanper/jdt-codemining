package org.eclipse.jdt.experimental.junit.codemining.tester;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.experimental.JavaCodeMiningPlugin;

public interface IJUnitMethodTester {

	public static boolean isTestMethod(IMethod method, boolean onlyPublicMethod, String[] annotationSignatures) {
		if (isMethod(method, onlyPublicMethod)) {
			try {
				Optional<IAnnotation> annotation = Arrays.stream(method.getAnnotations())
				        .filter(a -> annotationSignatures[0].equals(a.getElementName())).findFirst();
				if(annotation.isPresent()) {
					String[][] resolveType = method.getDeclaringType().resolveType(annotation.get().getElementName());
					if (resolveType.length > 0) {
						return Arrays.equals(annotationSignatures, 1, annotationSignatures.length - 1, resolveType[0], 0, resolveType[0].length - 1);
					}
				}
			} catch (JavaModelException e) {
				JavaCodeMiningPlugin.getDefault().getLog().error(e.getMessage(), e);
			}
		}
		return false;

	}

	public static boolean isMethod(IMethod method, boolean onlyPublicMethod) {
		try {
			int flags = method.getFlags();
			if (onlyPublicMethod && !Flags.isPublic(flags)) {
				return false;
			}
			// 'V' is void signature
			return !(method.isConstructor() || Flags.isAbstract(flags) || Flags.isStatic(flags)
					|| !"V".equals(method.getReturnType()));
		} catch (JavaModelException e) {
			// ignore
			return false;
		}
	}

	boolean isTestMethod(IMethod method);

}
