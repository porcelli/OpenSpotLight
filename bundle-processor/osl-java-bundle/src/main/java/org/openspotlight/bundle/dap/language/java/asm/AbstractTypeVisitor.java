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
package org.openspotlight.bundle.dap.language.java.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * This abstracy class has no goal other than enables the real implementation be focused just on necessary methods.
 * 
 * @author porcelli
 */
public abstract class AbstractTypeVisitor implements ClassVisitor {

    /**
     * {@inheritDoc}
     */
    public void visit( int version,
                       int access,
                       String name,
                       String signature,
                       String superName,
                       String[] interfaces ) {
    }

    /**
     * {@inheritDoc}
     */
    public AnnotationVisitor visitAnnotation( String desc,
                                              boolean visible ) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void visitAttribute( Attribute attr ) {
    }

    /**
     * {@inheritDoc}
     */
    public void visitEnd() {
    }

    /**
     * {@inheritDoc}
     */
    public FieldVisitor visitField( int access,
                                    String name,
                                    String desc,
                                    String signature,
                                    Object value ) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void visitInnerClass( String name,
                                 String outerName,
                                 String innerName,
                                 int access ) {
    }

    /**
     * {@inheritDoc}
     */
    public MethodVisitor visitMethod( int access,
                                      String name,
                                      String desc,
                                      String signature,
                                      String[] exceptions ) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void visitOuterClass( String owner,
                                 String name,
                                 String desc ) {
    }

    /**
     * {@inheritDoc}
     */
    public void visitSource( String source,
                             String debug ) {
    }
}
