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
package org.openspotlight.bundle.language.java.asm.model;

/**
 * Model class that reprents a Field Declaration inside a {@link TypeDefinition}.
 * 
 * @author porcelli
 */
public class FieldDeclaration {

    /** The field name. */
    private String        name      = null;

    /** The accessor. */
    private int           access;

    /** The isPrivate indicates if field is private. */
    private boolean       isPrivate = false;

    /** The type identifies the data type of field. */
    private TypeReference type      = null;

    /**
     * Instantiates a new field declaration.
     */
    public FieldDeclaration() {
    }

    /**
     * Gets the field name.
     * 
     * @return the field name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the field name.
     * 
     * @param name the new field name
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Gets the accessor.
     * 
     * @return the accessor
     */
    public int getAccess() {
        return access;
    }

    /**
     * Sets the accessor.
     * 
     * @param access the new accessor
     */
    public void setAccess( int access ) {
        this.access = access;
    }

    /**
     * Checks if is private.
     * 
     * @return true, if is private
     */
    public boolean isPrivate() {
        return isPrivate;
    }

    /**
     * Sets the private.
     * 
     * @param isPrivate indicate if it is a private field
     */
    public void setPrivate( boolean isPrivate ) {
        this.isPrivate = isPrivate;
    }

    /**
     * Gets the data type of the field.
     * 
     * @return the data type
     */
    public TypeReference getType() {
        return type;
    }

    /**
     * Sets the data type.
     * 
     * @param type the new data type
     */
    public void setType( TypeReference type ) {
        this.type = type;
    }
}
