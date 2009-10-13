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
package org.openspotlight.slql.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SLQLQueryInfo {
    private String                   id                  = null;
    private String                   targetUniqueId      = null;
    private Set<String>              intVariables        = new HashSet<String>();
    private Set<String>              decVariables        = new HashSet<String>();
    private Set<String>              boolVariables       = new HashSet<String>();
    private Set<String>              stringVariables     = new HashSet<String>();

    private Map<String, String>      messageVariables    = new HashMap<String, String>();
    private Map<String, Set<Object>> domainVariables     = new HashMap<String, Set<Object>>();

    private boolean                  hasTarget           = false;
    private boolean                  targetKeepsResult   = false;
    private String                   outputModelName     = null;
    private String                   defineTargetContent = null;
    private String                   content             = null;

    public boolean hasVariables() {
        if (intVariables.size() == 0 && decVariables.size() == 0 &&
            boolVariables.size() == 0 && stringVariables.size() == 0) {
            return false;
        }
        return true;
    }

    public boolean hasOutputModel() {
        if (outputModelName == null) {
            return false;
        }
        return true;
    }

    public String getOutputModelName() {
        return outputModelName;
    }

    public boolean hasTarget() {
        return hasTarget;
    }

    public Set<String> getIntVariables() {
        return intVariables;
    }

    public void setIntVariables( Set<String> intVariables ) {
        this.intVariables = intVariables;
    }

    public Set<String> getDecVariables() {
        return decVariables;
    }

    public void setDecVariables( Set<String> decVariables ) {
        this.decVariables = decVariables;
    }

    public Set<String> getBoolVariables() {
        return boolVariables;
    }

    public void setBoolVariables( Set<String> boolVariables ) {
        this.boolVariables = boolVariables;
    }

    public Set<String> getStringVariables() {
        return stringVariables;
    }

    public void setStringVariables( Set<String> stringVariables ) {
        this.stringVariables = stringVariables;
    }

    public Map<String, String> getMessageVariables() {
        return messageVariables;
    }

    public void setMessageVariables( Map<String, String> messageVariables ) {
        this.messageVariables = messageVariables;
    }

    public Map<String, Set<Object>> getDomainVariables() {
        return domainVariables;
    }

    public void setDomainVariables( Map<String, Set<Object>> domainVariables ) {
        this.domainVariables = domainVariables;
    }

    public boolean isHasTarget() {
        return hasTarget;
    }

    public void setHasTarget( boolean hasTarget ) {
        this.hasTarget = hasTarget;
    }

    public boolean isTargetKeepsResult() {
        return targetKeepsResult;
    }

    public void setTargetKeepsResult( boolean targetKeepsResult ) {
        this.targetKeepsResult = targetKeepsResult;
    }

    public String getDefineTargetContent() {
        return defineTargetContent;
    }

    public void setDefineTargetContent( String defineTargetContent ) {
        this.defineTargetContent = defineTargetContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent( String content ) {
        this.content = content;
    }

    public void setOutputModelName( String outputModelName ) {
        this.outputModelName = outputModelName;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getTargetUniqueId() {
        return targetUniqueId;
    }

    public void setTargetUniqueId( String targetUniqueId ) {
        this.targetUniqueId = targetUniqueId;
    }
}
