package blade.jetm;

import java.lang.reflect.Method;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.MeasurementRenderer;
import etm.core.renderer.SimpleTextRenderer;

/**
 * 监控方法运行时间
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class JetmKit {
	
	/**
	 * 监控器
	 */
	private static EtmMonitor monitor;
	
	/**
	 * 是否开启监控
	 */
	private static boolean OPEN_JETM = false;
	
	/**
	 * 是否已经启动
	 */
	private static boolean IS_START = false;
	
	/**
	 * 是否每次打印路由执行时长
	 */
	private static boolean PRINT_ROUTE = false;
	
	/**
	 * 打开监控
	 * @param printRoute	是否打开路由执行时长
	 */
	public static void open(boolean printRoute){
		JetmKit.OPEN_JETM = true;
		JetmKit.PRINT_ROUTE = printRoute;
		BasicEtmConfigurator.configure(true);
		monitor = EtmManager.getEtmMonitor();
		start();
	}
	
	/**
	 * 启动EtmMonitor
	 */
	public static void start(){
		if(OPEN_JETM && !IS_START){
			monitor.start();
			IS_START = true;
		}
	}
	
	/**
	 * 重启EtmMonitor
	 */
	public static void restart(){
		if(OPEN_JETM){
			monitor.reset();
		}
	}
	
	/**
	 * @return	返回EtmMonitor
	 */
	public static EtmMonitor etmMonitor(){
		return monitor;
	}
	
	/**
	 * 设置一个输出器
	 * 
	 * @param render	输出格式化实现
	 */
	public static void render(MeasurementRenderer render){
		if(OPEN_JETM && IS_START){
			monitor.render(render);
		}
	}
	
	/**
	 * 默认输出器样式
	 */
	public static void render(){
		if(OPEN_JETM && IS_START){
			monitor.render(new SimpleTextRenderer());
		}
	}
	
	/**
	 * 停止EtmMonitor
	 */
	public static void shutDown() {
		if(OPEN_JETM && IS_START){
			render();
			monitor.stop();
			IS_START = false;
		}
	}
	
	/**
	 * 创建一个监测点
	 * 
	 * @param method	要监测的方法
	 * @return			返回监测点
	 */
	public static JetmPoint createPoint(Method method){
		if(OPEN_JETM && IS_START){
			JetmPoint jetmPoint = new JetmPoint(monitor.createPoint(method.getName()));
			return jetmPoint;
		}
		return null;
	}
	
	/**
	 * 创建一个监测点
	 * 
	 * @param method	要监测的方法
	 * @return			返回监测点
	 */
	public static JetmPoint createPoint(String method){
		if(OPEN_JETM && IS_START){
			JetmPoint jetmPoint = new JetmPoint(monitor.createPoint(method));
			return jetmPoint;
		}
		return null;
	}
	
	/**
	 * 收集监测数据，表示该监测点监测结束
	 * 
	 * @param point		监测点对象
	 */
	public static void collect(JetmPoint point){
		if(null != point){
			point.collect();
		}
	}
	
	/**
	 * 收集监测数据并输出，表示该监测点监测结束
	 * 
	 * @param point
	 */
	public static void collectAndRender(JetmPoint point){
		if(null != point){
			point.collect();
			if(JetmKit.PRINT_ROUTE){
				monitor.render(new BladeTextRenderer());
			}
		}
	}
}
