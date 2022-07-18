package model;

public interface StatsCacheInterface {
	
	public Boolean put(String key, Long value);
	public Long get(String key);

}