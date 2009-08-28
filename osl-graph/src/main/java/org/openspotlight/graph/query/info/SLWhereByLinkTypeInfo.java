/*
 * OpenSpotLight - Open Source IT Governance Platform
 *  
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA 
 * or third-party contributors as indicated by the @author tags or express 
 * copyright attribution statements applied by the authors.  All third-party 
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E 
 * TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * This copyrighted material is made available to anyone wishing to use, modify, 
 * copy, or redistribute it subject to the terms and conditions of the GNU 
 * Lesser General Public License, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License  for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this distribution; if not, write to: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA 
 * 
 *********************************************************************** 
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os 
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.  
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.graph.query.info;

import static org.openspotlight.common.util.StringBuilderUtil.append;
import static org.openspotlight.common.util.StringBuilderUtil.appendIfNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspotlight.common.util.StringBuilderUtil;
import org.openspotlight.graph.query.SLConditionalOperatorType;
import org.openspotlight.graph.query.SLRelationalOperatorType;
import org.openspotlight.graph.query.SLSideType;

// TODO: Auto-generated Javadoc
/**
 * The Class SLWhereByLinkTypeInfo.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLWhereByLinkTypeInfo {

	/** The select by link type info. */
	private SLSelectByLinkTypeInfo selectByLinkTypeInfo;
	
	/** The where type info list. */
	private List<SLWhereLinkTypeInfo> whereTypeInfoList = new ArrayList<SLWhereLinkTypeInfo>();
	
	/**
	 * Instantiates a new sL where by link type info.
	 * 
	 * @param selectByLinkTypeInfo the select by link type info
	 */
	public SLWhereByLinkTypeInfo(SLSelectByLinkTypeInfo selectByLinkTypeInfo) {
		this.selectByLinkTypeInfo = selectByLinkTypeInfo;
	}
	
	/**
	 * Gets the where link type info list.
	 * 
	 * @return the where link type info list
	 */
	public List<SLWhereLinkTypeInfo> getWhereLinkTypeInfoList() {
		return whereTypeInfoList;
	}

	/**
	 * Sets the where type info list.
	 * 
	 * @param whereTypeInfoList the new where type info list
	 */
	public void setWhereTypeInfoList(List<SLWhereLinkTypeInfo> whereTypeInfoList) {
		this.whereTypeInfoList = whereTypeInfoList;
	}

	/**
	 * Gets the select by link type info.
	 * 
	 * @return the select by link type info
	 */
	public SLSelectByLinkTypeInfo getSelectByLinkTypeInfo() {
		return selectByLinkTypeInfo;
	}

	/**
	 * Sets the select by link type info.
	 * 
	 * @param selectByLinkTypeInfo the new select by link type info
	 */
	public void setSelectByLinkTypeInfo(SLSelectByLinkTypeInfo selectByLinkTypeInfo) {
		this.selectByLinkTypeInfo = selectByLinkTypeInfo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("\nWHERE\n");
		for (SLWhereLinkTypeInfo typeInfo : whereTypeInfoList) {
			StringBuilderUtil.append(buffer, typeInfo.getLinkTypeStatementInfo(), '\n');
		}
		return buffer.toString();
	}
	
	/**
	 * The Class SLWhereLinkTypeInfo.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	public static class SLWhereLinkTypeInfo {
		
		/** The name. */
		private String name;
		
		/** The link type statement info. */
		private SLLinkTypeStatementInfo linkTypeStatementInfo;
		
		/**
		 * Instantiates a new sL where link type info.
		 * 
		 * @param name the name
		 */
		public SLWhereLinkTypeInfo(String name) {
			this.name = name;
		}
		
		/**
		 * Gets the name.
		 * 
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Sets the name.
		 * 
		 * @param name the new name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Gets the link type statement info.
		 * 
		 * @return the link type statement info
		 */
		public SLLinkTypeStatementInfo getLinkTypeStatementInfo() {
			return linkTypeStatementInfo;
		}

		/**
		 * Sets the link type statement info.
		 * 
		 * @param whereStatementInfo the new link type statement info
		 */
		public void setLinkTypeStatementInfo(SLLinkTypeStatementInfo whereStatementInfo) {
			this.linkTypeStatementInfo = whereStatementInfo;
		}
		
		/**
		 * The Class SLLinkTypeStatementInfo.
		 * 
		 * @author Vitor Hugo Chagas
		 */
		public static class SLLinkTypeStatementInfo implements Serializable {

			/** The Constant serialVersionUID. */
			private static final long serialVersionUID = 1L;
			
			/** The type info. */
			private SLWhereLinkTypeInfo typeInfo;
			
			/** The condition info list. */
			private List<SLConditionInfo> conditionInfoList;
			
			/** The closed. */
			private boolean closed;
			
			/** The opened. */
			private boolean opened;
			
			/** The open brace stack trace. */
			private StackTraceElement[] openBraceStackTrace;
			
			/**
			 * Instantiates a new sL link type statement info.
			 * 
			 * @param typeInfo the type info
			 */
			public SLLinkTypeStatementInfo(SLWhereLinkTypeInfo typeInfo) {
				setOpened(true);
				conditionInfoList = new ArrayList<SLConditionInfo>();
				this.typeInfo = typeInfo;
			}

			/**
			 * Bookmark open bracket.
			 */
			public void bookmarkOpenBracket() {
				openBraceStackTrace = Thread.currentThread().getStackTrace();
			}

			/**
			 * Gets the open brace stack trace.
			 * 
			 * @return the open brace stack trace
			 */
			public StackTraceElement[] getOpenBraceStackTrace() {
				return openBraceStackTrace;
			}

			/**
			 * Gets the type info.
			 * 
			 * @return the type info
			 */
			public SLWhereLinkTypeInfo getTypeInfo() {
				return typeInfo;
			}

			/**
			 * Sets the type info.
			 * 
			 * @param typeInfo the new type info
			 */
			public void setTypeInfo(SLWhereLinkTypeInfo typeInfo) {
				this.typeInfo = typeInfo;
			}

			/**
			 * Adds the condition.
			 * 
			 * @return the sL condition info
			 */
			public SLConditionInfo addCondition() {
				SLConditionInfo conditionInfo = new SLConditionInfo(typeInfo);
				conditionInfoList.add(conditionInfo);
				conditionInfo.setOuterStatementInfo(this);
				return conditionInfo;
			}
			
			/**
			 * Adds the condition.
			 * 
			 * @param operator the operator
			 * 
			 * @return the sL condition info
			 */
			public SLConditionInfo addCondition(SLConditionalOperatorType operator) {
				SLConditionInfo conditionInfo = new SLConditionInfo(typeInfo, operator);
				conditionInfoList.add(conditionInfo);
				conditionInfo.setOuterStatementInfo(this);
				return conditionInfo;
			}
			
			/**
			 * Gets the condition info list.
			 * 
			 * @return the condition info list
			 */
			public List<SLConditionInfo> getConditionInfoList() {
				return conditionInfoList;
			}

			/**
			 * Sets the condition info list.
			 * 
			 * @param conditionalInfoList the new condition info list
			 */
			public void setConditionInfoList(List<SLConditionInfo> conditionalInfoList) {
				this.conditionInfoList = conditionalInfoList;
			}

			/**
			 * Checks if is closed.
			 * 
			 * @return true, if is closed
			 */
			public boolean isClosed() {
				return closed;
			}

			/**
			 * Sets the closed.
			 * 
			 * @param closed the new closed
			 */
			public void setClosed(boolean closed) {
				this.closed = closed;
			}

			/**
			 * Checks if is opened.
			 * 
			 * @return true, if is opened
			 */
			public boolean isOpened() {
				return opened;
			}

			/**
			 * Sets the opened.
			 * 
			 * @param opened the new opened
			 */
			public void setOpened(boolean opened) {
				this.opened = opened;
			}

			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				StringBuilder buffer = new StringBuilder();
				printWhereStatement(buffer, this, 1);
				return buffer.toString();
			}
			
			/**
			 * Prints the where statement.
			 * 
			 * @param buffer the buffer
			 * @param statementInfo the statement info
			 * @param tabLevel the tab level
			 */
			private void printWhereStatement(StringBuilder buffer, SLLinkTypeStatementInfo statementInfo, int tabLevel) {
				for (int i = 0; i < statementInfo.conditionInfoList.size(); i++) {
					SLConditionInfo conditionInfo = statementInfo.conditionInfoList.get(i);
					String tabs = StringUtils.repeat("\t", tabLevel);
					append(buffer, tabs, conditionInfo);
					if (conditionInfo.getInnerStatementInfo() != null) {
						append(buffer, '(', '\n');
						printWhereStatement(buffer, conditionInfo.getInnerStatementInfo(), tabLevel + 1);
						append(buffer, tabs, ')', '\n');
					}
					else {
						append(buffer, '\n');
					}
				}
			}
			
			/**
			 * The Class SLConditionInfo.
			 * 
			 * @author Vitor Hugo Chagas
			 */
			public static class SLConditionInfo {

				/** The type info. */
				private SLWhereLinkTypeInfo typeInfo;
				
				/** The relational operator. */
				private SLRelationalOperatorType relationalOperator;
				
				/** The conditional operator. */
				private SLConditionalOperatorType conditionalOperator;
				
				/** The inner statement info. */
				private SLLinkTypeStatementInfo innerStatementInfo;
				
				/** The outer statement info. */
				private SLLinkTypeStatementInfo outerStatementInfo;
				
				/** The conditional not operator. */
				private boolean conditionalNotOperator;
				
				/** The side. */
				private SLSideType side;
				
				/** The property name. */
				private String propertyName;
				
				/** The link type name. */
				private String linkTypeName;
				
				/** The value. */
				private Object value;
				
				/** The closed. */
				private boolean closed; 
				
				/** The relational not operator. */
				private boolean relationalNotOperator;

				/**
				 * Instantiates a new sL condition info.
				 * 
				 * @param typeInfo the type info
				 */
				public SLConditionInfo(SLWhereLinkTypeInfo typeInfo) {
					this(typeInfo, null);
				}

				/**
				 * Instantiates a new sL condition info.
				 * 
				 * @param typeInfo the type info
				 * @param conditionalOperator the conditional operator
				 */
				public SLConditionInfo(SLWhereLinkTypeInfo typeInfo, SLConditionalOperatorType conditionalOperator) {
					this.typeInfo = typeInfo;
					this.conditionalOperator = conditionalOperator;
				}

				/**
				 * Gets the property name.
				 * 
				 * @return the property name
				 */
				public String getPropertyName() {
					return propertyName;
				}

				/**
				 * Sets the property name.
				 * 
				 * @param propertyName the new property name
				 */
				public void setPropertyName(String propertyName) {
					this.propertyName = propertyName;
				}

				/**
				 * Gets the value.
				 * 
				 * @return the value
				 */
				public Object getValue() {
					return value;
				}

				/**
				 * Sets the value.
				 * 
				 * @param value the new value
				 */
				public void setValue(Object value) {
					this.value = value;
					setClosed(true);
				}

				/**
				 * Gets the side.
				 * 
				 * @return the side
				 */
				public SLSideType getSide() {
					return side;
				}

				/**
				 * Sets the side.
				 * 
				 * @param side the new side
				 */
				public void setSide(SLSideType side) {
					this.side = side;
				}

				/**
				 * Gets the link type name.
				 * 
				 * @return the link type name
				 */
				public String getLinkTypeName() {
					return linkTypeName;
				}

				/**
				 * Sets the link type name.
				 * 
				 * @param linkTypeName the new link type name
				 */
				public void setLinkTypeName(String linkTypeName) {
					this.linkTypeName = linkTypeName;
				}

				/**
				 * Gets the inner statement info.
				 * 
				 * @return the inner statement info
				 */
				public SLLinkTypeStatementInfo getInnerStatementInfo() {
					return innerStatementInfo;
				}

				/**
				 * Sets the inner statement info.
				 * 
				 * @param statementInfo the new inner statement info
				 */
				public void setInnerStatementInfo(SLLinkTypeStatementInfo statementInfo) {
					this.innerStatementInfo = statementInfo;
				}

				/**
				 * Checks if is closed.
				 * 
				 * @return true, if is closed
				 */
				public boolean isClosed() {
					return closed;
				}

				/**
				 * Sets the closed.
				 * 
				 * @param closed the new closed
				 */
				public void setClosed(boolean closed) {
					this.closed = closed;
				}

				/**
				 * Gets the relational operator.
				 * 
				 * @return the relational operator
				 */
				public SLRelationalOperatorType getRelationalOperator() {
					return relationalOperator;
				}

				/**
				 * Sets the relational operator.
				 * 
				 * @param operator the new relational operator
				 */
				public void setRelationalOperator(SLRelationalOperatorType operator) {
					this.relationalOperator = operator;
				}

				/**
				 * Gets the conditional operator.
				 * 
				 * @return the conditional operator
				 */
				public SLConditionalOperatorType getConditionalOperator() {
					return conditionalOperator;
				}

				/**
				 * Sets the conditional operator.
				 * 
				 * @param conditionalOperator the new conditional operator
				 */
				public void setConditionalOperator(SLConditionalOperatorType conditionalOperator) {
					this.conditionalOperator = conditionalOperator;
				}

				/**
				 * Gets the outer statement info.
				 * 
				 * @return the outer statement info
				 */
				public SLLinkTypeStatementInfo getOuterStatementInfo() {
					return outerStatementInfo;
				}

				/**
				 * Sets the outer statement info.
				 * 
				 * @param outerStatementInfo the new outer statement info
				 */
				public void setOuterStatementInfo(SLLinkTypeStatementInfo outerStatementInfo) {
					this.outerStatementInfo = outerStatementInfo;
				}

				/**
				 * Gets the type info.
				 * 
				 * @return the type info
				 */
				public SLWhereLinkTypeInfo getTypeInfo() {
					return typeInfo;
				}

				/**
				 * Sets the type info.
				 * 
				 * @param typeInfo the new type info
				 */
				public void setTypeInfo(SLWhereLinkTypeInfo typeInfo) {
					this.typeInfo = typeInfo;
				}

				/* (non-Javadoc)
				 * @see java.lang.Object#toString()
				 */
				@Override
				public String toString() {
					
					String typeName = typeInfo.getName();
					
					StringBuilder buffer = new StringBuilder();
					appendIfNotNull(buffer, conditionalOperator, conditionalOperator, ' ');
					appendIfNotNull(buffer, relationalOperator, '"', typeName, "\" ");
					appendIfNotNull(buffer, propertyName,  "property \"", propertyName, "\" ");
					appendIfNotNull(buffer, linkTypeName, "link \"", linkTypeName, "\" ");
					appendIfNotNull(buffer, relationalOperator, (relationalNotOperator ? "!" : ""), relationalOperator);
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

				/**
				 * Checks if is relational not operator.
				 * 
				 * @return true, if is relational not operator
				 */
				public boolean isRelationalNotOperator() {
					return relationalNotOperator;
				}

				/**
				 * Sets the relational not operator.
				 * 
				 * @param relationalNotOperator the new relational not operator
				 */
				public void setRelationalNotOperator(boolean relationalNotOperator) {
					this.relationalNotOperator = relationalNotOperator;
				}

				/**
				 * Checks if is conditional not operator.
				 * 
				 * @return true, if is conditional not operator
				 */
				public boolean isConditionalNotOperator() {
					return conditionalNotOperator;
				}

				/**
				 * Sets the conditional not operator.
				 * 
				 * @param conditionalNotOperator the new conditional not operator
				 */
				public void setConditionalNotOperator(boolean conditionalNotOperator) {
					this.conditionalNotOperator = conditionalNotOperator;
				}
			}
		}
	}
}
