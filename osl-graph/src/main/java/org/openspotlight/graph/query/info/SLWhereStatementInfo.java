package org.openspotlight.graph.query.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import static org.openspotlight.common.util.StringBuilderUtil.append;
import org.openspotlight.graph.query.SLConditionalOperatorType;

public class SLWhereStatementInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<SLWhereStatementConditionInfo> conditionInfoList;
	private boolean closed;
	private boolean opened;
	private StackTraceElement[] openBraceStackTrace;
	
	public SLWhereStatementInfo() {
		setOpened(true);
		conditionInfoList = new ArrayList<SLWhereStatementConditionInfo>();
	}
	
	public void bookmarkOpenBracket() {
		openBraceStackTrace = Thread.currentThread().getStackTrace();
	}

	public StackTraceElement[] getOpenBraceStackTrace() {
		return openBraceStackTrace;
	}

	public SLWhereStatementConditionInfo addStatementCondition() {
		SLWhereStatementConditionInfo conditionInfo = new SLWhereStatementConditionInfo();
		conditionInfoList.add(conditionInfo);
		return conditionInfo;
	}
	
	public SLWhereStatementConditionInfo addStatementCondition(SLConditionalOperatorType operator) {
		SLWhereStatementConditionInfo conditionInfo = new SLWhereStatementConditionInfo(operator);
		conditionInfoList.add(conditionInfo);
		return conditionInfo;
	}
	
	public SLWhereStatementConditionInfo addStatementCondition(String typeName) {
		SLWhereStatementConditionInfo conditionInfo = new SLWhereStatementConditionInfo(typeName);
		conditionInfoList.add(conditionInfo);
		return conditionInfo;
	}
	
	public List<SLWhereStatementConditionInfo> getConditionInfoList() {
		return conditionInfoList;
	}

	public void setConditionInfoList(List<SLWhereStatementConditionInfo> conditionalInfoList) {
		this.conditionInfoList = conditionalInfoList;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		printWhereStatement(buffer, this, 1);
		return buffer.toString();
	}
	
	private void printWhereStatement(StringBuilder buffer, SLWhereStatementInfo statementInfo, int tabLevel) {
		for (int i = 0; i < statementInfo.conditionInfoList.size(); i++) {
			SLWhereStatementConditionInfo conditionInfo = statementInfo.conditionInfoList.get(i);
			String tabs = StringUtils.repeat("\t", tabLevel);
			append(buffer, tabs, conditionInfo);
			if (conditionInfo.getStatementInfo() != null) {
				append(buffer, '(', '\n');
				printWhereStatement(buffer, conditionInfo.getStatementInfo(), tabLevel + 1);
				append(buffer, tabs, ')', '\n');
			}
			else {
				append(buffer, '\n');
			}
		}
	}

}
