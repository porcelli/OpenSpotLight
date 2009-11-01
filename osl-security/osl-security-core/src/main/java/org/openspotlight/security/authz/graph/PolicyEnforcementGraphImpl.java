package org.openspotlight.security.authz.graph;

import org.openspotlight.security.authz.EnforcementContext;
import org.openspotlight.security.authz.EnforcementException;
import org.openspotlight.security.authz.EnforcementResponse;
import org.openspotlight.security.authz.PolicyEnforcement;

public class PolicyEnforcementGraphImpl implements PolicyEnforcement {
    public EnforcementResponse checkAccess( EnforcementContext enforcementContext ) throws EnforcementException {
        return EnforcementResponse.GRANTED;
    }
}
