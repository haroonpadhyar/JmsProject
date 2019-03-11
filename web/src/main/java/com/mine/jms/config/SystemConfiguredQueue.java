package com.mine.jms.config;

/**
 * @author Haroon Anwar Padhyar.
 *         Created on 3/11/19 6:49 PM.
 */
public enum SystemConfiguredQueue {
  TestQ("test.Q"),
  InboundQ("inbound.Q");

  private String physicalQueue;

  SystemConfiguredQueue(String physicalQueue){
    this.physicalQueue = physicalQueue;
  }


  public String getPhysicalQueue() {
    return physicalQueue;
  }

}
