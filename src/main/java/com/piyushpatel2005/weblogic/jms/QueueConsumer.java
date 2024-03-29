package com.piyushpatel2005.weblogic.jms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class QueueConsumer implements MessageListener {

	public final static String SERVER="t3://localhost:7001"; // JMS server connection information
	public final static String JNDI_FACTORY="weblogic.jndi.WLInitialContextFactory"; // this is standard
	public final static String JMS_FACTORY = "com.piyushpatel2005.weblogic.base.cf"; // To see this go to admin console
	// Visit JMS Modules, visit specific module page. This should appear in Summary of Resources.
	// It is Connection Factory for this JMS server
	public final static String TOPIC = "com.piyushpatel2005.weblogic.base.dq"; // This should appear in the above page
	// Here, the name should be like package name in Java
	// This, we can find through JMS Admin Console, On left side, go to Environment, click Servers. Then, click specific Server instance
	// At the top of form, View JNDI Tree, look up this package and expand. Click to the queue name and you'll see Binding Name.
	
	private QueueConnectionFactory queueConnectionFactory;
	private QueueConnection queueConnection;
	private QueueSession queueSession;
	private QueueReceiver queueReceiver;
	private Queue queue;
	private boolean quit = false;
	
	public void init(Context context, String queueName) throws NamingException, JMSException {
		queueConnectionFactory = (QueueConnectionFactory) context.lookup(JMS_FACTORY);
		queueConnection = queueConnectionFactory.createQueueConnection();
		queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		queue = (Queue) context.lookup(queueName);
		queueReceiver = queueSession.createReceiver(queue);
		queueReceiver.setMessageListener(this);
		queueConnection.start();
	}
	
	public void close() throws JMSException {
		queueReceiver.close();
		queueSession.close();
		queueConnection.close();
	}
	
	private static InitialContext getInitialContext() throws NamingException {
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
		env.put(Context.PROVIDER_URL, SERVER);
		return new InitialContext(env);
	}
	
	public static void main(String[] args) throws Exception {
		InitialContext context = getInitialContext();
		QueueConsumer queueConsumer = new QueueConsumer();
		queueConsumer.init(context, QUEUE);
		System.out.println("Waiting to receive messages.");
		synchronized(queueConsumer) {
			while(!queueConsumer.quit) {
				try {
					queueConsumer.wait();
				} catch(InterruptedException ie) {
					
				}
			}
			queueConsumer.close();
		}
	}

	@Override
	public void onMessage(Message msg) {
		try {
			String msgText;
			if (msg instanceof TextMessage) {
				msgText = ((TextMessage) msg).getText();
			} else {
				msgText = msg.toString();
			}
			
			System.out.println("Message Received: " + msgText);
			
			if(msgText.equalsIgnoreCase("quit")) {
				synchronized(this) {
					quit = true;
					this.notifyAll();
				}
			}
		} catch (JMSException jmsException) {
			System.err.println("Exception : " + jmsException.getMessage());
		}
	}
	
}
