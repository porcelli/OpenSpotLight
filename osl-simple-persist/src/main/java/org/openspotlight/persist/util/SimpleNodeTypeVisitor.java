package org.openspotlight.persist.util;

/**
 * Visitor type to be used on
 * {@link SimpleNodeTypeVisitorSupport#acceptVisitorOn(Class, org.openspotlight.persist.annotation.SimpleNodeType, SimpleNodeTypeVisitor)}
 * static method.
 * 
 * @author feu
 * 
 * @param <T>
 */
public interface SimpleNodeTypeVisitor<T> {

	/**
	 * It visit a bean. It is not necessary to call this method on child itens,
	 * since it is done by reflection inside
	 * {@link SimpleNodeTypeVisitorSupport}.
	 * 
	 * @param <X>
	 * @param bean
	 */
	public <X extends T> void visitBean(X bean);

}
