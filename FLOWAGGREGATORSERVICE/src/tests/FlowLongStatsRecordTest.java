package tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.FlowLongStatsRecord;

public class FlowLongStatsRecordTest {
	@Test
	public void FlowLongStatsRecordIncTxandRx() {
		FlowLongStatsRecord record = new FlowLongStatsRecord();
		assertTrue(record.getRxCount()==0L);
		assertTrue(record.getTxCount()==0L);
		
		record.incTxAndRxCount(20, 50);
		assertTrue(record.getTxCount()==20L);
		assertTrue(record.getRxCount()==50L);
	
		record.incTxAndRxCount(20L, 500L);
		assertTrue(record.getTxCount()==40L);
		assertTrue(record.getRxCount()==550L);
	}
	
	@Test
	public void FlowLongStatsRecordInit() {
		FlowLongStatsRecord record = new FlowLongStatsRecord();
		assertTrue(record.getRxCount()==0L);
		assertTrue(record.getTxCount()==0L);
		
		FlowLongStatsRecord record_1 = new FlowLongStatsRecord(300L, 500L);
		assertTrue(record_1.getTxCount()==300L);
		assertTrue(record_1.getRxCount()==500L);		
	}
}