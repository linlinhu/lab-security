/**
 * 
 */
package cn.cdyxtech.lab.filter;

import cn.cdyxtech.lab.feign.PermissionAPIFeign;
import cn.cdyxtech.lab.feign.UserAPIFeign;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.util.CommonsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author jim.lee
 *
 */
@Component
public class MenuOperationFilter {

	@Value("${spring.application.code}")
	private String appCode;
	
	@Autowired
	private PermissionAPIFeign permissionAPIFeign;
	@Autowired
	private UserAPIFeign personAPIFeign;
	
	public String menuOperations(String menuCode) {
		JSONObject userDetail = personAPIFeign.detail(JWTThreadLocalUtil.getUserId());
		JSONObject user = userDetail.getJSONObject("result");
		JSONObject flockResult = personAPIFeign.getUserFlocks(JWTThreadLocalUtil.getUserId());
		JSONArray flocks = flockResult.getJSONArray("result");

		JSONArray operations = null;
		if(user.getIntValue("userType")!=1) {
			if(flocks.size()>0) {
				Long[] groupIds = new Long[flocks.size()];
				for(int i=0;i<flocks.size();i++) {
					groupIds[i]=flocks.getJSONObject(i).getLong("id");
				}
				JSONObject result = permissionAPIFeign.menuOperation(appCode, menuCode,groupIds);
				operations = result.getJSONObject("result").getJSONArray("operation");
			}
		}else{

			JSONObject result = permissionAPIFeign.menuOperation(appCode, menuCode);
			operations = result.getJSONArray("result");
		}

		if(operations!=null) {
			String[] operationCodes = new String[operations.size()];
			for(int i=0;i<operations.size();i++) {
				operationCodes[i] = operations.getJSONObject(i).getString("code");
			}
			return CommonsUtil.stringArrToString(operationCodes);
		}

		return "";
	}
}
