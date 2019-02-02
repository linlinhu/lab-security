/**
 * 
 */
package cn.cdyxtech.lab.filter;

import cn.cdyxtech.lab.feign.PermissionAPIFeign;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author jim.lee
 *
 */
@Component
public class MenuFilter {

	
	@Autowired
	private PermissionAPIFeign permissionAPIFeign;
	
	@Value("${spring.application.code}")
	private String appCode;
	
	public JSONArray buildMenByUserType(int type,Long...groupIds) {
		JSONObject menuResult  = new JSONObject();
		if(type!=1) {
		
			
			if(groupIds!=null && groupIds.length>0) {
				
				menuResult = permissionAPIFeign.userMenu(appCode, groupIds);
			}
			
		}else{
			menuResult = permissionAPIFeign.superMenu(appCode);
		}
		if(menuResult!=null 
				&& !menuResult.isEmpty() 
				&& menuResult.containsKey("success") 
				&& menuResult.getBooleanValue("success")) {
			return buildMenuList(menuResult.getJSONArray("result"));
			
		}
		return new JSONArray();
	}
	
	private JSONArray buildMenuList(JSONArray menuArr) {
		JSONArray menuList = new JSONArray();
		Set<Long> handledIds = new HashSet<>();
		for(int i=0;i<menuArr.size();i++) {
			JSONObject menu = menuArr.getJSONObject(i);
			if(handledIds.contains(menu.getLong("id"))) {
				continue;
			}
			JSONArray children = new JSONArray();
			if(!menu.getBooleanValue("leaf")) {
				for(int j=0;j<menuArr.size();j++) {
					JSONObject subMenu = menuArr.getJSONObject(j);
					if(subMenu.getLong("pid")!=null && menu.getLongValue("id")==subMenu.getLongValue("pid")) {
						children.add(subMenu);
						handledIds.add(subMenu.getLong("id"));
					}
				}
			}
			menu.put("children", children);
			menuList.add(menu);
		}
		return menuList;
	}
}
