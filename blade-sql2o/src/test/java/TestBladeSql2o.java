import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import blade.exception.BladeException;
import blade.plugin.sql2o.Model;
import blade.plugin.sql2o.Sql2oPlugin;

public class TestBladeSql2o {

	private Model model = null;
	
	@Before
    public void before() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// 配置数据库插件
		Sql2oPlugin sql2oPlugin = Sql2oPlugin.INSTANCE;
		sql2oPlugin.config("jdbc:mysql://127.0.0.1:3306/test", "com.mysql.jdbc.Driver", "root", "root");
		sql2oPlugin.openCache();
		sql2oPlugin.run();
		
		model = Model.getModel(BladeDemo.class);
	}
	
    @Test
    public void testTransaction() {
    	
        Sql2o sql2o = model.getSql2o();
        final String insertSql = "insert into blade_demo(val) values (:val)";
        final String updateSql = "update blade_demo set val = :val where id = :id";
        Connection con = sql2o.beginTransaction();
        try{
            con.createQuery(insertSql).addParameter("val", "foo").executeUpdate();
            con.createQuery(updateSql).addParameter("val2", "bar").executeUpdate();
            con.commit();
        } catch(Exception e){
        	if(null != con){
        		con.rollback();
        	}
        	e.printStackTrace();
        }
        
    }
    
    @Test
    public void testException() {
    	
    	try {
			Integer count = model.insert().param("val", "asdadasdadasdsaadsadadasdadsadasdadad").executeAndCommit();
			System.out.println(count);
		} catch (BladeException e1) {
			e1.printStackTrace();
		}
        
        
    }
}
