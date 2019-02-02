package cn.cdyxtech.lab.facade.impl;

import cn.cdyxtech.lab.facade.SafetyAccidentFacade;
import cn.cdyxtech.lab.feign.ResultCheckUtil;
import cn.cdyxtech.lab.feign.SecurityCheckAPI;
import cn.cdyxtech.lab.vo.AbstractVO;
import cn.cdyxtech.lab.vo.SafetyAccidentRecordVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("safetyAccidentFacade")
public class SafetyAccidentFacadeImpl implements SafetyAccidentFacade {

    @Autowired
    private SecurityCheckAPI securityCheckAPI;

    @Override
    public List<SafetyAccidentRecordVO> getSafetyAccidentRecordList(Long accId, String stage){
        JSONObject result = securityCheckAPI.accidentRecord(accId,stage);
        ResultCheckUtil.check(result);
        return AbstractVO.JSONArrayToVOList(result.getJSONArray("result"),SafetyAccidentRecordVO.class);
    }

    @Override
    public SafetyAccidentRecordVO recordDetail(Long id){
        JSONObject result = securityCheckAPI.accidentRecordDetail(id);
        ResultCheckUtil.check(result);
        return AbstractVO.JSONObjectToVO(result.getJSONObject("result"),SafetyAccidentRecordVO.class);
    }

    @Override
    public void saveSafetyAccidentRecord(SafetyAccidentRecordVO vo){
        JSONObject result= securityCheckAPI.saveAccidentRecord((JSONObject) JSON.toJSON(vo));
        ResultCheckUtil.check(result);
    }

    @Override
    public boolean isAccidentTeamMember(Long id,Long userId){
        JSONObject result = securityCheckAPI.isAccidentTeamMember(id,userId);
        ResultCheckUtil.check(result);
        return result.getBooleanValue("result");
    }

    @Override
    public void finishAccident(Long id){
        JSONObject result = securityCheckAPI.changeAccidentStatus(id,10);
        ResultCheckUtil.check(result);
    }

    @Override
    public boolean isAccidentFinished(Long id){
        JSONObject result = securityCheckAPI.getAccidentStatus(id);
        ResultCheckUtil.check(result);
        int status = result.getInteger("result");
        return status == 10;
    }
}
