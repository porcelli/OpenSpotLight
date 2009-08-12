package org.openspotlight.graph.query.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspotlight.graph.query.SLSideType;

public class SLSelectStatementInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<SLSelectStatementTypeInfo> typeInfoList;
	private List<SLSelectStatementByLinkInfo> byLinkInfoList;
	private SLWhereStatementInfo whereInfo;
	
	public SLSelectStatementInfo() {
		typeInfoList = new ArrayList<SLSelectStatementTypeInfo>();
		byLinkInfoList = new ArrayList<SLSelectStatementByLinkInfo>();
	}
	
	public SLSelectStatementTypeInfo addType(String name) {
		SLSelectStatementTypeInfo typeInfo = new SLSelectStatementTypeInfo(name);
		typeInfoList.add(typeInfo);
		return typeInfo;
	}
	
	public SLSelectStatementByLinkInfo addByLink(String name) {
		SLSelectStatementByLinkInfo byLinkInfo = new SLSelectStatementByLinkInfo(name);
		byLinkInfoList.add(byLinkInfo);
		return byLinkInfo;
	}
	
	public SLWhereStatementInfo addWhereInfo() {
		this.whereInfo = new SLWhereStatementInfo();
		return this.whereInfo;
	}

	public List<SLSelectStatementTypeInfo> getTypeInfoList() {
		return typeInfoList;
	}

	public List<SLSelectStatementByLinkInfo> getByLinkInfoList() {
		return byLinkInfoList;
	}

	public SLWhereStatementInfo getWhereStatementInfo() {
		return whereInfo;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		// SELECT clause ...
		buffer.append("SELECT\n");
		
		// types ...
		for (int i = 0; i < typeInfoList.size(); i++) {
			SLSelectStatementTypeInfo typeInfo = typeInfoList.get(i);
			if (i > 0) buffer.append(",\n");
			buffer.append('\t').append('"').append(typeInfo.getName());
			if (typeInfo.isSubTypes()) buffer.append(".*");
			buffer.append('"');
		}
		
		// by links ...
		int byLinkSize = byLinkInfoList.size();
		if (byLinkSize > 0) {
			buffer.append("\n\tby link\n");
			for (int i = 0; i < byLinkInfoList.size(); i++) {
				SLSelectStatementByLinkInfo byLinkInfo = byLinkInfoList.get(i);
				if (i > 0) buffer.append(",\n");
				buffer.append("\t\t").append(byLinkInfo.getName());
				
				// sides ...
				buffer.append('(');
				List<SLSideType> sides = new ArrayList<SLSideType>(byLinkInfo.getSides());
				for (int j = 0; j < sides.size(); j++) {
					SLSideType side = sides.get(j);
					if (j > 0) buffer.append('/');
					buffer.append(side.symbol());
				}
				buffer.append(')');
			}
			
			// where ...
			buffer.append("\nWHERE\n");
			buffer.append(whereInfo);
		}
		
		return buffer.toString();
	}
}





