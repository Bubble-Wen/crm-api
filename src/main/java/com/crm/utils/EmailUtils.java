// com/crm/utils/EmailUtils.java
package com.crm.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailUtils {

    @Autowired
    private JavaMailSender mailSender;

    // 发送审核通过邮件
    public void sendApprovalEmail(String to, String contractName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("your-email@qq.com"); // 与配置文件一致
            message.setTo(to);
            message.setSubject("合同审核通过通知");
            message.setText("您创建的合同《" + contractName + "》已审核通过，请注意查看详情。");
            mailSender.send(message);
            log.info("邮件发送成功，收件人：{}", to);
        } catch (Exception e) {
            log.error("邮件发送失败", e);
        }
    }
}