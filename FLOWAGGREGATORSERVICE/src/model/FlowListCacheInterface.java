package model;

import java.util.List;
import java.util.Set;

public interface FlowListCacheInterface {
	
	public Boolean put(Integer key, String value);
	public Set<String> get(Integer key);

}