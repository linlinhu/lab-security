package cn.cdyxtech.lab.facade.impl;

import cn.cdyxtech.lab.facade.ECOFacade;
import cn.cdyxtech.lab.facade.UserFacade;
import cn.cdyxtech.lab.feign.ResultCheckUtil;
import cn.cdyxtech.lab.feign.UserAPIFeign;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import cn.cdyxtech.lab.util.JWTUtil;
import cn.cdyxtech.lab.util.TestRandomUtil;
import cn.cdyxtech.lab.vo.AbstractVO;
import cn.cdyxtech.lab.vo.FlockVO;
import cn.cdyxtech.lab.vo.UserVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.dao.PagedResult;
import com.emin.base.exception.EminException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("userFacade")
public class UserFacadeImpl extends AbstractFacadeImpl<UserVO> implements UserFacade{


    @Autowired
    private UserAPIFeign userAPIFeign;

    @Autowired
    private ECOFacade ecoFacade;


    @Override
    public ResponseEntity<JSONObject> login(String mobile, String password, String code,String sessionKey){
    	JSONObject res = userAPIFeign.checkImgCode(sessionKey, code);
    	ResultCheckUtil.check(res);
        ResponseEntity<JSONObject> result =  userAPIFeign.login(mobile,password);
        JSONObject body = result.getBody();
        ResultCheckUtil.check(body);
        return result;
    }



    @Override
    public byte[] getValidateImg(String sessionKey){
        return userAPIFeign.getImg(sessionKey);
    }

    @Override
    public void logout(){
        JSONObject result = userAPIFeign.outLogin();
        ResultCheckUtil.check(result);
    }

    @Override
    public PagedResult<UserVO> getPagedUser(PageRequest pageRequest, String keyword,Long ecmId,boolean ecmDeep,Long[] flockIds){
        Long[] ecmIds =  ecoFacade.getSubEcmIds(ecmId,ecmDeep);
        JSONObject json = userAPIFeign.userList(ecmIds,flockIds,pageRequest.getCurrentPage(),pageRequest.getLimit(),keyword,"2");
        ResultCheckUtil.check(json);
        JSONObject result = json.getJSONObject("result");
        return toPagedVO(result);
    }

    @Override
    public void saveUser(UserVO vo,Long[] flockIds){
        if(vo!=null && vo.valid()){
            JSONObject json = (JSONObject) JSON.toJSON(vo);
            json.put("flockIds",flockIds);
            json.put("type",2);
            JSONObject result = userAPIFeign.saveOrUpdateUser(json);
            ResultCheckUtil.check(result);
            return;
        }
        throw new EminException("G00");

    }

    @Override
    //@CacheEvict(value = "userDetail",allEntries = true)
    public void deleteUsers(Long[] ids){
        JSONObject result = userAPIFeign.deleteUser(ids);
        ResultCheckUtil.check(result);
    }

    @Override
    //@Cacheable(value = "userDetail",key = "userDetail + #p0")
    public UserVO detail(Long id){
        if(id==null){
            return null;
        }
        JSONObject result = userAPIFeign.detail(id);
        ResultCheckUtil.check(result);
        return AbstractVO.JSONObjectToVO(result.getJSONObject("result"),this.clazz);
    }

    @Override
    public void enableUsers(Long[] ids){
        JSONObject result = userAPIFeign.statusUpdate(ids,true);
        ResultCheckUtil.check(result);
    }

    @Override
    public void disableUsers(Long[] ids){
        JSONObject result = userAPIFeign.statusUpdate(ids,false);
        ResultCheckUtil.check(result);
    }


    @Override
    public List<FlockVO> getRoleList(Long ecmId){
        if(ecmId==null){
            ecmId = JWTThreadLocalUtil.getEcmId();
        }
        if(ecmId==null){
            return new ArrayList<>();
        }
        JSONObject result = userAPIFeign.getFlocks(ecmId,1);
        ResultCheckUtil.check(result);
        List<FlockVO> list = AbstractVO.JSONArrayToVOList(result.getJSONArray("result"),FlockVO.class);
        return list;
    }
}
