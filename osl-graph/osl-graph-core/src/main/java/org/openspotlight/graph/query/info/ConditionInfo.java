/**
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

import static org.openspotlight.common.util.StringBuilderUtil.appendIfNotNull;

import org.openspotlight.graph.query.ConditionalOperatorType;
import org.openspotlight.graph.query.RelationalOperatorType;
import org.openspotlight.graph.query.SideType;
import org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo;

/**
 * The Class SLConditionInfo.
 * 
 * @author Vitor Hugo Chagas
 */
public class ConditionInfo {

    /** The closed. */
    private boolean                 closed;

    /** The conditional not operator. */
    private boolean                 conditionalNotOperator;

    /** The conditional operator. */
    private ConditionalOperatorType conditionalOperator;

    /** The inner statement info. */
    private SLLinkTypeStatementInfo innerStatementInfo;

    /** The link type info. */
    private WhereLinkTypeInfo       linkTypeInfo;

    /** The link type name. */
    private String                  linkTypeName;

    /** The outer statement info. */
    private SLLinkTypeStatementInfo outerStatementInfo;

    /** The property name. */
    private String                  propertyName;

    /** The relational not operator. */
    private boolean                 relationalNotOperator;

    /** The relational operator. */
    private RelationalOperatorType  relationalOperator;

    /** The side. */
    private SideType                side;

    /** The type info. */
    private WhereTypeInfo           typeInfo;

    /** The value. */
    private Object                  value;

    /**
     * Instantiates a new sL condition info.
     * 
     * @param linkTypeInfo the link type info
     */
    public ConditionInfo(
                            final WhereLinkTypeInfo linkTypeInfo) {
        this(linkTypeInfo, null);
    }

    /**
     * Instantiates a new sL condition info.
     * 
     * @param linkTypeInfo the type info
     * @param conditionalOperator the conditional operator
     */
    public ConditionInfo(
                            final WhereLinkTypeInfo linkTypeInfo, final ConditionalOperatorType conditionalOperator) {
        this.linkTypeInfo = linkTypeInfo;
        this.conditionalOperator = conditionalOperator;
    }

    /**
     * Instantiates a new sL condition info.
     * 
     * @param typeInfo the type info
     */
    public ConditionInfo(
                            final WhereTypeInfo typeInfo) {
        this(typeInfo, null);
    }

    /**
     * Instantiates a new sL condition info.
     * 
     * @param typeInfo the type info
     * @param conditionalOperator the conditional operator
     */
    public ConditionInfo(
                            final WhereTypeInfo typeInfo, final ConditionalOperatorType conditionalOperator) {
        this.typeInfo = typeInfo;
        this.conditionalOperator = conditionalOperator;
    }

    /**
     * Gets the conditional operator.
     * 
     * @return the conditional operator
     */
    public ConditionalOperatorType getConditionalOperator() {
        return conditionalOperator;
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
     * Gets the link type info.
     * 
     * @return the link type info
     */
    public WhereLinkTypeInfo getLinkTypeInfo() {
        return linkTypeInfo;
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
     * Gets the outer statement info.
     * 
     * @return the outer statement info
     */
    public SLLinkTypeStatementInfo getOuterStatementInfo() {
        return outerStatementInfo;
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
     * Gets the relational operator.
     * 
     * @return the relational operator
     */
    public RelationalOperatorType getRelationalOperator() {
        return relationalOperator;
    }

    /**
     * Gets the side.
     * 
     * @return the side
     */
    public SideType getSide() {
        return side;
    }

    public WhereTypeInfo getTypeInfo() {
        return typeInfo;
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
     * Checks if is closed.
     * 
     * @return true, if is closed
     */
    public boolean isClosed() {
        return closed;
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
     * Checks if is relational not operator.
     * 
     * @return true, if is relational not operator
     */
    public boolean isRelationalNotOperator() {
        return relationalNotOperator;
    }

    /**
     * Sets the closed.
     * 
     * @param closed the new closed
     */
    public void setClosed(final boolean closed) {
        this.closed = closed;
    }

    /**
     * Sets the conditional not operator.
     * 
     * @param conditionalNotOperator the new conditional not operator
     */
    public void setConditionalNotOperator(final boolean conditionalNotOperator) {
        this.conditionalNotOperator = conditionalNotOperator;
    }

    /**
     * Sets the conditional operator.
     * 
     * @param conditionalOperator the new conditional operator
     */
    public void setConditionalOperator(final ConditionalOperatorType conditionalOperator) {
        this.conditionalOperator = conditionalOperator;
    }

    /**
     * Sets the inner statement info.
     * 
     * @param statementInfo the new inner statement info
     */
    public void setInnerStatementInfo(final SLLinkTypeStatementInfo statementInfo) {
        innerStatementInfo = statementInfo;
    }

    /**
     * Sets the link type info.
     * 
     * @param typeInfo the new link type info
     */
    public void setLinkTypeInfo(final WhereLinkTypeInfo typeInfo) {
        linkTypeInfo = typeInfo;
    }

    /**
     * Sets the link type name.
     * 
     * @param linkTypeName the new link type name
     */
    public void setLinkTypeName(final String linkTypeName) {
        this.linkTypeName = linkTypeName;
    }

    /**
     * Sets the outer statement info.
     * 
     * @param outerStatementInfo the new outer statement info
     */
    public void setOuterStatementInfo(final SLLinkTypeStatementInfo outerStatementInfo) {
        this.outerStatementInfo = outerStatementInfo;
    }

    /**
     * Sets the property name.
     * 
     * @param propertyName the new property name
     */
    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Sets the relational not operator.
     * 
     * @param relationalNotOperator the new relational not operator
     */
    public void setRelationalNotOperator(final boolean relationalNotOperator) {
        this.relationalNotOperator = relationalNotOperator;
    }

    /**
     * Sets the relational operator.
     * 
     * @param operator the new relational operator
     */
    public void setRelationalOperator(final RelationalOperatorType operator) {
        relationalOperator = operator;
    }

    /**
     * Sets the side.
     * 
     * @param side the new side
     */
    public void setSide(final SideType side) {
        this.side = side;
    }

    public void setTypeInfo(final WhereTypeInfo typeInfo) {
        this.typeInfo = typeInfo;
    }

    /**
     * Sets the value.
     * 
     * @param value the new value
     */
    public void setValue(final Object value) {
        this.value = value;
        setClosed(true);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        final String typeName = linkTypeInfo.getName();

        final StringBuilder buffer = new StringBuilder();
        appendIfNotNull(buffer, conditionalOperator, conditionalOperator, (conditionalNotOperator ? " NOT " : ""), ' ');
        appendIfNotNull(buffer, relationalOperator, '"', typeName, "\" ");
        appendIfNotNull(buffer, propertyName, "property \"", propertyName, "\" ");
        appendIfNotNull(buffer, linkTypeName, "link \"", linkTypeName, "\" ");
        appendIfNotNull(buffer, relationalOperator, (relationalNotOperator ? "!" : ""), relationalOperator);
        if (value != null) {
            if (value instanceof Number) {
                appendIfNotNull(buffer, value, ' ', value);
            } else {
                appendIfNotNull(buffer, value, " \"", value, '"');
            }
        }
        return buffer.toString();
    }
}
