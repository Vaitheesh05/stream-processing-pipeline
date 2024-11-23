package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class Producer {

    public static void main(String[] args) {

        // Kafka configuration
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092"); // Kafka server address
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // Create Kafka producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

        // Kafka topic name
        String topic = "real_time_weblogs3";

        // Random data generators
        Random random = new Random();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] methods = {"GET", "POST", "PUT", "DELETE"};
        String[] paths = {"/home", "/about", "/api/login", "/api/data", "/contact"};
        String[] responseCodes = {"200", "404", "500", "403", "301"};

        try {
            while (true) {
                // Generate dynamic log fields
                String dateValue = dateFormat.format(new Date()); // Current timestamp
                String ipAddress = "192.168." + random.nextInt(256) + "." + random.nextInt(256);
                String host = "host-" + random.nextInt(100); // Simulated host name
                String url = methods[random.nextInt(methods.length)] + " " + paths[random.nextInt(paths.length)] + " HTTP/1.1";
                String responseCode = responseCodes[random.nextInt(responseCodes.length)];

                // Create log entry in JSON format
                String log = String.format(
                        "\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        dateValue, ipAddress, host, url, responseCode
                );

                // Send log to Kafka
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, ipAddress, log);
                producer.send(record);

                System.out.println("Sent: " + log);

                // Wait for 5 seconds before sending the next log
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}

