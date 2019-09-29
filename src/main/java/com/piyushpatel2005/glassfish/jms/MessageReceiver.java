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

            // This method will block until message arrives on queue
            // This requires infinite loop
//            Message message = consumer.receive();
            // This method waits for 10000 milliseconds before timing out
//            Message message2 = consumer.receive(10000);

//            if (message2 == null) {
//                System.out.println("No message received in time.");
//            }
//
//            MapMessage mapMessage = (MapMessage) message;
//            System.out.println("Messare received for employee " + mapMessage.getString("employeeName"));

        } catch (Exception e) {
            System.out.println(e);
//        } finally {
//            if (connection != null)
//                try {
//                    connection.close();
//                } catch (JMSException e) {
//                    System.out.println(e);
//                }
//            System.exit(0);
        }

    }
}
