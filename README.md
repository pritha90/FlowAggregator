# FlowAggregator

HTTP server used: Embedded Jetty 9.2.11
Left Jetty’s QueueThreadPool to the default [I am developing on a very out laptop :(]

For data aggregation used **Kafka** Queues, hosted on Confluent cloud.
Each **WriteApi** request will simply write to a Kafka topic with Flowkey= String(src_app + dest_app + vpc_id + hour) and value {bytes_tx, bytes_rx}
The server starts 2 Kafka consumer threads in the same group, which poll every 100 ms and aggregate Kafka messages based on key.
The consumer thread then updates a **Redis** Cache with <Flowkey, tx_aggregate;rx_aggregate> , Cache<hour, FlowkeysSet>.

Each **ReadApi**  hits the **Redis** Cache<hour, FlowkeysSet> and then retrieves <Flowkey, tx_aggregate;rx_aggregate> forEach Flowkey in FlowkeysSet.

Scalability:
The number of concurrent requests my AggregationService is capped by that of the HTTP server’s (Jetty in this case) capacity. Jetty9.x can do 64 concurrent sessions and has 1024 requests queued.
The number of consumer threads in the AggregationService can be increased for faster data processing (currently 2). We can also have consumers in different groups.

Kafka configuration: The cluster has one topic and 6 partitions, with a replication factor of 3.

Redis used as Cache: In my local setup I have redis running locally on my laptop. In production Redis would be running in the Cloud. Redis does asynch
replication across replicas and can persist to disk as append-only logs.

Availability: [Not implemented]
We can have 2 instances of the service deployed in a active<>passive mode, sharing periodic heartbeats. After a timeout the passive instance can serve requests with for example a VIP configuration.

Implemtation Caveats and hurdles:
1. I am quicker with gRPC server and coding in C++. Turned to Jetty and Java for familiarity 
from Grad school days and since the ask was for a HTTP server.
2. Setting up Kafka locally was painful and expecting someone else to follow steps was unrealistic, hence went with the free trial on Confluent Cloud.
3. The current implementation uses a Redis cache instead of a in-memory hash map as before. The user 
4. Issues in code: 
  
The source code is in the master branch.
Run:
1. Please install Redis locally or on CLoud - learn the host IP and port and supply as cmdline args as below. Follow  https://redis.io/docs/getting-started/installation/install-redis-on-mac-os/
2. Start Redis with cmd "redis-server" before starting the aggregator service.
3. Dowload aggregator_redis.jar and java.config (attachment in email for Confluent Cloud credentials) into a directory
4. Run  java -jar aggregator_redis.jar localhost 6379
5. Run sh ./curl_tests.sh

Code:
AggregationService.java is the main file that starts the server and initialises caches and Kafka Producer/Consumer.
