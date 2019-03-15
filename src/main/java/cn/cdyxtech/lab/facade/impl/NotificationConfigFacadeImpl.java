package cn.cdyxtech.lab.facade.impl;

import cn.cdyxtech.lab.facade.NotificationConfigFacade;
import cn.cdyxtech.lab.feign.NotificationAPIFeign;
import cn.cdyxtech.lab.feign.ResultCheckUtil;
import cn.cdyxtech.lab.vo.AbstractVO;
import cn.cdyxtech.lab.vo.NotificationConfigVO;
import cn.cdyxtech.lab.vo.SecurityTreeVO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("noticicationConfigFacade")
public class NotificationConfigFacadeImpl extends AbstractFacadeImpl<NotificationConfigVO> implements NotificationConfigFacade {

    @Autowired
    private NotificationAPIFeign notificationAPIFeign;

    @Override
    public void saveConfig(List<NotificationConfigVO> configVOList) {
        JSONObject result = notificationAPIFeign.saveNotificationConfig((JSONArray) JSONArray.toJSON(configVOList));
        ResultCheckUtil.check(result);
    }

    @Override
    public List<NotificationConfigVO> configs(){
        JSONObject result = notificationAPIFeign.findAllNotificationConfig();
        ResultCheckUtil.check(result);
        return toVOList(result.getJSONArray("result"));
    }

    @Override
    public List<SecurityTreeVO> getSecurityTree(String keyword){
        JSONObject result = notificationAPIFeign.getSecurityTree(keyword);
        ResultCheckUtil.check(result);
        return AbstractVO.JSONArrayToVOList(result.getJSONArray("result"),SecurityTreeVO.class);
    }


}
