package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.cdyxtech.lab.feign.PermissionAPIFeign.SERVICE_PREFIX;

@FeignClient(url = "${merisApiGateway}",name = "permissionService",path = SERVICE_PREFIX)
public interface PermissionAPIFeign {

    String SERVICE_PREFIX = "/api-perm";

    @GetMapping("/defaultAdminMenu/{appCode}/queryDetailByCode")
    JSONObject ecmAdminMenu(@PathVariable("appCode") String appCode);

    @GetMapping("/permission/{appCode}/permissions")
    JSONObject userMenu(@PathVariable("appCode") String appCode, @RequestParam("groupIds") Long[] groupIds);

    @GetMapping("/permission/{appCode}/queryByMenuCode/{menuCode}")
    JSONObject menuOperation(@PathVariable("appCode") String appCode, @PathVariable("menuCode") String menuCode, @RequestParam("groupIds") Long[] groupIds);

    @GetMapping("/menu/queryBySuper/{appCode}")
    JSONObject superMenu(@PathVariable("appCode") String appCode);

    @GetMapping("/operation/{appCode}/{menuCode}/queryByMenuCode")
    JSONObject menuOperation(@PathVariable("appCode") String appCode, @PathVariable("menuCode") String menuCode);
}
