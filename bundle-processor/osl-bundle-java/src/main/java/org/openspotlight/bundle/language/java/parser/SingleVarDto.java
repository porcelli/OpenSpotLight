package org.openspotlight.bundle.language.java.parser;

import org.antlr.runtime.tree.CommonTree;
import org.openspotlight.bundle.common.parser.SLCommonTree;

public class SingleVarDto {

	public final SLCommonTree typeTreeElement;
	public final SLCommonTree identifierTreeElement;

	public SingleVarDto(final CommonTree typeTreeElement,
			final CommonTree identifierTreeElement) {
		this.typeTreeElement = (SLCommonTree) typeTreeElement;
		this.identifierTreeElement = (SLCommonTree) identifierTreeElement;
	}

}
