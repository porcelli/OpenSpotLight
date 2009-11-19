package org.openspotlight.federation.finder;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.openspotlight.common.LazyType;
import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.persist.support.SimplePersistSupport;

public class JcrSessionArtifactFinder<A extends Artifact> extends AbstractArtifactFinder<A>
    implements ArtifactFinderWithSaveCapabilitie<A> {

    private static String ROOT_PATH = SharedConstants.DEFAULT_JCR_ROOT_NAME + "/{0}/artifacts";

    public static <X extends Artifact> ArtifactFinder<X> createArtifactFinder( final Class<X> artifactType,
                                                                               final Repository repository,
                                                                               final Session session ) {
        return new JcrSessionArtifactFinder<X>(artifactType, session, repository);
    }

    public static String getArtifactRootPathFor( final Repository repository ) {
        return MessageFormat.format(ROOT_PATH, repository.getName());
    }

    public final String    rootPath;

    private final Class<A> artifactType;

    private final Session  session;

    private JcrSessionArtifactFinder(
                                      final Class<A> artifactType, final Session session, final Repository repository ) {
        this.session = session;
        this.artifactType = artifactType;
        this.rootPath = getArtifactRootPathFor(repository);
    }

    public boolean canAcceptArtifactSource( final ArtifactSource artifactSource ) {
        return true;
    }

    public A findByPath( final String path ) {
        Assertions.checkNotEmpty("path", path);
        try {
            final Set<A> found = SimplePersistSupport.findNodesByProperties(this.rootPath, this.session, this.artifactType,
                                                                            LazyType.DO_NOT_LOAD,
                                                                            new String[] {"artifactCompleteName"},
                                                                            new Object[] {path});
            if (found.size() > 1) {
                throw new Exception("returned more than one result");
            }
            if (found.size() == 0) {
                return null;
            }
            return found.iterator().next();
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public Class<A> getArtifactType() {
        return this.artifactType;
    }

    public Class<? extends ArtifactSource> getSourceType() {
        return null;
    }

    public Set<String> retrieveAllArtifactNames( final String initialPath ) {
        try {
            final String propertyName = MessageFormat.format(SimplePersistSupport.PROPERTY_VALUE, "artifactCompleteName");
            final String nodeName = SimplePersistSupport.getJcrNodeName(this.artifactType);
            final String xpath;
            if (initialPath != null) {
                xpath = MessageFormat.format("{0}//{1}[jcr:contains(@{2},''{3}'')]", this.rootPath, nodeName, propertyName,
                                             initialPath);
            } else {
                xpath = MessageFormat.format("{0}//{1}", this.rootPath, nodeName);
            }

            final Query query = this.session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
            final QueryResult result = query.execute();
            final NodeIterator nodes = result.getNodes();
            final Set<String> names = new HashSet<String>();
            while (nodes.hasNext()) {
                final Node nextNode = nodes.nextNode();
                if (nextNode.hasProperty(propertyName)) {
                    final String propVal = nextNode.getProperty(propertyName).getValue().getString();
                    names.add(propVal);
                }
            }
            return names;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public void save( final A artifactToSave ) {

        SimplePersistSupport.convertBeanToJcr(this.rootPath, this.session, artifactToSave);
    }
}
