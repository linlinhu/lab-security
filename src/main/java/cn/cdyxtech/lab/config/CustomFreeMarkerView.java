/**
 * 
 */
package cn.cdyxtech.lab.config;

import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author jim.lee
 *
 */
public class CustomFreeMarkerView extends FreeMarkerView {


	@Override
	protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
		String base = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
		model.put("base", base);
		model.put("isPlatformUser", JWTThreadLocalUtil.isPlatformUser());
		super.exposeHelpers(model, request);
	}
}
