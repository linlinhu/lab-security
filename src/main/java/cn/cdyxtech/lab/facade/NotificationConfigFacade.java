package cn.cdyxtech.lab.facade;

import cn.cdyxtech.lab.vo.NotificationConfigVO;
import cn.cdyxtech.lab.vo.SecurityTreeVO;

import java.util.List;

public interface NotificationConfigFacade extends AbstractFacade<NotificationConfigVO> {
    void saveConfig(List<NotificationConfigVO> configVOList);

    List<NotificationConfigVO> configs();

    List<SecurityTreeVO> getSecurityTree();
}
