package com.mine.jms.service;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Haroon Anwar Padhyar.
 *         Created on 3/11/19 7:50 PM.
 */
@Component
@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
public class InboundListener implements MessageListener{
  private static final Logger logger = LoggerFactory.getLogger(InboundListener.class);

  @Override
  public void onMessage(Message message) {
    try{
      Thread.sleep(1000);
      logger.debug("onMessage: "+message.getJMSMessageID());
      logger.debug("onMessage: " + ((ActiveMQTextMessage) message).getText());
      throw new RuntimeException("AAAA");
    }catch(Exception e){
      e.printStackTrace();
      throw new RuntimeException(e);
    }

  }
}
