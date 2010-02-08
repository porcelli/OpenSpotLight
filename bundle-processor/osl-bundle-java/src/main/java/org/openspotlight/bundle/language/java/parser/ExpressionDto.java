package org.openspotlight.bundle.language.java.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openspotlight.bundle.language.java.metamodel.node.JavaType;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.graph.SLNode;

public class ExpressionDto {
	public final JavaType resultType;
	public final SLNode leaf;
	public final List<ExpressionDto> participants;

	public ExpressionDto(final JavaType resultType, final SLNode leaf,
			final ExpressionDto... dtos) {
		Assertions.checkNotNull("resultType", resultType);
		Assertions.checkNotNull("leaf", leaf);
		this.resultType = resultType;
		this.leaf = leaf;
		final List<ExpressionDto> tempParticipants = new ArrayList<ExpressionDto>();
		if (dtos != null) {
			for (final ExpressionDto dto : dtos) {
				if (dto != null) {
					tempParticipants.add(dto);
				}
			}
		}
		participants = Collections.unmodifiableList(tempParticipants);

	}
}
