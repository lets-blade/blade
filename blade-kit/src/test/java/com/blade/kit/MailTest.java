package com.blade.kit;

import java.io.IOException;

import blade.kit.MailKit;
import blade.kit.mail.MailTemplate;

public class MailTest {

	public static void main(String[] args) {
		// 发送方配置，邮箱帐号和密码
		MailKit.config(MailKit.SMTP_QQ, "921293209@qq.com", "helloworld");
		
		// 测试给【xxx@qq.com】发送邮件
		MailKit.send("xxx@qq.com", "测试发送邮件", "hello");
		
		// 测试异步发送邮件
		MailKit.asynSend("xxx", "测试异步发送邮件", "hello");
		
		// 测试给【xxx@qq.com】发送邮件并抄送给xoxo@qq.com
		MailKit.sendAndCc("xxx@qq.com", "xoxo@qq.com", "测试发送邮件", "hello");
		
		// 测试给【xxx@qq.com】发送邮件并带一个附件
		MailKit.send("xxx@qq.com", "测试发送邮件", "hello", "F:/aa.txt");
		
		// 使用模板给【xxx@qq.com】发送邮件并带一个附件
		try {
			MailTemplate template = new MailTemplate("F:/a.html", "xxx@qq.com", null);
			MailKit.send(template, "新主题");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
