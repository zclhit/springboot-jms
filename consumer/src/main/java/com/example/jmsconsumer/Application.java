package com.example.jmsconsumer;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.JmsTransactionManager;

import javax.jms.ConnectionFactory;

/**
 * MQ - 点对点模式
 * Created by jason-geng on 5/21/17.
 */
@SpringBootApplication
@EnableJms
public class Application {

    @Value("${jms.broker-url}")
    private String jmsBrokerUrl;

    @Value("${jms.user}")
    private String jmsUser;

    @Value("${jms.password}")
    private String jmsPassword;

    /**
     * 创建 ActiveMQ 的连接工厂
     */
    @Bean
    public ConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(jmsBrokerUrl);
        connectionFactory.setUserName(jmsUser);
        connectionFactory.setPassword(jmsPassword);
        return connectionFactory;
    }

    /**
     * JMS 队列的监听容器工厂
     */
    @Bean(name = Constant.QUEUE_CONTAINER)
    public DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory =
                new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        //设置连接数
        factory.setConcurrency("3-10");
        //重连间隔时间ms
        factory.setRecoveryInterval(1000L);
        return factory;
    }

    /**
     * JMS 监听topic工厂
     */
    @Bean(name = Constant.TOPIC_CONTAINER)
    public DefaultJmsListenerContainerFactory jmsTopicListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory =
                new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("1");
        factory.setPubSubDomain(true);
        return factory;
    }

    @Bean
    public JmsTransactionManager jmsTransactionManager(){
        return new JmsTransactionManager(connectionFactory());
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
