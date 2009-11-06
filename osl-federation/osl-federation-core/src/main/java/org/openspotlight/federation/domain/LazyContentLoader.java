package org.openspotlight.federation.domain;

import java.util.Set;

/**
 * The Interface LazyContentLoader.
 */
public interface LazyContentLoader {

    /**
     * Load content.
     * 
     * @param hash the hash
     * @return the string
     */
    public String loadContent( String hash );

    /**
     * Load syntax informations.
     * 
     * @param hash the hash
     * @param artifact the artifact
     * @return the set< syntax information>
     */
    public Set<SyntaxInformation> loadSyntaxInformations( String hash,
                                                          Artifact artifact );
}