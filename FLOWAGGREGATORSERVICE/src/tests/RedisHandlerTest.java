package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import model.FlowLongStatsRecord;
import redis.clients.jedis.Jedis;
import server.RedisHandlerImpl;

public class RedisHandlerTest {
	@Test
	public void HandlerPutKeyStats_1() {
		RedisHandlerImpl handler = new RedisHandlerImpl("", 5, false);
		handler.jedis = mock(Jedis.class);
		handler.put("key", new FlowLongStatsRecord());
		verify(handler.jedis, times(1)).set("key","0;0");
	}
	@Test
	public void HandlerPutKeyStats_2() {
		RedisHandlerImpl handler = new RedisHandlerImpl("", 5, false);
		handler.jedis = mock(Jedis.class);
		handler.put("key", new FlowLongStatsRecord(10004L, 900000000L));
		verify(handler.jedis, times(1)).set("key","10004;900000000");
	}
	@Test
	public void HandlerGettKeyStatsForGoodString() {
		RedisHandlerImpl handler = new RedisHandlerImpl("", 5, false);
		handler.jedis = mock(Jedis.class);
		when(handler.jedis.get("key")).thenReturn("10004;900000000");
		FlowLongStatsRecord record = handler.get("key");
		assertTrue(record.getTxCount()== 10004);
		assertTrue(record.getRxCount()== 900000000);
	}
	@Test
	public void HandlerGetKeyStatsForMalformedStringIsZero() {
		RedisHandlerImpl handler = new RedisHandlerImpl("", 5, false);
		handler.jedis = mock(Jedis.class);
		when(handler.jedis.get("key")).thenReturn("100040000000");
		FlowLongStatsRecord record = handler.get("key");
		assertTrue(record.getTxCount()== 0);
		assertTrue(record.getRxCount()== 0);
	}
	@Test
	public void HandlerGetKeyStatsWhenNullReturned() {
		RedisHandlerImpl handler = new RedisHandlerImpl("", 5, false);
		handler.jedis = mock(Jedis.class);
		when(handler.jedis.get("key")).thenReturn(null);
		FlowLongStatsRecord record = handler.get("key");
		assertTrue(record.getTxCount()== 0);
		assertTrue(record.getRxCount()== 0);
	}
	@Test
	public void HandlerPutString() {
		RedisHandlerImpl handler = new RedisHandlerImpl("", 5, false);
		handler.jedis = mock(Jedis.class);
		handler.put("key", "a;b;c;d");
		verify(handler.jedis, times(1)).sadd("key","a;b;c;d");
	}
	@Test
	public void HandlerGetMemberStringsEmptyWhenNullReturned() {
		RedisHandlerImpl handler = new RedisHandlerImpl("", 5, false);
		handler.jedis = mock(Jedis.class);
		when(handler.jedis.get("key")).thenReturn(null);
		Set<String> returned_set = handler.getMembers("key");
		assertTrue(returned_set!=null);
		assertTrue(returned_set.isEmpty());
	}
	@Test
	public void HandlerGetMemberStringsValid() {
		RedisHandlerImpl handler = new RedisHandlerImpl("", 5, false);
		handler.jedis = mock(Jedis.class);
		Set<String> input = new HashSet<String>(Arrays.asList("b","c"));
		when(handler.jedis.smembers("key")).thenReturn(input);
		Set<String> returned_set = handler.getMembers("key");
		assertTrue(returned_set!=null);
		assertEquals(returned_set, input);
	}
}