package com.piyushpatel2005.glassfish.jms;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

public class MapMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            MapMessage mapMessage = (MapMessage) message;

            try {
                String name = mapMessage.getString("employeeName");
                System.out.println("Employee Name received. " + name);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Invalid Message Received!");
        }
    }
}
