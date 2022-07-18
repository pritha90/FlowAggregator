package server;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.Producer;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.common.errors.TopicExistsException;
import org.json.JSONArray;
import org.json.JSONObject;

import model.Constants;
import model.DatabaseWriterInterface;
import model.FlowRecord;
import model.FlowStatsRecord;
import utility.FlowValidator;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class KafkaProducerImpl implements DatabaseWriterInterface {
	Producer<String, FlowStatsRecord> producer;
	final String topic = Constants.TOPIC_NAME;
	KafkaProducerImpl() throws Exception {
	    final Properties props = loadConfig(System.getProperty("user.dir")+"/java.config");

	    createTopic(topic, props);

	    // Add additional properties.
	    props.put(ProducerConfig.ACKS_CONFIG, "all");
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonSerializer");

	    producer = new KafkaProducer<String, FlowStatsRecord>(props);
	}

  // Create topic in Confluent Cloud
  public static void createTopic(final String topic,
                          final Properties cloudConfig) {
      final NewTopic newTopic = new NewTopic(topic, Optional.empty(), Optional.empty());
      try (final AdminClient adminClient = AdminClient.create(cloudConfig)) {
          adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
      } catch (final InterruptedException | ExecutionException e) {
          // Ignore if TopicExistsException, which may be valid if topic exists
          if (!(e.getCause() instanceof TopicExistsException)) {
              throw new RuntimeException(e);
          }
      }
  }

  @Override
  public void Write(JSONArray array) {
    for (Integer i = 0; i < array.length(); i++) {	
      JSONObject obj;
		try {
			obj = array.getJSONObject(i);	
			FlowRecord flow_record = FlowValidator.MakeFlowRecord(obj);
	       this.producer.send(new ProducerRecord<String, FlowStatsRecord>
	      (this.topic, flow_record.getKey(), flow_record.getFlowStatsRecord()), new Callback() {
	          @Override
	          public void onCompletion(RecordMetadata m, Exception e) {
	            if (e != null) {
	              e.printStackTrace();
	            } else {
	              System.out.printf("Produced record to topic %s partition [%d] @ offset %d%n", m.topic(), m.partition(), m.offset());
	            }
	          }
	      });
	    
	    producer.flush();
	
	    
	    } catch (Exception e) {
	    	System.err.println(e.getMessage());
	    }
	    }
  }

  public static Properties loadConfig(final String configFile) throws IOException {
	System.out.println(System.getProperty("user.dir"));
    if (!Files.exists(Paths.get(configFile))) {
      throw new IOException(configFile + " not found.");
    }
    final Properties cfg = new Properties();
    try (InputStream inputStream = new FileInputStream(configFile)) {
      cfg.load(inputStream);
    }
    return cfg;
  }
  
  protected void finalize()   {
	producer.close();
  }

}
