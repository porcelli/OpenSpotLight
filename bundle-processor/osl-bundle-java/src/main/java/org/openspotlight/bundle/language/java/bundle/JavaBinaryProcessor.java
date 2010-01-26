package org.openspotlight.bundle.language.java.bundle;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.asm.CompiledTypesExtractor;
import org.openspotlight.bundle.language.java.asm.model.TypeDefinition;
import org.openspotlight.bundle.language.java.resolver.JavaGraphNodeSupport;
import org.openspotlight.bundle.language.java.template.BeanShellTemplateSupport;
import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.InvocationCacheFactory;
import org.openspotlight.common.util.Sha1;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StreamArtifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;

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
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(artifact.getContent(), baos);
		final String uniqueContextName = UUID.nameUUIDFromBytes(
				Sha1.getSha1Signature(baos.toByteArray())).toString();
		interpreter.set("currentContextName", uniqueContextName);
		artifact.setUniqueContextName(uniqueContextName);
		final SLContext slContext = context.getGraphSession().createContext(
				uniqueContextName);
		slContext.getRootNode().setProperty(String.class,
				"classPathArtifactPath", artifact.getArtifactCompleteName());
		final SLGraphSession session = context.getGraphSession();
		final SLNode currentContextRootNode = session.createContext(
				uniqueContextName).getRootNode();
		final SLNode abstractContextRootNode = session.createContext(
				JavaConstants.ABSTRACT_CONTEXT).getRootNode();
		final JavaGraphNodeSupport helper = InvocationCacheFactory
				.createIntoCached(JavaGraphNodeSupport.class, new Class<?>[] {
						SLGraphSession.class, SLNode.class, SLNode.class },
						new Object[] { session, currentContextRootNode,
								abstractContextRootNode });
		interpreter.set("helper", helper);
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
