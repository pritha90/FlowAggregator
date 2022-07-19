package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

}