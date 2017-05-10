package wdsr.exercise4.receiver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsQueueReceiver {
	private static final Logger log = LoggerFactory.getLogger(JmsQueueReceiver.class);
	private static final String connectionAddress = "tcp://localhost:61616";
	private Session session;
	private final String queue_name;
	private MessageConsumer messageConsumer;
	private Connection connection;
	private ActiveMQConnectionFactory connectionFactory;
	static Destination destination;
	
	
	public JmsQueueReceiver(final String _queue)
	{
		this.queue_name = _queue;
		connectionFactory = new ActiveMQConnectionFactory(connectionAddress);
		connectionFactory.setTrustAllPackages(true);
	}
	
	public List<String> recieveMessage() {
        List<String> messageList = new ArrayList<>();
        try {
            Message message = messageConsumer.receive(100);
            while (message != null){
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    messageList.add(textMessage.getText());
                }
                message = messageConsumer.receive(100);
            }
        } catch (JMSException e) {
        	log.error("recieveMesage error !");
            e.printStackTrace();
        }
        return messageList;
    }
	
	public void createSession() throws JMSException {

        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        destination = session.createQueue(queue_name);
        connection.start();
        messageConsumer = session.createConsumer(destination);
    }
	
	 public void shutdown() {
	        try {
	            connection.close();
	            session.close();
	        } catch (JMSException e) {
	            System.out.print(e.getMessage());
	        }
	    }
	
	
}
