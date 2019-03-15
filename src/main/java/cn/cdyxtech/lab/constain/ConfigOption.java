package cn.cdyxtech.lab.constain;

import com.alibaba.fastjson.annotation.JSONField;

public interface ConfigOption {

    String CHECK_DATA_GROUP = "checkData";

    String DEFAULT_CHECKDATA_ITEM_CODE = "defaultCheckData";

    String GRADE_CONFIG_GROUP_CODE = "gradeConfig";

    String GRADE_CONFIG_ITEM_CODE = "gradeColor";

    // 隐患等级
    String POTENTIAL_HAZARD_LEVEL_GROUP = "potentialHazardLevel";


    enum ConfigGroup{

        LAB_CATEGORY("labCategory","实验室分类"),
        DANGEROUS_SOURCE_CATEGORY("dangerousSourceCategory","危险源分类"),
        SAFETY_INSPECTION_CATEGORY("safetyInspectionCategory","安全检查分类");
        @JSONField
        private String code;
        @JSONField
        private String name;
        ConfigGroup(String code,String name){
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static ConfigGroup getByCode(String code){
            for(ConfigGroup group:values()){
                if(group.getCode().equals(code)){
                    return group;
                }
            }
            return null;
        }
    }


    class ConfigItem{
        private Long id;
        private Long groupId;
        private String groupCode;
        private String code;
        private String name;
        private String value;
        private String valueType;
        private Long pid;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getGroupId() {
            return groupId;
        }

        public void setGroupId(Long groupId) {
            this.groupId = groupId;
        }

        public String getGroupCode() {
            return groupCode;
        }

        public void setGroupCode(String groupCode) {
            this.groupCode = groupCode;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValueType() {
            return valueType;
        }

        public void setValueType(String valueType) {
            this.valueType = valueType;
        }

        public Long getPid() {
            return pid;
        }

        public void setPid(Long pid) {
            this.pid = pid;
        }
    }

}
