package blade.plugin.sql2o;

/**
 * 用于保存数据库元信息
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class DBConfig {
	
	private String drive;
	private String url;
	private String user;
	private String password;
	
	public DBConfig(String drive, String url, String user, String password) {
		super();
		this.drive = drive;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public String getDrive() {
		return drive;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}
	
}
