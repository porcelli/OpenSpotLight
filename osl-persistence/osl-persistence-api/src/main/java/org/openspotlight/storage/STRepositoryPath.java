package org.openspotlight.storage;

/**
 * Created by User: feu - Date: Apr 19, 2010 - Time: 9:34:57 AM
 */
public class STRepositoryPath {

    public static STRepositoryPath repositoryPath(String repositoryPath){
        return new STRepositoryPath(repositoryPath);
    }
    
    private final String repositoryPath;

    public STRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public String getRepositoryPathAsString() {
        return repositoryPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        STRepositoryPath that = (STRepositoryPath) o;

        if (repositoryPath != null ? !repositoryPath.equals(that.repositoryPath) : that.repositoryPath != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return repositoryPath != null ? repositoryPath.hashCode() : 0;
    }
}
