package com.piyushpatel2005.weblogic.jms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class TopicPublisherDemo {

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
	private TopicPublisher topicSender;
	private Topic topic;
	private TextMessage message;
	
	public void init(Context context, String topicName) throws NamingException, JMSException {
		topicConnectionFactory = (TopicConnectionFactory) context.lookup(JMS_FACTORY);
		topicConnection = topicConnectionFactory.createTopicConnection();
//		topicConnection = topicConnectionFactory.createTopicConnection("userName", "password");
		topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		topic = (Topic) context.lookup(topicName);
		topicSender = topicSession.createPublisher(topic);
		message = topicSession.createTextMessage();
		topicConnection.start();
	}
	
	public void post(String msg) throws JMSException {
		message.setText(msg);
		this.topicSender.publish(message);
	}
	
	public void close() throws JMSException {
		topicSender.close();
		topicSession.close();
		topicConnection.close();
	}
	
	private static void sendToServer(TopicPublisherDemo topicPublisherDemo) throws IOException, JMSException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		boolean readFlag = true;
		System.out.println("Enter message to send to weblogic server topic (Enter quit to end:): ");
		while(readFlag) {
			System.out.print("Enter message: ");
			String msg = bufferedReader.readLine();
			if(msg.equals("quit")) {
				topicPublisherDemo.post(msg);
				System.exit(0);
			}
			topicPublisherDemo.post(msg);
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
		TopicPublisherDemo topicPublisherDemo = new TopicPublisherDemo();
		topicPublisherDemo.init(context, TOPIC);
		sendToServer(topicPublisherDemo);
		topicPublisherDemo.close();
	}
	
}
