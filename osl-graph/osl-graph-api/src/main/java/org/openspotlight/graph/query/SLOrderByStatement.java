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
package org.openspotlight.graph.query;


/**
 * The Interface SLOrderByStatement.
 * 
 * @author Vitor Hugo Chagas
 */
public interface SLOrderByStatement {
	
	/**
	 * Type.
	 * 
	 * @param typeName the type name
	 * 
	 * @return the type
	 */
	public Type type(String typeName);
	
	/**
	 * Order by end.
	 * 
	 * @return the end
	 */
	public End orderByEnd();
	
	/**
	 * The Interface Type.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	public static interface Type {
		
		/**
		 * Sub types.
		 * 
		 * @return the type
		 */
		public Type subTypes();
		
		/**
		 * Property.
		 * 
		 * @param name the name
		 * 
		 * @return the property
		 */
		public Property property(String name);
		
		/**
		 * The Interface Property.
		 * 
		 * @author Vitor Hugo Chagas
		 */
		public static interface Property {
			
			/**
			 * Ascending.
			 * 
			 * @return the sL order by statement
			 */
			public SLOrderByStatement ascending();
			
			/**
			 * Descending.
			 * 
			 * @return the sL order by statement
			 */
			public SLOrderByStatement descending();
			
			/**
			 * Order by end.
			 * 
			 * @return the end
			 */
			public End orderByEnd();
			
		}
	}
	
	/**
	 * The Interface End.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	public static interface End extends SLSelectFacade {
		
		/**
		 * Keep result.
		 * 
		 * @return the end
		 */
		public End keepResult();
		
        /**
         * Limit.
         * 
         * @param size the size
         * @return the end
         */
        public End limit( Integer size );

        /**
         * Limit.
         * 
         * @param size the size
         * @param offset the offset
         * @return the end
         */
        public End limit( Integer size,
                          Integer offset );

		/**
		 * Execute x times.
		 * 
		 * @return the end
		 */
		public End executeXTimes();
		
		/**
		 * Execute x times.
		 * 
		 * @param x the x
		 * 
		 * @return the end
		 */
		public End executeXTimes(int x);
		
		/**
		 * Collator.
		 * 
		 * @param strength the strength
		 * 
		 * @return the end
		 */
		public End collator(int strength);
	}

}
