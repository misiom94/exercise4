package wdsr.exercise4;

import java.util.List;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wdsr.exercise4.receiver.JmsTopicReciever;

public class Test {

	private static final Logger log = LoggerFactory.getLogger(Test.class);
    private static final String TOPIC_NAME = "misiom94.TOPIC";
    
	 public static void main(String[] args) {
		 JmsTopicReciever jmsTopicReciever = new JmsTopicReciever(TOPIC_NAME);
	        try {
	            jmsTopicReciever.createSession();
	            List<String> messages = jmsTopicReciever.getMessage();
	            for (String message : messages) {
	                log.info("Received message '" + message + "'");
	            }
	            log.info("Number of messages:" +messages.size());
	            jmsTopicReciever.shutdown();
	        } catch (JMSException e) {
	            log.error(e.getMessage());
	        }
	    }


	}

