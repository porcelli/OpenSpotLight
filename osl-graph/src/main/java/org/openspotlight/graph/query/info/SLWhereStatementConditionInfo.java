package org.openspotlight.graph.query.info;

import static org.openspotlight.common.util.StringBuilderUtil.appendIfNotNull;

import java.io.Serializable;

import org.openspotlight.graph.query.SLConditionalOperatorType;
import org.openspotlight.graph.query.SLEqualityOperatorType;
import org.openspotlight.graph.query.SLRelationalOperatorType;
import org.openspotlight.graph.query.SLSideType;
import org.openspotlight.graph.query.SLStringLikeOperator;

public class SLWhereStatementConditionInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private SLWhereStatementInfo statementInfo;
	private SLConditionalOperatorType conditionalOperator;
	private SLEqualityOperatorType equalityOperator;
	private SLRelationalOperatorType relationalOperator;
	private SLStringLikeOperator stringLikeOperator;
	private SLSideType side;
	private String typeName;
	private String propertyName;
	private String linkTypeName;
	private Object value;
	private boolean subTypes;
	private boolean closed; 

	public SLWhereStatementConditionInfo() {}

	public SLWhereStatementConditionInfo(String typeName) {
		this.typeName = typeName;
	}

	public SLWhereStatementConditionInfo(SLConditionalOperatorType operator) {
		this.conditionalOperator = operator;
	}

	public SLConditionalOperatorType getConditionalOperator() {
		return conditionalOperator;
	}

	public void setConditionalOperator(SLConditionalOperatorType operator) {
		this.conditionalOperator = operator;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public SLEqualityOperatorType getEqualityOperator() {
		return equalityOperator;
	}

	public void setEqualityOperator(SLEqualityOperatorType equalityOperator) {
		this.equalityOperator = equalityOperator;
	}

	public SLRelationalOperatorType getRelationalOperator() {
		return relationalOperator;
	}

	public void setRelationalOperator(
			SLRelationalOperatorType relationalOperatorType) {
		this.relationalOperator = relationalOperatorType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
		setClosed(true);
	}

	public SLStringLikeOperator getStringLikeOperator() {
		return stringLikeOperator;
	}

	public void setStringLikeOperator(SLStringLikeOperator stringLikeOperator) {
		this.stringLikeOperator = stringLikeOperator;
	}

	public SLSideType getSide() {
		return side;
	}

	public void setSide(SLSideType side) {
		this.side = side;
	}

	public String getLinkTypeName() {
		return linkTypeName;
	}

	public void setLinkTypeName(String linkTypeName) {
		this.linkTypeName = linkTypeName;
	}

	public SLWhereStatementInfo getStatementInfo() {
		return statementInfo;
	}

	public void setStatementInfo(SLWhereStatementInfo statementInfo) {
		this.statementInfo = statementInfo;
	}
	public boolean isSubTypes() {
		return subTypes;
	}

	public void setSubTypes(boolean subTypes) {
		this.subTypes = subTypes;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		appendIfNotNull(buffer, conditionalOperator, conditionalOperator, ' ');
		appendIfNotNull(buffer, typeName, '"', typeName, (subTypes ? ".*" : ""), "\" ");
		appendIfNotNull(buffer, propertyName,  "property \"", propertyName, "\" ");
		appendIfNotNull(buffer, linkTypeName, "link \"", linkTypeName, "\" ");
		appendIfNotNull(buffer, equalityOperator, equalityOperator);
		appendIfNotNull(buffer, relationalOperator, relationalOperator);
		appendIfNotNull(buffer, stringLikeOperator, stringLikeOperator);
		if (value != null) {
			if (value instanceof Number) {
				appendIfNotNull(buffer, value, ' ', value);		
			}
			else {
				appendIfNotNull(buffer, value, " \"", value, '"');
			}
		}
		return buffer.toString();
	}
}
