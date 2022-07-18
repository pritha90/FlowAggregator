# FlowAggregator

HTTP server used: Embedded Jetty 9.2.11
Left Jetty’s QueueThreadPool to the default 200 [I am developing on a very out laptop :(]

For data aggregation used Kafka, hosted on Confluent cloud.
Each WriteApi request will simply write to a Kafka topic with Flowkey= String(src_app + dest_app + vpc_id + hour) and value {bytes_tx, bytes_rx}
The server starts 2 Kafka consumer threads in the same group, which poll every 100 ms and aggregate Kafka messages based on key.
The consumer thread then update a 3 global caches Cache<Flowkey, tx_aggregate> , Cache<Flowkey, rx_aggregate>, Cache<hour, FlowkeysList>
Because of lack of time I used a hash map in memory. If I had more time, I would use a Redis Database.

Each ReadApi first hits Cache<hour, FlowkeysList> and then the 2 caches to retrieve already aggregated tx, rx count.

Scalability:
The number of concurrent requests my AggregationService is capped by that of the HTTP server’s (Jetty in this case) capacity. Jetty9.x can do 64 concurrent sessions and has 1024 requests queued.
The number of consumer threads in the AggregationService can be increased for faster data processing (currently 2). We can also have consumers in different groups.

Kafka configuration: The cluster has one topic and 6 partitions, with a replication factor of 3.

Availability:
We can have 2 instances of the service deployed in a active<>passive mode, sharing periodic heartbeats. After a timeout the passive instance can serve requests with for example a VIP configuration.

Implemtation Caveats and hurdles:
1. I am quicker with gRPC server and coding in C++. Turned to Jetty and Java for familiarity 
from Grad school days and since the ask was for a HTTP server.
2. Setting up Kafka locally was painful and expecting someone else to follow steps was unrealistic, hence went with the free trial on Confluent Cloud.
3. Time was up before interfacing with Redis.
4. Issues in code: 
	
	a. Updates to the tx and rx counter cache should be made atomic, since now a GET could result in tx data for a processed flow but rx data was yet to be updated.
	
	b. Now the whole hash map is locked for a single row updation, but with Redis the synchronisation will be more optimised.
	
	c. Could not add unit tests. Have all the interfaces in place to easily mock db interactions and unit test code.
	
	d. Could add a linter plugin on my eclipse (apologize for the bad indents in some places)
  
The source code is in the master branch.
Run:
1. Dowload aggregator.jar and java.config (attachment in email for Confluent Cloud credentials) into a directory
2. java aggregator.jar 
3. Run sh ./curl_tests.sh
