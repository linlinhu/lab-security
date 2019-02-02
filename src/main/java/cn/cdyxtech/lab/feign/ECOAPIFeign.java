package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.cdyxtech.lab.feign.ECOAPIFeign.SERVICE_PREFIX;

@FeignClient(url = "${merisApiGateway}",name = "ecoService",path = SERVICE_PREFIX)
public interface ECOAPIFeign {

    String SERVICE_PREFIX="/api-eco";

    @GetMapping("/eco/{ecmId}/getTopByEcm")
    JSONObject getTopByEcm(@PathVariable("ecmId") Long ecmId, @RequestParam("needSelf") boolean needSelf);

    @GetMapping("/eco/{chainId}/{ecmId}/subNodes")
    JSONObject subNodes(@PathVariable("chainId") Long chainId, @PathVariable("ecmId") Long ecmId, @RequestParam("needDeep") boolean needDeep);

    @GetMapping("/eco/{ecmId}/getParentByEcm")
    JSONObject getParentEcm(@PathVariable("ecmId") Long ecmId);

}
