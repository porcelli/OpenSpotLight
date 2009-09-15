package org.openspotlight.tool.dap.language.java.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public abstract class AbstractASMVisitor implements ClassVisitor {

	public void visit(int arg0, int arg1, String arg2, String arg3,
			String arg4, String[] arg5) {
	}

	public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
		return null;
	}

	public void visitAttribute(Attribute arg0) {
	}

	public void visitEnd() {
	}

	public FieldVisitor visitField(int arg0, String arg1, String arg2,
			String arg3, Object arg4) {
		return null;
	}

	public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
	}

	public MethodVisitor visitMethod(int arg0, String arg1, String arg2,
			String arg3, String[] arg4) {
		return null;
	}

	public void visitOuterClass(String arg0, String arg1, String arg2) {
	}

	public void visitSource(String arg0, String arg1) {
	}

}