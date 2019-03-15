/**
 * 
 */
package cn.cdyxtech.lab.filter;

import cn.cdyxtech.lab.feign.PermissionAPIFeign;
import cn.cdyxtech.lab.util.JsonObjectHelper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

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
		Map<Long, List<JSONObject>> menuParentMap = new HashMap<>();
		JSONArray menuList = new JSONArray();
		int size = menuArr == null || menuArr.isEmpty() ? 0 : menuArr.size();
		JSONArray topMenus = new JSONArray();
		JSONObject m = null;
		Long pid = null;
		List<JSONObject> ms = null;
		JsonObjectHelper helper = null;
		for (int i = 0; i < size; i++) {
			m = menuArr.getJSONObject(i);
			helper = new JsonObjectHelper(m);
			pid = helper.getValue("pid", Long.class);
			if (pid != null) {
				ms = menuParentMap.getOrDefault(pid, new ArrayList<>());
				ms.add(m);
				menuParentMap.put(pid, ms);
			} else {
				topMenus.add(m);
			}
		}
		Long menuId = null;
		JSONArray children = null;
		for (int i = 0; i < topMenus.size(); i++) {
			m = topMenus.getJSONObject(i);
			helper = new JsonObjectHelper(m);
			menuId = helper.getValue("id", Long.class);
			if (menuId == null) {
				continue;
			}
			children = this.getSubChildren(menuParentMap, menuId);
			if (children!=null && !children.isEmpty()) {
				m.put("children", children);
			}
			menuList.add(m);
		}
		return menuList;
	}

	private JSONArray getSubChildren(Map<Long, List<JSONObject>> menuParentMap, Long curId) {
		JSONArray children = new JSONArray();
		if (menuParentMap == null || menuParentMap.isEmpty() || curId == null || !menuParentMap.containsKey(curId)) {
			return children;
		}
		List<JSONObject> cList = menuParentMap.get(curId);
		if (cList != null && !cList.isEmpty()) {
			cList.forEach(e -> {
				JsonObjectHelper helper = new JsonObjectHelper(e);
				Long mId = helper.getValue("id", Long.class);
				if (helper.getValue("leaf", false, Boolean.class)) {
					e.put("children", this.getSubChildren(menuParentMap, mId));
				}
				children.add(e);
			});
		}
		return children;
	}
}
