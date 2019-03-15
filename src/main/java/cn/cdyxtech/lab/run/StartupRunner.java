package cn.cdyxtech.lab.run;


import cn.cdyxtech.lab.constain.ConfigOption;
import cn.cdyxtech.lab.feign.CommonAPIFeign;
import cn.cdyxtech.lab.feign.ResultCheckUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

@Component
public class StartupRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupRunner.class);

    @Autowired
    private CommonAPIFeign commonServiceAPIFeign;

    @Value("classpath:/initDataFile/lab_initData.json")
    private Resource labInitData;

    @Value("classpath:/initDataFile/lab_checkData.json")
    private Resource labCheckData;

    @Value("classpath:/initDataFile/lab_initPH.json")
    private Resource labPHData;

    @Override
    public void run(String... args) throws IOException {


        JSONObject checkResult = commonServiceAPIFeign.configIsExists(ConfigOption.CHECK_DATA_GROUP);
        ResultCheckUtil.check(checkResult);
        if (!checkResult.getBooleanValue("result")) {
            InputStream checkFile = labCheckData.getInputStream();
            String checkJson = this.jsonRead(checkFile);
            JSONObject group = new JSONObject();
            group.put("groupCode", ConfigOption.CHECK_DATA_GROUP);
            group.put("isTree", true);
            group.put("name",  ConfigOption.CHECK_DATA_GROUP);
            JSONObject createResult = commonServiceAPIFeign.createConfig(group);
            ResultCheckUtil.check(createResult);
            Long groupId = createResult.getJSONObject("result").getLong("id");
            ConfigOption.ConfigItem item = new ConfigOption.ConfigItem();
            item.setCode(ConfigOption.DEFAULT_CHECKDATA_ITEM_CODE);
            item.setValue(checkJson);
            item.setGroupId(groupId);
            item.setName("不合格项");
            item.setValueType("String");
            JSONObject createItemsResult = commonServiceAPIFeign.createConfigItem((JSONObject) JSON.toJSON(item));
            ResultCheckUtil.check(createItemsResult);
        }
        /*JSONObject phResult = commonServiceAPIFeign.configIsExists(ConfigOption.POTENTIAL_HAZARD_LEVEL_GROUP);
        ResultCheckUtil.check(phResult);
        if (!phResult.getBooleanValue("result")) {
            InputStream phData = labPHData.getInputStream();
            String phJSON = this.jsonRead(phData);
            JSONObject group = new JSONObject();
            group.put("groupCode", ConfigOption.POTENTIAL_HAZARD_LEVEL_GROUP);
            group.put("isTree", false);
            group.put("name",  ConfigOption.POTENTIAL_HAZARD_LEVEL_GROUP);
            JSONObject createResult = commonServiceAPIFeign.createConfig(group);
            ResultCheckUtil.check(createResult);
            Long groupId = createResult.getJSONObject("result").getLong("id");
            JSONArray items = JSONArray.parseArray(phJSON);
            for (int i=0;i<items.size();i++){
                items.getJSONObject(i).put("groupId", groupId);
            }
            JSONObject createItemsResult = commonServiceAPIFeign.createConfigItems(items);
            ResultCheckUtil.check(createItemsResult);
        }*/


        InputStream file = labInitData.getInputStream();
        String jsonData = this.jsonRead(file);
        JSONObject initData = JSONObject.parseObject(jsonData);
        for (ConfigOption.ConfigGroup configGroup : ConfigOption.ConfigGroup.values()) {
            try {
                JSONObject result = commonServiceAPIFeign.configIsExists(configGroup.getCode());
                ResultCheckUtil.check(result);
                if (!result.getBooleanValue("result")) {
                    //初始化配置组
                    JSONObject group = new JSONObject();
                    group.put("groupCode", configGroup.getCode());
                    group.put("isTree", true);
                    group.put("name", configGroup.getName());
                    JSONObject createResult = commonServiceAPIFeign.createConfig(group);

                    ResultCheckUtil.check(createResult);
                    Long groupId = createResult.getJSONObject("result").getLong("id");
                    //加载默认配置数据
                    JSONArray items = initData.getJSONArray(configGroup.getCode());
                    for (int i = 0; i < items.size(); i++) {
                        items.getJSONObject(i).put("groupId", groupId);
                    }

                    JSONObject createItemsResult = commonServiceAPIFeign.createConfigItems(items);

                    ResultCheckUtil.check(createItemsResult);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }


    }

    private String jsonRead(InputStream file) {
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder();
        try {
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
        } catch (Exception e) {

        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return buffer.toString();
    }
}
