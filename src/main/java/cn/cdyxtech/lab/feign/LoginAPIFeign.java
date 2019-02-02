
package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static cn.cdyxtech.lab.feign.LoginAPIFeign.SERVICE_PREFIX;

@FeignClient(url = "${merisApiGateway}",name = "login",path = SERVICE_PREFIX)
public interface LoginAPIFeign {

	String SERVICE_PREFIX="/api-lab-basicinformation-extension";
	
	/**
	 * * 根据主体ID集合查询所有类型，1：学校；2：学院；3：实验室
	 * @param ecmIds           
	 */
	@GetMapping("/user/findEcmWithTypeByEcmIds")
    JSONObject findEcmWithTypeByEcmIds(@RequestParam(value = "ecmIds") Long[] ecmIds);
}
