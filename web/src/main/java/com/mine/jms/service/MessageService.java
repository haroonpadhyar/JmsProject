package com.mine.jms.service;


import java.io.Serializable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mine.jms.config.SystemConfiguredQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Messaging service to send message to configured Queue.
 *
 * @author: Haroon
 */
@Service
@Transactional
public class MessageService {
  private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private JmsTemplate jmsTemplate;

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void sendMessage(SystemConfiguredQueue queue, Serializable message) {
    if(logger.isInfoEnabled()) {
      logger.info("Entering MessageService.sendMessage(SystemConfiguredQueue queue, Serializable message)");
    }

    if (logger.isDebugEnabled()) {
      logger.debug("QUEUE:" + queue.getPhysicalQueue());
      logger.debug("MSG:" + message);
    }

    jmsTemplate.convertAndSend(queue.getPhysicalQueue(), message);
    if (logger.isDebugEnabled()) {
      logger.debug("Message successfully sent to destination!");
    }

    if (logger.isInfoEnabled()) {
      logger.info("Exiting MessageService.sendMessage(SystemConfiguredQueue queue, Serializable message)");
    }
  }

  public void sendJsonMessage(SystemConfiguredQueue queue, Serializable message) {
    if(logger.isDebugEnabled()) {
      logger.debug("In MessageService.sendJsonMessage");
    }

    String messageXML = marshalMessage(message);
    if (logger.isDebugEnabled()) {
      logger.debug("Message to send in JSON: " + messageXML);
      logger.debug("Destination Queue: " + queue);
    }
    sendMessage(queue, messageXML);
  }

  private String marshalMessage(Serializable message) {
    try {
      String jsonString = objectMapper.writeValueAsString(message);
      if(logger.isInfoEnabled()) {
        logger.info("json string for message : " + message + " is  " + jsonString);
      }
      return jsonString;
    }
    catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
}
