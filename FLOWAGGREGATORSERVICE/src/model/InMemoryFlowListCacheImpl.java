package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public class InMemoryFlowListCacheImpl implements FlowListCacheInterface {

	Map<Integer, Set<String>> cache;
	
	public InMemoryFlowListCacheImpl() {
		Map<Integer, Set<String>> hash_map = new HashMap<>();  
		this.cache = Collections.synchronizedMap(hash_map);
	}
	
	@Override
	public Boolean put(Integer key, String value) {
		if (!this.cache.containsKey(key)) {
			this.cache.put(key, new HashSet<String>());
		}
		this.cache.get(key).add(value);
		return true;
	}

	@Override
	public Set<String> get(Integer key) {
		if (!this.cache.containsKey(key)) {
			this.cache.put(key, new HashSet<String>());
		}
		return this.cache.getOrDefault(key, new HashSet<String>());
	}

}