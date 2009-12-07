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
package org.openspotlight.bundle.dap.language.java.asm.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a type reference parameterized (generics).
 * 
 * @author porcelli
 */
public class ParameterizedTypeReference implements TypeReference {

    /** The type arguments. */
    private List<TypeReference> typeArguments = new LinkedList<TypeReference>();

    /** The enclosed type. */
    private TypeReference       type          = null;

    /**
     * Instantiates a new parameterized type reference.
     * 
     * @param typeArguments the type arguments
     * @param type the type
     */
    public ParameterizedTypeReference(
                                       List<TypeReference> typeArguments, TypeReference type ) {
        this.typeArguments = typeArguments;
        this.type = type;
    }

    /**
     * Gets the type arguments.
     * 
     * @return the type arguments
     */
    public List<TypeReference> getTypeArguments() {
        return typeArguments;
    }

    /**
     * Sets the type arguments.
     * 
     * @param typeArguments the new type arguments
     */
    public void setTypeArguments( List<TypeReference> typeArguments ) {
        this.typeArguments = typeArguments;
    }

    /**
     * Gets the enclosed type.
     * 
     * @return the enclosed type
     */
    public TypeReference getType() {
        return type;
    }

    /**
     * Sets the enclosed type.
     * 
     * @param type the new enclosed type
     */
    public void setType( TypeReference type ) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.tool.dap.language.java.asm.model.TypeReference#getFullName()
     */
    public String getFullName() {
        return type.getFullName() + getParameterizedFormat();
    }

    /* (non-Javadoc)
     * @see org.openspotlight.tool.dap.language.java.asm.model.TypeReference#getName()
     */
    public String getName() {
        return type.getName() + getParameterizedFormat();
    }

    /**
     * Genarates the parameterized format.
     * 
     * @return the parameterized format
     */
    private String getParameterizedFormat() {
        StringBuffer sb = new StringBuffer();
        sb.append('<');
        for (int i = 0; i < typeArguments.size(); i++) {
            sb.append(typeArguments.get(i).getFullName());
            if (i < typeArguments.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append('>');
        return sb.toString();
    }
}
