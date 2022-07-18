package model;
public class FlowStatsRecord {

    Integer txCount;
    Integer rxCount;

    public FlowStatsRecord() {
    }

    public FlowStatsRecord(Integer bytes_tx,Integer bytes_rx) {
        this.txCount = bytes_tx;
        this.rxCount = bytes_rx;
    }

    public Integer getTxCount() {
        return txCount;
    }
    public Integer getRxCount() {
        return rxCount;
    }
    public String toString() {
    	System.out.println(new com.google.gson.Gson().toJson(this));
        return new com.google.gson.Gson().toJson(this);
    }
}