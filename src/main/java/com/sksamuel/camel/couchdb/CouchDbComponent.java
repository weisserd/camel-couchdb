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

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen K Samuel samspade79@gmail.com 21 May 2012 07:04:15
 * 
 */
public class CouchDbComponent extends DefaultComponent {

	private static final Logger	logger	= LoggerFactory.getLogger(CouchDbComponent.class);

	public CouchDbComponent() {
	}

	public CouchDbComponent(CamelContext context) {
		super(context);
	}

	@Override
	protected CouchDbEndpoint createEndpoint(String uri, String remaining, Map<String, Object> params) throws Exception {
		CouchDbEndpoint e = new CouchDbEndpoint(uri, remaining, this);
		setProperties(e, params);
		logger.info("Created CouchDB Endpoint [{}]", e);
		return e;
	}
}
