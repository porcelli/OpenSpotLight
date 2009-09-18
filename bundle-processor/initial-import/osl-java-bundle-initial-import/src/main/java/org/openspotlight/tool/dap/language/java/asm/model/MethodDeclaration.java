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
package org.openspotlight.tool.dap.language.java.asm.model;

import java.util.LinkedList;
import java.util.List;

public class MethodDeclaration {
    private String                name             = null;
    private int                   access;
    private TypeRef               returnType       = null;
    private List<MethodParameter> parameters       = new LinkedList<MethodParameter>();
    private boolean               isConstructor    = false;
    private List<TypeRef>         thrownExceptions = new LinkedList<TypeRef>();
    private List<TypeParameter>   typeParameters   = new LinkedList<TypeParameter>();
    private boolean               isPrivate        = false;

    public MethodDeclaration() {
    }

    public int getAccess() {
        return access;
    }

    public void setAccess( int access ) {
        this.access = access;
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public void setTypeParameters( List<TypeParameter> typeParameters ) {
        this.typeParameters = typeParameters;
    }

    public List<TypeRef> getThrownExceptions() {
        return thrownExceptions;
    }

    public void setThrownExceptions( List<TypeRef> thrownExceptions ) {
        this.thrownExceptions = thrownExceptions;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public void setConstructor( boolean isConstructor ) {
        this.isConstructor = isConstructor;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public TypeRef getReturnType() {
        return returnType;
    }

    public void setReturnType( TypeRef returnType ) {
        this.returnType = returnType;
    }

    public List<MethodParameter> getParameters() {
        return parameters;
    }

    public void setParameters( List<MethodParameter> parameters ) {
        this.parameters = parameters;
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("(");
        for (int i = 0; i < parameters.size(); i++) {
            sb.append(parameters.get(i).getDataType().getFullName());
            if (i != (parameters.size() - 1)) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public void setFullName( String fullName ) {
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate( boolean isPrivate ) {
        this.isPrivate = isPrivate;
    }
}
