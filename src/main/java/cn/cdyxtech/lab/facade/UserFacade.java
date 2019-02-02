package cn.cdyxtech.lab.facade;

import cn.cdyxtech.lab.vo.FlockVO;
import cn.cdyxtech.lab.vo.UserVO;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.dao.PagedResult;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserFacade extends AbstractFacade<UserVO> {
	ResponseEntity<JSONObject> login(String mobile, String password, String code, String sessionKey);

    byte[] getValidateImg(String sessionKey);

    void logout();

    PagedResult<UserVO> getPagedUser(PageRequest pageRequest, String keyword,Long ecmId,boolean ecmDeep,Long[] flockIds);

    void saveUser(UserVO vo, Long[] flockIds);

    void deleteUsers(Long[] ids);

    UserVO detail(Long id);

    void enableUsers(Long[] ids);

    void disableUsers(Long[] ids);

    List<FlockVO> getRoleList(Long ecmId);
}
