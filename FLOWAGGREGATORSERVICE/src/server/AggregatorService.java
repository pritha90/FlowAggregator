package server;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import model.StatsCacheInterface;
import model.DatabaseWriterInterface;
import model.FlowListCacheInterface;
import model.InMemoryFlowListCacheImpl;
import model.InMemoryStatsCacheImpl;

public class AggregatorService {
	
	public static void main(String[] args) throws Exception {
		
		StatsCacheInterface tx_global_cache = new InMemoryStatsCacheImpl();
		StatsCacheInterface rx_global_cache = new InMemoryStatsCacheImpl();
		FlowListCacheInterface flow_list_global_cache = new InMemoryFlowListCacheImpl();
		DatabaseWriterInterface db_writer;
		try {
			 db_writer = new KafkaProducerImpl();
		} catch (Exception e) {
			System.err.println("Server could not start, since connection DB failed");
			return;
		}
	
		Server server = new Server();
		registerHttpConnector(server);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.addServlet(new ServletHolder(new FlowsHandlerServlet(tx_global_cache,
				rx_global_cache, flow_list_global_cache, db_writer)),"/flows");
		server.setHandler(context);
		
		System.out.println("Server Starting. Listening for user requests on 8080.");
	
		Thread periodic_aggregator_1 = new Thread(new KafkaFlowConsumer
				("consumer-1", tx_global_cache, rx_global_cache, flow_list_global_cache));
		Thread periodic_aggregator_2 = new Thread(new KafkaFlowConsumer
				("consumer-2", tx_global_cache, rx_global_cache, flow_list_global_cache));
		
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