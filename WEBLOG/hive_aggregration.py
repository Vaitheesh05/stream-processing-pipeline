import pyspark
from pyspark.sql.session import SparkSession
from pyspark.sql.functions import *
from os import *

spark = SparkSession.builder.appName("Hive_Spark").master("local[*]").enableHiveSupport().getOrCreate()  # Spark 2.x

spark.sparkContext.setLogLevel("ERROR")

spark.sql("use hadoop_workshop")

df = spark.sql("select * from weblog_dynamicpart order by id")


df1=df.groupBy(['host_name','ip_address']).agg({'response_code':'sum','url':'count'})\
    .withColumnRenamed("sum(response_code)","Total_ResponseCode")\
    .withColumnRenamed("count(url)","Total_URL")

sqlproperties = {"user": "hudi_user", "password": "password", "driver": "org.postgresql.Driver"}

print("\n", "Mysql Ingestion started", "\n")

df1.write.jdbc(url="jdbc:postgresql://localhost:5432/mydatabase", table="hive_agg", mode="overwrite", properties=sqlproperties)