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
package org.openspotlight.common.concurrent;

/**
 * Interface to be used to share the same lock object when there's a complex
 * object three with the same lock.
 *
 * <ul>
 * <li>When the new object contains another {@link LockContainer}, use its
 * "parent" container as a lock object</li>
 * <li>Synchronize public methods. Private ones are not mandatory</li>
 * <li>If the method is simple, avoid synchronization</li>
 * <li>If you are uncertain about the synchronization needs, just synchronize</li>
 * <li>If the public method contains only a simple delegation to an synchronized
 * object with the same lock, there's no need to synchronize it</li>
 * <li>If it call any external method or constructor, just synchronize</li>
 *
 * </ul>
 * How to use it? As the old fashion way:
 *
 * <pre>
 * private final Object lock;
 *
 * Constructor(){
 *   this.lock = new Object();
 * }
 * //or
 * Constructor(LockContainer parent){
 *   this.lock = parent.getLockObject();
 * }
 * synchronized(lock){
 * 	//...
 * }
 * </pre>
 *
 * <b>WARNING:</b> Use the {@link #getLockObject()} method to get the lock
 * object. Do not use the parent itself NEVER!
 *
 * @author feu
 *
 */
public interface LockContainer {

	/**
	 * Returns the lock object to be used on synchronized statements.
	 *
	 * @return
	 */
	Object getLockObject();

}
