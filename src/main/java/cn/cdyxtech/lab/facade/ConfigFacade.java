package cn.cdyxtech.lab.facade;



import cn.cdyxtech.lab.constain.ConfigOption;
import cn.cdyxtech.lab.vo.CheckDataVO;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public interface ConfigFacade {


    void saveCheckData(Long ecmId, String data);

    void saveConfigItem(String groupCode, ConfigOption.ConfigItem item);

    void saveConfigItem(ConfigOption.ConfigGroup configGroup, ConfigOption.ConfigItem item);

    List<ConfigOption.ConfigItem> getConfigItems(ConfigOption.ConfigGroup configGroup);

    List<ConfigOption.ConfigItem> getConfigItems(String groupCode);

    List<CheckDataVO> getCheckData(Long ecmId);

    ConfigOption.ConfigItem getConfigItem(String groupCode, String itemCode);

    JSONArray getCheckItem(String itemCode);

    void deleteConfigItem(Long id);

    void saveCheckItem(String categoryCode, String array);
}
