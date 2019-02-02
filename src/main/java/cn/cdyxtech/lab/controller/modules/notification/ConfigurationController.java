package cn.cdyxtech.lab.controller.modules.notification;

import cn.cdyxtech.lab.constain.ConfigOption;
import cn.cdyxtech.lab.controller.ResponseBack;
import cn.cdyxtech.lab.facade.ConfigFacade;
import cn.cdyxtech.lab.facade.NotificationConfigFacade;
import cn.cdyxtech.lab.util.TestRandomUtil;
import cn.cdyxtech.lab.vo.NotificationConfigVO;
import cn.cdyxtech.lab.vo.SecurityTreeVO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.controller.BaseController;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/notification-config")
public class ConfigurationController extends BaseController {

    @Autowired
    private ConfigFacade configFacade;

    @Autowired
    private NotificationConfigFacade notificationConfigFacade;

    @GetMapping("/index")
    public String index(Map<String,Object> data){
        List<ConfigOption.ConfigItem> phLevels = configFacade.getConfigItems(ConfigOption.POTENTIAL_HAZARD_LEVEL_GROUP);
        List<NotificationConfigVO> configList = notificationConfigFacade.configs();
        data.put("configs",configList);
        data.put("phLevels",phLevels);

        return "modules/notification/config/index";
    }

    @PostMapping("/saveConfig")
    @ResponseBody
    public ResponseBack<Boolean> saveConfig(@RequestBody List<NotificationConfigVO> vos){
        notificationConfigFacade.saveConfig(vos);
        return ResponseBack.success(true);
    }

    @GetMapping("/getPersonTree")
    @ResponseBody
    public ResponseBack<List<SecurityTreeVO>> getPersonTree(){
        return ResponseBack.success(notificationConfigFacade.getSecurityTree());
    }

    @GetMapping("/testPersonTreeData")
    @ResponseBody
    public ResponseBack<JSONArray> testPersonTree(){
        JSONArray data = new JSONArray();
        //Scholl
        JSONObject scholl = new JSONObject();
        scholl.put("text","西南科技大学");
        scholl.put("nodes",generatePersons());
        scholl.put("type","d");
        scholl.put("selectable",false);
        data.add(scholl);
        JSONObject sfd = new JSONObject();
        sfd.put("text","安全中心");
        sfd.put("selectable",false);
        sfd.put("type","d");
        sfd.put("nodes",generatePersons());
        data.add(sfd);

        JSONObject college1  = new JSONObject();
        college1.put("text","计算机学院");
        college1.put("nodes",generatePersons());
        college1.put("selectable",false);
        college1.put("type","d");
        data.add(college1);

        JSONObject college2  = new JSONObject();
        college2.put("text","土木工程学院");
        college2.put("nodes",generatePersons());
        college2.put("selectable",false);
        college2.put("type","d");
        data.add(college2);


        return ResponseBack.success(data);
    }

    private JSONArray generatePersons(){
        JSONArray persons = new JSONArray();
        int size = RandomUtils.nextInt(1,5);
        for(int i=0;i<size;i++){
            JSONObject person = new JSONObject();
            String name =TestRandomUtil.getChineseName();
            person.put("id",UUID.randomUUID().toString());
            person.put("text",name);
            person.put("name",name);
            person.put("selectable",false);
            person.put("mobile",TestRandomUtil.getTel());
            person.put("type","p");
            person.put("tags",new String[]{"人员职位"});
            persons.add(person);
        }
        return persons;
    }
}
