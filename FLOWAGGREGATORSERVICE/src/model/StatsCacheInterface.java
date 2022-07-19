package model;

public interface StatsCacheInterface {
	
	public Boolean put(String key, FlowLongStatsRecord value);
	public FlowLongStatsRecord get(String key);

}