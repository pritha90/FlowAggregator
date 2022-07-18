package model;
public class FlowRecord {

	String key;
	FlowStatsRecord stats;

    public FlowRecord() {
    }

    public FlowRecord(String key, FlowStatsRecord stats) {
        this.key = key;
        this.stats = stats;
    }

    public String getKey() {
        return key;
    }
    public FlowStatsRecord getFlowStatsRecord() {
        return stats;
    }
}