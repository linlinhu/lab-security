package cn.cdyxtech.lab.controller.modules.securityCenter;

import cn.cdyxtech.lab.constain.ConfigOption;
import cn.cdyxtech.lab.controller.ResponseBack;
import cn.cdyxtech.lab.facade.ConfigFacade;
import cn.cdyxtech.lab.facade.ECOFacade;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import com.alibaba.fastjson.JSONArray;
import com.emin.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/security-center-initConfig")
public class InitConfigController extends BaseController {

    private static final String MODULE_PATH = "modules/security-center/initConfig";

    @Autowired
    private ConfigFacade configFacade;

    @Autowired
    private ECOFacade ecoFacade;

    @GetMapping("/index")
    public String index(Map<String,Object> data){
        data.put("configGroups",ConfigOption.ConfigGroup.values());
        return MODULE_PATH+"/index";
    }

    @GetMapping("/list")
    public String index(Map<String,Object> data,String groupCode,String groupName){
        List<ConfigOption.ConfigItem> items = configFacade.getConfigItems(ConfigOption.ConfigGroup.getByCode(groupCode));
        data.put("categories",items);
        data.put("groupCode",groupCode);
        data.put("groupName",groupName);
        return MODULE_PATH+"/category/list";
    }

    @GetMapping("/detail")
    public String detail(Map<String,Object> data,String groupCode,String itemCode,String groupName){
        data.put("gloableCheckList",configFacade.getCheckData(ecoFacade.getTopEcmId()));
        data.put("item",configFacade.getConfigItem(groupCode,itemCode));
        data.put("checkItems",configFacade.getCheckItem(itemCode).toJSONString());
        data.put("groupName",groupName);
        data.put("groupCode",groupCode);
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
        configFacade.saveCheckItem(item.getCode(),checkDataItems);
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
