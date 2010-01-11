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

import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.HashCodes;

/**
 * Class to be used as a hierarchy to priorities. One example is under thread
 * pool task executor. Imagine that a processor executes tasks with priorities.
 * It will execute first the task one, and after task two, and so on. Sometimes
 * should be good to execute task 2.1 before task 3 and after task 2. This
 * behavior can be achieved here by instantiating a
 * {@link Priority#createPriority(int...)} passing 2,1 as a parameter.
 * 
 * @author feu
 * 
 */
public final class Priority implements Comparable<Priority> {

	public static Priority createPriority(final int... priorities) {
		Assertions.checkNotNull("priorities", priorities);
		Assertions.checkCondition("prioritiesNotEmpty", priorities.length > 0);
		Priority parent = null;
		for (int i = priorities.length; i != 0; i--) {
			parent = new Priority(priorities[i - 1], parent);
		}
		return parent;
	}

	private final int priorityNumber;

	private final int hashcode;

	private final Priority subPriority;

	private Priority(final int priorityNumber, final Priority subPriority) {
		Assertions.checkCondition("positiveValue", priorityNumber > 0);
		this.priorityNumber = priorityNumber;
		this.subPriority = subPriority;
		hashcode = HashCodes.hashOf(priorityNumber,
				subPriority != null ? subPriority.hashcode : 0);
	}

	private int compareIntValues(final int thisVal, final int thatVal) {
		return thisVal < thatVal ? -1 : thisVal == thatVal ? 0 : 1;

	}

	public int compareTo(final Priority o) {
		Priority thisPriority = this;
		Priority thatPriority = o;
		while (true) {
			final int thisVal = thisPriority != null ? thisPriority.priorityNumber
					: 0;
			final int thatVal = thatPriority != null ? thatPriority.priorityNumber
					: 0;
			final int result = compareIntValues(thisVal, thatVal);
			if (result != 0) {
				return result;
			}
			thisPriority = thisPriority.subPriority;
			thatPriority = thatPriority.subPriority;
			if (thisPriority == null && thatPriority == null) {
				return 0;
			}
			if (thisPriority == null || thatPriority == null) {
				if (thisPriority == null) {
					return compareIntValues(1, 0);
				}
				return compareIntValues(0, 1);
			}
		}
	}

	public boolean equals(final Object o) {
		if (!(o instanceof Priority)) {
			return false;
		}
		return compareTo((Priority) o) == 0;
	}

	@Override
	public int hashCode() {
		return hashcode;
	}

}
