package wdsr.exercise4.receiver;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JmsTopicReciever {
	private static final Logger log = LoggerFactory.getLogger(JmsTopicReciever.class);
	private static final String URL_HOST = "tcp://localhost:61616";
	private final String topicName;

	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private MessageConsumer consumer;
	Destination destination;

	public JmsTopicReciever(final String topicName) {

		this.topicName = topicName;
		connectionFactory = new ActiveMQConnectionFactory(URL_HOST);
		connectionFactory.setTrustAllPackages(true);
	}
	public void createSession() throws JMSException {

		connection = connectionFactory.createTopicConnection();
		connection.setClientID("misiom94");
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		connection.start();
		destination = session.createTopic(topicName);
		consumer = session.createConsumer(destination);
	}

	public List<String> getMessage() {
		List<String> messages = new ArrayList<>();
		try {
			Message message = consumer.receive();
			while (message != null){
				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					messages.add(textMessage.getText());
				}
				message = consumer.receive(1000);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return messages;
	}

	
	public void shutdown() {

		try {

			consumer.setMessageListener(null);
			session.close();
			connection.close();

		} catch (JMSException e) {
			log.error(e.getMessage());
		}

	}
	
}
