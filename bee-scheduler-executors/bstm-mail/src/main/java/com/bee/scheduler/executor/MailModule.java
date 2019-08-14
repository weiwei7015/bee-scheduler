package com.bee.scheduler.executor;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.ExecutionContext;
import com.bee.scheduler.core.ExecutionException;
import com.bee.scheduler.core.ExecutionResult;
import com.bee.scheduler.core.ExecutorModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author weiwei 用于发送邮件
 */
public class MailModule implements ExecutorModule {
    @Override
    public String getId() {
        return "MailModule";
    }

    @Override
    public String getName() {
        return "MailModule";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getAuthor() {
        return "weiwei";
    }

    @Override
    public String getDescription() {
        return "用于发送邮件";
    }

    @Override
    public String getParamTemplate() {
        return "{\r" +
                "    \"smtp_host\":\"\",\r" +
                "    \"smtp_port\":465,\r" +
                "    \"smtp_auth\":true,\r" +
                "    \"smtp_ssl_enable\":true,\r" +
                "    \"from\":\"xxx@xx.com\",\r" +
                "    \"recipients_to\":\"xxx@xx.com,xxx@xx.com\",\r" +
                "    \"recipients_cc\":\"xxx@xx.com,xxx@xx.com\",\r" +
                "    \"subject\":\"\",\r" +
                "    \"content\":\"\",\r" +
                "    \"account\":\"\",\r" +
                "    \"password\":\"\"\r" +
                "}";
    }

    @Override
    public ExecutionResult exec(ExecutionContext context) throws Exception {
        JSONObject taskParam = context.getParam();
        Log logger = context.getLogger();

        //        String protocol = taskParam.getString("protocol");
        String protocol = "smtp";
        String smtpHost = taskParam.getString("smtp_host");
        Integer smtpPort = taskParam.getInteger("smtp_port");
        Boolean smtpAuth = taskParam.getBoolean("smtp_auth");
        Boolean smtpSSLEnable = taskParam.getBoolean("smtp_ssl_enable");
        String from = taskParam.getString("from");
        String recipientTo = taskParam.getString("recipients_to");
        String recipientCc = taskParam.getString("recipients_cc");
        String subject = taskParam.getString("subject");
        String content = taskParam.getString("content");
        String account = taskParam.getString("account");
        String password = taskParam.getString("password");

        //参数预处理
        recipientTo = StringUtils.trimToEmpty(recipientTo);
        if (StringUtils.isEmpty(recipientTo)) {
            throw new ExecutionException("参数有误:recipients_to");
        }
        recipientCc = StringUtils.trimToEmpty(recipientCc);

        Properties properties = new Properties();
        properties.put("mail.transport.protocol", protocol);
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", smtpAuth);
        properties.put("mail.smtp.ssl.enable", smtpSSLEnable);

        Session session = Session.getInstance(properties);
        Message message = new MimeMessage(session);
        //发件人
        message.setFrom(new InternetAddress(from));
        //收件人
        ArrayList<InternetAddress> recipientToList = new ArrayList<>();
        for (String address : StringUtils.split(recipientTo, ",")) {
            recipientToList.add(new InternetAddress(address));
        }
        message.setRecipients(Message.RecipientType.TO, recipientToList.toArray(new Address[0]));
        //抄送人
        if (StringUtils.isNotEmpty(recipientCc)) {
            ArrayList<InternetAddress> recipientCcList = new ArrayList<>();
            for (String address : StringUtils.split(recipientCc, ",")) {
                recipientCcList.add(new InternetAddress(address));
            }
            message.setRecipients(Message.RecipientType.CC, recipientCcList.toArray(new Address[0]));
        }
        // 邮件标题
        message.setSubject(subject);
        // 邮件内容
        message.setText(content);
        // 得到邮差对象
        Transport transport = session.getTransport();
        // 连接自己的邮箱账户
        transport.connect(account, password);
        // 发送邮件
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();

        return ExecutionResult.success();
    }
}
