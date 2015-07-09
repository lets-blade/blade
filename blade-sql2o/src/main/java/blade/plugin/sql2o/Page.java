package blade.plugin.sql2o;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页类
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 * @param <T>
 */
public class Page<M> {

	/**
	 * 当前页
	 */
	private int page = 1;
	
	/**
	 * 每页条数
	 */
	private int pageSize = 10;
	
	/**
	 * 总页数
	 */
	private int totalPage = 1;
	
	/**
	 * 总记录数
	 */
	private long totalCount = 0L;
	/**
	 * 上一页
	 */
	private int prev_page = 1;
	/**
	 * 下一页
	 */
	private int next_page = 1;
	/**
	 * 首页
	 */
	private int home_page = 1;
	/**
	 * 尾页
	 */
	private Integer last_page = 1;
	
	/**
	 * 链接
	 */
	private String url = "";
	/**
	 * 固定导航数
	 */
	private Integer navNum = 1;
	
	/**
	 * 数据集
	 */
	private List<M> results = new ArrayList<M>();

	/**
	 * @param totleCount 	总记录数
	 * @param page			当前第几页
	 * @param pageSize	 	每页显示条数
	 */
	public Page(long totalCount, int page, int pageSize) {

		this.page = page;

		this.pageSize = pageSize;

		//总条数
		this.totalCount = totalCount;
		
		//总页数
		this.totalPage = ((int) ((this.totalCount + this.pageSize - 1) / pageSize));
		
		//首页
		this.home_page = 1;

		//尾页
		this.last_page = totalPage;

		//上一页
		this.prev_page = Math.max(this.page - 1, home_page);

		//下一页
		this.next_page = Math.min(this.page + 1, last_page);

	}

	public int getPage() {
		return this.page;
	}

	public Integer getPageSize() {
		return this.pageSize;
	}

	public int getTotalPage() {
		return this.totalPage;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public Integer getPrev_page() {
		return this.prev_page;
	}

	public Integer getNext_page() {
		return this.next_page;
	}

	public Integer getHome_page() {
		return this.home_page;
	}

	public Integer getLast_page() {
		return this.last_page;
	}

	public List<M> getResults() {
		return this.results;
	}

	public void setResults(List<M> results) {
		this.results = results;
	}

	public Integer getNavNum() {
		return this.navNum;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public void setPrev_page(int prev_page) {
		this.prev_page = prev_page;
	}

	public void setNext_page(int next_page) {
		this.next_page = next_page;
	}

	public void setHome_page(int home_page) {
		this.home_page = home_page;
	}

	public void setLast_page(Integer last_page) {
		this.last_page = last_page;
	}

	public void setNavNum(Integer navNum) {
		this.navNum = navNum;
	}
	
}
