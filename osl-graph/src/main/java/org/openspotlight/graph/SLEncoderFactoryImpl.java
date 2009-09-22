package org.openspotlight.graph;


public class SLEncoderFactoryImpl implements SLEncoderFactory {

	public SLEncoder getFakeEncoder() {
		return new SLFakeEncoder();
	}

	public SLEncoder getUUIDEncoder() {
		return new SLUUIDEncoder();
	}
}