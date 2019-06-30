package com.piyushpatel2005.weblogic.jms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class QueuePublisher {

	public final static String SERVER="t3://localhost:7001";
	public final static String JNDI_FACTORY="weblogic.jndi.WLInitialContextFactory";
	public final static String JMS_FACTORY = "com.piyushpatel2005.weblogic.base.cf";
	public final static String QUEUE = "com.piyushpatel2005.weblogic.base.dq";
	
	private QueueConnectionFactory queueConnectionFactory;
	private QueueConnection queueConnection;
	private QueueSession queueSession;
	private QueueSender queueSender;
	private Queue queue;
	private TextMessage message;
	
	public void init(Context context, String queueName) throws NamingException, JMSException {
		queueConnectionFactory = (QueueConnectionFactory) context.lookup(JMS_FACTORY);
		queueConnection = queueConnectionFactory.createQueueConnection();
		queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		queue = (Queue) context.lookup(queueName);
		queueSender = queueSession.createSender(queue);
		message = queueSession.createTextMessage();
		queueConnection.start();
	}
	
	public void post(String msg) throws JMSException {
		this.message.setText(msg);
		this.queueSender.send(message);
	}
	
	public void close() throws JMSException {
		queueSender.close();
		queueSession.close();
		queueConnection.close();
	}
	
	private static void sendToServer(QueuePublisher queuePublisher) throws IOException, JMSException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		boolean readFlag = true;
		System.out.println("Enter messag eto send to weblogic server (Enter quit to end:): ");
		while(readFlag) {
			System.out.print("Enter message: ");
			String msg = bufferedReader.readLine();
			if(msg.equals("quit")) {
				queuePublisher.post(msg);
				System.exit(0);
			}
			queuePublisher.post(msg);
			System.out.println();
		}
		bufferedReader.close();
	}
	
	private static InitialContext getInitialContext() throws NamingException {
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
		env.put(Context.PROVIDER_URL, SERVER);
		return new InitialContext(env);
	}
	
	public static void main(String[] args) throws Exception {
		InitialContext context = getInitialContext();
		QueuePublisher queuePublisher = new QueuePublisher();
		queuePublisher.init(context, QUEUE);
		sendToServer(queuePublisher);
		queuePublisher.close();
	}
	
}
