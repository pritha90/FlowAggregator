package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InMemoryStatsCacheImpl implements StatsCacheInterface {

	Map<String, Long> cache;
	
	public InMemoryStatsCacheImpl() {
		Map<String, Long> hash_map = new HashMap<>();  
		this.cache = Collections.synchronizedMap(hash_map);
	}
	
	@Override
	public Boolean put(String key, Long value) {
		this.cache.put(key, value);
		return true;
	}

	@Override
	public Long get(String key) {
		return this.cache.getOrDefault(key, 0L);
	}

}