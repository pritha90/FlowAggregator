package model;

import java.util.HashSet;
import java.util.Set;

public interface StatsCacheInterface {
	
	public Boolean put(String key, FlowLongStatsRecord value);
	public FlowLongStatsRecord get(String key);
	public Boolean put(String key, String value) ;
	public Set<String> getMembers(String key);
	
	public void InitConnection();
}