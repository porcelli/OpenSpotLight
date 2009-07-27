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

package org.openspotlight.federation.data.load.db;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturn;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Pojo class to store the script to get database metadata for a database type.
 * This class should be getter by {@link DatabaseMetadataScriptManager}.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public final class DatabaseMetadataScript {
    
    private Map<ScriptType, List<String>> scripts = new EnumMap<ScriptType, List<String>>(
            ScriptType.class);
    
    private boolean immutable = false;
    
    private String name;
    
    private List<CustomTypeScript> customTypeCreationScripts;
    
    private String tableCreationScript;
    private String indexCreationScript;
    private String triggerCreationScript;
    private String procedureCreationScript;
    private String functionCreationScript;
    private String viewCreationScript;
    
    /**
     * Function to fill script map in a correct way
     */
    private void fillScriptMap() {
        this.scripts = new EnumMap<ScriptType, List<String>>(ScriptType.class);
        this
                .putItemOnScripMap(ScriptType.FUNCTION,
                        this.functionCreationScript);
        this.putItemOnScripMap(ScriptType.INDEX, this.indexCreationScript);
        this.putItemOnScripMap(ScriptType.PROCEDURE,
                this.procedureCreationScript);
        this.putItemOnScripMap(ScriptType.TABLE, this.tableCreationScript);
        this.putItemOnScripMap(ScriptType.TRIGGER, this.triggerCreationScript);
        this.putItemOnScripMap(ScriptType.VIEW, this.viewCreationScript);
        if (!this.customTypeCreationScripts.isEmpty()) {
            List<String> customScripts = new ArrayList<String>(
                    this.customTypeCreationScripts.size());
            for (final CustomTypeScript script : this.customTypeCreationScripts) {
                customScripts.add(script.getSql().trim().replaceAll("\n", "")); //$NON-NLS-1$//$NON-NLS-2$
            }
            customScripts = this.scripts.put(ScriptType.CUSTOM,
                    unmodifiableList(customScripts));
        }
        this.scripts = unmodifiableMap(this.scripts);
    }
    
    /**
     * 
     * @param type
     * @return the script list by its type
     */
    public List<String> findScriptListByType(final ScriptType type) {
        checkNotNull("type", type); //$NON-NLS-1$
        return this.scripts.get(type);
    }
    
    /**
     * 
     * @return the list of select custom types creation scripts
     */
    public List<CustomTypeScript> getCustomTypeCreationScripts() {
        return this.customTypeCreationScripts;
    }
    
    /**
     * 
     * @return the function creation select script
     */
    public String getFunctionCreationScript() {
        return this.functionCreationScript;
    }
    
    /**
     * 
     * @return the index creation select script
     */
    public String getIndexCreationScript() {
        return this.indexCreationScript;
    }
    
    /**
     * 
     * @return the unique name of this database
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * 
     * @return the procedure creation select script
     */
    
    public String getProcedureCreationScript() {
        return this.procedureCreationScript;
    }
    
    /**
     * 
     * @return the table creation select script
     */
    public String getTableCreationScript() {
        return this.tableCreationScript;
    }
    
    /**
     * 
     * @return the trigger creation select script
     */
    public String getTriggerCreationScript() {
        return this.triggerCreationScript;
    }
    
    /**
     * 
     * @return the view creation select script
     */
    public String getViewCreationScript() {
        return this.viewCreationScript;
    }
    
    /**
     * helper function to fill script map
     * 
     * @param type
     * @param sql
     */
    private void putItemOnScripMap(final ScriptType type, final String sql) {
        assert type != null;
        
        if (sql != null) {
            this.scripts.put(type, unmodifiableList(asList(sql)));
        } else {
            final List<String> empty = emptyList();
            this.scripts.put(type, unmodifiableList(empty));
        }
        
    }
    
    /**
     * Sets the custom type creation select script.
     * 
     * @param customTypeCreationScripts
     */
    public void setCustomTypeCreationScripts(
            final List<CustomTypeScript> customTypeCreationScripts) {
        if (this.immutable) {
            throw logAndReturn(new UnsupportedOperationException());
        }
        this.customTypeCreationScripts = customTypeCreationScripts;
    }
    
    /**
     * Sets the function creation select script.
     * 
     * @param functionCreationScript
     */
    public void setFunctionCreationScript(final String functionCreationScript) {
        if (this.immutable) {
            throw logAndReturn(new UnsupportedOperationException());
        }
        this.functionCreationScript = functionCreationScript;
    }
    
    /**
     * Transform this pojo in a immutable pojo.
     */
    public void setImmutable() {
        if (!this.immutable) {
            this.immutable = true;
            if (this.customTypeCreationScripts == null) {
                this.customTypeCreationScripts = emptyList();
            }
            this.customTypeCreationScripts = unmodifiableList(this.customTypeCreationScripts);
            
            for (final CustomTypeScript script : this.customTypeCreationScripts) {
                script.setImmutable();
            }
            this.fillScriptMap();
            
        }
        
    }
    
    /**
     * Sets the index creation select script.
     * 
     * @param indexCreationScript
     */
    public void setIndexCreationScript(final String indexCreationScript) {
        if (this.immutable) {
            throw logAndReturn(new UnsupportedOperationException());
        }
        this.indexCreationScript = indexCreationScript;
    }
    
    /**
     * Sets the name of this database.
     * 
     * @param name
     */
    public void setName(final String name) {
        if (this.immutable) {
            throw logAndReturn(new UnsupportedOperationException());
        }
        this.name = name;
    }
    
    /**
     * Sets the procedure creation select script.
     * 
     * @param procedureCreationScript
     */
    public void setProcedureCreationScript(final String procedureCreationScript) {
        if (this.immutable) {
            throw logAndReturn(new UnsupportedOperationException());
        }
        this.procedureCreationScript = procedureCreationScript;
    }
    
    /**
     * Sets the table creation select script.
     * 
     * @param tableCreationScript
     */
    public void setTableCreationScript(final String tableCreationScript) {
        if (this.immutable) {
            throw logAndReturn(new UnsupportedOperationException());
        }
        this.tableCreationScript = tableCreationScript;
    }
    
    /**
     * Sets the trigger creation select script.
     * 
     * @param triggerCreationScript
     */
    public void setTriggerCreationScript(final String triggerCreationScript) {
        if (this.immutable) {
            throw logAndReturn(new UnsupportedOperationException());
        }
        this.triggerCreationScript = triggerCreationScript;
    }
    
    /**
     * Sets the view creation select script.
     * 
     * @param viewCreationScript
     */
    public void setViewCreationScript(final String viewCreationScript) {
        if (this.immutable) {
            throw logAndReturn(new UnsupportedOperationException());
        }
        this.viewCreationScript = viewCreationScript;
    }
    
}
