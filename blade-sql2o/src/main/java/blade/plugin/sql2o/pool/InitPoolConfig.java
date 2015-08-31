package blade.plugin.sql2o.pool;

import java.util.ArrayList;
import java.util.List;

/**
 * 初始化数据连接池配置
 * @author biezhi
 *
 */
public class InitPoolConfig {
	
	static List<PoolConfig> poolConfigList;
	
	static{
		poolConfigList = new ArrayList<PoolConfig>();
	}
	
	public static void add(PoolConfig poolConfig){
		poolConfigList.add(poolConfig);
	}
	
}
