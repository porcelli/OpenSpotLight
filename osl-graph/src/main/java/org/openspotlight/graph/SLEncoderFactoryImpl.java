package org.openspotlight.graph;

import org.openspotlight.graph.SLEncoder;
import org.openspotlight.graph.SLEncoderFactory;

public class SLEncoderFactoryImpl implements SLEncoderFactory {

	public SLEncoder getFakeEncoder() {
		return new SLFakeEncoder();
	}

	public SLEncoder getUUIDEncoder() {
		return new SLUUIDEncoder();
	}
}