package blade.plugin.sql2o.ds;

import javax.sql.DataSource;

/**
 * 抽象数据源
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public interface AbstractDataSource{

    DataSource getDataSource();
    
}
