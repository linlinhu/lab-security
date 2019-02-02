/**
 * 
 */
package cn.cdyxtech.lab.interceptor;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;
import com.emin.base.util.CookieUtil;

import cn.cdyxtech.lab.annotation.IgnoreIterceptor;
import cn.cdyxtech.lab.feign.ResultCheckUtil;
import cn.cdyxtech.lab.feign.UserAPIFeign;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import cn.cdyxtech.lab.util.JWTUtil;


/**
 * @author jim.lee
 *
 */
public class WebInterceptor extends HandlerInterceptorAdapter {

	private static Logger LOGGER = LoggerFactory.getLogger(WebInterceptor.class);

	@Autowired
	private UserAPIFeign personAPIFeign;

	public static boolean isRequestFromAJAX(HttpServletRequest request){
		String requestType = request.getHeader("X-Requested-With");
		if("XMLHttpRequest".equals(requestType)){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		
		/*JWTThreadLocalUtil.setJwt("cn.cdyxtech.super.token");
		JWTThreadLocalUtil.setEcmId(1l);*/
		//@todo superEcmId  
		Long ecmId = null;
		
		if (!this.filterPath(request, handler)) {
            return true;
        }
		
		JSONObject res;
		String jwt = CookieUtil.getValue(request,"Authorization");
		boolean validation = true;

		if(StringUtils.isBlank(jwt)){
			validation = false;
		}else{
			try {
				JSONObject json = JWTUtil.validateToken(jwt);
				System.out.println(json);
				JSONArray ecmIds = json.getJSONArray("ecmIds");
				JWTThreadLocalUtil.setEcmId(ecmIds.size()>0?ecmIds.getLong(0):null);
				JWTThreadLocalUtil.setUserId(json.getLong("id"));
				JWTThreadLocalUtil.setJwt(jwt);
			} catch (RuntimeException e) {
				validation = false;
			}

		}
		if(!validation){
			if(isRequestFromAJAX(request)){
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
			}else{
				response.sendRedirect("/login");
			}
			return false;
		}
		return true;
	}
	
    /**
     * 当前返回结果集为true：表示需要去处理拦截，返回false表示不需要拦截
     *
     * @param request
     * @param arg2
     * @return
     */
    private boolean filterPath(HttpServletRequest request, Object arg2) {
        if (HandlerMethod.class.isAssignableFrom(arg2.getClass())) {
            HandlerMethod handlerMethod = (HandlerMethod) arg2;
            Method method = handlerMethod.getMethod();
            IgnoreIterceptor ignoreIterceptor = method.getAnnotation(IgnoreIterceptor.class);
            if (ignoreIterceptor != null) {
                //标志位true的时候，表示当前路径不需要拦截，则返回false
                return !ignoreIterceptor.value();
            } else {
                return true;
            }
        }
        return true;
    }


	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
	}

	@Override
	public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) {
	}

}
