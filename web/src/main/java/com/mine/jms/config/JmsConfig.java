package com.mine.jms.config;

import java.io.File;
import java.net.InetAddress;
import java.util.Collections;
import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.IndividualDeadLetterStrategy;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import org.apache.activemq.store.jdbc.LeaseDatabaseLocker;
import org.apache.activemq.store.jdbc.adapter.MySqlJDBCAdapter;
import org.apache.activemq.store.jdbc.adapter.TransactJDBCAdapter;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
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
//  private static final String BROKER_URL ="tcp://localhost:6616";
  private static final String BROKER_URL ="vm://localhost"; // will use KahaDB persistence
//  private static final String BROKER_URL ="vm://broker1?marshal=false&broker.persistent=false";


  /**
   * vm://broker1?marshal=false&broker.persistent=false
   *
   *  **Be careful with embedded brokers**
   *
   * If you are using the VM transport and wish to explicitly configure an Embedded Broker there is a chance that you
   * could create the JMS connections first before the broker starts up.
   * Currently ActiveMQ will auto-create a broker if you use the VM transport and there is not one already configured.
   * (In 5.2 it is possible to use the waitForStart and create=false options for the connection uri)
   *
   * So to work around this if you are using Spring you may wish to use the depends-on attribute so that your
   * JMS ConnectionFactory depends on the embedded broker to avoid this happening. e.g.
   *
   * <bean id="broker" class="org.apache.activemq.xbean.BrokerFactoryBean">
   *     <property name="config" value="classpath:org/apache/activemq/xbean/activemq.xml" />
   *     <property name="start" value="true" />
   *   </bean>
   *
   *   <bean id="connectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory" depends-on="broker">
   *     <property name="brokerURL" value="vm://localhost"/>
   *   </bean>
   */

  @Autowired
  private DataSource dataSource;

  // Comment the broker configuration to use implicit broker (in case of VM transport) that will be created automatically.
  @Bean
  public BrokerService broker() throws Exception{
    BrokerService broker = new BrokerService();
    broker.addConnector(BROKER_URL);
    broker.setPersistent(true); // By default KahaDB and location is tomcat\bin\activemq-data\localhost\KahaDB
    broker.setBrokerName("Broker: "+ InetAddress.getLocalHost().getHostName());
    broker.setPersistenceAdapter(persistenceAdapter());
    broker.setUseJmx(true);
    broker.setDestinationPolicy(policyMap());

    broker.start();
    return broker;
  }

  private PolicyMap policyMap(){
    PolicyMap policyMap = new PolicyMap();
    PolicyEntry policyEntry = new PolicyEntry();
//    policyEntry.setQueue(SystemConfiguredQueue.TestQ.getPhysicalQueue());
    policyEntry.setQueue(">");
    IndividualDeadLetterStrategy deadLetterStrategy = new IndividualDeadLetterStrategy();
    deadLetterStrategy.setQueuePrefix("DLQ.");
    deadLetterStrategy.setProcessExpired(false);
    deadLetterStrategy.setExpiration(11*1000);
    policyEntry.setDeadLetterStrategy(deadLetterStrategy);
    policyEntry.setExpireMessagesPeriod(2*60*1000); //how often the broker should look for messages whose expiration date has arrived, and process them (e.g. by moving them to the DLQ)

    policyMap.setPolicyEntries(Collections.singletonList(policyEntry));
    return policyMap;
  }

  // KahaDB
  private PersistenceAdapter persistenceAdapter() throws Exception{
    KahaDBPersistenceAdapter persistenceAdapter = new KahaDBPersistenceAdapter();
    persistenceAdapter.setDirectory(new File("C:\\Projects\\ehis-projects\\configcomponent\\local\\tomcat\\bin\\activemq-data\\localhost\\KahaDB"));
    return persistenceAdapter;
  }

//  SQL Server
//  private PersistenceAdapter persistenceAdapter() throws Exception{
//    JDBCPersistenceAdapter persistenceAdapter = new JDBCPersistenceAdapter();
//    persistenceAdapter.setDataSource(dataSource);
//    LeaseDatabaseLocker locker = new LeaseDatabaseLocker();
//    locker.setLockAcquireSleepInterval(1000L);
//    persistenceAdapter.setLocker(locker);
//    persistenceAdapter.setLockKeepAlivePeriod(5000L);
//    persistenceAdapter.setCreateTablesOnStartup(true);
//    persistenceAdapter.setAdapter(new TransactJDBCAdapter()); // A JDBC Adapter for Transact-SQL based databases such as SQL Server or Sybase
//    return persistenceAdapter;
//  }

//  MySQL
//  private PersistenceAdapter persistenceAdapter() throws Exception{
//    JDBCPersistenceAdapter persistenceAdapter = new JDBCPersistenceAdapter();
//    persistenceAdapter.setDataSource(dataSource);
//    LeaseDatabaseLocker locker = new LeaseDatabaseLocker();
//    locker.setLockAcquireSleepInterval(1000L);
//    persistenceAdapter.setLocker(locker);
//    persistenceAdapter.setLockKeepAlivePeriod(5000L);
//    persistenceAdapter.setCreateTablesOnStartup(true);
//    persistenceAdapter.setAdapter(new MySqlJDBCAdapter());
//    return persistenceAdapter;
//  }

  @Bean
  public ConnectionFactory connectionFactory() {
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
    connectionFactory.setBrokerURL(BROKER_URL);

    // This is overriding redelivery policy by client.
//    RedeliveryPolicyMap redeliveryPolicyMap = connectionFactory.getRedeliveryPolicyMap();
//    RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
    RedeliveryPolicy redeliveryPolicy = connectionFactory.getRedeliveryPolicy();
    redeliveryPolicy.setInitialRedeliveryDelay(500);
    redeliveryPolicy.setRedeliveryDelay(1000);
//    redeliveryPolicy.setBackOffMultiplier(2);
//    redeliveryPolicy.setUseExponentialBackOff(true);
    redeliveryPolicy.setMaximumRedeliveries(2);
//    redeliveryPolicyMap.put(new ActiveMQQueue(">"), redeliveryPolicy);

    return connectionFactory;
  }

  @Bean
  public JmsTemplate jmsTemplate() {
    JmsTemplate jmsTemplate = new JmsTemplate();
    jmsTemplate.setConnectionFactory(connectionFactory());
    jmsTemplate.setSessionTransacted(true);
//    jmsTemplate.setSessionAcknowledgeMode(1);
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
  @QueueQualifier(value = SystemConfiguredQueue.DLQ_TestQ)
  public Queue dlqTestQ() {
    ActiveMQQueue activeMQQueue = new ActiveMQQueue();
    activeMQQueue.setPhysicalName(SystemConfiguredQueue.DLQ_TestQ.getPhysicalQueue());
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
  @QueueQualifier(value = SystemConfiguredQueue.DLQ_InboundQ)
  public Queue dlqInboundQ() {
    ActiveMQQueue activeMQQueue = new ActiveMQQueue();
    activeMQQueue.setPhysicalName(SystemConfiguredQueue.DLQ_InboundQ.getPhysicalQueue());
    return activeMQQueue;
  }

//  @Bean
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
    // https://activemq.apache.org/artemis/docs/1.0.0/undelivered-messages.html
    // https://dzone.com/articles/jdbc-master-slave-persistence-setup-with-activemq
  }
}
