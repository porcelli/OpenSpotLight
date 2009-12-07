package org.openspotlight.federation.domain;

public class DbArtifactSource extends ArtifactSource {
    private static final long serialVersionUID = -430683831296857466L;

    private String            driverClass;
    private String            user;
    private String            password;
    private DatabaseType      type;
    private int               maxConnections;

    public String getDriverClass() {
        return this.driverClass;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    public String getPassword() {
        return this.password;
    }

    public DatabaseType getType() {
        return this.type;
    }

    public String getUser() {
        return this.user;
    }

    public void setDriverClass( final String driverClass ) {
        this.driverClass = driverClass;
    }

    public void setMaxConnections( final int maxConnections ) {
        this.maxConnections = maxConnections;
    }

    public void setPassword( final String password ) {
        this.password = password;
    }

    public void setType( final DatabaseType type ) {
        this.type = type;
    }

    public void setUser( final String user ) {
        this.user = user;
    }
}
