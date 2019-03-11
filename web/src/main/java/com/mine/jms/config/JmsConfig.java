package com.mine.jms.config;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Haroon Anwar Padhyar.
 *         Created on 3/11/19 6:44 PM.
 */
public class JmsConfig {

  @Bean
  public BrokerService broker() throws Exception{
    BrokerService broker = new BrokerService();
    broker.addConnector("tcp://localhost:6616");
    broker.setPersistent(false);
    broker.start();
    return broker;
  }

  @Bean
  public ConnectionFactory connectionFactory() {
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
    connectionFactory.setBrokerURL("tcp://localhost:6616");
    return connectionFactory;
  }

  @Bean
  public JmsTemplate jmsTemplate() {
    JmsTemplate jmsTemplate = new JmsTemplate();
    jmsTemplate.setConnectionFactory(connectionFactory());
    return jmsTemplate;
  }

  @Bean
  @QueueQualifier(value = SystemConfiguredQueue.TestQ)
  public Queue testQ() {
    ActiveMQQueue activeMQQueue = new ActiveMQQueue();
    activeMQQueue.setPhysicalName(SystemConfiguredQueue.TestQ.getPhysicalQueue());
    return activeMQQueue;
  }



  @Bean
  @QueueQualifier(value = SystemConfiguredQueue.InboundQ)
  public Queue inboundQ() {
    ActiveMQQueue activeMQQueue = new ActiveMQQueue();
    activeMQQueue.setPhysicalName(SystemConfiguredQueue.InboundQ.getPhysicalQueue());
    return activeMQQueue;
  }

  @Bean
  public PlatformTransactionManager jmsTransactionManager() {
    JmsTransactionManager jmsTransactionManager = new JmsTransactionManager();
    jmsTransactionManager.setConnectionFactory(connectionFactory());
    return jmsTransactionManager;
  }

  // Listener
  @Autowired
  MessageListener inboundListener;

  @Autowired
  MessageListener testListener;

  @Bean
  public DefaultMessageListenerContainer inboundListenerContainer() {
    DefaultMessageListenerContainer listenerContainer = new DefaultMessageListenerContainer();
    listenerContainer.setConnectionFactory(connectionFactory());
    listenerContainer.setDestination(inboundQ());
    listenerContainer.setMessageListener(inboundListener);
    listenerContainer.setTransactionManager(jmsTransactionManager());
    return listenerContainer;
  }

  @Bean
  public DefaultMessageListenerContainer testListenerContainer() {
    DefaultMessageListenerContainer listenerContainer = new DefaultMessageListenerContainer();
    listenerContainer.setConnectionFactory(connectionFactory());
    listenerContainer.setDestination(testQ());
    listenerContainer.setMessageListener(testListener);
    listenerContainer.setTransactionManager(jmsTransactionManager());
    return listenerContainer;
  }

  public void abc(){
    // https://dzone.com/articles/jdbc-master-slave-persistence-setup-with-activemq
  }
}
