package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.cdyxtech.lab.feign.ECMAPIFeign.SERVICE_PREFIX;

@FeignClient(url="${merisApiGateway}",name = "ecmService",path = SERVICE_PREFIX)
public interface ECMAPIFeign {

    String SERVICE_PREFIX = "/api-ecm";

    @GetMapping("/ecm/queryPage")
    JSONObject pagedMall(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("keyword") String keyword);

    @PostMapping(value="/ecm/save",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    JSONObject save(@RequestBody String mall);

    @PostMapping("/ecm/deleteEcmById")
    JSONObject delete(@RequestParam("ecmId") Long id);

    @GetMapping("/ecm/queryDetail")
    JSONObject detail(@RequestParam("id") Long id);
}
