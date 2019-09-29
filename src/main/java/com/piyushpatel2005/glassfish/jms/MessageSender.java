package com.piyushpatel2005.glassfish.jms;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageSender {
    public static void main(String[] args) {

        Connection connection = null;
        try {
            Context ctx = new InitialContext();
            Queue queue = (Queue) ctx.lookup("jms/EmployeeManagementQueue");
            ConnectionFactory cf = (ConnectionFactory) ctx.lookup("jms/ConnectionFactory");

            connection = cf.createConnection();
            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

            MessageProducer messageProducer = session.createProducer(queue);

//            TextMessage message = session.createTextMessage("The time is now " + new java.util.Date());
//            messageProducer.send(message);

            MapMessage message = session.createMapMessage();

            message.setString("employeeName", "Piyush Patel");
            message.setString("employeeJobRole", "Engineer");
            message.setDouble("employeeSalary", 1000);

            // set delivery mode to non-persistent
//            messageProducer.setDeliveryDelay(DeliveryMode.NON_PERSISTENT);
            messageProducer.setPriority(7);
            messageProducer.setTimeToLive(10000);
            messageProducer.send(message);
            messageProducer.send(message);
            messageProducer.send(message);

            // messages will be added to queue only when commit is executed. If one of them fails, then commit will throw exception and no message will be posted.
            // If error occurs, we can execute session.rollback()
            session.commit();

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
