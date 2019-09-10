package qainfotech.kafka.runner;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import qait.kafka.stepDef.ConsumerCreator;
import qait.kafka.stepDef.ProducerCreator;
import qait.kafka.stepDef.kafkaConstants;
public class App {
	public static void main(String[] args) {
		runProducer();
		runConsumer();
	}

	static void runConsumer() {
		
		Consumer<Long, String> consumer = ConsumerCreator.createConsumer();
		try {	
		int noMessageFound = 0;
		while (true) {
			ConsumerRecords<Long, String> consumerRecords = consumer.poll(300);// (1000);
			  
			// 1000 is the time in milliseconds consumer will wait if no record is found at
			// broker.
			if (consumerRecords.count() == 0) {
				
					System.out.println("###########" + noMessageFound);
					noMessageFound++;
				
				if (noMessageFound > kafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
					// If no message found count is reached to threshold exit loop.
					break;
				else
					continue;
			}
			// print each record.
			consumerRecords.forEach(Arecord -> {
				System.out.println("\n1.Record Key " + Arecord.key());
				System.out.println("2.Record value " + Arecord.value());
				System.out.println("3.Record partition " + Arecord.partition());
				System.out.println("4.Record offset " + Arecord.offset());
			});
			// commits the offset of record to broker.
			consumer.commitAsync();
		}}
		catch(Exception e) {
			e.printStackTrace();
		}
		consumer.close();
	}

	static void runProducer() {
		Producer<Long, String> producer = ProducerCreator.createProducer();
		for (int index = 0; index < kafkaConstants.MESSAGE_COUNT; index++) {
			ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(kafkaConstants.TOPIC_NAME,
					"This is record " + index);
			try {
				RecordMetadata metadata = producer.send(record).get();
				System.out.println("Record sent with key " + index + " to partition " + metadata.partition()
						+ " with offset " + metadata.offset());
			} catch (ExecutionException e) {
				System.out.println("Error in sending record");
				System.out.println(e);
			} catch (InterruptedException e) {
				System.out.println("Error in sending record");
				System.out.println(e);
			}
		}
	}
}