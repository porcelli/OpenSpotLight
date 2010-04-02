/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.graph;

import com.ibm.icu.text.Normalizer;
import org.openspotlight.graph.annotation.SLCollator;

import java.lang.reflect.Method;
import java.text.Collator;
import java.util.Locale;


/**
 * The Class SLCollatorSupport.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLCollatorSupport {
	
	/**
	 * Gets the collator key prop name.
	 * 
	 * @param name the name
	 * @param collatorStrength the collator strength
	 * 
	 * @return the collator key prop name
	 */
	public static String getCollatorKeyPropName(String name, int collatorStrength) {
		String propName = null;
		switch (collatorStrength) {
		case Collator.PRIMARY:
			propName = SLCommonSupport.toUserPropertyName(name, SLConsts.PROPERTY_NAME_PRIMARY_KEY);
			break;
		case Collator.SECONDARY:
			propName = SLCommonSupport.toUserPropertyName(name, SLConsts.PROPERTY_NAME_SECONDARY_KEY);			
			break;
		case Collator.TERTIARY:
			propName = SLCommonSupport.toUserPropertyName(name, SLConsts.PROPERTY_NAME_TERTIARY_KEY);			
			break;
		default:
			propName = SLCommonSupport.toUserPropertyName(name);
			break;
		}
		return propName;
	}

	/**
	 * Gets the collator description prop name.
	 * 
	 * @param name the name
	 * @param collatorStrength the collator strength
	 * 
	 * @return the collator description prop name
	 */
	public static String getCollatorDescriptionPropName(String name, int collatorStrength) {
		String propName = null;
		switch (collatorStrength) {
		case Collator.PRIMARY:
			propName = SLCommonSupport.toUserPropertyName(name, SLConsts.PROPERTY_NAME_PRIMARY_DESCRIPTION);
			break;
		case Collator.SECONDARY:
			propName = SLCommonSupport.toUserPropertyName(name, SLConsts.PROPERTY_NAME_SECONDARY_DESCRIPTION);			
			break;
		case Collator.TERTIARY:
			propName = SLCommonSupport.toUserPropertyName(name, SLConsts.PROPERTY_NAME_TERTIARY_DESCRIPTION);			
			break;
		default:
			propName = SLCommonSupport.toUserPropertyName(name);
			break;
		}
		return propName;
	}

	/**
	 * Gets the collator key.
	 * 
	 * @param strength the strength
	 * @param value the value
	 * 
	 * @return the collator key
	 */
	public static String getCollatorKey(int strength, String value) {
		Collator collator = Collator.getInstance(Locale.US);
		collator.setStrength(strength);
		return new String(collator.getCollationKey(value).toByteArray());
	}
	
	
	/**
	 * Gets the collator description.
	 * 
	 * @param strength the strength
	 * @param value the value
	 * 
	 * @return the collator description
	 */
	public static String getCollatorDescription(int strength, String value) {
		switch (strength) {
		case Collator.IDENTICAL:
			return value;
		case Collator.PRIMARY:
			return removeAccents(value).toLowerCase();
		case Collator.SECONDARY:
			return value.toLowerCase();
		case Collator.TERTIARY:
			return removeAccents(value);
		}
		return null;
	}
	
	/**
	 * Removes the accents.
	 * 
	 * @param text the text
	 * 
	 * @return the string
	 */
	public static String removeAccents(String text) {
	    return Normalizer.decompose(text, false, 0).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
	/**
	 * Gets the property collator.
	 * 
	 * @param nodeType the node type
	 * @param name the name
	 * 
	 * @return the property collator
	 */
	public static Collator getPropertyCollator(Class<?> nodeType, String name) {
		SLCollator collatorAnnot = null;
		Method method = getGetterMethod(nodeType, name);
		if (method != null) {
			collatorAnnot = method.getAnnotation(SLCollator.class);
		}
		if (collatorAnnot == null) {
			method = getSetterMethod(nodeType, name);
			if (method != null) {
				collatorAnnot = method.getAnnotation(SLCollator.class);
			}
		}
		Locale locale;
		int strength;
		if (collatorAnnot == null) {
			locale = Locale.US;
			strength = Collator.IDENTICAL;
		}
		else {
			locale = new Locale(collatorAnnot.locale());
			strength = collatorAnnot.strength();
		}
		Collator collator = Collator.getInstance(locale);
		collator.setStrength(strength);
		return collator;
	}
	
	/**
	 * Gets the node collator.
	 * 
	 * @param nodeType the node type
	 * 
	 * @return the node collator
	 */
	public static Collator getNodeCollator(Class<? extends SLNode> nodeType) {
		Locale locale = getCollatorLocale(nodeType);
		int strength = getCollatorStrength(nodeType);
		Collator collator = Collator.getInstance(locale);
		collator.setStrength(strength);
		return collator;
	}
	
	/**
	 * Checks if is collator strength identical.
	 * 
	 * @param nodeType the node type
	 * 
	 * @return true, if is collator strength identical
	 */
	public static boolean isCollatorStrengthIdentical(Class<? extends SLNode> nodeType) {
		return getCollatorStrength(nodeType) == Collator.IDENTICAL;
	}
	
	/**
	 * Gets the collator strength.
	 * 
	 * @param nodeType the node type
	 * 
	 * @return the collator strength
	 */
	public static int getCollatorStrength(Class<? extends SLNode> nodeType) {
		int strength = Collator.IDENTICAL;
		SLCollator collator = nodeType.getAnnotation(SLCollator.class);
		if (collator != null) {
			strength = collator.strength();
		}
		return strength;
	}
	
	/**
	 * Gets the collator locale.
	 * 
	 * @param nodeType the node type
	 * 
	 * @return the collator locale
	 */
	public static Locale getCollatorLocale(Class<? extends SLNode> nodeType) {
		Locale locale = Locale.US;
		SLCollator collator = nodeType.getAnnotation(SLCollator.class);
		if (collator != null) {
			locale = new Locale(collator.locale());
		}
		return locale;
	}
	
	/**
	 * Gets the setter method.
	 * 
	 * @param type the type
	 * @param name the name
	 * 
	 * @return the setter method
	 */
	private static Method getSetterMethod(Class<?> type, String name) {
		Method method = null;
		try {
			String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
			method = type.getMethod(methodName, String.class);
		} 
		catch (NoSuchMethodException e) {}
		return method;
	}

	/**
	 * Gets the getter method.
	 * 
	 * @param type the type
	 * @param name the name
	 * 
	 * @return the getter method
	 */
	private static Method getGetterMethod(Class<?> type, String name) {
		Method method = null;
		try {
			String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
			method = type.getMethod(methodName, new Class<?>[] {});
		} 
		catch (NoSuchMethodException e) {}
		return method;
	}
}
