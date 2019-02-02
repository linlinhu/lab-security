package cn.cdyxtech.lab.vo;

import java.util.List;

public class SafetyAccidentRecordVO extends AbstractVO{

    private Long id;

    private Long accId;

    private String content;

    private Long createTime;

    private Long lastModifyTime;

    private Long userId;

    private String recordStage;

    private String userName;

    private List<AttachmentVO> files;

    public Long getAccId() {
        return accId;
    }

    public void setAccId(Long accId) {
        this.accId = accId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
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

    public String getRecordStage() {
        return recordStage;
    }

    public void setRecordStage(String recordStage) {
        this.recordStage = recordStage;
    }

    public List<AttachmentVO> getFiles() {
        return files;
    }

    public void setFiles(List<AttachmentVO> files) {
        this.files = files;
    }



    @Override
    public boolean valid() {
        return true;
    }

    public static class AttachmentVO extends AbstractVO{

        private String fileName;

        private String fileUrl;

        private String viewIcon;

        private String fileSize;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getViewIcon() {
            return viewIcon;
        }

        public void setViewIcon(String viewIcon) {
            this.viewIcon = viewIcon;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        @Override
        public boolean valid() {
            return true;
        }


    }
}
