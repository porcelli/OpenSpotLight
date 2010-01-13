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
package org.openspotlight.federation.domain.artifact;

import java.io.Serializable;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Compare;
import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

/**
 * The Class SyntaxInformation.
 */
@Name("syntax_information")
public class SyntaxInformation implements Comparable<SyntaxInformation>,
		SimpleNodeType, Serializable {

	private static final long serialVersionUID = 9056717121341748618L;

	/** The hashcode. */
	private volatile int hashcode;

	/** The stream artifact. */
	private Artifact streamArtifact;

	/** The line start. */
	private int lineStart;

	/** The line end. */
	private int lineEnd;

	/** The column start. */
	private int columnStart;

	/** The column end. */
	private int columnEnd;

	/** The type. */
	private SyntaxInformationType type;

	/**
	 * Instantiates a new syntax information.
	 * 
	 * @param streamArtifact
	 *            the stream artifact
	 * @param lineStart
	 *            the line start
	 * @param lineEnd
	 *            the line end
	 * @param columnStart
	 *            the column start
	 * @param columnEnd
	 *            the column end
	 * @param type
	 *            the type
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@SuppressWarnings("boxing")
	public int compareTo(final SyntaxInformation o) {
		Compare.compareAll(Arrays.of(streamArtifact, lineStart, lineEnd,
				columnStart, columnEnd, type), Arrays.andOf(o.streamArtifact,
				o.lineStart, o.lineEnd, o.columnStart, o.columnEnd, o.type));
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("boxing")
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof SyntaxInformation)) {
			return false;
		}
		final SyntaxInformation that = (SyntaxInformation) o;
		final boolean result = Equals.eachEquality(Arrays.of(streamArtifact,
				lineStart, lineEnd, columnStart, columnEnd, type), Arrays
				.andOf(that.streamArtifact, that.lineStart, that.lineEnd,
						that.columnStart, that.columnEnd, that.type));
		return result;
	}

	/**
	 * Gets the column end.
	 * 
	 * @return the column end
	 */
	@KeyProperty
	public int getColumnEnd() {
		return columnEnd;
	}

	/**
	 * Gets the column start.
	 * 
	 * @return the column start
	 */
	@KeyProperty
	public int getColumnStart() {
		return columnStart;
	}

	/**
	 * Gets the line end.
	 * 
	 * @return the line end
	 */
	@KeyProperty
	public int getLineEnd() {
		return lineEnd;
	}

	/**
	 * Gets the line start.
	 * 
	 * @return the line start
	 */
	@KeyProperty
	public int getLineStart() {
		return lineStart;
	}

	/**
	 * Gets the stream artifact.
	 * 
	 * @return the stream artifact
	 */
	@ParentProperty
	public Artifact getStreamArtifact() {
		return streamArtifact;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@KeyProperty
	public SyntaxInformationType getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashcode;
	}

	public void setColumnEnd(final int columnEnd) {
		this.columnEnd = columnEnd;
	}

	public void setColumnStart(final int columnStart) {
		this.columnStart = columnStart;
	}

	public void setLineEnd(final int lineEnd) {
		this.lineEnd = lineEnd;
	}

	public void setLineStart(final int lineStart) {
		this.lineStart = lineStart;
	}

	public void setStreamArtifact(final Artifact streamArtifact) {
		this.streamArtifact = streamArtifact;
	}

	public void setType(final SyntaxInformationType type) {
		this.type = type;
	}

}
