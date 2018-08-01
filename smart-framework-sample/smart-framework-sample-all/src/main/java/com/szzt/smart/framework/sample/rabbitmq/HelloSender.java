//package com.szzt.smart.framework.sample.rabbitmq;
//
//import com.szzt.smart.framework.sample.entity.User;
//import org.springframework.amqp.core.AmqpTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class HelloSender
//{
//    @Autowired
//    private AmqpTemplate template;
//
//    public void send()
//    {
//        template.convertAndSend("queue", "hello,rabbit BQR");
//    }
//
//    public void sendUser(User user)
//    {
//        template.convertAndSend("queue", user);
//    }
//}