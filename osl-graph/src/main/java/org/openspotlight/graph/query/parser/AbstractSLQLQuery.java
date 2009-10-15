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
package org.openspotlight.graph.query.parser;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;

public abstract class AbstractSLQLQuery implements SLQLQuery {

    private static final long           serialVersionUID = 5945900887330334999L;

    protected Map<String, SLQLVariable> variables        = null;
    protected SLQLQuery                 targetQuery      = null;
    protected String                    id               = null;
    protected String                    outputModelName  = null;
    protected boolean                   isTarget         = false;
    protected SLQLQuery                 target           = null;

    public AbstractSLQLQuery(
                              final String id, final Set<SLQLVariable> variables, final String outputModelName,
                              final boolean isTarget,
                              final SLQLQuery target ) {
        Assertions.checkNotNull("isTarget", isTarget);
        Assertions.checkNotEmpty("id", id);

        this.id = id;
        this.isTarget = isTarget;
        this.target = target;

        if (outputModelName != null && outputModelName.trim().length() > 0) {
            this.outputModelName = outputModelName;
        }

        if (variables != null && variables.size() > 0) {
            for (SLQLVariable slqlVariable : variables) {
                this.variables.put(slqlVariable.getName(), slqlVariable);
            }
        }
    }

    public abstract Collection<SLNode> execute( final SLGraphSession session,
                                                final Map<String, ?> variableValues,
                                                final Collection<SLNode> inputNodes ) throws SLGraphSessionException;

    public String getId() {
        return id;
    }

    public String getOutputModelName() {
        return outputModelName;
    }

    public Collection<SLQLVariable> getVariables() {
        return null;
    }

    public boolean hasOutputModel() {
        return outputModelName != null;
    }

    public boolean isTarget() {
        return isTarget;
    }

    public boolean hasTarget() {
        return target != null;
    }

    public SLQLQuery getTarget() {
        return target;
    }

    public boolean hasVariables() {
        return variables != null;
    }

    protected void validateAndInit( final SLGraphSession session,
                                    final Map<String, ?> variableValues,
                                    final Collection<SLNode> inputNodes ) {
        Assertions.checkNotNull("session", session);

        if (this.hasVariables()) {
            Assertions.checkNotEmpty("variableValues", variableValues);
        } else {
            Assertions.checkNullMandatory("variableValues", variableValues);
        }

        if (this.hasTarget()) {
            Assertions.checkNotEmpty("inputNodes", inputNodes);
        } else {
            Assertions.checkNullMandatory("inputNodes", inputNodes);
        }

        if (variableValues != null && variableValues.size() > 0) {
            for (Entry<String, ?> activeVariableValue : variableValues.entrySet()) {
                if (variables.containsKey(activeVariableValue.getKey())) {
                    SLQLVariable activeVar = variables.get(activeVariableValue.getKey());
                    activeVar.setValue(activeVariableValue.getValue());
                } else {
                    Exceptions.logAndThrow(new IllegalArgumentException("Variable Not Found"));
                }
            }
        }
    }

    protected void setupVariableValues( final Map<String, ?> variableValues ) {
        for (Entry<String, ?> variableNameAndValue : variableValues.entrySet()) {
            SLQLVariable variable = variables.get(variableNameAndValue.getKey());
            variable.setValue(variableNameAndValue.getValue());
        }
    }

    protected boolean getBooleanValue( final String variableName ) {
        if (variables.containsKey(variableName)) {
            return ((SLQLVariableBoolean)variables.get(variableName)).getValue();
        }
        return false;
    }

    protected float getDecValue( final String variableName ) {
        if (variables.containsKey(variableName)) {
            return ((SLQLVariableFloat)variables.get(variableName)).getValue();
        }
        return -1;
    }

    protected int getIntValue( final String variableName ) {
        if (variables.containsKey(variableName)) {
            return ((SLQLVariableInteger)variables.get(variableName)).getValue();
        }
        return -1;
    }

    protected String getStringValue( final String variableName ) {
        if (variables.containsKey(variableName)) {
            return ((SLQLVariableString)variables.get(variableName)).getValue();
        }
        return "";
    }
}
