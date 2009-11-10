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
import org.openspotlight.common.util.Strings;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.persist.support.SimplePersistSupport;

public class JcrSessionArtifactFinder<A extends Artifact> extends AbstractArtifactFinder<A> {

    public static String ARTIFACTS_ROOT_PATH = SharedConstants.DEFAULT_JCR_ROOT_NAME + "/artifacts";

    public static <X extends Artifact> ArtifactFinder<X> createArtifactFinder( final Class<X> artifactType,
                                                                               final Session session ) {
        return new JcrSessionArtifactFinder<X>(artifactType, session);
    }

    private final Class<A> artifactType;

    private final Session  session;

    private JcrSessionArtifactFinder(
                                      final Class<A> artifactType, final Session session ) {
        this.session = session;
        this.artifactType = artifactType;
    }

    public boolean canAcceptArtifactSource( final ArtifactSource artifactSource ) {
        return true;
    }

    public A findByPath( final ArtifactSource artifactSource,
                         final String rawPath ) {
        Assertions.checkNotNull("artifactSource", artifactSource);
        Assertions.checkNotEmpty("rawPath", rawPath);
        try {
            final String path = artifactSource.getUniqueReference() + "/" + rawPath;
            final Set<A> found = SimplePersistSupport.findNodesByProperties(ARTIFACTS_ROOT_PATH, this.session, this.artifactType,
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

    public Set<String> retrieveAllArtifactNames( final ArtifactSource artifactSource,
                                                 final String initialPath ) {
        try {
            final String propertyName = MessageFormat.format(SimplePersistSupport.PROPERTY_VALUE, "artifactCompleteName");
            final String nodeName = SimplePersistSupport.getJcrNodeName(this.artifactType);
            final String nodeInitialPath = initialPath != null ? artifactSource.getUniqueReference() + "/" + initialPath : artifactSource.getUniqueReference();
            //FIXME test this
            final String xpath = MessageFormat.format("{0}//{1}[jcr:contains(@{2},''{3}'')]", ARTIFACTS_ROOT_PATH, nodeName,
                                                      propertyName, nodeInitialPath);
            final Query query = this.session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
            final QueryResult result = query.execute();
            final NodeIterator nodes = result.getNodes();
            final Set<String> names = new HashSet<String>();
            while (nodes.hasNext()) {
                final Node nextNode = nodes.nextNode();
                if (nextNode.hasProperty(propertyName)) {
                    final String propVal = nextNode.getProperty(propertyName).getValue().getString();
                    final String newPropVal = Strings.removeBegginingFrom(artifactSource.getUniqueReference() + "/", propVal);
                    names.add(newPropVal);
                }
            }
            return names;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }
}
