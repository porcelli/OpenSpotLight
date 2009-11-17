package org.openspotlight.federation.domain;

public class JavaArtifactSource extends ArtifactSource {
    private String virtualMachineVersion;

    public String getVirtualMachineVersion() {
        return this.virtualMachineVersion;
    }

    public void setVirtualMachineVersion( final String virtualMachineVersion ) {
        this.virtualMachineVersion = virtualMachineVersion;
    }
}
