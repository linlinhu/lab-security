package cn.cdyxtech.lab.facade.impl;

import cn.cdyxtech.lab.facade.AbstractFacade;
import cn.cdyxtech.lab.vo.AbstractVO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PagedResult;
import com.emin.base.util.ReflectionUtils;

import java.util.List;

public class AbstractFacadeImpl<VO extends AbstractVO> implements AbstractFacade<VO> {


    protected Class<VO> clazz;

    @Override
    public void setClazz(Class<VO> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    public AbstractFacadeImpl() {
        this.clazz = (Class<VO>) ReflectionUtils.getSuperClassGenricType(this.getClass(), 0);
    }

    public AbstractFacadeImpl(Class<VO> clazz) {
        this.clazz = clazz;
    }

    @Override
    public List<VO> toVOList(JSONArray array){
        return array.toJavaList(this.clazz);
    }

    @Override
    public VO toVO(JSONObject json){
        return json.toJavaObject(this.clazz);
    }

    @Override
    public PagedResult<VO> toPagedVO(JSONObject json){
        PagedResult<VO> pagedResult = new PagedResult<>(null,json.getInteger("nextOffset"),json.getInteger("totalCount"),json.getInteger("currentPage"));
        JSONArray array = json.getJSONArray("resultList");
        pagedResult.setResultList(this.toVOList(array));
        pagedResult.setPageSize(json.getInteger("pageSize"));
        pagedResult.setTotalPageNum(json.getInteger("totalPageNum"));
        return pagedResult;
    }

}
