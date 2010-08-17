/**
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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openspotlight.common.Disposable;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;

/**
 * This class is used to wrap resources with lazy initialization. This also synchronizes the important methods using the Lock
 * passed on the constructor
 * 
 * @author feu
 * @param <R>
 */
public abstract class AtomicLazyResource<R> implements Disposable {

    private R          reference = null;
    private final Lock lock;

    /**
     * creates a new instance using the specified {@link Lock} .
     * 
     * @param lock
     */
    protected AtomicLazyResource(
                                  final Lock lock) {
        this.lock = lock;
    }

    /**
     * creates a new instance using the specified {@link Lock} .
     */
    protected AtomicLazyResource() {
        this.lock = new ReentrantLock();
    }

    /**
     * This method will be called before try to close resources. Why try? Because the wrapped resource "could" implement
     * {@link Disposable}, but this isn't mandatory.
     * 
     * @param mayBeNullReference
     */
    protected void afterTryToCloseResources(final R mayBeNullReference) {

    }

    @Override
    public final void closeResources() {
        synchronized (this.lock) {
            if (this.reference instanceof Disposable) {
                ((Disposable) this.reference).closeResources();
            }
            this.afterTryToCloseResources(this.reference);
        }

    }

    /**
     * Method used to newPair a new reference. It will be called once and within a synchronized block.
     * 
     * @return
     */
    protected abstract R createReference()
        throws Exception;

    public final R get()
        throws SLRuntimeException {
        try {
            this.lock.tryLock();
            if (this.reference == null) {
                try {
                    this.reference = this.createReference();
                } catch (final Exception e) {
                    throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
                }
            }
            return this.reference;
        } finally {
            this.lock.unlock();
        }
    }

    public final Lock getLockObject() {

        return this.lock;
    }

}
