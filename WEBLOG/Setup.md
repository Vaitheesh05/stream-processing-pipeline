1.Start all the daemon for the following component:
  Hadoop
  Zookeeper:
  >>nohup bin/zookeeper-server-start.sh config/zookeeper.properties >> nohup.out &
  Kafka Server:
  >>nohup bin/kafka-server-start.sh config/server.properties >> nohup.out &

2.Create topic:
  >>bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic real_time_weblogs

3.Run the Kafka Producer(Java file) and verify by the Kafka consumer
  >>bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic real_time_weblogs --from-beginning

4.PostgreSQL
  >>use mydatabase;
  >>create table weblogdetails (id int NOT NULL AUTO_INCREMENT,log_timestamp varchar(150), ip_address varchar(150),host_name varchar(150),url varchar(150),response_code varchar(150),PRIMARY KEY (id));

5.Using Spark-Submit to post the response from producer to Database
  >>spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.2.3,org.postgresql:postgresql:42.6.0 ~/Desktop/Persistant_Folder/Python/Producer_to_DB.py

6.Create the SQOOP job for migrating the data from RDBMS(PostgreSQL) to HDFS
  >>sqoop job --create Sqoop_weblogdetails_test1 -- import --driver org.postgresql.Driver --connect jdbc:postgresql://localhost:5432/mydatabase --username hudi_user --password 'password' --table weblogdetails --target-dir /airflowproject/Sqoop_weblogdetails_test1 --incremental append --check-column id --last-value 0 -m 1 --direct
  >>sqoop job --exec Sqoop_weblogdetails_test1

7.Create the hive table on top of the HDFS
  >>use test1;
  >>create external table weblog_external (id int, datevalue string,ipaddress string, host string, url string, responsecode int) row format delimited fields terminated by ',' stored as textfile location '/airflowproject/Sqoop_weblogdetails_test1';
  
  set hive.exec.dynamic.partition=true;
  set hive.exec.dynamic.partition.mode=nonstrict;
  set hive.exec.max.dynamic.partitions=1000;

  >>create table weblog_dynamicpart (id int, datevalue string, ipaddress string, url string, responsecode int) partitioned by (host string) row format delimited fields terminated by ',' stored as textfile;
  >>insert into weblog_dynamicpart partition(host) select id, datevalue, ipaddress, url, responsecode, host from weblog_external as b where not exists (select a.id from weblog_dynamicpart as a where a.id = b.id);

8.Using Spark-submit aggregrate the response_code and URL
  >>spark-submit hive_aggregration.py


9.Create the dashboard in grafana to showcase:
  ->Total_ResponseCode by Host and IP Address
  ->Total_URL by Host and IP Address
  
AKIAIIXPW3EUYOH76KOA



