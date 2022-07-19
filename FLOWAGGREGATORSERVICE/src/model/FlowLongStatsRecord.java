package model;
public class FlowLongStatsRecord {

    Long txCount;
    Long rxCount;

    public FlowLongStatsRecord() {
        this.txCount = 0L;
        this.rxCount = 0L;
    }

    public FlowLongStatsRecord(Long bytes_tx, Long bytes_rx) {
        this.txCount = bytes_tx;
        this.rxCount = bytes_rx;
    }

    public Long getTxCount() {
        return txCount;
    }
    public Long getRxCount() {
        return rxCount;
    }
    public void incTxCount(Integer val) {
    	this.txCount += val;
    }
    public void incRxCount(Integer val) {
        this.rxCount += val;
    }
    public void incTxCount(Long val) {
    	this.txCount += val;
    }
    public void incRxCount(Long val) {
        this.rxCount += val;
    }
    public void incTxAndRxCount(Integer tx, Integer rx) {
    	incTxCount(tx);
    	incRxCount(rx);
    }
    public void incTxAndRxCount(Long tx, Long rx) {
    	incTxCount(tx);
    	incRxCount(rx);
    }
    public String toString() {
    	System.out.println(new com.google.gson.Gson().toJson(this));
        return new com.google.gson.Gson().toJson(this);
    }
}