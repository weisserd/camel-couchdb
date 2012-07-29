package com.sksamuel.camel.couchdb;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Before;
import org.junit.Test;
import org.lightcouch.Changes;
import org.lightcouch.ChangesResult.Row;
import org.lightcouch.CouchDbContext;
import org.lightcouch.CouchDbInfo;
import org.mockito.Mock;

/**
 * @author Stephen K Samuel samspade79@gmail.com 29 Jul 2012 16:56:51
 * 
 */
public class CouchDbChangesetTrackerTest {

	@Mock
	private Changes				changes;

	@Mock
	private CouchDbClientWrapper		client;

	@Mock
	private CouchDbEndpoint			endpoint;

	@Mock
	private CouchDbConsumer			consumer;

	private CouchDbChangesetTracker	tracker;

	@Mock
	private CouchDbContext			context;

	@Mock
	private CouchDbInfo			info;

	@Mock
	private Row					row3;

	@Mock
	private Row					row2;

	@Mock
	private Row					row1;

	@Mock
	private Processor				processor;

	@Before
	public void before() {
		initMocks(this);
		when(client.context()).thenReturn(context);
		when(context.info()).thenReturn(info);
		when(info.getUpdateSeq()).thenReturn("100");

		when(client.changes()).thenReturn(changes);
		when(changes.continuousChanges()).thenReturn(changes);
		when(changes.includeDocs(true)).thenReturn(changes);
		when(changes.since(anyString())).thenReturn(changes);
		when(changes.heartBeat(anyLong())).thenReturn(changes);
		when(changes.style(anyString())).thenReturn(changes);

		when(row1.getSeq()).thenReturn("seq1");
		when(row2.getSeq()).thenReturn("seq2");
		when(row3.getSeq()).thenReturn("seq3");

		when(row1.getId()).thenReturn("id1");
		when(row2.getId()).thenReturn("id2");
		when(row3.getId()).thenReturn("id3");

		tracker = new CouchDbChangesetTracker(endpoint, consumer, client);

	}

	@Test
	public void testExchangeCreatedWithCorrectProperties() {
		when(changes.hasNext()).thenReturn(true, true, true, false);
		when(changes.next()).thenReturn(row1, row2, row3);

		tracker.run();

		verify(endpoint).createCouchExchange("seq1", "id1", null);
		verify(endpoint).createCouchExchange("seq2", "id2", null);
		verify(endpoint).createCouchExchange("seq3", "id3", null);
	}

	@Test
	public void testProcessorInvoked() throws Exception {
		when(changes.hasNext()).thenReturn(true, false);
		when(changes.next()).thenReturn(row1);
		when(consumer.getProcessor()).thenReturn(processor);

		tracker.run();

		verify(endpoint).createCouchExchange("seq1", "id1", null);
		verify(processor).process(any(Exchange.class));
	}
}
