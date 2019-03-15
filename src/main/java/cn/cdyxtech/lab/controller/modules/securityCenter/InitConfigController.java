package cn.cdyxtech.lab.controller.modules.securityCenter;

import cn.cdyxtech.lab.constain.ConfigOption;
import cn.cdyxtech.lab.controller.ResponseBack;
import cn.cdyxtech.lab.facade.ConfigFacade;
import cn.cdyxtech.lab.facade.ECOFacade;
import cn.cdyxtech.lab.filter.MenuOperationFilter;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import cn.cdyxtech.lab.vo.CheckDataVO;
import cn.cdyxtech.lab.vo.GradeConfigVO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/security-center-initConfig")
public class InitConfigController extends BaseController {

    private static final String MODULE_PATH = "modules/security-center/initConfig";

    @Autowired
    private ConfigFacade configFacade;
    @Autowired
    private MenuOperationFilter menuOperationFilter;

    @Autowired
    private ECOFacade ecoFacade;

    @GetMapping("/index")
    public String index(Map<String,Object> data){
        String operationCodes = menuOperationFilter.menuOperations("security-center-initConfig");
        System.out.println(operationCodes);
        data.put("configGroups",ConfigOption.ConfigGroup.values());
        data.put("operationCodes",operationCodes);
        return MODULE_PATH+"/index";
    }

    @GetMapping("/list")
    public String index(Map<String,Object> data,String groupCode,String groupName,String operationCodes){
        List<ConfigOption.ConfigItem> items = configFacade.getConfigItems(ConfigOption.ConfigGroup.getByCode(groupCode));
        data.put("categories",items);
        data.put("groupCode",groupCode);
        data.put("groupName",groupName);
        data.put("operationCodes",operationCodes);
        return MODULE_PATH+"/category/list";
    }

    @GetMapping("/detail")
    public String detail(Map<String,Object> data,String groupCode,String itemCode,String groupName,String operationCodes){
        ConfigOption.ConfigItem item = configFacade.getConfigItem(groupCode,itemCode);
        item.setGroupCode(groupCode);
        List<CheckDataVO> voList = configFacade.getCheckData(ecoFacade.getTopEcmId());
        Collections.sort(voList);
        ConfigOption.ConfigItem gradeConfig = configFacade.getConfigItem(ConfigOption.GRADE_CONFIG_GROUP_CODE,ConfigOption.GRADE_CONFIG_ITEM_CODE);
        if(gradeConfig!=null){
            GradeConfigVO vo = JSONObject.parseObject(gradeConfig.getValue(),GradeConfigVO.class);
            Map<String, String> colorMap = vo.getGradeColors().stream().collect(Collectors.toMap(GradeConfigVO.GradeColor::getGradeStr, GradeConfigVO.GradeColor::getColor));
            data.put("colorMap",JSONObject.toJSONString(colorMap));
        }
        data.put("gloableCheckList",voList);
        data.put("item",item);
        data.put("checkItems",configFacade.getCheckItem(item).toJSONString());
        data.put("groupName",groupName);
        data.put("groupCode",groupCode);
        data.put("operationCodes",operationCodes);
        return MODULE_PATH+"/category/detail";
    }

    @PostMapping("/addCategory")
    @ResponseBody
    public ResponseBack<Boolean> addCategory(String groupCode,String name){
        ConfigOption.ConfigItem item = new ConfigOption.ConfigItem();
        item.setName(name);
        item.setValue(name);
        configFacade.saveConfigItem(ConfigOption.ConfigGroup.getByCode(groupCode),item);
        return ResponseBack.success(true);
    }

    @PostMapping("/deleteCategory")
    @ResponseBody
    public ResponseBack<Boolean> deleteCategory(Long id){
        configFacade.deleteConfigItem(id);
        return ResponseBack.success(true);
    }

    @PostMapping("/saveConfig")
    @ResponseBody
    public ResponseBack<Boolean> saveConfig(@ModelAttribute ConfigOption.ConfigItem item,String groupCode,String checkDataItems){
        configFacade.saveConfigItem(ConfigOption.ConfigGroup.getByCode(groupCode),item);
        configFacade.saveCheckItem(item,checkDataItems);
        return ResponseBack.success(true);
    }

    @GetMapping("/labCategoryList")
    @ResponseBody
    public ResponseBack<List<ConfigOption.ConfigItem>> getLabCategory(){
        return ResponseBack.success(configFacade.getConfigItems(ConfigOption.ConfigGroup.LAB_CATEGORY));
    }

    @GetMapping("/dangerousCategoryList")
    @ResponseBody
    public ResponseBack<List<ConfigOption.ConfigItem>> getDangerousCategory(){
        return ResponseBack.success(configFacade.getConfigItems(ConfigOption.ConfigGroup.DANGEROUS_SOURCE_CATEGORY));
    }

    @GetMapping("/potentialHazardLevelList")
    @ResponseBody
    public ResponseBack<List<ConfigOption.ConfigItem>> getPotentialHazardLevelList(){
        List<ConfigOption.ConfigItem> items = configFacade.getConfigItems(ConfigOption.POTENTIAL_HAZARD_LEVEL_GROUP);
        return ResponseBack.success(items);
    }
}
