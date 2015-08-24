package blade.test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import blade.kit.TaskKit;


public class TaskTest {

	public static void main(String[] args) {
		TaskKit.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				System.out.println("每隔两秒执行一次");
			}
		}, 2);
		
		TaskKit.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				System.out.println("在指定的延时之后开始以固定的频率来运行任务。后续任务的启动时间不受前次任务延时影响。");
			}
		}, 10, 3, TimeUnit.SECONDS);
		
		TaskKit.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				System.out.println("在指定的时间点启动，两次任务间保持固定的时间间隔");
			}
		}, new Date(), 3, TimeUnit.SECONDS);
		
	}
	
}
