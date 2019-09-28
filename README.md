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