package blade;

import java.util.Map;

import blade.kit.StringKit;

public class BladeConfigurator {

	private BladeConfig bladeConfig;
	
	private Map<String, String> configMap;
	
	public BladeConfigurator(BladeConfig bladeConfig,
			Map<String, String> configMap) {
		this.bladeConfig = bladeConfig;
		this.configMap = configMap;
	}
	
	private static final String BLADE_ROUTE = "blade.route";
	private static final String BLADE_INTERCEPTOR = "blade.interceptor";
	private static final String BLADE_IOC = "blade.ioc";
	private static final String BLADE_PREFIX = "blade.prefix";
	private static final String BLADE_SUFFIX = "blade.suffix";
	private static final String BLADE_FILTER_FOLDER = "blade.filter_folder";
	private static final String BLADE_DBURL = "blade.dburl";
	private static final String BLADE_DBDRIVER = "blade.dbdriver";
	private static final String BLADE_DBUSER = "blade.dbuser";
	private static final String BLADE_DBPASS = "blade.dbpass";
	private static final String BLADE_OPENCACHE = "blade.opencache";
	private static final String BLADE_ENCODING = "blade.encoding";
	private static final String BLADE_VIEW_404 = "blade.view404";
	private static final String BLADE_VIEW_500 = "blade.view500";
	private static final String BLADE_DEBUG = "blade.debug";
	
	/**
	 * 配置文件属性
	 * blade.route=
	 * blade.interceptor=
	 * blade.ioc=
	 * blade.prefix=
	 * blade.suffix=
	 * blade.filter_folder=
	 * blade.dburl=
	 * blade.dbdriver=
	 * blade.dbuser=
	 * blade.dbpass=
	 * blade.opencache=
	 * blade.encoding=
	 * blade.view404=
	 * blade.view500=
	 * blade.debug=
	 */
	public void run() {
		if (null != configMap && configMap.size() > 0) {
			String route = configMap.get(BLADE_ROUTE);
			String interceptor = configMap.get(BLADE_INTERCEPTOR);
			String ioc = configMap.get(BLADE_IOC);
			String prefix = configMap.get(BLADE_PREFIX);
			String suffix = configMap.get(BLADE_SUFFIX);
			String filter_folder = configMap.get(BLADE_FILTER_FOLDER);
			String dburl = configMap.get(BLADE_DBURL);
			String dbdriver = configMap.get(BLADE_DBDRIVER);
			String dbuser = configMap.get(BLADE_DBUSER);
			String dbpass = configMap.get(BLADE_DBPASS);
			String opencache = configMap.get(BLADE_OPENCACHE);
			String encoding = configMap.get(BLADE_ENCODING);
			String view404 = configMap.get(BLADE_VIEW_404);
			String view500 = configMap.get(BLADE_VIEW_500);
			String debug = configMap.get(BLADE_DEBUG);
			
			if (StringKit.isNotBlank(route)) {
				String[] blade_routes = StringKit.split(route, ",");
				bladeConfig.setRoutePackages(blade_routes);
			}
			
			if (StringKit.isNotBlank(filter_folder)) {
				String[] blade_filter_folders = StringKit.split(filter_folder, ",");
				bladeConfig.setStaticFolders(blade_filter_folders);
			}
			
			if (StringKit.isNotBlank(interceptor)) {
				bladeConfig.setInterceptorPackage(interceptor);
			}
			
			if (StringKit.isNotBlank(ioc)) {
				String[] blade_iocs = StringKit.split(ioc, ",");
				bladeConfig.setIocPackages(blade_iocs);
			}
			
			if (StringKit.isNotBlank(prefix)) {
				bladeConfig.setViewPrefix(prefix);
			}
			
			if (StringKit.isNotBlank(suffix)) {
				bladeConfig.setViewSuffix(suffix);
			}
			
			if (StringKit.isNotBlank(dburl) && StringKit.isNotBlank(dbdriver) &&
					StringKit.isNotBlank(dbuser) && StringKit.isNotBlank(dbpass)) {
				bladeConfig.setDbUrl(dburl);
				bladeConfig.setDbDriver(dbdriver);
				bladeConfig.setDbUser(dbuser);
				bladeConfig.setDbPass(dbpass);
				
				if (StringKit.isNotBlank(opencache)) {
					Boolean opencacheBool = Boolean.valueOf(opencache);
					bladeConfig.setOpenCache(opencacheBool);
				}
			}
			
			if (StringKit.isNotBlank(encoding)) {
				bladeConfig.setEncoding(encoding);
			}
			
			if (StringKit.isNotBlank(view404)) {
				bladeConfig.setView404(view404);
			}
			
			if (StringKit.isNotBlank(view500)) {
				bladeConfig.setView500(view500);
			}
			
			if (StringKit.isNotBlank(debug)) {
				Boolean debugBool = Boolean.valueOf(debug);
				bladeConfig.setDebug(debugBool);
			}
		}
	}
}