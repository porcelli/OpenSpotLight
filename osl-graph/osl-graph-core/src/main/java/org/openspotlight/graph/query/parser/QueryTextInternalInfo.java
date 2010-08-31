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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Class SLQueryTextInternalInfo. This is a simple data class.
 * 
 * @author porcelli
 */
public class QueryTextInternalInfo {

    /** The query unique id. */
    private String                         id                  = null;

    /** The target unique id. */
    private String                         targetUniqueId      = null;

    /** The int variables. */
    private Set<String>                    intVariables        = new HashSet<String>();

    /** The dec variables. */
    private Set<String>                    decVariables        = new HashSet<String>();

    /** The bool variables. */
    private Set<String>                    boolVariables       = new HashSet<String>();

    /** The string variables. */
    private Set<String>                    stringVariables     = new HashSet<String>();

    /** The message variables. */
    private Map<String, String>            messageVariables    = new HashMap<String, String>();

    /** The domain variables. */
    private Map<String, Set<Serializable>> domainVariables     = new HashMap<String, Set<Serializable>>();

    /** The string constants . */
    private Map<Integer, String>           stringsConstant     = new HashMap<Integer, String>();

    /** The has target. */
    private boolean                        hasTarget           = false;

    /** The target keeps result. */
    private boolean                        targetKeepsResult   = false;

    /** The output model name. */
    private String                         outputModelName     = null;

    /** The define target content. */
    private String                         defineTargetContent = null;

    /** The content. */
    private String                         content             = null;

    /**
     * Gets the bool variables.
     * 
     * @return the bool variables
     */
    public Set<String> getBoolVariables() {
        return this.boolVariables;
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Gets the dec variables.
     * 
     * @return the dec variables
     */
    public Set<String> getDecVariables() {
        return this.decVariables;
    }

    /**
     * Gets the define target content.
     * 
     * @return the define target content
     */
    public String getDefineTargetContent() {
        return this.defineTargetContent;
    }

    /**
     * Gets the domain variables.
     * 
     * @return the domain variables
     */
    public Map<String, Set<Serializable>> getDomainVariables() {
        return this.domainVariables;
    }

    /**
     * Gets the unique query id.
     * 
     * @return the unique query id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the int variables.
     * 
     * @return the int variables
     */
    public Set<String> getIntVariables() {
        return this.intVariables;
    }

    /**
     * Gets the message variables.
     * 
     * @return the message variables
     */
    public Map<String, String> getMessageVariables() {
        return this.messageVariables;
    }

    /**
     * Gets the output model name.
     * 
     * @return the output model name
     */
    public String getOutputModelName() {
        return this.outputModelName;
    }

    /**
     * Gets the string variables.
     * 
     * @return the string variables
     */
    public Set<String> getStringVariables() {
        return this.stringVariables;
    }

    /**
     * Gets the target unique id.
     * 
     * @return the target unique id
     */
    public String getTargetUniqueId() {
        return this.targetUniqueId;
    }

    /**
     * Checks for output model.
     * 
     * @return true, if has output model
     */
    public boolean hasOutputModel() {
        if (this.outputModelName == null) {
            return false;
        }
        return true;
    }

    /**
     * Checks for target.
     * 
     * @return true, if has target
     */
    public boolean hasTarget() {
        return this.hasTarget;
    }

    /**
     * Checks for variables.
     * 
     * @return true, if has variables
     */
    public boolean hasVariables() {
        if (this.intVariables.size() == 0 && this.decVariables.size() == 0 && this.boolVariables.size() == 0
            && this.stringVariables.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * Checks if is checks for target.
     * 
     * @return true, if is checks for target
     */
    public boolean isHasTarget() {
        return this.hasTarget;
    }

    /**
     * Checks if is target keeps result.
     * 
     * @return true, if is target keeps result
     */
    public boolean isTargetKeepsResult() {
        return this.targetKeepsResult;
    }

    /**
     * Sets the bool variables.
     * 
     * @param boolVariables the new bool variables
     */
    public void setBoolVariables( final Set<String> boolVariables ) {
        this.boolVariables = boolVariables;
    }

    /**
     * Sets the content.
     * 
     * @param content the new content
     */
    public void setContent( final String content ) {
        this.content = content;
    }

    /**
     * Sets the dec variables.
     * 
     * @param decVariables the new dec variables
     */
    public void setDecVariables( final Set<String> decVariables ) {
        this.decVariables = decVariables;
    }

    /**
     * Sets the define target content.
     * 
     * @param defineTargetContent the new define target content
     */
    public void setDefineTargetContent( final String defineTargetContent ) {
        this.defineTargetContent = defineTargetContent;
    }

    /**
     * Sets the domain variables.
     * 
     * @param domainVariables the domain variables
     */
    public void setDomainVariables( final Map<String, Set<Serializable>> domainVariables ) {
        this.domainVariables = domainVariables;
    }

    /**
     * Sets the checks for target.
     * 
     * @param hasTarget the new checks for target
     */
    public void setHasTarget( final boolean hasTarget ) {
        this.hasTarget = hasTarget;
    }

    /**
     * Sets the unique query id.
     * 
     * @param id the new id
     */
    public void setId( final String id ) {
        this.id = id;
    }

    /**
     * Sets the int variables.
     * 
     * @param intVariables the new int variables
     */
    public void setIntVariables( final Set<String> intVariables ) {
        this.intVariables = intVariables;
    }

    /**
     * Sets the message variables.
     * 
     * @param messageVariables the message variables
     */
    public void setMessageVariables( final Map<String, String> messageVariables ) {
        this.messageVariables = messageVariables;
    }

    /**
     * Sets the output model name.
     * 
     * @param outputModelName the new output model name
     */
    public void setOutputModelName( final String outputModelName ) {
        this.outputModelName = outputModelName;
    }

    /**
     * Sets the string variables.
     * 
     * @param stringVariables the new string variables
     */
    public void setStringVariables( final Set<String> stringVariables ) {
        this.stringVariables = stringVariables;
    }

    /**
     * Sets the target keeps result.
     * 
     * @param targetKeepsResult the new target keeps result
     */
    public void setTargetKeepsResult( final boolean targetKeepsResult ) {
        this.targetKeepsResult = targetKeepsResult;
    }

    /**
     * Sets the target unique id.
     * 
     * @param targetUniqueId the new target unique id
     */
    public void setTargetUniqueId( final String targetUniqueId ) {
        this.targetUniqueId = targetUniqueId;
    }

    /**
     * Gets the strings constant.
     * 
     * @return the strings constant
     */
    public Map<Integer, String> getStringsConstant() {
        return stringsConstant;
    }

    /**
     * Sets the strings constant.
     * 
     * @param stringsConstant the strings constant
     */
    public void setStringsConstant( Map<Integer, String> stringsConstant ) {
        this.stringsConstant = stringsConstant;
    }
}
