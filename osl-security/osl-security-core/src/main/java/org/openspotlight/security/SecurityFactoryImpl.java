package org.openspotlight.security;

import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.security.authz.PolicyEnforcement;
import org.openspotlight.security.authz.graph.PolicyEnforcementGraphImpl;
import org.openspotlight.security.idm.SystemUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.security.idm.auth.IdentityManager;
import org.openspotlight.security.idm.auth.IdentityManagerSimpleImpl;

public class SecurityFactoryImpl extends SecurityFactory {

    @Override
    public IdentityManager createIdentityManager( JcrConnectionDescriptor jcrDescriptor ) {
        return new IdentityManagerSimpleImpl();
    }

    @Override
    public PolicyEnforcement createGraphPolicyEnforcement( JcrConnectionDescriptor jcrDescriptor ) {
        return new PolicyEnforcementGraphImpl();
    }

    @Override
    public SystemUser createSystemUser() {
        return new SystemUser() {
            public String getId() {
                return SystemUser.SYSTEM_USER_NAME;
            }
        };
    }

    @Override
    public User createUser( final String id ) {
        return new User() {

            public String getId() {
                return id;
            }
        };
    }
}
