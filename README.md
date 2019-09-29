# JMS Producer and Consumer examples (GlassFish)

This repository includes examples of JMS message producers and consumers for JMS queues as well as topics.

For more information check [this reference](https://docs.oracle.com/middleware/1212/wls/JMSPG/implement.htm#JMSPG187)

To check JMS connection information, read the comments in the code. You need to configure and add `wlthin3client.jar` file into this project.
For GlassFish project examples, get `gf-client.jar` file.

There are different types of messages we can send.
1. TextMessage
2. MapMessage (key value pairs)
3. ObjectMessage (can be any Java serializable object)
4. Bytes Message (array of bytes as payload)
5. StreamMessage (Strings and primitives)

We can set delivery mode, priorities and time to live for messages.
There are 10 levels of priority ranging from 0 to 9. 0 is the minimum priority. JMS tries to deliver higher-priority messages first but this is not guaranteed.
Message Expiration time is called "TimeToLive" which is set in milliseconds. The default value is 0 which means it expires in 0 seconds.

We can enable transactions on JMS by enabling it in `createSession` method.
By setting `createSession(true,...)`, we can have enabled transaction. If we enable, we must call `session.commit()` to put the messages to the queue. Usually, the messages will be put one by one and usually transaction will be disabled for the producers.
The second argument in this method has little meaning for the senders, but we need it because Java works on interfaces and there is no single argument version for this method.

On the receiver side, `Session.AUTO_ACKNOWLEDGE` means JMS will auto acknowledge if Listener succeeded without exception.
We can set `CLIENT_ACKNOWLEDGE` to acknowledge method. However, this method works only with `consumer.receive()` method and not with listener interface.
