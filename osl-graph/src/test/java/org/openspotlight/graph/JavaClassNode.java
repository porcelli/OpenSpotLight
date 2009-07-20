package org.openspotlight.graph;

import java.util.Date;

import org.openspotlight.graph.annotation.SLProperty;

//@SLTransient
//@CollatorLevel(IDENTICAL)
//@RenderHint(key="format", value="cube");
//@RenderHint(key="foreGroundColor" value="back");
public interface JavaClassNode extends JavaElementNode {
	
	public static final Integer MODIFIER_PUBLIC = 1;
	public static final Integer MODIFIER_PRIVATE = 2;
	public static final Integer MODIFIER_PROTECTED = 3;
	public static final Integer MODIFIER_DEFAULT = 4;
	
	//@SLProperty(collatorLevel=IDENTICAL)
	@SLProperty
	public String getClassName() throws SLGraphSessionException;
	public void setClassName(String className) throws SLGraphSessionException;
	
	@SLProperty
	public Integer getModifier() throws SLGraphSessionException;
	public void setModifier(Integer modifier) throws SLGraphSessionException;
	
	@SLProperty
	public Date getCreationTime() throws SLGraphSessionException;
	public void setCreationTime(Date creationTime) throws SLGraphSessionException;
}

