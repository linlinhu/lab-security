package cn.cdyxtech.lab.facade.impl;


import cn.cdyxtech.lab.constain.ConfigOption;
import cn.cdyxtech.lab.facade.ConfigFacade;
import cn.cdyxtech.lab.facade.ECOFacade;
import cn.cdyxtech.lab.feign.CommonAPIFeign;
import cn.cdyxtech.lab.feign.ResultCheckUtil;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import cn.cdyxtech.lab.util.PinyinUtil;
import cn.cdyxtech.lab.vo.CheckDataVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.emin.base.exception.EminException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component("configFacade")
public class ConfigFacadeImpl implements ConfigFacade {

    @Autowired
    private CommonAPIFeign commonAPIFeign;

    @Autowired
    private ECOFacade ecoFacade;

    private Long createConfigGroup(ConfigOption.ConfigGroup configGroup){
        //初始化配置组
        Long ecmId = ecoFacade.getTopEcmId(JWTThreadLocalUtil.getEcmId());
        JSONObject group = new JSONObject();
        group.put("groupCode", configGroup.getCode()+"_"+ecmId);
        group.put("isTree", true);
        group.put("name", configGroup.getName());
        JSONObject createResult = commonAPIFeign.createConfig(group);
        ResultCheckUtil.check(createResult);
        return createResult.getJSONObject("result").getLong("id");
    }
    private Long createConfigGroup(String name,String groupCode){
        Long ecmId = ecoFacade.getTopEcmId(JWTThreadLocalUtil.getEcmId());
        //初始化配置组
        JSONObject group = new JSONObject();
        group.put("groupCode", groupCode+"_"+ecmId);
        group.put("isTree", true);
        group.put("name", name);
        JSONObject createResult = commonAPIFeign.createConfig(group);
        ResultCheckUtil.check(createResult);
        return createResult.getJSONObject("result").getLong("id");
    }

    @Override
    @CacheEvict(value = "checkData",key = "#ecmId")
    public void saveCheckData(Long ecmId, String data){
        try{
            JSONArray.parseArray(data);
        }catch (JSONException e){
            throw new EminException("G00");
        }
        String groupCode = ConfigOption.CHECK_DATA_GROUP;
        String itemCode = ConfigOption.DEFAULT_CHECKDATA_ITEM_CODE+"_"+ecmId;
        JSONObject result = commonAPIFeign.getConfigItemByCode(groupCode,itemCode);
        ResultCheckUtil.check(result);
        ConfigOption.ConfigItem item = new ConfigOption.ConfigItem();
        if(result.get("result")!=null) {
            item = result.getJSONObject("result").toJavaObject(ConfigOption.ConfigItem.class);
        }else{
            JSONObject getGroupResult = commonAPIFeign.getConfigGroupByCode(groupCode);
            Long groupId = getGroupResult.getJSONObject("result").getLong("id");
            item.setGroupId(groupId);
            item.setValueType("String");
            item.setName("不合格项");
            item.setCode(itemCode);
        }
        item.setValue(data);
        JSONObject createItemsResult = commonAPIFeign.createConfigItem((JSONObject) JSON.toJSON(item));
        ResultCheckUtil.check(createItemsResult);
    }
    @Override
    public void saveConfigItem(String groupCode, ConfigOption.ConfigItem item){
        Long ecmId = ecoFacade.getTopEcmId(JWTThreadLocalUtil.getEcmId());
        JSONObject result = commonAPIFeign.configIsExists(groupCode+"_"+ecmId);
        ResultCheckUtil.check(result);
        Long groupId;
        if (!result.getBooleanValue("result")) {
            groupId = createConfigGroup(groupCode,groupCode);
        }else{
            JSONObject getGroupResult = commonAPIFeign.getConfigGroupByCode(groupCode+"_"+ecmId);
            groupId = getGroupResult.getJSONObject("result").getLong("id");
        }
        if(StringUtils.isBlank(item.getCode())){
            item.setCode(PinyinUtil.getPingYin(item.getName())+"_"+RandomStringUtils.randomAlphanumeric(5));
        }

        item.setGroupId(groupId);
        item.setValueType("String");
        JSONObject createResult = commonAPIFeign.createConfigItem((JSONObject) JSON.toJSON(item));
        ResultCheckUtil.check(createResult);

    }


