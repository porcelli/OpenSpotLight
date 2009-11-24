/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.jboss.identity.idm.impl.store.hibernate;

import org.jboss.identity.idm.spi.store.IdentityStoreSession;
import org.jboss.identity.idm.common.exception.IdentityException;
import org.hibernate.SessionFactory;

/**
 * Wrapper around HibernateEntityManager
 *
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class HibernateIdentityStoreSessionImpl implements IdentityStoreSession
{

   private final SessionFactory sessionFactory;

   public HibernateIdentityStoreSessionImpl(SessionFactory sessionFactory)
   {
      this.sessionFactory = sessionFactory;
   }

   public Object getSessionContext()
   {
      return sessionFactory.getCurrentSession();
   }

   public void close() throws IdentityException
   {
      sessionFactory.getCurrentSession().close();
   }

   public void save() throws IdentityException
   {
      sessionFactory.getCurrentSession().flush();
   }

   public void clear() throws IdentityException
   {
      sessionFactory.getCurrentSession().clear();
   }

   public boolean isOpen()
   {
      return sessionFactory.getCurrentSession().isOpen();
   }

   public boolean isTransactionSupported()
   {
      return true;
   }

   public void startTransaction()
   {
      sessionFactory.getCurrentSession().getTransaction().begin();
   }

   public void commitTransaction()
   {
      sessionFactory.getCurrentSession().getTransaction().commit();
   }

   public void rollbackTransaction()
   {
      sessionFactory.getCurrentSession().getTransaction().rollback();
   }

   public boolean isTransactionActive()
   {
      return sessionFactory.getCurrentSession().getTransaction().isActive();
   }
}
