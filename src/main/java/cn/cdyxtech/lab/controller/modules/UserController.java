package cn.cdyxtech.lab.controller.modules;

import cn.cdyxtech.lab.controller.ResponseBack;
import cn.cdyxtech.lab.facade.ECOFacade;
import cn.cdyxtech.lab.facade.UserFacade;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.ResultCheckUtil;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import cn.cdyxtech.lab.vo.FlockVO;
import cn.cdyxtech.lab.vo.UserVO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.controller.BaseController;
import com.emin.base.dao.PagedResult;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 用户管理Controller
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserFacade userFacade;
    @Autowired
    private BasicInfoAPI basicInfoApi;
    @Autowired
    private ECOFacade ecoFacade;


    @GetMapping("/index")
    public String index(Map<String,Object> data,
                        Long ecmId,
                        Long[] queryFlockIds,
                        Long additionFlockId,
                        Boolean ecmDeep,
                        String[] showColumns,
                        String[] showOperations,
                        String operationCodes,
                        String controlTypes){

        if(ArrayUtils.isNotEmpty(showColumns)){
            data.put("showColumns",JSONArray.toJSONString(showColumns));
        }
        if(ArrayUtils.isNotEmpty(showOperations)){
            data.put("showOperations",showOperations);
        }
        if(ArrayUtils.isNotEmpty(queryFlockIds)){
            data.put("queryFlockIds",JSONArray.toJSONString(queryFlockIds));
        }
        if(operationCodes != null){
            data.put("operationCodes", operationCodes);
        }
        if(controlTypes != null){
            data.put("controlTypes", controlTypes);
        }
        data.put("additionFlockId",additionFlockId);
        data.put("ecmId",ecmId==null?JWTThreadLocalUtil.getEcmId():ecmId);
        data.put("ecmDeep",ecmDeep==null?false:ecmDeep);

        return "modules/user/index";
    }

    @GetMapping("/form")
    public String form(Map<String,Object> data,Long id){
        if(id!=null){
            UserVO vo = userFacade.detail(id);
            data.put("user",vo);
        }
        List<FlockVO> roleList = userFacade.getRoleList(null);
        data.put("roleList",roleList);
        return "modules/user/form";
    }

    @GetMapping("/list")
    public String list(Map<String,Object> data, String keyword,String[] showColumns, Long ecmId, Long[] flockIds, Boolean ecmDeep,Long[] controlTypes){
        if(controlTypes == null || controlTypes.length==0) {
            controlTypes = new Long[]{1L};
        }
        PagedResult<UserVO> pagedResult = userFacade.getPagedUser(getPageRequestData(),keyword,ecmId,ecmDeep,flockIds,controlTypes);
        if(showColumns!=null && showColumns.length>0){
            data.put("showColumns",showColumns);
        }
        if(flockIds==null || flockIds.length==0){
            Long topEcmId = ecoFacade.getTopEcmId(ecmId);
            JSONObject res = basicInfoApi.scInfo(topEcmId);
            ResultCheckUtil.check(res);
            data.put("flockId",res.getJSONObject("result").getLong("flockId"));

        }
        data.put("pagedResult",pagedResult);
        data.put("userId",JWTThreadLocalUtil.getUserId());
        return "modules/user/list";
    }

    @PostMapping("/disable")
    @ResponseBody
    public ResponseBack<Boolean> disableUser(Long[] ids){
        userFacade.disableUsers(ids);
        return ResponseBack.success(true);
    }

    @PostMapping("/enable")
    @ResponseBody
    public ResponseBack<Boolean> enableUser(Long[] ids){
        userFacade.enableUsers(ids);
        return ResponseBack.success(true);
    }
    @PostMapping("/delete")
    @ResponseBody
    public ResponseBack<Boolean> deleteUser(Long[] ids){
        userFacade.deleteUsers(ids);
        return ResponseBack.success(true);
    }


    @PostMapping("/save")
    @ResponseBody
    public ResponseBack<UserVO> saveUser(@Valid @ModelAttribute UserVO vo,Long ecmId,Long[] flockIds, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ResponseBack.error(bindingResult.getFieldError().getDefaultMessage());
        }
        userFacade.saveUser(vo,flockIds);
        return ResponseBack.success(vo);
    }

}
