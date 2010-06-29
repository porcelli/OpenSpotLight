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

import org.openspotlight.common.Disposable;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class wraps a factory to describe if the produced item should be shared between threads or not. In case of multi threaded
 * environment it will store the item on a thread local variable, but it can close all created items, if this item implements
 * {@link Disposable};
 * 
 * @author feu
 * @param <T>
 */
public class MultipleProvider<T> implements Disposable {

    public boolean useOnePerThread() {
        return useOnePerThread;
    }

    /**
     * constructor
     * 
     * @param factory
     */
    public MultipleProvider(
                             ItemFactory<T> factory ) {
        this.factory = factory;
        this.useOnePerThread = factory.useOnePerThread();
        if (useOnePerThread) {
            this.threadLocalItem = null;
            this.singleItem = factory.createNew();
        } else {
            this.threadLocalItem = new ThreadLocal<T>();
            this.singleItem = null;
        }
    }

    /**
     * Item factory class to describe if the item should be shared between threads or not.
     * 
     * @author feu
     * @param <T>
     */
    public interface ItemFactory<T> {

        public T createNew();

        public boolean useOnePerThread();

    }

    private CopyOnWriteArrayList<Disposable> openedItems = new CopyOnWriteArrayList<Disposable>();

    private final boolean                    useOnePerThread;

    private final ItemFactory<T>             factory;

    private final ThreadLocal<T>             threadLocalItem;

    private final T                          singleItem;

    /**
     * returns the item. If this is a multithreaded environment it will newPair one if this one doesn't exists.
     * 
     * @return
     */
    public synchronized T get() {
        if (useOnePerThread) {
            return singleItem;
        } else {
            T t = threadLocalItem.get();
            if (t == null) {
                t = factory.createNew();
                if (t instanceof Disposable) {
                    openedItems.add((Disposable)t);
                }
                threadLocalItem.set(t);
            }
            return t;
        }
    }

    /**
     * It will close all opened items and its factory in case of each one implemented or not {@link Disposable}.
     */
    public synchronized void closeResources() {
        if (useOnePerThread) {
            if (singleItem instanceof Disposable) {
                ((Disposable)singleItem).closeResources();
            } else {
                for (Disposable d : openedItems) {
                    d.closeResources();
                }
            }
        }
        if (factory instanceof Disposable) {
            ((Disposable)factory).closeResources();
        }

    }

}
