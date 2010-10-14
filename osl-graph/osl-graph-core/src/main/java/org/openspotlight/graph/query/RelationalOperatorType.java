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
package org.openspotlight.graph.query;

import org.openspotlight.common.util.StringBuilderUtil;
import org.openspotlight.common.util.Strings;

// TODO: Auto-generated Javadoc
/**
 * The Enum SLRelationalOperatorType.
 * 
 * @author Vitor Hugo Chagas
 */
public enum RelationalOperatorType implements OperatorType {

    /** The EQUAL. */
    EQUAL("="),

    /** The GREATE r_ than. */
    GREATER_THAN(">"),

    /** The LESSE r_ than. */
    LESSER_THAN("<"),

    /** The GREATE r_ o r_ equa l_ than. */
    GREATER_OR_EQUAL_THAN(">="),

    /** The LESSE r_ o r_ equa l_ than. */
    LESSER_OR_EQUAL_THAN("<="),

    /** The START s_ with. */
    STARTS_WITH("..*"),

    /** The END s_ with. */
    ENDS_WITH("*.."),

    /** The CONTAINS. */
    CONTAINS("<*>");

    /** The symbol. */
    private String symbol;

    /**
     * Instantiates a new sL relational operator type.
     * 
     * @param symbol the symbol
     */
    RelationalOperatorType(
                              String symbol ) {
        this.symbol = symbol;
    }

    /**
     * {@inheritDoc}
     */
    public String symbol() {
        return symbol;
    }

    /**
     * X path expression.
     * 
     * @param leftOperand the left operand
     * @param rightOperand the right operand
     * @param applyNot the apply not
     * @return the string
     */
    public String xPathExpression( Object leftOperand,
                                   Object rightOperand,
                                   boolean applyNot ) {
        StringBuilder buffer = new StringBuilder();
        if (this.equals(STARTS_WITH)) {
            StringBuilderUtil.append(buffer, "jcr:like(@", leftOperand, ", '", rightOperand, "%')");
        } else if (this.equals(ENDS_WITH)) {
            StringBuilderUtil.append(buffer, "jcr:like(@", leftOperand, ", '%", rightOperand, "')");
        } else if (this.equals(CONTAINS)) {
            StringBuilderUtil.append(buffer, "jcr:like(@", leftOperand, ", '%", rightOperand, "%')");
        } else {
            StringBuilderUtil.append(buffer, leftOperand, ' ', symbol, ' ', Strings.quote(rightOperand));
        }
        if (applyNot) {
            buffer.insert(0, "not(");
            buffer.append(')');
        }
        return buffer.toString();
    }

    @Override
    public String toString() {
        return symbol;
    }

}
