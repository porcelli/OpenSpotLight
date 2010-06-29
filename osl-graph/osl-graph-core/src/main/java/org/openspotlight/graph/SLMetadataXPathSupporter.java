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
package org.openspotlight.graph;

import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Messages;
import org.openspotlight.common.util.StringBuilderUtil;
import org.openspotlight.graph.meta.SLMetadata.BooleanOperator;
import org.openspotlight.graph.meta.SLMetadata.LogicOperator;
import org.openspotlight.graph.meta.SLMetadata.MetaNodeTypeProperty;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;

import java.util.List;

import static org.openspotlight.common.util.Exceptions.logAndThrow;

public class SLMetadataXPathSupporter {

    public static String buildXpathForMetaNodeType( final String metadataRootPath,
                                                    final SLRecursiveMode recursiveMode,
                                                    final VisibilityLevel visibility,
                                                    final MetaNodeTypeProperty property2Find,
                                                    final LogicOperator logicOp,
                                                    final BooleanOperator booleanOp,
                                                    final List<String> values ) {

        if ((logicOp == LogicOperator.LIKE_CONTAINS || logicOp == LogicOperator.LIKE_BEGINS_WITH || logicOp == LogicOperator.LIKE_ENDS_WITH)
            && (property2Find == null || property2Find == MetaNodeTypeProperty.NAME)) {
            Exceptions.logAndThrow(new IllegalArgumentException(
                                                                "Cannot search meta node types by name using any 'Like' operator."));
        }

        final StringBuilder statement = new StringBuilder(metadataRootPath);

        boolean isRestrictionOpened = false;

        if (recursiveMode.equals(SLRecursiveMode.RECURSIVE)) {
            statement.append("//*");
        } else {
            statement.append("/*");
        }
        if (visibility != null) {
            final String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_VISIBILITY);
            isRestrictionOpened = true;
            StringBuilderUtil.append(statement, "[(", propName, "='", visibility.toString(), "') ");
        }

        if (values != null) {
            if (!isRestrictionOpened) {
                isRestrictionOpened = true;
                StringBuilderUtil.append(statement, "[(");
            } else {
                StringBuilderUtil.append(statement, " and (");
            }
            String propName = SLConsts.INTERNAL_NODE_NAME;
            if (property2Find != null && property2Find == MetaNodeTypeProperty.DESCRIPTION) {
                propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_DESCRIPTION);
            }

            String booleanOperator = " and ";
            if (booleanOp != null && booleanOp == BooleanOperator.OR) {
                booleanOperator = " or ";
            }
            if (logicOp == null || logicOp == LogicOperator.EQUALS) {
                for (int i = 0; i < values.size(); i++) {
                    StringBuilderUtil.append(statement, propName, "='", values.get(i), '\'');
                    if ((i + 1) != values.size()) {
                        StringBuilderUtil.append(statement, booleanOperator);
                    } else {
                        StringBuilderUtil.append(statement, ')');
                    }
                }
            } else {
                for (int i = 0; i < values.size(); i++) {
                    StringBuilderUtil.append(statement, "jcr:like(@", propName, ",'");
                    if (logicOp == LogicOperator.LIKE_ENDS_WITH || logicOp == LogicOperator.LIKE_CONTAINS) {
                        StringBuilderUtil.append(statement, "%");
                    }
                    StringBuilderUtil.append(statement, values.get(i));
                    if (logicOp == LogicOperator.LIKE_BEGINS_WITH || logicOp == LogicOperator.LIKE_CONTAINS) {
                        StringBuilderUtil.append(statement, "%");
                    }
                    StringBuilderUtil.append(statement, "')");
                    if ((i + 1) != values.size()) {
                        StringBuilderUtil.append(statement, booleanOperator);
                    } else {
                        StringBuilderUtil.append(statement, ')');
                    }
                }
            }
        }

        if (isRestrictionOpened) {
            StringBuilderUtil.append(statement, ']');
        }

        return statement.toString();
    }

    /**
     * Should not be instantiated.
     */
    private SLMetadataXPathSupporter() {
        logAndThrow(new IllegalStateException(Messages.getString("invalidConstructor")));
    }
}
