/**
 * 
 */
package cn.cdyxtech.lab.controller;

import cn.cdyxtech.lab.facade.ECOFacade;
import cn.cdyxtech.lab.facade.UserFacade;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import cn.cdyxtech.lab.util.JWTUtil;
import cn.cdyxtech.lab.util.JsonObjectHelper;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.ResultCheckUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.emin.base.util.CookieUtil;
import cn.cdyxtech.lab.annotation.IgnoreIterceptor;
import cn.cdyxtech.lab.constain.ApplicationConstain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author jim.lee
 *
 */
@Controller
public class LoginController extends HeaderCommonController {

	@Autowired
	private UserFacade userFacade;
	@Autowired
	private BasicInfoAPI basicInfoAPI;
	@Autowired
	private ECOFacade ecoFacade;

	@Value("${cookieDomain:localhost}")
	private String cookieDomain;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@IgnoreIterceptor
	public String loginPage() {
		return "login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	@IgnoreIterceptor
	public ResponseBack<Boolean> login(String username, String password, String code, String sessionKey) {
		ResponseEntity<JSONObject> result = userFacade.login(username, password, code, sessionKey);
		String jwtToken = result.getHeaders().getFirst(JWTUtil.HEADER_STRING);
		CookieUtil.addCookie(getResponse(), ApplicationConstain.AUTHORIZATION_KEY, jwtToken, cookieDomain, 60 * 60 * 24 * 7);
		JSONObject infos = result.getBody().getJSONObject("result");
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(infos);
		JsonObjectHelper helper = new JsonObjectHelper(jsonObject);
		JSONArray ecmIdArr = helper.getValue("ecmIds", JSONArray.class);
		Long[] ecmIds = parseJsonArray2Longs(ecmIdArr);// 需要获取ecmIds
		Long userId = infos.getLong("id");// 需要获取userId
		JWTThreadLocalUtil.setJwt(jwtToken);
		JSONObject ecmInfoes = this.packageEmcIdInfo(ecmIds);
		ecmInfoes.put("userId", userId);
		CookieUtil.addCookie(getResponse(),ApplicationConstain.USER_ECM_ID_INFOS_KEY,
				ecmInfoes.toJSONString(), cookieDomain, 60 * 60 * 24 * 7);
		return ResponseBack.success(true);
	}
	/**
	 * 根据ecmid查询相关的主体信息
	 * @param ecmId
	 * @return
	 */
	private JSONObject packageEmcIdInfo(Long[] ecmIds) {
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
			JWTThreadLocalUtil.setRootEcmId(highestEcmId);
			back.put("highestEcmId", highestEcmId);
		}
		return back;
	}

	@RequestMapping(value = "/getValidImg", method = RequestMethod.GET)
	@ResponseBody
	@IgnoreIterceptor
	public byte[] getImg(String sequence) {
		return userFacade.getValidateImg(sequence);

	}

	@RequestMapping("/logout")
	@ResponseBody
	public ResponseBack<Boolean> logout() {
		CookieUtil.delCookie(getResponse(), "Authorization");
		return ResponseBack.success(true);
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
}
