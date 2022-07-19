package server;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import model.StatsCacheInterface;
import utility.FlowValidator;
import model.Constants;
import model.FlowListCacheInterface;
import model.FlowLongStatsRecord;
import model.FlowStatsRecord;
import model.InMemoryStatsCacheImpl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaFlowConsumer implements Runnable{
	
	String consumer_name = "default_consumer";
	StatsCacheInterface tx_global_cache;
	FlowListCacheInterface flow_list_global_cache;
	
	public KafkaFlowConsumer(String consumer_name, StatsCacheInterface tx_global_cache,
			FlowListCacheInterface flow_list_global_cache){
		this.consumer_name = consumer_name;
		this.tx_global_cache = tx_global_cache;
		this.flow_list_global_cache = flow_list_global_cache;
    }

    @Override
    public void run() {
        final String topic = Constants.TOPIC_NAME;

        Properties props;
		try {
			props = loadConfig(System.getProperty("user.dir")+"/java.config");
	        // Add additional properties.
	        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
	        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonDeserializer");
	        props.put(KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, FlowStatsRecord.class);
	        props.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-1");
	        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

	        final Consumer<String, FlowStatsRecord> consumer = new KafkaConsumer<String, FlowStatsRecord>(props);
	        consumer.subscribe(Arrays.asList(topic));
	        
	        System.out.printf("%s started.\n", this.consumer_name);
	        
	        try {
	          while (true) {
	              ConsumerRecords<String, FlowStatsRecord> records = consumer.poll(Duration.ofMillis(100));
	              Map<String, FlowLongStatsRecord> local_map = new HashMap<>();
	              for (ConsumerRecord<String, FlowStatsRecord> record : records) {
	                String key = record.key();
	                FlowStatsRecord stat_record = record.value();
	                
                	FlowLongStatsRecord agg_record = local_map.getOrDefault(key, new FlowLongStatsRecord());
                	agg_record.incTxAndRxCount(stat_record.getTxCount(),stat_record.getRxCount());
                	local_map.put(key, agg_record);
	                
	                System.out.printf("%s consumed with key %s %n", this.consumer_name, key);
	             }
	              
	              if (!local_map.isEmpty()) {
		              for (Map.Entry<String, FlowLongStatsRecord> entry : local_map.entrySet()) {
		            	  FlowLongStatsRecord cache_record = this.tx_global_cache.get(
		            			  entry.getKey());
		            	  cache_record.incTxAndRxCount(entry.getValue().getTxCount(), entry.getValue().getRxCount());
		            	  this.tx_global_cache.put(entry.getKey(), cache_record);
		            	  Integer hour_param = FlowValidator.GetHourParameter(entry.getKey());
		            	  this.flow_list_global_cache.put(hour_param, entry.getKey());
		              }
	              }
	            }
	          
	        } finally {
		          consumer.close();
		    } 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


  public static Properties loadConfig(String configFile) throws IOException {
	System.out.println(configFile);
    if (!Files.exists(Paths.get(configFile))) {
      throw new IOException(configFile + " not found.");
    }
    final Properties cfg = new Properties();
    try (InputStream inputStream = new FileInputStream(configFile)) {
      cfg.load(inputStream);
    }
    return cfg;
  }
}
