package cn.cdyxtech.lab.facade;

import cn.cdyxtech.lab.vo.AbstractVO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PagedResult;

import java.util.List;

public interface AbstractFacade<VO extends AbstractVO> {
    void setClazz(Class<VO> clazz);

    List<VO> toVOList(JSONArray array);

    VO toVO(JSONObject json);

    PagedResult<VO> toPagedVO(JSONObject json);
}
