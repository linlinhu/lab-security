package cn.cdyxtech.lab.vo;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

public class UserVO extends AbstractVO{

    private static final long serialVersionUID = 6749988471070950677L;

    private Long id;

    @NotBlank(message = "{user.name.blank}")
    private String name;

    @NotBlank(message = "{user.mobile.blank}")
    @Pattern(regexp = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$",message = "{user.mobile.pattern}")
    private String mobile;

    @Pattern(regexp = "^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$",message = "{user.email.pattern}")
    private String email;

    @NotBlank(message = "{user.idcard.blank}")
    @Pattern(regexp = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$",message = "${user.idcard.pattern}")
    private String idCard;

    public  JSONArray personFlocks;

    private String realName;

    private String username;

    private Integer userStatus;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.realName = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
        this.username = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
        this.name = realName;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public JSONArray getPersonFlocks() {
        return personFlocks;
    }

    public void setPersonFlocks(JSONArray personFlocks) {
        this.personFlocks = personFlocks;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    @Override
    public boolean valid() {
        return StringUtils.isNotBlank(name) && StringUtils.isNotBlank(mobile);
    }


}
