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

import org.lightcouch.Changes;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbContext;
import org.lightcouch.Response;

/**
 * @author Stephen K Samuel samspade79@gmail.com 29 Jul 2012 16:28:38
 * 
 *         Necessary to allow mockito to mock this client. Once LightCouch library adds an interface for the client,
 *         this class can be removed.
 * 
 */
public class CouchDbClientWrapper {

	private final CouchDbClient	client;

	public CouchDbClientWrapper(CouchDbClient client) {
		this.client = client;
	}

	public Response update(Object doc) {
		return client.update(doc);
	}

	public Response save(Object doc) {
		return client.save(doc);
	}

	public Changes changes() {
		return client.changes();
	}

	public CouchDbContext context() {
		return client.context();
	}

}
