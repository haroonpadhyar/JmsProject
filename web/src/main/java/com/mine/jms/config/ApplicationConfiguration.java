package com.mine.jms.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Haroon Anwar Padhyar.
 *         Created on 3/11/19 6:32 PM.
 */

@Configuration
@ComponentScan({"com.mine.jms"})
@Import({ JmsConfig.class })
@EnableWebMvc
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableScheduling
//@PropertySource("classpath:application.properties")
public class ApplicationConfiguration extends WebMvcConfigurerAdapter {

  /*@Bean
  public MappingJackson2HttpMessageConverter converter() {
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
    mappingJackson2HttpMessageConverter.setPrettyPrint(Boolean.TRUE);
    mappingJackson2HttpMessageConverter.setObjectMapper(jacksonObjectMapper());
    return mappingJackson2HttpMessageConverter;
  }

  @Bean
  public ObjectMapper jacksonObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setDateFormat(new SimpleDateFormat(SystemConstant.TIME_FORMAT));
    objectMapper.setTimeZone(TimeZone.getDefault());
    return objectMapper;
  }*/

  /*@Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(converter());
  }

  @Bean
  public FreeMarkerConfigurationFactoryBean freemarkerConfiguration() {
    FreeMarkerConfigurationFactoryBean fmConfigFactoryBean = new FreeMarkerConfigurationFactoryBean();
    fmConfigFactoryBean.setTemplateLoaderPath("/WEB-INF/templates/");
    return fmConfigFactoryBean;
  }

  @Bean
  MultipartResolver multipartResolver() {
    CommonsMultipartResolver resolver = new CommonsMultipartResolver();
    resolver.setMaxUploadSize(2000000);
    return resolver;
  }

  @Bean
  public EventScheduler eventCreator() {
    return new EventScheduler();
  }*/
}
