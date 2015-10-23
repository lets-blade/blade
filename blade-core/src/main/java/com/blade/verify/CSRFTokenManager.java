package com.blade.verify;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.blade.servlet.Request;
import com.blade.servlet.Session;

import blade.kit.HashidKit;
import blade.kit.StringKit;

/**
 * CSRF token管理器
 * @author biezhi
 *
 */
public class CSRFTokenManager {
	
	public static final String CSRF_PARAM_NAME = "_CSRFToken";
    
	private static CSRFConfig config = new CSRFConfig();
	
	private static HashidKit HASHID = new HashidKit(CSRF_PARAM_NAME, config.length);
	
	private CSRFTokenManager() {
	}
	
	/*public static void config(String salt, int length){
		CSRFTokenManager.length = length;
		HASHID = new HashidKit(salt, length);
	}*/
	
	public static void config(CSRFConfig config){
		CSRFTokenManager.config = config;
		HASHID = new HashidKit(config.salt, config.length);
	}
	
	/**
	 * 创建一个token
	 * @param session
	 * @return
	 */
    public static String createToken(HttpSession session) {
        String token = null;
        synchronized (session) {
            Object objToken = session.getAttribute(CSRF_PARAM_NAME);
            if (null == objToken) {
            	token = HASHID.encode( System.currentTimeMillis() );
            	session.setAttribute(CSRF_PARAM_NAME, token);
            } else {
            	token = objToken.toString();
			}
        }
        return token;
    }
    
    /**
	 * 创建一个token
	 * @param session
	 * @return
	 */
    public static String createToken(Session session) {
        String token = null;
        synchronized (session) {
            Object objToken = session.attribute(CSRF_PARAM_NAME);
            if (null == objToken) {
            	token = HASHID.encode( System.currentTimeMillis() );
            	session.attribute(CSRF_PARAM_NAME, token);
            } else {
            	token = objToken.toString();
			}
        }
        return token;
    }
    
    /**
	 * csrf验证
	 * @param request
	 * @return
	 */
	public static boolean verifyCsrfForm(HttpServletRequest request) {
		// 从 session 中得到 csrftoken 属性
		HttpSession session = request.getSession();
		Object oToken = session.getAttribute(CSRFTokenManager.CSRF_PARAM_NAME);
		String sToken = ( null != oToken ) ? oToken.toString() : null;
		if (sToken == null) {
			// 产生新的 token 放入 session 中
			sToken = CSRFTokenManager.createToken(session);
			System.out.println("生成token：" + sToken);
			return true;
		} else {
			String pToken = request.getParameter(CSRFTokenManager.CSRF_PARAM_NAME);
			if (StringKit.isNotBlank(pToken) && sToken.equals(pToken)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean verifyCsrfForm(Request request) {
		// 从 session 中得到 csrftoken 属性
		Session session = request.session();
		Object oToken = session.attribute(CSRFTokenManager.CSRF_PARAM_NAME);
		String sToken = ( null != oToken ) ? oToken.toString() : null;
		if (sToken == null) {
			// 产生新的 token 放入 session 中
			sToken = CSRFTokenManager.createToken(session);
			System.out.println("生成token：" + sToken);
			return true;
		} else {
			String pToken = request.query(CSRFTokenManager.CSRF_PARAM_NAME);
			if (StringKit.isNotBlank(pToken) && sToken.equals(pToken)) {
				return true;
			}
		}
		
		return false;
	}
	
}
