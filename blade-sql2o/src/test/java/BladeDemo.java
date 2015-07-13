import blade.plugin.sql2o.Model;
import blade.plugin.sql2o.Table;

/**
 * 
 * CREATE TABLE `blade_demo` (
   `id` int(10) NOT NULL AUTO_INCREMENT,
   `val` varchar(10) NOT NULL,
   PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf-8
 */

@Table("blade_demo")
public class BladeDemo extends Model {

	private static final long serialVersionUID = -9181747713955766758L;
	private Integer id;
	private String val;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

}
