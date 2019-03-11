package com.mine.jms.controller;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.mine.jms.config.SystemConfiguredQueue;
import com.mine.jms.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * JMS Test
 *
 * @author haroon.
 */

@RestController
public class JmsController {
  private static final Logger logger = LoggerFactory.getLogger(JmsController.class);

  @Autowired
  private MessageService messageService;

	@RequestMapping(value = "/jms/test", method = RequestMethod.GET)
	public ResponseEntity getTest() {
    logger.debug("Testing...");

    String txt = "Time was: "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd hh:mm:ss:SSS"));
    System.out.println(txt);
    logger.debug(txt);
    messageService.sendMessage(SystemConfiguredQueue.InboundQ, txt);
    messageService.sendJsonMessage(SystemConfiguredQueue.InboundQ, new Serializable() {
          private String name = "Arshad";
          private String age = "35";

          public String getName() {
            return name;
          }
          public String getAge() {
            return age;
          }
        });
    messageService.sendMessage(SystemConfiguredQueue.TestQ, "Test <---> "+txt);
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    return responseEntity;
	}


}
