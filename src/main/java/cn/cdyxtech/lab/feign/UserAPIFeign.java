/**
 * 
 */
package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static cn.cdyxtech.lab.feign.UserAPIFeign.SERVICE_PREFIX;

/**
 * @author jim.lee
 *
 */
@FeignClient(url = "${merisApiGateway}",name = "userCenter",path = SERVICE_PREFIX)
public interface UserAPIFeign {

	String SERVICE_PREFIX="/api-user";

	/**
	 * * 用户登录 *
	 * 
	 * @param username
	 *            账号
	 * @param password
	 *            密码
	 * @param code
	 *            验证码
	 */
	@PostMapping(value = "/login/login")
    ResponseEntity<JSONObject> login(@RequestParam(value = "account") String account,
                                     @RequestParam(value = "password") String password);

	/** * 获取验证图片 */
	@GetMapping(value = "/login/getImg")
	byte[] getImg(@RequestParam(value = "sessionKey") String sessionKey);
	
	/** * 验证图片上的验证码 */
	@GetMapping(value = "/login/checkImgCode")
	JSONObject checkImgCode(@RequestParam(value = "sessionKey") String sessionKey,
			@RequestParam(value = "code") String code);
	
	/**
	 * *验证用户是否登录 *
	 *
	 * @param token
	 *            用过户登录时获取到的token值
	 */
	@RequestMapping(value = "/validate", method = RequestMethod.POST)
	JSONObject userValidate(@RequestParam(value = "token") String token);

	/** *用户退出登录 * @param token 用过户登录时获取到的token值 */
	@RequestMapping(value = "/api/member/user/outLogin", method = RequestMethod.POST)
	JSONObject outLogin();

	/** * 用户详情 * @param id 被查询的用户id */
	@RequestMapping(value = "/api/member/user/detail", method = RequestMethod.GET)
	JSONObject detail(@RequestParam(value = "id") Long id);

	/**
	 * 根据token获取用户信息
	 * @param token
	 * @return
	 */
	@GetMapping("/validateToken")
	JSONObject validateToken();

	/**
	 * 分页获取用户
	 * @param page
	 * @param limit
	 * @param keyword
	 * @return
	 */
	@GetMapping("/api/member/user/list")
	JSONObject userList(@RequestParam("ecmIds") Long[] ecmId,@RequestParam("flockIds")Long[] flockIds,@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("keyword") String keyword, @RequestParam("type") String type);

	@PostMapping(value = "/register/saveOrUpdateUser")
	JSONObject saveOrUpdateUser(@RequestParam("realName") String name, @RequestParam("mobile") String mobile, @RequestParam("id") Long id);

	@PostMapping(value = "/register/saveUserWithEcm")
	JSONObject saveOrUpdateUser(@RequestBody JSONObject user);

	@PostMapping("/api/member/user/deleteBatch")
	JSONObject deleteUser(@RequestParam("userIds") Long[] id);

	@PostMapping(value="/api/member/user/saveOrUpdateUserInfo",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	JSONObject saveOrUpdateUserInfo(@RequestParam("infoStr") String info);

	@GetMapping("/api/member/user/queryFlocksByPersonId/{personId}")
	JSONObject getUserFlocks(@PathVariable("personId") Long userId);

	@PostMapping("/api/member/user/statusBatch")
	JSONObject statusUpdate(@RequestParam("ids") Long[] ids,@RequestParam("status") boolean status);


	@GetMapping("/api/flock/getEcmFlock")
	JSONObject getFlocks(@RequestParam("ecmId") Long ecmId,@RequestParam("flockType") Integer flockType);



}