    @Override
    public void saveConfigItem(ConfigOption.ConfigGroup configGroup, ConfigOption.ConfigItem item){
        Long ecmId = ecoFacade.getTopEcmId(JWTThreadLocalUtil.getEcmId());
        JSONObject result = commonAPIFeign.configIsExists(configGroup.getCode()+"_"+ecmId);
        ResultCheckUtil.check(result);
        Long groupId;
        if (!result.getBooleanValue("result")) {
            groupId = createConfigGroup(configGroup);
        }else{
            JSONObject getGroupResult = commonAPIFeign.getConfigGroupByCode(configGroup.getCode()+"_"+ecmId);
            groupId = getGroupResult.getJSONObject("result").getLong("id");
        }
        if(StringUtils.isBlank(item.getCode())){
            item.setCode(PinyinUtil.getPingYin(item.getName())+"_"+RandomStringUtils.randomAlphanumeric(5));
        }

        item.setGroupId(groupId);
        item.setValueType("String");
        JSONObject createResult = commonAPIFeign.createConfigItem((JSONObject) JSON.toJSON(item));
        ResultCheckUtil.check(createResult);

    }

    @Override
    public List<ConfigOption.ConfigItem> getConfigItems(ConfigOption.ConfigGroup configGroup){
        Long ecmId = ecoFacade.getTopEcmId(JWTThreadLocalUtil.getEcmId());
        List<ConfigOption.ConfigItem> defaults = getDefaultConfigItems(configGroup.getCode(),false);
        List<ConfigOption.ConfigItem> configItems = getDefaultConfigItems(configGroup.getCode()+"_"+ecmId,true);
        for(ConfigOption.ConfigItem defaultItem : defaults){
            boolean contains = false;
            for(ConfigOption.ConfigItem item : configItems){
                if(item.getName().equals(defaultItem.getName())){
                    contains = true;
                    break;
                }
            }
            if(!contains){
                configItems.add(defaultItem);
            }
        }

        return configItems;
    }
    @Override
    public List<ConfigOption.ConfigItem> getConfigItems(String groupCode){
        Long ecmId = ecoFacade.getTopEcmId(JWTThreadLocalUtil.getEcmId());

        List<ConfigOption.ConfigItem> configItems = getDefaultConfigItems(groupCode+"_"+ecmId,true);

        return configItems;
    }

    private List<ConfigOption.ConfigItem> getDefaultConfigItems(String groupCode,boolean needId){

        JSONObject checkResult = commonAPIFeign.configIsExists(groupCode);
        ResultCheckUtil.check(checkResult);
        if(checkResult.getBooleanValue("result")){
            JSONObject result = commonAPIFeign.getDefaultConfigItems(groupCode);
            ResultCheckUtil.check(result);
            JSONArray array = result.getJSONArray("result");
            if(needId){
                return array.toJavaList(ConfigOption.ConfigItem.class);
            }else{
                SimplePropertyPreFilter simplePropertyPreFilter = new SimplePropertyPreFilter();
                simplePropertyPreFilter.getExcludes().add("id");
                return JSONArray.parseArray(JSON.toJSONString(array,simplePropertyPreFilter),ConfigOption.ConfigItem.class);
            }
        }
        return new ArrayList<>();

    }


    @Override
    @Cacheable(value = "checkData",key = "#ecmId")
    public List<CheckDataVO> getCheckData(Long ecmId){
        if(ecmId==null){
            return Collections.emptyList();
        }
        String groupCode = ConfigOption.CHECK_DATA_GROUP;
        String itemCode = ConfigOption.DEFAULT_CHECKDATA_ITEM_CODE+"_"+ecmId;
        JSONObject result = commonAPIFeign.getConfigItemByCode(groupCode,itemCode);
        ResultCheckUtil.check(result);
        if(result.get("result")!=null){
           ConfigOption.ConfigItem item = result.getJSONObject("result").toJavaObject(ConfigOption.ConfigItem.class);
           return JSONArray.parseArray(item.getValue(),CheckDataVO.class);
        }
        result = commonAPIFeign.getConfigItemByCode( ConfigOption.CHECK_DATA_GROUP, ConfigOption.DEFAULT_CHECKDATA_ITEM_CODE);
        ResultCheckUtil.check(result);
        ConfigOption.ConfigItem item = result.getJSONObject("result").toJavaObject(ConfigOption.ConfigItem.class);
        return JSONArray.parseArray(item.getValue(),CheckDataVO.class);
    }

