package com.crm.service;

public interface EmailService {
    /**
     * 发送简单邮件
     */
    void sendSimpleMail(String to, String subject, String content);

    /**
     * 发送HTML格式邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param htmlContent HTML内容
     */
//    void sendHtmlMail(String to, String subject, String htmlContent);
}
