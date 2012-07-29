package com.sksamuel.camel.couchdb;

/**
 * @author Stephen K Samuel samspade79@gmail.com 29 Jul 2012 13:58:09
 * 
 */
public class CouchDbException extends RuntimeException {

	public CouchDbException() {
		super();
	}

	public CouchDbException(String message, Throwable cause) {
		super(message, cause);
	}

	public CouchDbException(String message) {
		super(message);
	}

	public CouchDbException(Throwable cause) {
		super(cause);
	}

}
