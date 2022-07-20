package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InMemoryStatsCacheImpl implements StatsCacheInterface {

	Map<String, FlowLongStatsRecord> cache;
	
	public InMemoryStatsCacheImpl() {
		Map<String, FlowLongStatsRecord> hash_map = new HashMap<>();  
		this.cache = Collections.synchronizedMap(hash_map);
	}
	
	@Override
	public Boolean put(String key, FlowLongStatsRecord value) {
		this.cache.put(key, value);
		return true;
	}

	@Override
	public FlowLongStatsRecord get(String key) {
		return this.cache.getOrDefault(key, new FlowLongStatsRecord(0L,0L));
	}

	@Override
	public Boolean put(String key, String value) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Set<String> getMembers(String key) {
		// TODO Auto-generated method stub
		return new HashSet<String>();
	}

	@Override
	public void InitConnection() {
		// TODO Auto-generated method stub
		
	}

}