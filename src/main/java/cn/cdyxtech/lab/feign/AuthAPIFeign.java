
package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import static cn.cdyxtech.lab.feign.AuthAPIFeign.SERVICE_PREFIX;

@FeignClient(url = "${merisApiGateway}", name = "auth", path = SERVICE_PREFIX)
public interface AuthAPIFeign {

	String SERVICE_PREFIX = "/api-auth";

	/**
	 * * 获取验证码(图片)
	 * 
	 * @param validateType
	 *            验证码类别定义,目前支持:commonImage,sms
	 * @param sequence
	 *            当前请求唯一标识符
	 * @param expire
	 *            过期时间,单位毫秒
	 * @param strategy
	 *            验证码内容生成策略,0:混合秘钥,1:纯数字,2:纯字母
	 *            ,100:计算性的验证码(默认选择),200:随机字符,Default value : 100
	 * @param extendParams
	 *            扩展参数传递,json格式
	 */
	@GetMapping(value = "/validate/validateCode/{validateType}")
	byte[] getValidateCode(@PathVariable("validateType") String validateType, @RequestParam("sequence") String sequence,
			@RequestParam("expire") Long expire,
			@RequestParam(value = "strategy", defaultValue = "100", required = false) Integer strategy,
			@RequestParam(value = "extendParams", required = false) String extendParams);
	/**
	 * 获取手机验证码
	 * @param validateType 验证码类别定义,目前支持sms
	 * @param sequence 当前请求唯一标识符
	 * @param expire 过期时间,单位毫秒
	 * @param strategy 验证码内容生成策略,0:混合秘钥,1:纯数字,2:纯字母 ,100:计算性的验证码(默认选择)
	 * @param extendParams 扩展参数传递,json格式
	 */
	@GetMapping(value = "/validate/validateCode/{validateType}",headers = {"Authorization=emin.smart.mall.super.token"})
	JSONObject getMobileCode(
			@PathVariable(value="validateType") String validateType,
			@RequestParam(value="sequence") String sequence,
			@RequestParam(value="expire") Long expire,
			@RequestParam(value="strategy") Long strategy,
			@RequestParam(value="extendParams") String extendParams);

	/**
	 * * 验证
	 * 
	 * @param validateType
	 *            验证码类别定义,目前支持:commonImage,sms
	 * @param sequence
	 *            当前请求唯一标识符(与验证码的一致)
	 * @param validateValue
	 *            匹配验证码值
	 * @param extendParams
	 *            扩展参数传递,json格式
	 */
	@PostMapping(value = "/validate/check/{validateType}")
	JSONObject check(@PathVariable("validateType") String validateType, 
			@RequestParam("sequence") String sequence,
			@RequestParam("validateValue") String validateValue,
			@RequestParam(value = "extendParams", required = false) String extendParams);

	/**
	 * 用户登录
	 * 
	 * @param params
	 *            参数传递
	 * @param sequence
	 *            当前请求唯一标识符(与验证码的一致)
	 * @param loginType
	 *            登录类别
	 */
	/* @PostMapping(value = "/authorize/login/{loginType}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	ResponseEntity<JSONObject> login(@RequestParam(value = "params") String params,
            @RequestParam(value = "sequence") String sequence,
			@PathVariable(value = "loginType") String loginType); */
	@PostMapping(value = "/authorize/login/{loginType}")
	JSONObject login(@RequestParam(value = "params") String params,
			@RequestParam(value = "sequence") String sequence,
			@PathVariable(value = "loginType") String loginType);
	
	/**
	 * * 请求结果，用于扫码登录
	 * @param sequence 当前请求唯一标识符(与验证码（二维码）的一致)
	 */
	@GetMapping(value = "/request/result")
	JSONObject requestResult( @RequestParam("sequence") String sequence);		
}