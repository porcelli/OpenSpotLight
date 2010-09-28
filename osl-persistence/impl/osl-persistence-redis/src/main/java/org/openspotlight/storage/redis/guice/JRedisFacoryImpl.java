/**
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

package org.openspotlight.storage.redis.guice;

import static java.util.Collections.emptySet;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jredis.JRedis;
import org.jredis.ri.alphazero.JRedisClient;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.redis.RedisServerExecutor;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by IntelliJ IDEA. User: feuteston Date: 30/03/2010 Time: 17:45:29 To
 * change this template use File | Settings | File Templates.
 */
@Singleton
public class JRedisFacoryImpl implements JRedisFactory {

	private final Map<Partition, JRedisServerDetail> mappedServerConfig;

	private JRedisServerDetail defaultImpl = null;

	private List<JRedis> allInstances = new ArrayList<JRedis>();

	private final ThreadLocal<Map<Partition, JRedis>> threadLocalCache = new ThreadLocal<Map<Partition, JRedis>>();

	@Inject
	JRedisFacoryImpl(
			final Map<Partition, JRedisServerDetail> mappedServerConfig,
			@StartRedisLocally final boolean needsToStart) {
		this.mappedServerConfig = mappedServerConfig;
		for (final JRedisServerDetail d : this.mappedServerConfig.values()) {
			if (d.isDefaultConfig()) {
				defaultImpl = d;
				break;
			}
		}
		if (needsToStart) {
			final Partition samplePartition = mappedServerConfig.keySet()
					.iterator().next();
			RedisServerExecutor.INSTANCE.startServerIfNecessary(
					samplePartition, this);
		}
	}

	@Override
	public JRedis getFrom(final Partition partition) {
		JRedisServerDetail tmpServerDetail = mappedServerConfig.get(partition);
		if (tmpServerDetail == null) {
			tmpServerDetail = defaultImpl;
		}
		if (tmpServerDetail == null) {
			throw new IllegalArgumentException(
					"there's no server configured for partition "
							+ partition.getPartitionName()
							+ " and no defaultServer created also");
		}
		final JRedisServerDetail serverDetail = tmpServerDetail;
		Map<Partition, JRedis> cache = threadLocalCache.get();
		if (cache == null) {
			cache = new HashMap<Partition, JRedis>();
			threadLocalCache.set(cache);
		}
		JRedis jRedis = cache.get(partition);
		try {
			if (jRedis == null) {

				jRedis = (JRedis) Proxy.newProxyInstance(getClass()
						.getClassLoader(), new Class<?>[] { JRedis.class },
						new InvocationHandler() {

							private final JRedis redis = new JRedisClient(
									serverDetail.getServerName(), serverDetail
											.getServerPort(), serverDetail
											.getPassword(), serverDetail
											.getDb());

							@Override
							public Object invoke(final Object proxy,
									final Method method, final Object[] args)
									throws Throwable {
								// System.out.println(">>> " + method.getName()
								// + " " + Arrays.toString(args));
								return method.invoke(redis, args);

							}
						});
				cache.put(partition, jRedis);
				synchronized (allInstances) {
					allInstances.add(jRedis);
				}
			}
		} catch (final Exception e) {
			throw logAndReturnNew(e, SLRuntimeException.class);
		}
		return jRedis;
	}

	@Override
	public Set<JRedis> getAllActive() {
		Set<JRedis> result;
		final Map<Partition, JRedis> cache = threadLocalCache.get();
		if (cache != null) {
			result = ImmutableSet.copyOf(cache.values());
		} else {
			result = emptySet();
		}
		return result;
	}

	@Override
	public void closeResources() {
		List<JRedis> copy;
		synchronized (allInstances) {
			copy = new ArrayList<JRedis>(allInstances);
			allInstances.clear();
		}
		for (JRedis j : copy) {
			j.quit();
		}
	}

}
