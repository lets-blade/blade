package blade.test;

import org.junit.Before;
import org.junit.Test;

import blade.kit.MailKit;

public class MailTest {

	@Before
	public void before(){
		MailKit.config(MailKit.SMTP_QQ, "", "");
	}
	
	@Test
	public void testSendMail(){
		MailKit.send("xxx", "测试发送邮件", "hello");
	}
	
	@Test
	public void testAsynSendMail(){
		MailKit.asynSend("xxx", "测试异步发送邮件", "hello");
	}
	
}