    @Override
    public ConfigOption.ConfigItem getConfigItem(String groupCode, String itemCode){
        Long currentEcmId = JWTThreadLocalUtil.getEcmId();
        Long ecmId = currentEcmId;//此处应该根据当前ECMID获取学校的ECMID
        JSONObject r = commonAPIFeign.configIsExists(groupCode+"_"+ecmId);
        ResultCheckUtil.check(r);
        if(r.getBooleanValue("result")){
            r = commonAPIFeign.getConfigItemByCode(groupCode+"_"+ecmId,itemCode);
            ResultCheckUtil.check(r);
            if(r.get("result")!=null){
                return r.getJSONObject("result").toJavaObject(ConfigOption.ConfigItem.class);
            }

        }
        JSONObject result = commonAPIFeign.getConfigItemByCode(groupCode,itemCode);
        ResultCheckUtil.check(result);
        if(result.get("result")!=null){
            JSONObject j = result.getJSONObject("result");
            j.remove("id");
            return j.toJavaObject(ConfigOption.ConfigItem.class);
        }
        return null;
    }

    @Override
    public JSONArray getCheckItem(String itemCode){
        Long currentEcmId = JWTThreadLocalUtil.getEcmId();
        Long ecmId = ecoFacade.getTopEcmId(currentEcmId);
        JSONObject result  = commonAPIFeign.getConfigItemByCode(ConfigOption.CHECK_DATA_GROUP,"checkItem_"+itemCode+"_"+JWTThreadLocalUtil.getEcmId());
        ResultCheckUtil.check(result);
        if(result.get("result")!=null){
            ConfigOption.ConfigItem checkItem = result.getJSONObject("result").toJavaObject(ConfigOption.ConfigItem.class);
            return JSONArray.parseArray(checkItem.getValue());
        }else{
           return new JSONArray();
        }
    }

    @Override
    public void deleteConfigItem(Long id){
        JSONObject result = commonAPIFeign.deleteConfigItem(id);
        ResultCheckUtil.check(result);

    }

    @Override
    public void saveCheckItem(String categoryCode, String array){

        JSONObject checkResult = commonAPIFeign.configIsExists(ConfigOption.CHECK_DATA_GROUP);
        ResultCheckUtil.check(checkResult);
        ConfigOption.ConfigItem checkDataItem = new ConfigOption.ConfigItem();
        Long ecmId = ecoFacade.getTopEcmId(JWTThreadLocalUtil.getEcmId());
        String itemCode = "checkItem_"+categoryCode+"_"+ecmId;
        if (checkResult.getBooleanValue("result")) {
            JSONObject getGroupResult = commonAPIFeign.getConfigGroupByCode(ConfigOption.CHECK_DATA_GROUP);
            Long groupId = getGroupResult.getJSONObject("result").getLong("id");
            JSONObject result = commonAPIFeign.getConfigItemByCode(ConfigOption.CHECK_DATA_GROUP,itemCode);
            ResultCheckUtil.check(result);
            if(result.get("result")!=null){
                checkDataItem = result.getJSONObject("result").toJavaObject(ConfigOption.ConfigItem.class);
            }else{
                checkDataItem.setGroupId(groupId);
                checkDataItem.setCode(itemCode);
                checkDataItem.setName(itemCode);
                checkDataItem.setValueType("String");
            }
            checkDataItem.setValue(array);
            JSONObject createResult = commonAPIFeign.createConfigItem((JSONObject) JSON.toJSON(checkDataItem));
            ResultCheckUtil.check(createResult);
        }



    }


}
