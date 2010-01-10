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
package org.openspotlight.federation.finder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Files;
import org.openspotlight.common.util.Strings;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.PathElement;
import org.openspotlight.federation.domain.artifact.StringArtifact;

public class LocalSourceStreamArtifactFinder extends
		AbstractArtifactFinder<StringArtifact> {

	private final ArtifactSource artifactSource;

	public LocalSourceStreamArtifactFinder(final ArtifactSource artifactSource) {
		super(artifactSource.getRepository().getName());
		Assertions.checkNotNull("artifactSource", artifactSource);
		Assertions.checkCondition("fileExists", new File(artifactSource
				.getInitialLookup()
				+ "/").exists());
		this.artifactSource = artifactSource;
	}

	public void closeResources() {
		// TODO Auto-generated method stub

	}

	public Class<StringArtifact> getArtifactType() {
		return StringArtifact.class;
	}

	public Class<? extends ArtifactSource> getSourceType() {
		return null;
	}

	protected StringArtifact internalFindByPath(final String rawPath) {
		Assertions.checkNotEmpty("rawPath", rawPath);
		for (final ChangeType t : ChangeType.values()) {
			try {

				final String location = MessageFormat.format("{0}/{1}/{2}",
						artifactSource.getInitialLookup(), t.toString()
								.toLowerCase(), rawPath);

				final File file = new File(location);
				if (!file.exists()) {
					continue;
				}

				final FileInputStream resource = new FileInputStream(file);
				final BufferedReader reader = new BufferedReader(
						new InputStreamReader(resource));
				final StringBuilder buffer = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
					buffer.append('\n');
				}
				final String content = buffer.toString();
				final StringArtifact streamArtifact = Artifact.createArtifact(
						StringArtifact.class, rawPath, t);
				streamArtifact.setContent(content);
				return streamArtifact;
			} catch (final Exception e) {
				throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
			}
		}

		return null;

	}

	protected StringArtifact internalFindByRelativePath(
			final StringArtifact relativeTo, final String path) {
		Assertions.checkNotNull("artifactSource", artifactSource);
		Assertions.checkNotNull("relativeTo", relativeTo);
		Assertions.checkNotEmpty("path", path);
		final String newPath = PathElement.createRelativePath(
				relativeTo.getParent(), path).getCompletePath();

		return findByPath(newPath);
	}

	protected Set<StringArtifact> internalListByPath(final String rawPath) {
		try {
			final Set<StringArtifact> result = new HashSet<StringArtifact>();
			final Set<String> allFilePaths = retrieveAllArtifactNames(rawPath);
			for (final String path : allFilePaths) {
				final StringArtifact sa = findByPath(path);
				result.add(sa);
			}
			return result;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	protected Set<String> internalRetrieveAllArtifactNames(
			final String initialPath) {
		final String rawPath = initialPath == null ? "." : initialPath;
		try {
			final Set<String> result = new HashSet<String>();
			for (final ChangeType t : ChangeType.values()) {

				final String location = MessageFormat.format("{0}/{1}/{2}",
						artifactSource.getInitialLookup(), t.toString()
								.toLowerCase(), rawPath);

				final String pathToRemove = Files
						.getNormalizedFileName(new File(artifactSource
								.getInitialLookup()))
						+ "/" + t.toString().toLowerCase() + "/";
				final Set<String> pathList = Files.listFileNamesFrom(location);

				for (final String p : pathList) {
					final String correctRelativePath = Strings
							.removeBegginingFrom(pathToRemove, p);
					result.add(correctRelativePath);
				}
			}
			return result;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}
}
