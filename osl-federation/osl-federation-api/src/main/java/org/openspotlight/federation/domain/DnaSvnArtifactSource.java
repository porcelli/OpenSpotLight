package org.openspotlight.federation.domain;

public class DnaSvnArtifactSource extends ArtifactSource {
    private static final long serialVersionUID = 780480895292133774L;

    private String            userName;
    private String            password;

    public String getPassword() {
        return this.password;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setPassword( final String password ) {
        this.password = password;
    }

    public void setUserName( final String userName ) {
        this.userName = userName;
    }

}
