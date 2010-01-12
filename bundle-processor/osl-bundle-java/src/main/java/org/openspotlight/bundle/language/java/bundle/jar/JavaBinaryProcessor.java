package org.openspotlight.bundle.language.java.bundle.jar;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.openspotlight.bundle.language.java.asm.CompiledTypesExtractor;
import org.openspotlight.bundle.language.java.asm.model.TypeDefinition;
import org.openspotlight.bundle.language.java.template.BeanShellTemplateSupport;
import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StreamArtifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;

import bsh.Interpreter;

public class JavaBinaryProcessor implements
		BundleProcessorArtifactPhase<StreamArtifact> {

	public void beforeProcessArtifact(final StreamArtifact artifact) {

	}

	public void didFinishToProcessArtifact(final StreamArtifact artifact,
			final LastProcessStatus status) {

	}

	public Class<StreamArtifact> getArtifactType() {
		return StreamArtifact.class;
	}

	public LastProcessStatus processArtifact(final StreamArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) throws Exception {
		final CompiledTypesExtractor extractor = new CompiledTypesExtractor();
		final List<TypeDefinition> types = extractor.getJavaTypes(artifact
				.getContent(), artifact.getArtifactCompleteName());
		final String script = BeanShellTemplateSupport
				.createBeanShellScriptToImpotJar(types);
		final Interpreter interpreter = new Interpreter();
		interpreter.set("session", context.getGraphSession());
		interpreter.set("currentContextName", "example");
		final BufferedReader reader = new BufferedReader(new StringReader(
				script));
		String line;
		final Lock lock = context.getGraphSession().getLockObject();
		boolean hasError = false;
		synchronized (lock) {
			while ((line = reader.readLine()) != null) {
				try {
					interpreter.eval(line);
				} catch (final Exception e) {
					hasError = true;
					Exceptions.catchAndLog("error on line: " + e.getClass()
							+ " " + line, e);
					Exceptions.catchAndLog("error on line: " + e.getClass()
							+ line + " caused by: ", e.getCause());
				}
			}
		}
		return hasError ? LastProcessStatus.ERROR : LastProcessStatus.PROCESSED;
	}

}
