package blade.plugin.sql2o;

public class DBConfig {

	/**
	 * 驱动名称
	 */
	private String driverName;

	/**
	 * 连接地址
	 */
	private String url;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 密码
	 */
	private String passWord;

	public DBConfig() {
		// TODO Auto-generated constructor stub
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	@Override
	public String toString() {
		return "DBConfig [driverName=" + driverName + ", url=" + url
				+ ", userName=" + userName + ", passWord=" + passWord + "]";
	}

}
