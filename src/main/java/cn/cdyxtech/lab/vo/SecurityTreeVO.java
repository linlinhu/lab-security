package cn.cdyxtech.lab.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SecurityTreeVO extends AbstractVO{

    private String name;

    private String type = "d";

    private List<SecurityTreeUserVO> securityUserList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("text")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("nodes")
    public List<SecurityTreeUserVO> getSecurityUserList() {
        return securityUserList;
    }

    public void setSecurityUserList(List<SecurityTreeUserVO> securityUserList) {
        this.securityUserList = securityUserList;
    }

    @Override
    public boolean valid() {
        return true;
    }

    public static class SecurityTreeUserVO extends AbstractVO{

        private String realName;

        private String mobile;

        private String text;

        private String roleName;

        private String[] tags;

        private String type = "p";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @JsonProperty("name")
        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getText() {
            return realName;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String[] getTags() {
            return new String[]{roleName};
        }

        public void setTags(String[] tags) {
            this.tags = tags;
        }

        @Override
        public boolean valid() {
            return true;
        }
    }
}
