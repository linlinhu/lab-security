package cn.cdyxtech.lab.controller;

import cn.cdyxtech.lab.feign.ResultCheckUtil;
import com.alibaba.fastjson.JSONArray;
import cn.cdyxtech.lab.feign.UserAPIFeign;
import cn.cdyxtech.lab.filter.MenuFilter;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import cn.cdyxtech.lab.util.UserClaim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
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
        Long userId = JWTThreadLocalUtil.getUserId();
        JSONObject userDetail = personAPIFeign.detail(userId);
        JSONObject user = userDetail.getJSONObject("result");
        JSONObject flock;
        ResultCheckUtil.check(userDetail);
        JSONArray flocks = user.getJSONArray("personFlocks");
        int userType = user.getIntValue("userType");
       
        if(flocks.size()==0 && userType!=1){
            data.put("noPermissions",true);
        }else{
            JSONArray menuList;
            if(flocks.size()>0){
                List<Long> groupIdList = new ArrayList<>();
                for(int j=0;j<flocks.size();j++){
                    flock = flocks.getJSONObject(j);
                    if(flock.getLong("type").equals(1L) && (
                        flock.getJSONObject("flock").getLong("controlType").equals(0L) 
                        ||
                        flock.getJSONObject("flock").getLong("controlType").equals(2L)
                    )) {
                        groupIdList.add(flock.getLong("flockId"));
                    }
                }
                menuList = menuFilter.buildMenByUserType(userType, groupIdList.stream().toArray(Long[]::new));
            }else{
                menuList = menuFilter.buildMenByUserType(userType);
            }
            data.put("menus",menuList);
        };
        
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
