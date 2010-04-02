package org.openspotlight.federation;

import org.junit.Test;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.domain.artifact.SyntaxInformationType;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.jcr.provider.SessionWithLock;
import org.openspotlight.persist.support.SimplePersistSupport;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ArtifactWithSyntaxPersisting {
	public static void main(final String... args) throws Exception {
		final ArtifactWithSyntaxPersisting test = new ArtifactWithSyntaxPersisting();
		test.shouldPersistLotsOfStuff();
	}

	Random r = new Random();

	String[] sampleLines = {
			"ABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCD\n",
			"     SDFSDFSDFSDFS FDS DFSDF\n", "     sdfsfasdfasd FDS DFSDF\n",
	"\n" };

	String[] samplePaths = { "dir1", "dir2", "dirthreebigger", "anotherPath" };

	int maxPath = 16;

	private String content = null;

	Set<StringArtifact> createLotsOfStuff() {
		final Set<StringArtifact> stuffList = new HashSet<StringArtifact>();
		for (int i = 0; i < 100; i++) {
			final StringArtifact sa = createNewDummyArtifact();
			stuffList.add(sa);
		}
		return stuffList;
	}

	private StringArtifact createNewDummyArtifact() {
		final int pathListSize = r.nextInt(maxPath - 2) + 2;
		final StringBuilder path = new StringBuilder();
		for (int i = 0; i < pathListSize; i++) {
			final int pathIndex = r.nextInt(samplePaths.length);
			path.append(samplePaths[pathIndex]);
			if (i + 1 != pathListSize) {
				path.append("/");
			}
		}
		final StringArtifact sa = Artifact.createArtifact(StringArtifact.class,
				path.toString(), ChangeType.INCLUDED);
		sa.getContent().setTransient(getContent());
		for (int i = 0; i < 16000; i++) {
			sa.addSyntaxInformation(i, i, i, i, SyntaxInformationType.COMMENT,
					null);
		}

		return sa;
	}

	private String getContent() {

		if (content == null) {
			final StringBuilder builder = new StringBuilder();
			for (int i = 0; i < 4000; i++) {
				final String line = sampleLines[r.nextInt(sampleLines.length)];
				builder.append(line);
			}
			content = builder.toString();
		}
		return content;
	}

	@Test
	public void shouldPersistLotsOfStuff() throws Exception {
		final Set<StringArtifact> lotsOfStuff = createLotsOfStuff();
		final JcrConnectionProvider provider = JcrConnectionProvider
		.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
		final SessionWithLock session = provider.openSession();
		int count = 0;
		final long start = System.currentTimeMillis();
		for (final StringArtifact a : lotsOfStuff) {
			count++;
			SimplePersistSupport.convertBeanToJcr("a/b/c", session, a);
			session.save();
			if (count % 10 == 0) {
				System.out.println(count);
			}
		}
		final long end = System.currentTimeMillis();
		System.out.println("spent on jcr convert and saving: " + (end - start)
				/ 1000 + "s");
	}

}
