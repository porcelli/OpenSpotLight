package org.openspotlight.bundle.language.java.parser;

import org.openspotlight.bundle.language.java.metamodel.node.JavaType;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.graph.SLNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpressionDto {
	public final JavaType resultType;
	public final SLNode leaf;
	public final List<ExpressionDto> participants;

	public static final ExpressionDto NULL_EXPRESSION = new ExpressionDto();

	private ExpressionDto() {
		resultType = null;
		leaf = null;
		participants = Collections
				.unmodifiableList(new ArrayList<ExpressionDto>(0));
	}

	public ExpressionDto(final JavaType resultType, final ExpressionDto... dtos) {
		Assertions.checkNotNull("resultType", resultType);
		this.resultType = resultType;
		leaf = resultType;
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

	@Override
	public String toString() {
		if (this == NULL_EXPRESSION) {
			return "ExpressionDTO[NULL]";
		}
		return "ExpressionDTO[leaf: " + leaf.getName() + " , resultType: "
				+ resultType.getName() + " ] and " + participants.size()
				+ " participants";
	}
}
