#data-pipeline

In this I have used Alpakka Kafka , Alpakka cassandra and Alpakka Elasticsearch . In this the data from Kafka is transferred to Cassandra and Elasticsearch as well.
To run this, follow the below steps :
 1. Clone the repository :
  
   `git clone https://github.com/knoldus/alpakka-pipeline.git` 
   
 2. Start Kafka and create a topic named "per". Start a producer for per and send data in form of eaw json. For example :
 {"id":"90","name":"Mini","city":"Mumbai"}
 3. Start elasticsearch-7.6.2 using command : 
 
 `bin/elasticsearch`
 
 3.Start Cassandra using command :
 
 `./bin/cassandra -f` 
 
 4. Go to the main directory and run the following command one after another:
 
 `sbt` `clean` `compile` `run`
