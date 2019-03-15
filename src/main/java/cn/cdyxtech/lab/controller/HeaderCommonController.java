package cn.cdyxtech.lab.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.controller.BaseController;
import com.emin.base.exception.EminException;
import com.emin.base.util.CookieUtil;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.constain.ConfigOption;
import cn.cdyxtech.lab.facade.ConfigFacade;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import cn.cdyxtech.lab.util.JWTUtil;
import cn.cdyxtech.lab.util.JsonObjectHelper;
import cn.cdyxtech.lab.util.ResponseBackHelper;
import cn.cdyxtech.lab.util.UserClaim;

public abstract class HeaderCommonController extends BaseController {

    private Long[] jsonArrToLongArr(JSONArray jsonArray) {
        Long[] idArr = null;
        if (!(jsonArray == null || jsonArray.isEmpty())) {
            List<Long> ids = jsonArray.stream().map(e -> Long.valueOf(e.toString())).collect(Collectors.toCollection(ArrayList::new));
            idArr = ids.toArray(new Long[ids.size()]);
        }
        return idArr;
    }
    
    /**
     * @param
     * @return com.emin.platform.permission.util.UserClaim
     * @auth Anson
     * @name 验证当前token值，若非法以及过期都返回空,表明当前token非法
     * @date 18-5-4
     * @since 1.0.0
     */
    protected UserClaim validateAuthorizationToken() {
        String authorization = JWTThreadLocalUtil.getJwt();
        if (StringUtils.isBlank(authorization)) {
            return new UserClaim();
        }
        try {
        	JSONObject authJson=JWTUtil.validateToken(authorization);
        	UserClaim userClaim = UserClaim.instance(authJson);
        	//获取当前用户的emcid相关信息
        	String ecmInfoStr = CookieUtil.getValue(getRequest(), ApplicationConstain.USER_ECM_ID_INFOS_KEY);
        	if(StringUtils.isNoneBlank(ecmInfoStr)){
        		/*JSONObject ecmInfoJson = (JSONObject) JSONObject.toJSON(ecmInfoStr);*/
        		JSONObject ecmInfoJson = JSONObject.parseObject(ecmInfoStr);
        		if (ecmInfoJson!=null && !ecmInfoJson.isEmpty()) {
        			//包装当前用户对应的ecm相关信息
            		JsonObjectHelper helper = new JsonObjectHelper(ecmInfoJson);
            		Long userId = helper.getValue("userId", Long.class);
            		if (userClaim.getId().compareTo(userId)==0) {
						//确定是同一个用户的时候
            			userClaim.setSchoolEcmId(helper.getValue("schoolEcmId", Long.class));
            			userClaim.setBranchEcmId(helper.getValue("branchEcmId", Long.class));
            			userClaim.setLabEcmId(helper.getValue("labEcmId", Long.class));
            			userClaim.setHighestEcmId(helper.getValue("highestEcmId", Long.class));
                        userClaim.setHighestEcmIdType(helper.getValue("highestEcmIdType", Integer.class));
                        userClaim.setPersonalHeigherEcmId(helper.getValue("personalHeigherEcmId", Long.class));
					}
				}
        	}
        	
        	return userClaim;
		} catch (RuntimeException e) {
			  return new UserClaim();
		}
    }
        
        


    protected void dealException(JSONObject res) {
        if (!res.isEmpty()) {
            if (!res.getBooleanValue("success")) {
                throw new EminException(res.getString("code"));
            }
            if (!res.containsKey("result")) {
                throw new EminException("BASE_0.0.1");
            }
        } else {
            throw new EminException("BASE_0.0.0");
        }
    }

	private HttpServletRequest request;
    protected  Object getParameterValue(String parameter) {
        String[] parameterArray = getParameterArray(parameter);
        Object result;
        try {
            if (parameterArray != null && parameterArray.length == 1) {
                result = parameterArray[0];
            } else {
                result = request.getAttribute(parameter);
            }
        } catch (Exception e) {
            result = null;
        }
        
        return result == null || result.equals("") ? null : result;
     }


     @Autowired
     private ConfigFacade configFacade;

    protected JSONObject gradeReloadResult(JSONObject result) {
       
        JSONArray resultList = result.getJSONArray("resultList");
        List<ConfigOption.ConfigItem> hiddenDangerGrades = configFacade.getConfigItems(ConfigOption.POTENTIAL_HAZARD_LEVEL_GROUP);
        for (int i = 0; i < resultList.size(); i++) {
            Integer grade = resultList.getJSONObject(i).getInteger("unqualifiedGrade");
            for (int j = 0; j < hiddenDangerGrades.size(); j++) {// 遍历隐患等级的value，如果value包含unqualifiedGrade，则重新赋value给resultList遍历对象的unqualifiedGrade
                String gradesStr = hiddenDangerGrades.get(j).getValue();
                String[] grades = gradesStr.split(",");
                for (String gradeStr : grades) {
                    if (Integer.parseInt(gradeStr) == grade) {
                        resultList.getJSONObject(i).put("unqualifiedGrade", gradesStr);
                    }
                }
            }
        }
        result.put("resultList", resultList);
        return result;
    }
     


}
