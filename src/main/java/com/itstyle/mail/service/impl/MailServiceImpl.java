package com.itstyle.mail.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.itstyle.mail.model.Email;
import com.itstyle.mail.service.IMailService;
import com.itstyle.mail.util.Constants;
import com.itstyle.mail.util.MailUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class MailServiceImpl implements IMailService {
	@Autowired
	private JavaMailSender mailSender;
	@Value("${spring.mail.username}")
	public String USER_NAME;
	@Autowired
	public Configuration configuration;
	@Autowired
	private SpringTemplateEngine  templateEngine;

	@Override
	public void send(Email mail) throws Exception {
		MailUtil mailUtil = new MailUtil();
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(USER_NAME);
		message.setTo(mail.getEmail());
		message.setSubject(mail.getSubject());
		message.setText(mail.getContent());
		mailUtil.start(mailSender, message);
	}

	@Override
	public void sendHtml(Email mail) throws Exception {
		MailUtil mailUtil = new MailUtil();
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(USER_NAME);
		helper.setTo(mail.getEmail());
		helper.setSubject(mail.getSubject());
		helper.setText(
				"<html><body><img src=\"cid:springcloud\" ></body></html>",
				true);
		// 发送图片
		File file = ResourceUtils.getFile("classpath:static"
				+ Constants.SF_FILE_SEPARATOR + "image"
				+ Constants.SF_FILE_SEPARATOR + "springcloud.png");
		helper.addInline("springcloud", file);
		// 发送附件
		file = ResourceUtils.getFile("classpath:static"
				+ Constants.SF_FILE_SEPARATOR + "file"
				+ Constants.SF_FILE_SEPARATOR + "关注科帮网获取更多源码.zip");
		helper.addAttachment("科帮网", file);
		mailUtil.startHtml(mailSender, message);
	}

	@Override
	public void sendFreemarker(Email mail) throws Exception {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(USER_NAME);
		helper.setTo(mail.getEmail());
		helper.setSubject(mail.getSubject());
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("content", mail.getContent());
		Template template = configuration.getTemplate(mail.getTemplate());
		String text = FreeMarkerTemplateUtils.processTemplateIntoString(
				template, model);
		helper.setText(text, true);
		mailSender.send(message);
	}
	@Override
	public void sendThymeleaf(Email mail) throws Exception {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(USER_NAME);
		helper.setTo(mail.getEmail());
		helper.setSubject(mail.getSubject());
		Context context = new Context();
		context.setVariable("email", mail);
		String text = templateEngine.process(mail.getTemplate(), context);
		helper.setText(text, true);
		mailSender.send(message);
	}
}
