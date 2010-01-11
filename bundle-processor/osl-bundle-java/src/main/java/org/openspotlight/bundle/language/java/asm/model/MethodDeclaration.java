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

import java.util.LinkedList;
import java.util.List;

/**
 * Model class that reprents a Method Declaration inside a {@link TypeDefinition}.
 * 
 * @author porcelli
 */
public class MethodDeclaration {

    /** The method name. */
    private String                          name             = null;

    /** The accessor. */
    private int                             access;

    /** The isPrivate indicates if method is private. */
    private boolean                         isPrivate        = false;

    /** The isConstructor defines if this method is a contructor. */
    private boolean                         isConstructor    = false;

    /** The method return type. */
    private TypeReference                   returnType       = null;

    /** The parameters. */
    private List<MethodParameterDefinition> parameters       = new LinkedList<MethodParameterDefinition>();

    /** The exceptions that can be throwed by this method. */
    private List<TypeReference>             thrownExceptions = new LinkedList<TypeReference>();

    /** The type parameters(related to generics). */
    private List<TypeParameter>             typeParameters   = new LinkedList<TypeParameter>();

    /**
     * Instantiates a new method declaration.
     */
    public MethodDeclaration() {
    }

    /**
     * Gets the accessor.
     * 
     * @return the accessor
     */
    public int getAccess() {
        return this.access;
    }

    public String getFullName() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.name);
        sb.append("(");
        for (int i = 0; i < this.parameters.size(); i++) {
            sb.append(this.parameters.get(i).getDataType().getFullName());
            if (i != (this.parameters.size() - 1)) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Gets the method name.
     * 
     * @return the method name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the parameters.
     * 
     * @return the parameters
     */
    public List<MethodParameterDefinition> getParameters() {
        return this.parameters;
    }

    /**
     * Gets the return type.
     * 
     * @return the return type
     */
    public TypeReference getReturnType() {
        return this.returnType;
    }

    /**
     * Gets the thrown exceptions.
     * 
     * @return the thrown exceptions
     */
    public List<TypeReference> getThrownExceptions() {
        return this.thrownExceptions;
    }

    /**
     * Gets the type parameters. This data is related to Generics.
     * 
     * @return the type parameters
     */
    public List<TypeParameter> getTypeParameters() {
        return this.typeParameters;
    }

    /**
     * Checks if is constructor.
     * 
     * @return true, if is constructor
     */
    public boolean isConstructor() {
        return this.isConstructor;
    }

    /**
     * Checks if is private.
     * 
     * @return true, if is private
     */
    public boolean isPrivate() {
        return this.isPrivate;
    }

    /**
     * Sets the accessor.
     * 
     * @param access the new accessor
     */
    public void setAccess( final int access ) {
        this.access = access;
    }

    /**
     * Sets the constructor.
     * 
     * @param isConstructor indicates if it is a constructor
     */
    public void setConstructor( final boolean isConstructor ) {
        this.isConstructor = isConstructor;
    }

    public void setFullName( final String fullName ) {
    }

    /**
     * Sets the method name.
     * 
     * @param name the new method name
     */
    public void setName( final String name ) {
        this.name = name;
    }

    /**
     * Sets the parameters.
     * 
     * @param parameters the new parameters
     */
    public void setParameters( final List<MethodParameterDefinition> parameters ) {
        this.parameters = parameters;
    }

    /**
     * Sets the private.
     * 
     * @param isPrivate indicates if it is a private method
     */
    public void setPrivate( final boolean isPrivate ) {
        this.isPrivate = isPrivate;
    }

    /**
     * Sets the return type.
     * 
     * @param returnType the new return type
     */
    public void setReturnType( final TypeReference returnType ) {
        this.returnType = returnType;
    }

    /**
     * Sets the thrown exceptions.
     * 
     * @param thrownExceptions the new thrown exceptions
     */
    public void setThrownExceptions( final List<TypeReference> thrownExceptions ) {
        this.thrownExceptions = thrownExceptions;
    }

    /**
     * Sets the type parameters. This data is related to Generics.
     * 
     * @param typeParameters the new type parameters
     */
    public void setTypeParameters( final List<TypeParameter> typeParameters ) {
        this.typeParameters = typeParameters;
    }

}
