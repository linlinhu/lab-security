package cn.cdyxtech.lab.facade;

import cn.cdyxtech.lab.vo.SafetyAccidentRecordVO;

import java.util.List;

public interface SafetyAccidentFacade {
    List<SafetyAccidentRecordVO> getSafetyAccidentRecordList(Long accId, String stage);

    SafetyAccidentRecordVO recordDetail(Long id);

    void saveSafetyAccidentRecord(SafetyAccidentRecordVO vo);

    boolean isAccidentTeamMember(Long id, Long userId);

    void finishAccident(Long id);

    boolean isAccidentFinished(Long id);
}
