package cn.cdyxtech.lab.controller;

// import cn.cdyxtech.lab.config.ConfigBean;
import cn.cdyxtech.lab.feign.UserAPIFeign;
import cn.cdyxtech.lab.filter.MenuFilter;
import cn.cdyxtech.lab.util.UserClaim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

@Controller
public class IndexController extends HeaderCommonController {

    private Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Autowired
    private UserAPIFeign personAPIFeign;
    @Autowired
    private MenuFilter menuFilter;
    // @Autowired
    // private ConfigBean configBean;

    @GetMapping("/")
    public String index(Map<String,Object> data){
       /* Long userId = JWTThreadLocalUtil.getUserId();
        JSONObject userDetail = personAPIFeign.detail(userId);
        JSONObject user = userDetail.getJSONObject("result");
        ResultCheckUtil.check(userDetail);
        JSONObject flockResult = personAPIFeign.getUserFlocks(userId);
        JSONArray flocks = flockResult.getJSONArray("result");
        int userType = user.getIntValue("userType");
        if(flocks.size()==0 && userType!=1){
            data.put("noPermissions",true);
        }else{
            JSONArray menuList;
            if(flocks.size()>0){
                Long[] groupIds = new Long[flocks.size()];
                for(int j=0;j<flocks.size();j++){
                    groupIds[j] = flocks.getJSONObject(j).getLong("id");
                }
                menuList = menuFilter.buildMenByUserType(userType, groupIds);
            }else{
                menuList = menuFilter.buildMenByUserType(userType);
            }
            data.put("menus",menuList);
        }
        data.put("userDetail",user);*/
    	UserClaim userClaim = this.validateAuthorizationToken();
    	String reanName = userClaim.getRealName();
    	String mobile = userClaim.getMobile();
    	data.put("reanName", reanName);
        data.put("mobile", mobile);
        data.put("userClaim", userClaim);

        logger.info("登录用户ecm组成信息===>" + JSONObject.toJSONString(userClaim) + "+++++++++++++++++++++++++++++++++++++++++");
        return "index";
    }
}
