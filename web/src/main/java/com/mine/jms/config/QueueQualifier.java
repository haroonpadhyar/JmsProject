package com.mine.jms.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Haroon Anwar Padhyar.
 *         Created on 3/11/19 6:48 PM.
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD,
    ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface QueueQualifier {
  SystemConfiguredQueue value() ;
}
