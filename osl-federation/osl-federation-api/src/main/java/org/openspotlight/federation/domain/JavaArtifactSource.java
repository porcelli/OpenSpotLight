package org.openspotlight.federation.domain;

public class JavaArtifactSource extends ArtifactSource {
    private static final long serialVersionUID = 5899043044379454212L;

    private String            virtualMachineVersion;

    public String getVirtualMachineVersion() {
        return this.virtualMachineVersion;
    }

    public void setVirtualMachineVersion( final String virtualMachineVersion ) {
        this.virtualMachineVersion = virtualMachineVersion;
    }
}
