package cn.cdyxtech.lab.filter;

import cn.cdyxtech.lab.feign.ECMAPIFeign;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultMallFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMallFilter.class);

    @Autowired
    ECMAPIFeign mallAPIFeign;

    public Long getDefaultMallId(){
        try {
            Long ecmId = JWTThreadLocalUtil.getEcmId();
            if(ecmId==null || ecmId.longValue()==0l){
                JSONObject mallResult = mallAPIFeign.pagedMall(1,1,null);
                JSONArray mallList = mallResult.getJSONObject("result").getJSONArray("resultList");
                if(mallList.size()>0){
                    return  mallList.getJSONObject(0).getLong("id");
                }
            }else{
                return ecmId;
            }

        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
        return null;
    }


}
