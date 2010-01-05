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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;

/**
 * The Class SLMetaLinkTypeImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLMetaLinkTypeImpl implements SLMetaLinkType {

    private final Lock              lock;

    /** The metadata. */
    private final SLMetadata        metadata;

    /** The p node. */
    private final SLPersistentNode  pNode;

    /** The link type. */
    private Class<? extends SLLink> linkType;

    /**
     * Instantiates a new sL meta link type impl.
     * 
     * @param metadata the metadata
     * @param pNode the node
     */
    SLMetaLinkTypeImpl(
                        final SLMetadata metadata, final SLPersistentNode pNode ) {
        this.metadata = metadata;
        this.pNode = pNode;
        lock = pNode.getLockObject();
    }

    public Lock getLockObject() {
        return lock;
    }

    // @Override
    /*
     * (non-Javadoc)
     * 
     * @see org.openspotlight.graph.SLMetaElement#getMetadata()
     */
    public SLMetadata getMetadata() throws SLGraphSessionException {
        return metadata;
    }

    // @Override
    /*
     * (non-Javadoc)
     * 
     * @see org.openspotlight.graph.SLMetaLinkType#getMetalinks()
     */
    @SuppressWarnings( "unchecked" )
    public Collection<SLMetaLink> getMetalinks() throws SLGraphSessionException {
        synchronized (lock) {

            try {
                final Collection<SLMetaLink> metaLinks = new ArrayList<SLMetaLink>();
                final Collection<SLPersistentNode> typePairNodes = pNode
                                                                        .getNodes();

                for (final SLPersistentNode typePairNode : typePairNodes) {

                    final SLPersistentProperty<String> aClassNameProp = typePairNode
                                                                                    .getProperty(String.class,
                                                                                                 SLConsts.PROPERTY_NAME_A_CLASS_NAME);
                    final SLPersistentProperty<String> bClassNameProp = typePairNode
                                                                                    .getProperty(String.class,
                                                                                                 SLConsts.PROPERTY_NAME_B_CLASS_NAME);

                    final Class<? extends SLNode> aType = (Class<? extends SLNode>)Class
                                                                                        .forName(aClassNameProp.getValue());
                    final Class<? extends SLNode> bType = (Class<? extends SLNode>)Class
                                                                                        .forName(bClassNameProp.getValue());

                    final Collection<SLPersistentNode> linkNodes = typePairNode
                                                                               .getNodes();

                    for (final SLPersistentNode linkNode : linkNodes) {

                        final int direction = linkNode
                                                      .getProperty(Integer.class,
                                                                   SLConsts.PROPERTY_NAME_DIRECTION)
                                                      .getValue();

                        boolean bidirectional = false;
                        Class<? extends SLNode> sourceType = null;
                        Class<? extends SLNode> targetType = null;
                        List<Class<? extends SLNode>> sideTypes = new ArrayList<Class<? extends SLNode>>();

                        if (direction == SLConsts.DIRECTION_AB) {
                            sourceType = aType;
                            targetType = bType;
                        } else if (direction == SLConsts.DIRECTION_BA) {
                            sourceType = bType;
                            targetType = aType;
                        } else {
                            bidirectional = true;
                            sideTypes.add(aType);
                            sideTypes.add(bType);
                        }

                        if (sourceType != null && targetType != null) {
                            sideTypes = new ArrayList<Class<? extends SLNode>>();
                            sideTypes.add(sourceType);
                            sideTypes.add(targetType);
                        }

                        final SLMetaLink metaLink = new SLMetaLinkImpl(
                                                                       linkNode, this, sourceType, targetType,
                                                                       sideTypes, bidirectional, this);
                        metaLinks.add(metaLink);
                    }
                }

                return metaLinks;
            } catch (final Exception e) {
                throw new SLGraphSessionException(
                                                  "Error on attempt to retrieve meta links.", e);
            }
        }
    }

    // @Override
    /*
     * (non-Javadoc)
     * 
     * @see org.openspotlight.graph.SLMetaLinkType#getMetaLinks(java.lang.Class,
     * java.lang.Class, java.lang.Boolean)
     */
    public Collection<SLMetaLink> getMetaLinks(
                                                final Class<? extends SLNode> sourceType,
                                                final Class<? extends SLNode> targetType,
                                                final Boolean bidirectional ) throws SLGraphSessionException {
        synchronized (lock) {
            return getMetaLinks(sourceType.getName(), targetType.getName(), bidirectional);
        }
    }

    public Collection<SLMetaLink> getMetaLinks( String sourceType,
                                                String targetType,
                                                Boolean bidirectional ) throws SLGraphSessionException {
        final Collection<SLMetaLink> metaLinks = getMetalinks();
        final Iterator<SLMetaLink> iter = metaLinks.iterator();
        while (iter.hasNext()) {
            boolean remove = false;
            final SLMetaLink metaLink = iter.next();
            if (bidirectional != null && !bidirectional) {
                if (sourceType != null
                    && !sourceType.equals(metaLink.getSourceType().getName())) {
                    remove = true;
                }
                if (!remove && targetType != null
                    && !targetType.equals(metaLink.getTargetType().getName())) {
                    remove = true;
                }
            } else {
                if (sourceType != null) {
                    remove = sourceType.equals(metaLink.getSideTypes().get(
                                                                           0).getName())
                             || sourceType.equals(metaLink.getSideTypes()
                                                          .get(1).getName());
                }
                if (!remove && targetType != null) {
                    remove = targetType.equals(metaLink.getSideTypes().get(
                                                                           0).getName())
                             || targetType.equals(metaLink.getSideTypes()
                                                          .get(1).getName());
                }
            }
            if (remove) {
                iter.remove();
            }
        }
        return metaLinks;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openspotlight.graph.SLMetaLinkType#getType()
     */
    @SuppressWarnings( "unchecked" )
    // @Override
    public Class<? extends SLLink> getType() throws SLGraphSessionException {
        synchronized (lock) {

            if (linkType == null) {
                try {
                    linkType = (Class<? extends SLLink>)Class.forName(pNode
                                                                           .getName());
                } catch (final Exception e) {
                    throw new SLGraphSessionException(
                                                      "Error on attempt to retrieve link type.", e);
                }
            }
            return linkType;
        }
    }
}
