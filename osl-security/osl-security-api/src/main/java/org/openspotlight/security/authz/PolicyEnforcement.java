package org.openspotlight.security.authz;

public interface PolicyEnforcement {
    public EnforcementResponse checkAccess( EnforcementContext enforcementContext ) throws EnforcementException;
}
