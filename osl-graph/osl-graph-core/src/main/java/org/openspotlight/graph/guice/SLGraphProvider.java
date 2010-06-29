package org.openspotlight.graph.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphImpl;
import org.openspotlight.graph.persistence.SLPersistentTree;
import org.openspotlight.graph.persistence.SLPersistentTreeFactory;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.authz.PolicyEnforcement;
import org.openspotlight.security.idm.SystemUser;
import org.openspotlight.security.idm.auth.IdentityManager;

/**
 * Created by User: feu - Date: Apr 29, 2010 - Time: 10:09:02 AM
 */
@Singleton
public class SLGraphProvider implements Provider<SLGraph>{
    private SLGraphImpl graph;

    @Inject
    public SLGraphProvider(SimplePersistFactory simplePersistFactory, JcrConnectionDescriptor descriptor, SecurityFactory securityFactory,SLPersistentTreeFactory factory){

        try{
        final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(descriptor);
        provider.openRepository();

        final SLPersistentTree tree = factory.createPersistentTree(descriptor);
        final SystemUser systemUser = securityFactory.createSystemUser();
        final IdentityManager identityManager = securityFactory.createIdentityManager(descriptor);
        final PolicyEnforcement graphPolicyEnforcement = securityFactory.createGraphPolicyEnforcement(descriptor);

        graph = new SLGraphImpl(tree, systemUser, identityManager, graphPolicyEnforcement,
                simplePersistFactory);


        }catch(Exception e){
            Exceptions.logAndThrowNew(e, SLRuntimeException.class);
        }

    }


    public SLGraph get() {
        return graph;  
    }
}
