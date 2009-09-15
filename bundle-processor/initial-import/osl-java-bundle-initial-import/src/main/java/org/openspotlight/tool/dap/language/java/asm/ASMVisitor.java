package org.openspotlight.tool.dap.language.java.asm;


import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.openspotlight.common.Pair;
import org.openspotlight.tool.dap.language.java.asm.model.Field;
import org.openspotlight.tool.dap.language.java.asm.model.JavaType;
import org.openspotlight.tool.dap.language.java.asm.model.MethodDeclaration;
import org.openspotlight.tool.dap.language.java.asm.model.SimpleTypeRef;
import org.openspotlight.tool.dap.language.java.asm.model.JavaType.JavaTypeDef;

public class ASMVisitor extends AbstractASMVisitor {

	private JavaType type = null;

	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		type = new JavaType();

		Pair<String, String> packageAndTypeName = getPackageAndTypeNames(name);
		type.setPackageName(packageAndTypeName.getK1());
		type.setTypeName(packageAndTypeName.getK2());

		if ((access & Opcodes.ACC_INTERFACE) > 0) {
			type.setType(JavaTypeDef.INTERFACE);
		} else if ((access & Opcodes.ACC_ENUM) > 0) {
			type.setType(JavaTypeDef.ENUM);
		} else if ((access & Opcodes.ACC_ANNOTATION) > 0) {
			type.setType(JavaTypeDef.ANNOTATION);
		} else {
			type.setType(JavaTypeDef.CLASS);
		}
		type.setAccess(access);

		if (superName != null) {
			Pair<String, String> superPackageAndTypeName = getPackageAndTypeNames(superName);
			SimpleTypeRef superType = new SimpleTypeRef(superPackageAndTypeName
					.getK1(), superPackageAndTypeName.getK2());
			type.setExtendsDef(superType);
		}

		for (String interfaceName : interfaces) {
			Pair<String, String> interfacePackageAndTypeName = getPackageAndTypeNames(interfaceName);
			SimpleTypeRef interfaceType = new SimpleTypeRef(
					interfacePackageAndTypeName.getK1(),
					interfacePackageAndTypeName.getK2());
			type.getImplementsDef().add(interfaceType);
		}
	}

	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		DirectASMParser asmParser = new DirectASMParser(desc);

		Field field = new Field();
		field.setName(name);
		field.setType(asmParser.type());
		field.setAccess(access);

		type.getFields().add(field);

		return null;
	}

	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		if (name.equals("<clinit>")) {
			return null;
		}

		DirectASMParser asmParser = new DirectASMParser(desc);

		MethodDeclaration methodDeclaration = new MethodDeclaration();
		if (name.equals("<init>")) {
			methodDeclaration.setConstructor(true);
			methodDeclaration.setName(type.getTypeName());
		} else {
			methodDeclaration.setName(name);
		}

		methodDeclaration = asmParser.method(methodDeclaration.getName(),
				methodDeclaration.isConstructor());

		if (exceptions != null) {
			for (String exceptionName : exceptions) {
				Pair<String, String> exceptionPackageAndTypeName = getPackageAndTypeNames(exceptionName);
				SimpleTypeRef exceptionType = new SimpleTypeRef(
						exceptionPackageAndTypeName.getK1(),
						exceptionPackageAndTypeName.getK2());
				methodDeclaration.getThrownExceptions().add(exceptionType);
			}
		}

		methodDeclaration.setAccess(access);
		type.getMethods().add(methodDeclaration);

		return null;
	}

	private Pair<String, String> getPackageAndTypeNames(String name) {

		String packageName = name.substring(0, name.lastIndexOf('/')).replace(
				"/", ".");
		String className = name.substring(name.lastIndexOf('/') + 1);

		return new Pair<String, String>(packageName, className);
	}

	public JavaType getType() {
		return type;
	}
}