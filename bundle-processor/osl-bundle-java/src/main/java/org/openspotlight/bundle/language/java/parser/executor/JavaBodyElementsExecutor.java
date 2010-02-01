package org.openspotlight.bundle.language.java.parser.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.antlr.runtime.tree.CommonTree;

public class JavaBodyElementsExecutor {
	private final List<CommonTree> importedList = new ArrayList<CommonTree>();
	private final Stack<CommonTree> elementStack = new Stack<CommonTree>();

	public void addToImportedList(final CommonTree imported) {
		importedList.add(imported);
	}

	public CommonTree popFromElementStack() {
		return elementStack.pop();
	}

	public void pushToElementStack(final CommonTree imported) {
		elementStack.push(imported);
	}

}
