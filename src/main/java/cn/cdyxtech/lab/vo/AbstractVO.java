package cn.cdyxtech.lab.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractVO implements Serializable {

    public static <VO extends AbstractVO> VO JSONObjectToVO(JSONObject jsonObject, Class<VO> voClass){
        return jsonObject.toJavaObject(voClass);
    }

    public static <VO extends AbstractVO> List<VO> JSONArrayToVOList(JSONArray jsonArray, Class<VO> voClass){
        return jsonArray.toJavaList(voClass);
    }

    public abstract boolean valid();
}
