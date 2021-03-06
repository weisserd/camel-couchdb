/*
 * Copyright 2012 Stephen Keith Samuel
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.sksamuel.camel.couchdb;

import org.apache.camel.Exchange;
import org.lightcouch.Changes;
import org.lightcouch.ChangesResult;
import org.lightcouch.CouchDbInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * @author Stephen K Samuel samspade79@gmail.com 29 Jul 2012 13:29:38
 * 
 */
public class CouchDbChangesetTracker implements Runnable {

	private static final Logger		logger	= LoggerFactory.getLogger(CouchDbChangesetTracker.class);

	private volatile boolean		stopped;

	private final CouchDbClientWrapper	couchClient;

	private final CouchDbEndpoint		endpoint;

	private final CouchDbConsumer		consumer;

	private Changes				changes;

	public CouchDbChangesetTracker(CouchDbEndpoint endpoint, CouchDbConsumer consumer, CouchDbClientWrapper couchClient) {
		this.endpoint = endpoint;
		this.consumer = consumer;
		this.couchClient = couchClient;
		initChanges();
	}

	void initChanges() {
		CouchDbInfo dbInfo = couchClient.context().info();
		String since = dbInfo.getUpdateSeq(); // get latest update seq
		logger.debug("Last sequence [{}]", since);
		changes = couchClient.changes().style(endpoint.getStyle()).includeDocs(true).since(since).heartBeat(endpoint.getHeartbeat())
				.continuousChanges();
	}

	public boolean isStopped() {
		return stopped;
	}

	@Override
	public void run() {
		while (changes.hasNext()) { // blocks until a feed is received

			try {

				ChangesResult.Row feed = changes.next();
				if (feed.isDeleted() && !endpoint.isDeletes())
					continue;
				if (!feed.isDeleted() && !endpoint.isUpdates())
					continue;

				String seq = feed.getSeq();
				JsonObject doc = feed.getDoc();

				Exchange exchange = endpoint.createCouchExchange(seq, feed.getId(), doc, feed.isDeleted());
				logger.trace("Created exchange [exchange={}, _id={}, seq={}", new Object[] { exchange, feed.getId(), seq });
				consumer.getProcessor().process(exchange);

			} catch (Exception e) {
				logger.trace("Error={}", e);
			}
		}
		stopped = true;
	}

	public void stop() {
		changes.stop();
	}
}
