package cn.cdyxtech.lab.facade.impl;

import cn.cdyxtech.lab.constain.ECOOption;
import cn.cdyxtech.lab.facade.ECOFacade;
import cn.cdyxtech.lab.feign.ECOAPIFeign;
import cn.cdyxtech.lab.feign.ResultCheckUtil;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ECOFacadeImpl implements ECOFacade {

    @Autowired
    private ECOAPIFeign ecoAPIFeign;



    @Override
    public Long getTopEcmId(){
        return getTopEcmId(JWTThreadLocalUtil.getEcmId());
    }
    @Cacheable(value = "topEcm",key = "#ecmId")
    @Override
    public Long getTopEcmId(Long ecmId){
        if(ecmId==null){
            ecmId = JWTThreadLocalUtil.getEcmId();
        }
        if(ecmId == null){
            return null;
        }
        ECOOption.ChainNode node = getTopNode(ecmId);
        if(node!=null){
            return node.getEcmId();
        }
        return null;
    }

    private ECOOption.ChainNode getTopEcm(){
        Long ecmId = JWTThreadLocalUtil.getEcmId();
        if(ecmId==null){
            return null;
        }
        return getTopNode(ecmId);
    }

    @Override
    public Long[] getSubEcmIds(boolean deep){
        return getSubEcmIds(null,deep);
    }

    @Override
    public Long[] getSubEcmIds(Long ecmId,boolean deep){
        if(ecmId==null){
            ecmId = JWTThreadLocalUtil.getEcmId();
        }
        if(ecmId == null){
            return null;
        }
        ECOOption.ChainNode top = getTopNode(ecmId);
        Set<Long> ids = new HashSet<>();
        ids.add(ecmId);
        JSONObject result = ecoAPIFeign.subNodes(top.getChainId(),ecmId,deep);
        ResultCheckUtil.check(result);
        recursion(ids,result.getJSONArray("result"),deep);
        return ids.toArray(new Long[ids.size()]);
    }

    private void recursion(Set<Long> ecmIds,JSONArray children,boolean deep){
        if(children==null || children.size()==0){
            return;
        }
        for(int i=0;i<children.size();i++){
            JSONObject j = children.getJSONObject(i);
            Long ecmId = j.getLong("ecmId");
            ecmIds.add(ecmId);
            if(deep){
                if(j.getBooleanValue("hasChild")){
                    recursion(ecmIds,j.getJSONArray("children"),deep);
                }
            }

        }
    }

    @Override
    public Long[] getParentEcm(){
        return getParentEcm(null);
    }

    @Override
    public Long[] getParentEcm(Long ecmId){
        if(ecmId==null){
            ecmId = JWTThreadLocalUtil.getEcmId();
        }
        JSONObject result = ecoAPIFeign.getParentEcm(ecmId);
        ResultCheckUtil.check(result);
        JSONArray nodeArray = result.getJSONArray("result");
        Long[] ids = new Long[nodeArray.size()];
        for(int i=0;i<nodeArray.size();i++){
            ids[i] = nodeArray.getJSONObject(i).getLong("ecmId");
        }
        return ids;
    }

    private ECOOption.ChainNode getTopNode(Long ecmId){
        if(ecmId==null){
            ecmId = JWTThreadLocalUtil.getEcmId();
        }
        if(ecmId==null){
            return null;
        }
        JSONObject result = ecoAPIFeign.getTopByEcm(ecmId,true);
        ResultCheckUtil.check(result);
        JSONArray array = result.getJSONArray("result");
        if(array.size()>1){
            for(int i=0;i<array.size();i++){
                JSONObject node = array.getJSONObject(i);
                Long nodeEcmId = node.getLong("ecmId");
                if(nodeEcmId.longValue()!=ecmId.longValue()){
                    return node.toJavaObject(ECOOption.ChainNode.class);
                }
            }
        }else if(array.size()==1){
            return array.getJSONObject(0).toJavaObject(ECOOption.ChainNode.class);
        }
        return null;
    }



}
