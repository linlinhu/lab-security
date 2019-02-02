package cn.cdyxtech.lab.facade;

public interface ECOFacade {

    Long getTopEcmId();

    Long getTopEcmId(Long ecmId);

    Long[] getSubEcmIds(boolean deep);

    Long[] getSubEcmIds(Long ecmId,boolean deep);

    Long[] getParentEcm();

    Long[] getParentEcm(Long ecmId);
}
