package com.piyushpatel2005.glassfish.jms;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

public class MessageReceiver {
    public static void main(String[] args) {

        Connection connection = null;
        try {
            Context ctx = new InitialContext();
            Queue queue = (Queue) ctx.lookup("jms/EmployeeManagementQueue");
            ConnectionFactory cf = (ConnectionFactory) ctx.lookup("jms/ConnectionFactory");

            connection = cf.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer consumer = session.createConsumer(queue);

            consumer.setMessageListener(new MapMessageListener());

            connection.start();

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (JMSException e) {
                    System.out.println(e);
                }
            System.exit(0);
        }

    }
}
