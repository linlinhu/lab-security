package cn.cdyxtech.lab.controller.modules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;
import com.emin.base.util.CookieUtil;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.AuthAPIFeign;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.feign.NotificationAPIFeign;
import cn.cdyxtech.lab.feign.UserAPIFeign;
import cn.cdyxtech.lab.util.UserClaim;

import java.util.Map;

@Controller
@RequestMapping("/my-message")
public class MyMessageController extends HeaderCommonController{
	@Autowired
    private MelAPIFeign melApiFeign;
	@Autowired
    private NotificationAPIFeign notificationAPIFeign;
    @Autowired
    private BasicInfoAPI basicInfoAPI;
    @Autowired
    private AuthAPIFeign authAPIFeign;
    @Autowired
    private UserAPIFeign userAPIFeign; 
	
    private static final String VALIDATE_TYPE_SMS = "sms";
    UserClaim userClaim = this.validateAuthorizationToken();
    
    @GetMapping("/index")
    public String index(Map<String,Object> data,Long type){
    	userClaim = this.validateAuthorizationToken();
        Long ecmId = userClaim.getHighestEcmId();
        data.put("ecmId",ecmId);
        data.put("timestamp", System.currentTimeMillis());
        data.put("type", type);
        return "modules/my-message/manage";
    }
    
    @GetMapping("/list")
    public String list(Map<String,Object> data, String keyword, Long startTime, Long endTime,Long messageType){
        Long userId = userClaim.getId();
        if (userId == null) {
            throw new EminException("404");
        }
        String sort = "readStatus,createTime";
    	String order = "asc,desc";
        Integer limit= getPageRequestData().getLimit();
        Integer page= getPageRequestData().getCurrentPage();
        JSONObject res = notificationAPIFeign.querypage(keyword, startTime, endTime, userId, messageType, sort, order, page, limit);
        this.dealException(res);
        data.put("data",res.getJSONObject("result"));
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", listTpl().getJSONArray("groups").getJSONObject(0));
        return "tpl/examination/list";
    }
    
    @GetMapping("/detail")
    public String detail(Map<String,Object> data,Long id){
    	JSONObject res = notificationAPIFeign.detail(id, this.validateAuthorizationToken().getId(),false);
    	this.dealException(res);
        data.put("id", id);
        data.put("moduleName", "我的消息");
        data.put("data", res.getJSONObject("result"));
        return "modules/my-message/detail";
    }
    
    @GetMapping("/unread-message-count")
	@ResponseBody
	public JSONObject myNotReadMessageCount() {
    	Long userId = this.validateAuthorizationToken().getId();
		JSONObject res = notificationAPIFeign.myNotReadMessageCount(userId);
		this.dealException(res);
		return res;
	}
    
    @GetMapping("/query-simple-message")
	@ResponseBody
	public JSONObject queryMySimpleMessage(Long count,Long readStatus) {
    	userClaim = this.validateAuthorizationToken();
    	Long userId = userClaim.getId();
    	if(count == null) {
    		count = 3L;
    	}
    	if (readStatus == null) {
    		readStatus = 0L;
		}
		JSONObject res = notificationAPIFeign.queryMySimpleMessage(userId, count, readStatus);
		this.dealException(res);
		res.put("count", count);
		return res;
	}
    @PostMapping("/remove")
    @ResponseBody
    public JSONObject remove(Long id){
        if (id == null) {
            throw new EminException("404");
        }
        userClaim = this.validateAuthorizationToken();
    	Long userId = userClaim.getId();
        JSONObject res = notificationAPIFeign.delMyMessage(id, userId);
        this.dealException(res);
        return res;
    }
    @GetMapping("/read")
    @ResponseBody
    public JSONObject setRead(Long id){
        if (id == null) {
            throw new EminException("404");
        }
        JSONObject res = notificationAPIFeign.setRead(id, this.validateAuthorizationToken().getId());
        this.dealException(res);
        return res;
    }
    //我的信息（用户详情）
    @GetMapping("/personal-info")
    public String personalInfo(Map<String,Object> data){
		ResponseEntity<JSONObject> result = basicInfoAPI.userLoginInfo(this.validateAuthorizationToken().getId()); //获取用户信息
        JSONObject infos = result.getBody().getJSONObject("result");
        JSONObject res = basicInfoAPI.findInfoById(infos.getLong("id"), infos.getLong("highestEcmId"), infos.getLong("highestEcmIdType"));
        
        data.put("data", res.getJSONObject("result"));
        return "modules/my-message/personal-info";
    }
    @GetMapping("/modification")
    public String modification(Map<String,Object> data,Long id){
        data.put("mobile", this.validateAuthorizationToken().getMobile());
        return "modules/my-message/modify-mobile";
    }
    /**
	 * 获取手机验证码
	 * @param validateType 验证码类别定义,sms表示短信验证码
	 * @param sequence 当前请求唯一标识符
	 * @param expire 过期时间,单位毫秒
	 * @param strategy 验证码内容生成策略,0:混合秘钥,1:纯数字,2:纯字母 ,100:计算性的验证码(默认选择)
	 * @param extendParams 扩展参数传递,json格式
	 */
	@RequestMapping("/getMobileCode")
	@ResponseBody
	public JSONObject getMobileCode(String mobile, String sequence, Long expire, Long strategy, String extendParams) {
		if(expire == null) {
			expire = 60 * 1000 * 10L;
		}
		if(strategy == null) {
			strategy = 200L;
		}
		JSONObject res = authAPIFeign.getMobileCode(VALIDATE_TYPE_SMS, sequence, expire, strategy, extendParams);
		this.dealException(res);
		return res;
    }
    /**
	 * * 修改用户名或者电话
	 */
    @PostMapping("/modify-user-info")
	@ResponseBody
	public JSONObject modifyUserInfo(String name, String mobile) {
        Long id = this.validateAuthorizationToken().getId();
 		JSONObject res = userAPIFeign.saveOrUpdateUser(name, mobile, id, mobile);
		this.dealException(res);
		return res;
    }

    /**
	 * * 验证
	 * @param validateType 验证码类别定义,目前支持:commonImage,sms
	 * @param sequence 当前请求唯一标识符(与验证码的一致)
	 * @param validateValue  匹配验证码值
	 * @param extendParams 扩展参数传递,json格式
	 */
    @RequestMapping("/check")
	@ResponseBody
	public JSONObject check(String validateType, String sequence, String validateValue, String extendParams) {
        if(validateType == null) {
            validateType = VALIDATE_TYPE_SMS;
        }
 		JSONObject res = authAPIFeign.check(validateType, sequence, validateValue, extendParams);
		this.dealException(res);
		return res;
    }
    
    @GetMapping("/modify-success")
    public String modifySuccess(Map<String,Object> data,String mobile){
        CookieUtil.delCookie(getResponse(), "Authorization");
        data.put("mobile", mobile);
        return "modules/my-message/success";
    }
    
    public JSONObject listTpl() {
        JSONObject res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "my-message", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }
    
    public JSONObject formTpl(String formCode) {
        String code = "form-" + formCode;
        JSONObject res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, code, "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }
}
