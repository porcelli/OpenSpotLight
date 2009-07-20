package org.openspotlight.graph;

import org.openspotlight.graph.annotation.SLProperty;

public interface SLLineReference extends SLNode {
	
	public static final int LINE_TYPE_1 = 1;
	public static final int LINE_TYPE_2 = 2;

	@SLProperty
	public Integer getStartLine() throws SLGraphSessionException;
	public void setStartLine(Integer startLine) throws SLGraphSessionException;
	
	@SLProperty
	public Integer getEndLine() throws SLGraphSessionException;
	public void setEndLine(Integer endLine) throws SLGraphSessionException;

	@SLProperty
	public Integer getStartColumn() throws SLGraphSessionException;
	public void setStartColumn(Integer startColumn) throws SLGraphSessionException;
	
	@SLProperty
	public Integer getEndColumn() throws SLGraphSessionException;
	public void setEndColumn(Integer endColumn) throws SLGraphSessionException;
	
	@SLProperty
	public Integer getLineType() throws SLGraphSessionException;
	public void setLineType(Integer lineType) throws SLGraphSessionException;
	
	@SLProperty
	public String getStatement() throws SLGraphSessionException;
	public void setStatement(String statement) throws SLGraphSessionException;
}
