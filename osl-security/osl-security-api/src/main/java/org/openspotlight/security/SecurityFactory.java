package org.openspotlight.security;

import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.security.authz.PolicyEnforcement;
import org.openspotlight.security.idm.SystemUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.security.idm.auth.IdentityManager;

public abstract class SecurityFactory extends AbstractFactory {

    public abstract IdentityManager createIdentityManager( JcrConnectionDescriptor jcrDescriptor );

    public abstract PolicyEnforcement createGraphPolicyEnforcement( JcrConnectionDescriptor jcrDescriptor );

    public abstract SystemUser createSystemUser();

    public abstract User createUser( String id );

}
