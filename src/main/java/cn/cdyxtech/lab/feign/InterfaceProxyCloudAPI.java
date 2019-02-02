
package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import static cn.cdyxtech.lab.feign.InterfaceProxyCloudAPI.SERVICE_PREFIX;

@FeignClient(url = "${labApiGateway}",name = "interfaceProxyCloud", path = SERVICE_PREFIX)
public interface InterfaceProxyCloudAPI {

    String SERVICE_PREFIX = "/api-lab-interface-proxy-cloud";

    /**
     * 根据学校编号查询学院列表
     *
     * @param ecmId   学校主体编号
     * @param page
     * @param limit
     * @param keyword
     * @return
     */
    @GetMapping("/statistics/securityUserList")
    JSONObject securityUsers(@RequestParam("id") Integer ecmId,
                           @RequestParam("type") Integer type ,
                           @RequestParam("keyword") String keyword);
    /**
     * 获取学院列表
     * @param ecmId 学校主体编号
     */
    @GetMapping("/statistics/getCollegeList")
    JSONObject getCollegeList(@RequestParam("ecmId") Long ecmId);
}
