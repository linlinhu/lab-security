/**
 * 
 */
package cn.cdyxtech.lab.controller;

import cn.cdyxtech.lab.facade.ECOFacade;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import cn.cdyxtech.lab.util.JWTUtil;
import cn.cdyxtech.lab.util.JsonObjectHelper;
import cn.cdyxtech.lab.feign.AuthAPIFeign;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.ResultCheckUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.emin.base.util.CookieUtil;
import cn.cdyxtech.lab.annotation.IgnoreIterceptor;
import cn.cdyxtech.lab.constain.ApplicationConstain;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author jim.lee
 *
 */
@Controller
public class LoginController extends HeaderCommonController {

	@Autowired
	private BasicInfoAPI basicInfoAPI;
	@Autowired
	private ECOFacade ecoFacade;
	@Autowired
	private AuthAPIFeign authAPIFeign;

	@Value("${cookieDomain}")
	private String cookieDomain;

	String COMMON_IMG = "commonImage";
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@IgnoreIterceptor
	public String loginPage() {
		return "login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	@IgnoreIterceptor
	public JSONObject login(String username, String password, String code, String sessionKey,Long userId) {

		JSONObject res = new JSONObject();
		if(userId == null) {
			JSONObject params = new JSONObject();
			JSONObject avlidate = authAPIFeign.check(COMMON_IMG, sessionKey, code, null); //验证
			this.dealException(avlidate);
			params.put("username", username);
			params.put("password", password);
			res = authAPIFeign.login(params.toJSONString(), sessionKey, "userPassword"); //登录
			this.dealException(res);
			userId = res.getLong("result");
		}
		res = this.setUserInfo(userId);
		return res;
	}
	/**
	 * 根据ecmid查询相关的主体信息
	 * @param ecmId
	 * @return
	 */
	private JSONObject packageEmcIdInfo(Long[] ecmIds,Long userId) {
		JSONObject back = new JSONObject();
		if (ecmIds != null) {
			/*建立关系*/
			Map<String, String> typeProperties = new HashMap<>();
			typeProperties.put("1", "schoolEcmId");
			typeProperties.put("2", "branchEcmId");
			typeProperties.put("3", "labEcmId");
			/*查询相关ecmid的类型*/
			JSONObject res = basicInfoAPI.findEcmWithTypeByEcmIds(ecmIds);
			ResultCheckUtil.check(res);
			JSONArray result = res.getJSONArray("result");
			if (!result.isEmpty()) {
				result.stream().filter(Objects::nonNull).map(e->(JSONObject) e).forEach(item->{
					Long ecmId = item.getLong("ecmId");
					String type = item.getString("type");
					String property = typeProperties.get(type);
					if(StringUtils.isNoneBlank(property)){
						back.put(property, ecmId);
					}
				});
			}
			Long highestEcmId = ecoFacade.getTopEcmId(ecmIds[0]);
			res = basicInfoAPI.getHighestType(userId, highestEcmId);
			ResultCheckUtil.check(res);
			Integer highestEcmIdType = res.getInteger("result");
			if(!highestEcmIdType.equals(-1)) {
				highestEcmIdType = 4;
			} 
			JWTThreadLocalUtil.setRootEcmId(highestEcmId);
			back.put("highestEcmId", highestEcmId);
			back.put("highestEcmIdType", highestEcmIdType);
		}
		return back;
	}

	@RequestMapping(value = "/getValidImg", method = RequestMethod.GET)
	@ResponseBody
	@IgnoreIterceptor
	public byte[] getImg(String sequence, Long expire, Long height, Long width,String fontSize,String validateType) {
		if(expire == null) {
			expire = 1000 * 60 * 10L;
		}
		JSONObject extendParams = new JSONObject();
		if(width == null){
			width = 80L;
		}
		if(height == null){
			height = 40L;
		}
		if(fontSize == null){
			fontSize = "36";
		}
		if(validateType == null) {
			validateType = COMMON_IMG;
		}
		if(validateType.equals(COMMON_IMG)) {
			extendParams.put("fontSize", fontSize);
			extendParams.put("height", height);
			extendParams.put("width", width);
		} else {
			extendParams.put("isCreateImg", true);
		}
		
		return authAPIFeign.getValidateCode(validateType, sequence, expire, 100, extendParams.toJSONString());
	}

	@RequestMapping("/logout")
	@ResponseBody
	public ResponseBack<Boolean> logout() {
		CookieUtil.delCookie(getResponse(), "Authorization");
		return ResponseBack.success(true);
	}

	@RequestMapping(value="/qrcode-valid",method=RequestMethod.GET)
	@ResponseBody
	@IgnoreIterceptor
	public void valid(String sequence,HttpServletResponse response) throws IOException {
	
		//指定返回的内容格式 必须是 text/event-stream 否则客户端接收不到
		response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		//HTML5 EventSource接受消息的固定前缀 data:
		StringBuffer sb = new StringBuffer("data:");
		JSONObject res = authAPIFeign.requestResult(sequence);
		sb.append(res);
		//HTML5 EventSource接受消息的固定后缀 nn
		sb.append("\n\n");
		pw.write(sb.toString());
		pw.flush();
	}

	/**
	 * to Long[]
	 * 
	 * @param list
	 * @return
	 */
	private static Long[] parseJsonArray2Longs(final List<Object> list) {
		Long[] longs = null;
		if (list != null && !list.isEmpty()) {
			longs = list.stream().filter(Objects::nonNull).filter(e -> NumberUtils.isNumber(e.toString()))
					.map(e -> Long.parseLong(e.toString())).toArray(Long[]::new);
		}
		return longs;
	}
	public JSONObject setUserInfo(Long userId) {//用户信息缓存
		JSONObject res = new JSONObject();
		ResponseEntity<JSONObject> result = basicInfoAPI.userLoginInfo(userId); //获取用户信息
		String jwtToken = result.getHeaders().getFirst(JWTUtil.HEADER_STRING);
		CookieUtil.addCookie(getResponse(), ApplicationConstain.AUTHORIZATION_KEY, jwtToken, 60 * 60 * 24 * 7);
		JSONObject infos = result.getBody().getJSONObject("result");
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(infos);
		JsonObjectHelper helper = new JsonObjectHelper(jsonObject);
		JSONArray ecmIdArr = helper.getValue("ecmIds", JSONArray.class);
		Long[] ecmIds = parseJsonArray2Longs(ecmIdArr);// 需要获取ecmIds
		res = new JSONObject();
		userId = infos.getLong("id");// 需要获取userId
		if(ecmIds == null) {
			res.put("success", false);
			res.put("message", "当前用户还未通过审核，无法登陆");
			return res;
		} else {
			res.put("success", true);
		}
		JWTThreadLocalUtil.setJwt(jwtToken);
		JSONObject ecmInfoes = this.packageEmcIdInfo(ecmIds,userId);
		ecmInfoes.put("userId", userId);
		ecmInfoes.put("personalHeigherEcmId", infos.getLong("highestEcmId"));
		CookieUtil.addCookie(getResponse(),ApplicationConstain.USER_ECM_ID_INFOS_KEY,
				ecmInfoes.toJSONString(), 60 * 60 * 24 * 7);
		return res;
	}
}
