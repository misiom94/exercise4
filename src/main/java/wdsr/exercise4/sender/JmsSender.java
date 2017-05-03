package wdsr.exercise4.sender;

import java.math.BigDecimal;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import wdsr.exercise4.Order;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsSender {
	private static final Logger log = LoggerFactory.getLogger(JmsSender.class);
	
	private final String queueName;
	private final String topicName;
	
	private Session session;
	private ActiveMQConnectionFactory connectionFactory; 
	private Connection connection;
	private final String connectionUri = "tcp://localhost:62616";
	
	public void createConnection() throws JMSException{
		connectionFactory = new ActiveMQConnectionFactory(connectionUri); 
		connection = connectionFactory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); 
	}
	
	public JmsSender(final String queueName, final String topicName) {
		this.queueName = queueName;
		this.topicName = topicName;
		try {
			createConnection();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method creates an Order message with the given parameters and sends it as an ObjectMessage to the queue.
	 * @param orderId ID of the product
	 * @param product Name of the product
	 * @param price Price of the product
	 */
	public void sendOrderToQueue(final int orderId, final String product, final BigDecimal price) {
		try {
			Destination destination = session.createQueue(queueName);
			MessageProducer messageProducer = session.createProducer(destination);
			Order orderToSend = new Order(orderId, product, price);
			ObjectMessage messageToSend = session.createObjectMessage();
			messageToSend.setObject(orderToSend);
			messageToSend.setJMSType("Order");
			messageToSend.setStringProperty("WDSR-System", "OrderProcessor");
			messageProducer.send(messageToSend);
			session.close();
			connection.close();
			log.info("Message send !");
		} catch (JMSException e) {
			e.printStackTrace();
			log.info("Error while sending message !");
		}
	}

	/**
	 * This method sends the given String to the queue as a TextMessage.
	 * @param text String to be sent
	 */
	public void sendTextToQueue(String text) {
		try {
			Destination destination = session.createQueue(queueName);
			MessageProducer producer = session.createProducer(destination);
			TextMessage textMessage = session.createTextMessage();
			textMessage.setText(text);
			textMessage.setJMSType("Order");
			textMessage.setStringProperty("WDSR-System", "OrderProcessor");
			producer.send(textMessage);
			session.close();
			connection.close();
			log.info("textMessage was send succesfuly!");
		} catch (JMSException e) {
			e.printStackTrace();
			log.info("ERROR while sending textmessage!");
		}
	}

	/**
	 * Sends key-value pairs from the given map to the topic as a MapMessage.
	 * @param map Map of key-value pairs to be sent.
	 */
	public void sendMapToTopic(Map<String, String> map) {
		try {
			Destination destination = session.createTopic(topicName);
			MessageProducer messageProducer = session.createProducer(destination);
			MapMessage mapMessage = session.createMapMessage();
			for (Map.Entry<String, String> entryMap : map.entrySet()) {
				mapMessage.setString(entryMap.getKey(), entryMap.getValue());
			}
			messageProducer.send(mapMessage);
			session.close();
			connection.close();
			log.info("mapMessage was send succesfuly!");
		} catch (JMSException e) {
			e.printStackTrace();
			log.info("ERROR while sending mapMessage!");
		}
	}
}
