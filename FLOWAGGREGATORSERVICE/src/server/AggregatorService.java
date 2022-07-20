package server;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import model.StatsCacheInterface;
import redis.clients.jedis.Jedis;
import model.DatabaseWriterInterface;
import model.FlowListCacheInterface;
import model.InMemoryFlowListCacheImpl;
import model.InMemoryStatsCacheImpl;

public class AggregatorService {
	
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Server could not start, since Redis host and port are not provided");
			return;			
		}
		String redis_host = args[0];
		Integer redis_port = Integer.parseInt(args[1]);
		DatabaseWriterInterface db_writer;
		try {
			 db_writer = new KafkaProducerImpl();
		} catch (Exception e) {
			System.err.println("Server could not start, since connection to Kafka failed");
			return;
		}
	
		Server server = new Server();
		registerHttpConnector(server);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.addServlet(new ServletHolder(new FlowsHandlerServlet(new RedisHandler(redis_host, redis_port, /*uses_jedis_pool=*/true),
				 null, db_writer)),"/flows");
		server.setHandler(context);
		
		System.out.println("Server Starting. Listening for user requests on 8080.");
	
		Thread periodic_aggregator_1 = new Thread(new KafkaFlowConsumer
				("consumer-1", new RedisHandler(redis_host, redis_port,/*uses_jedis_pool=*/false), null));
		Thread periodic_aggregator_2 = new Thread(new KafkaFlowConsumer
				("consumer-2", new RedisHandler(redis_host, redis_port,/*uses_jedis_pool=*/false), null));
		
		periodic_aggregator_1.start();
		periodic_aggregator_2.start();
		server.start();
		
		periodic_aggregator_1.join();
		periodic_aggregator_2.join();
		server.join();
	}

	private static void registerHttpConnector(Server server){
		ServerConnector http_connector = new ServerConnector(server);
		http_connector.setPort(8080);
		server.addConnector(http_connector);
	}
}