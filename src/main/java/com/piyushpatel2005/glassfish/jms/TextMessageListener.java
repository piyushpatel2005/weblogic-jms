package com.piyushpatel2005.glassfish.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class TextMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            String text = null;
            try {
                text = textMessage.getText();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }

            System.out.println(text);
        } else {
            System.out.println("Invalid Message Received!");
        }
    }
}
