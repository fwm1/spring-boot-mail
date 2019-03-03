package com.itstyle.mail.web;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.itstyle.mail.common.model.Email;
import com.itstyle.mail.common.model.Result;
import com.itstyle.mail.service.IMailService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Api(tags ="邮件管理")
@RestController
@RequestMapping("/mail")
public class mailController {
	
	@Autowired
	private IMailService mailService;
	
	@PostMapping("send")
	public Result send(Email mail) {
		try {
			mailService.sendFreemarker(mail);
		} catch (Exception e) {
			e.printStackTrace();
			return  Result.error();
		}
		return  Result.ok();
	}
	
	@PostMapping("list")
	public Result list(Email mail) {
		return mailService.listMail(mail);
	}

	@PostMapping("scheduleMail")
	public Result scheduleMail(Email email, String dateStr){
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
			System.out.print(date.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			mailService.scheduleMail(email, dateStr);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error();
		}
		return Result.ok();
	}
}
