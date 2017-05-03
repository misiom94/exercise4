package wdsr.exercise4.receiver;

import java.math.BigDecimal;

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

import wdsr.exercise4.PriceAlert;
import wdsr.exercise4.VolumeAlert;

/**
 * TODO Complete this class so that it consumes messages from the given queue and invokes the registered callback when an alert is received.
 * 
 * Assume the ActiveMQ broker is running on tcp://localhost:62616
 */
public class JmsQueueReceiver {
	private static final Logger log = LoggerFactory.getLogger(JmsQueueReceiver.class);
	private final String connectionUri = "tcp://localhost:62616";
	private ActiveMQConnectionFactory connectionFactory; 
    private Connection connection; 
    private Session session; 
    private Destination destination; 
    private AlertService alertService;
	
	
	/**
	 * Creates this object
	 * @param queueName Name of the queue to consume messages from.
	 */
	public JmsQueueReceiver(final String queueName) {
		connectionFactory = new ActiveMQConnectionFactory(connectionUri); 
		connectionFactory.setTrustAllPackages(true);
        try {
			connection = connectionFactory.createConnection();
			connection.start(); 
	        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); 
	        destination = session.createQueue(queueName);
		} catch (JMSException e) {
			log.info("JmsQueueReciever Error !");
			e.printStackTrace();
		} 
	}

	/**
	 * Registers the provided callback. The callback will be invoked when a price or volume alert is consumed from the queue.
	 * @param alertService Callback to be registered.
	 */
	
	public void registerCallback(AlertService alertService) {
		 try {
				MessageConsumer consumer = session.createConsumer(destination);
				this.alertService = alertService;
				consumer.setMessageListener(new EventListener()); 
			} catch (JMSException e) {
				e.printStackTrace();
			}
	}
	public class EventListener implements MessageListener{
		@Override
		public void onMessage(Message message) { 
		
			if(message instanceof ObjectMessage){
				ObjectMessage objectMessage = (ObjectMessage) message;
				runServiceAllert(objectMessage);
			}else if(message instanceof TextMessage){
				TextMessage textMessage = (TextMessage) message;
				runServiceAllert(textMessage);
			}
		
	}
	
	private void runServiceAllert(ObjectMessage objectMessage){	
		try {
				if(objectMessage.getJMSType().equals("PriceAlert")){
					PriceAlert priceAlert = (PriceAlert) objectMessage.getObject();
					alertService.processPriceAlert(priceAlert);
				}else if(objectMessage.getJMSType().equals("VolumeAlert")){
					VolumeAlert volumeAlert = (VolumeAlert) objectMessage.getObject();
					alertService.processVolumeAlert(volumeAlert);
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
	}
	
	private void runServiceAllert(TextMessage textMessage){
		try {
			String text = textMessage.getText();
			String[] splited = text.split("\\r?\\n");
			String[] result = new String[3];
			for (int i = 0; i < splited.length; i++) {
				result[i] = splited[i].substring(splited[i].indexOf('=') + 1).trim();	
			}
			String[] values = result; 
			String type = textMessage.getJMSType();
			
			if(type.equals("PriceAlert")){
				alertService.processPriceAlert(new PriceAlert(Long.valueOf(values[0]), values[1], new BigDecimal(values[2])));
			}else if(type.equals("VolumeAlert")){
				alertService.processVolumeAlert(new VolumeAlert(Long.valueOf(values[0]), values[1], Long.valueOf(values[2])));
			}
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}
	

	// TODO
	// This object should start consuming messages when registerCallback method is invoked.
	
	// This object should consume two types of messages:
	// 1. Price alert - identified by header JMSType=PriceAlert - should invoke AlertService::processPriceAlert
	// 2. Volume alert - identified by header JMSType=VolumeAlert - should invoke AlertService::processVolumeAlert
	// Use different message listeners for and a JMS selector 
	
	// Each alert can come as either an ObjectMessage (with payload being an instance of PriceAlert or VolumeAlert class)
	// or as a TextMessage.
	// Text for PriceAlert looks as follows:
	//		Timestamp=<long value>
	//		Stock=<String value>
	//		Price=<long value>
	// Text for VolumeAlert looks as follows:
	//		Timestamp=<long value>
	//		Stock=<String value>
	//		Volume=<long value>
	
	// When shutdown() method is invoked on this object it should remove the listeners and close open connection to the broker.   
}
	public void shutdown() {
		if (connection != null) { 
            try {
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			} 
        } 
		
	}
}
