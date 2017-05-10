package wdsr.exercise4.producer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsQueueProducer{
	private static final Logger log = LoggerFactory.getLogger(JmsQueueProducer.class);

	private final String queueName;
	private static final String HOST_URL = "tcp://localhost:61616";
	MessageProducer messageProducer;
	ConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	Destination destination;
	

	public JmsQueueProducer(final String _queue) {
		this.queueName = _queue;
		connectionFactory = new ActiveMQConnectionFactory(HOST_URL);
	}

	private void connect() throws JMSException {

		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	private void close() throws JMSException {
		session.close();
		connection.close();
	}


	public void sendString(String text,int deliveryMode) {
		try {

			connect();
			destination = session.createQueue(queueName);
			messageProducer = session.createProducer(destination);
			connection.start();
			TextMessage message = session.createTextMessage(text);
			messageProducer.setDeliveryMode(deliveryMode);
			messageProducer.send(message);
			close();

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}


}
