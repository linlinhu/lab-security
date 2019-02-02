package cn.cdyxtech.lab.vo;

import java.util.List;

public class NotificationConfigVO extends AbstractVO {

    private Long id;

    private String unqualifiedProGrade;

    private Integer orderNumber;

    private List<NotificationConfigDetailVO> detail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnqualifiedProGrade() {
        return unqualifiedProGrade;
    }

    public void setUnqualifiedProGrade(String unqualifiedProGrade) {
        this.unqualifiedProGrade = unqualifiedProGrade;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<NotificationConfigDetailVO> getDetail() {
        return detail;
    }

    public void setDetail(List<NotificationConfigDetailVO> detail) {
        this.detail = detail;
    }

    public static class NotificationConfigDetailVO extends AbstractVO{

        private Integer noticeType; // 类型 1：按部门 2：按人员
        private Integer deptType; //部门类型 3学院 2安全中心 1学校
        private Integer orderNumber;
        private Long  userId;
        private String userName;
        private String userPhone;


        public Integer getNoticeType() {
            return noticeType;
        }

        public void setNoticeType(Integer noticeType) {
            this.noticeType = noticeType;
        }

        public Integer getDeptType() {
            return deptType;
        }

        public void setDeptType(Integer deptType) {
            this.deptType = deptType;
        }

        public Integer getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(Integer orderNumber) {
            this.orderNumber = orderNumber;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserPhone() {
            return userPhone;
        }

        public void setUserPhone(String userPhone) {
            this.userPhone = userPhone;
        }

        @Override
        public boolean valid() {
            return true;
        }
    }


    @Override
    public boolean valid() {
        return true;
    }


}
