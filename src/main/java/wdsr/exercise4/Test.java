package wdsr.exercise4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wdsr.exercise4.producer.JmsSender;

public class Test {
	
	static final int NON_PERSISTENT_MODE = 1;
	static final int PERSISTENT_MODE = 2;
	static final int MESSAGES_COUNT = 10000;
	static final String QUEUE_NAME = "misiom94.QUEUE";

	private static final Logger log = LoggerFactory.getLogger(Test.class);
	
	public static void main(String[] args) {
		
		log.info("Start sending messages in NON PERSISTENT mode");
		log.info(String.format("%d messages in NON PERSISTENT mode sent in %d milliseconds", MESSAGES_COUNT, sendMessages(NON_PERSISTENT_MODE)));
		log.info("Start sending messages in PERSISTENT mode: ");
		log.info(String.format("%d messagesin PERSISTENT sent in %d milliseconds", MESSAGES_COUNT, sendMessages(PERSISTENT_MODE)));
		
		
	}
	
	private static long sendMessages(int persistentMode){
		JmsSender jmsSender = new JmsSender(QUEUE_NAME);
		long start, stop, time;
		final String text = "test_";
		start = System.currentTimeMillis();
		for (int i = 0; i < MESSAGES_COUNT; i++) {
			jmsSender.sendString(String.format(text + "%d", i), persistentMode);
		}
		stop = System.currentTimeMillis();
		time = stop - start;
		return time;
	}

}


