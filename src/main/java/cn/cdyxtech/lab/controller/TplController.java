package cn.cdyxtech.lab.controller;

import cn.cdyxtech.lab.constain.ConfigOption;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.facade.ConfigFacade;
import cn.cdyxtech.lab.feign.BasicInfoAPI;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;


@Controller
@RequestMapping("/tpl")
public class TplController extends HeaderCommonController {
   
    
    @Autowired
    private BasicInfoAPI basicInfoApi;

    @GetMapping("/relate-list")
    public String index(Map<String,Object> data, String dataUrl, String[] showColumns){
        if(showColumns!=null){
            data.put("showColumns",JSONArray.toJSONString(showColumns));
        }
        data.put("timestamp", System.currentTimeMillis());
        data.put("dataUrl", dataUrl);
        data.put("showColumns", showColumns);
        return "modules/tpl/relateList";
    }

    /**
     * 查看范围
     * @param data
     * @return
     */
    @GetMapping("/view-scope")
    public String viewScope(Map<String,Object> data){
        data.put("timestamp", System.currentTimeMillis());
        String allStr = "{\"name\":\"全校\",\"id\":\"\"}";
        JSONArray items = new JSONArray();
        items.add(JSONObject.parseObject(allStr));
        try {
            if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
                throw new EminException("404");
            }
            Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
            JSONObject res = basicInfoApi.sbranchPage(ecmId, 1, 100, null);
            this.dealException(res);
            JSONArray branchs = res.getJSONObject("result").getJSONArray("resultList");
            for (int i = 0; i < branchs.size(); i++) {
                items.add(branchs.getJSONObject(i));
            }
        } catch (Exception e) {
            items = new JSONArray();
        } finally {
            data.put("items", items);
        }
        
        data.put("items", items);
        return "tpl/dropdown";
    }

    @Autowired
    private ConfigFacade configFacade;
    /**
     * 危险源
     * @param data
     * @return
     */
    @GetMapping("/danger-sources")
    public String dangerSources(Map<String,Object> data){
        data.put("timestamp", System.currentTimeMillis());
        String allStr = "{\"name\":\"全部\",\"id\":\"\"}";
        JSONArray items = new JSONArray();
        items.add(JSONObject.parseObject(allStr));
        try {
            List<ConfigOption.ConfigItem> datas = configFacade.getConfigItems(ConfigOption.ConfigGroup.DANGEROUS_SOURCE_CATEGORY);
            for (int i = 0; i < datas.size(); i++) {
                items.add(datas.get(i));
            }
        } catch (Exception e) {
            
        } finally {
            data.put("items", items);
        }
        return "tpl/dropdown";
    }

    /**
     * 实验室分类
     */
    @GetMapping("/lab-types")
    public String labTypes(Map<String,Object> data){
        data.put("timestamp", System.currentTimeMillis());
        String allStr = "{\"name\":\"全部\",\"id\":\"\"}";
        JSONArray items = new JSONArray();
        items.add(JSONObject.parseObject(allStr));
        try {
            List<ConfigOption.ConfigItem> datas = configFacade.getConfigItems(ConfigOption.ConfigGroup.LAB_CATEGORY);
            for (int i = 0; i < datas.size(); i++) {
                items.add(datas.get(i));
            }
        } catch (Exception e) {
            
        } finally {
            data.put("items", items);
        }
        return "tpl/dropdown";
    }

    
    /**
     * 实验室等级
     */
    @GetMapping("/lab-grades")
    public String labGrades(Map<String,Object> data){
        data.put("timestamp", System.currentTimeMillis());
        String res = "[{\"name\":\"全部\",\"value\":\"\"},{\"name\":\"Ⅰ级\",\"value\":\"Ⅰ级\"},{\"name\":\"Ⅱ级\",\"value\":\"Ⅱ级\"},{\"name\":\"Ⅲ级\",\"value\":\"Ⅲ级\"},{\"name\":\"Ⅳ级\",\"value\":\"Ⅳ级\"}]";
        data.put("items", JSONObject.parseArray(res));
        return "tpl/dropdown";
    }

     /**
     * 实验室等级
     */
    @GetMapping("/check-grades")
    public String checkGrades(Map<String,Object> data){
        data.put("timestamp", System.currentTimeMillis());
        String res = "[{\"name\":\"全部\",\"value\":\"\"},{\"name\":\"1级\",\"value\":\"1\"},{\"name\":\"2级\",\"value\":\"2\"},{\"name\":\"3级\",\"value\":\"3\"},{\"name\":\"4级\",\"value\":\"4\"},{\"name\":\"5级\",\"value\":\"5\"},{\"name\":\"6级\",\"value\":\"6\"}]";
        data.put("items", JSONObject.parseArray(res));
        return "tpl/dropdown";
    }

    /**
     * 隐患
     */
    @GetMapping("/hidden-danger-grades")
    public String hiddenDangerGrades(Map<String,Object> data){

        data.put("timestamp", System.currentTimeMillis());
        String allStr = "{\"name\":\"全部\",\"id\":\"\"}";
        JSONArray items = new JSONArray();
        items.add(JSONObject.parseObject(allStr));

        try {
            List<ConfigOption.ConfigItem> datas = configFacade.getConfigItems(ConfigOption.POTENTIAL_HAZARD_LEVEL_GROUP);
            for (int i = 0; i < datas.size(); i++) {
                items.add(datas.get(i));
            }
        } catch (Exception e) {
            
        } finally {
            data.put("items", items);
        }
        
        return "tpl/dropdown";
    }

    /**
     * 安全检查类型
     * @param data
     * @return
     */
    @GetMapping("/check-types")
    public String checkTypes(Map<String,Object> data){
        data.put("timestamp", System.currentTimeMillis());
        String allStr = "{\"name\":\"全部\",\"id\":\"\"}";
        JSONArray items = new JSONArray();
        items.add(JSONObject.parseObject(allStr));
        try {
            List<ConfigOption.ConfigItem> datas = configFacade.getConfigItems(ConfigOption.ConfigGroup.SAFETY_INSPECTION_CATEGORY);
            for (int i = 0; i < datas.size(); i++) {
                items.add(datas.get(i));
            }
        } catch (Exception e) {
            
        } finally {
            data.put("items", items);
        }
        return "tpl/dropdown";
    }
}
