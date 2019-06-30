package com.piyushpatel2005.weblogic.jms;


import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class TopicSubscriberDemo implements MessageListener {

	public final static String SERVER="t3://localhost:7001"; // JMS server connection information
	public final static String JNDI_FACTORY="weblogic.jndi.WLInitialContextFactory"; // this is standard
	public final static String JMS_FACTORY = "com.piyushpatel2005.weblogic.base.cf"; // To see this go to admin console
	// Visit JMS Modules, visit specific module page. This should appear in Summary of Resources.
	// It is Connection Factory for this JMS server
	public final static String TOPIC = "com.piyushpatel2005.weblogic.base.dt"; // This should appear in the above page
	// Here, the name should be like package name in Java
	// This, we can find through JMS Admin Console, On left side, go to Environment, click Servers. Then, click specific Server instance
	// At the top of form, View JNDI Tree, look up this package and expand. Click to the topic name and you'll see Binding Name.
	
	private TopicConnectionFactory topicConnectionFactory;
	private TopicConnection topicConnection;
	private TopicSession topicSession;
	private TopicSubscriber topicReceiver;
	private Topic topic;
	private boolean quit = false;
	
	public void init(Context context, String topicName) throws NamingException, JMSException {
		topicConnectionFactory = (TopicConnectionFactory) context.lookup(JMS_FACTORY);
		topicConnection = topicConnectionFactory.createTopicConnection();
//		topicConnection = topicConnectionFactory.createTopicConnection("userName", "password");
		topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		topic = (Topic) context.lookup(topicName);
		topicReceiver = topicSession.createSubscriber(topic);
		topicReceiver.setMessageListener(this);
		topicConnection.start();
	}

	public void close() throws JMSException {
		topicReceiver.close();
		topicSession.close();
		topicConnection.close();
	}
	
	private static InitialContext getInitialContext() throws NamingException {
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
		env.put(Context.PROVIDER_URL, SERVER);
		return new InitialContext(env);
	}
	
	public static void main(String[] args) throws Exception {
		InitialContext context = getInitialContext();
		TopicSubscriberDemo topicSubscriberDemo = new TopicSubscriberDemo();
		topicSubscriberDemo.init(context, TOPIC);
		synchronized(topicSubscriberDemo) {
			while(!topicSubscriberDemo.quit) {
				try {
					topicSubscriberDemo.wait();
				} catch(InterruptedException ie) {
					
				}
			}
			topicSubscriberDemo.close();
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
