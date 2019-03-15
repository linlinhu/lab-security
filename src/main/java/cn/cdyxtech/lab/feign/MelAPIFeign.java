package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.cdyxtech.lab.feign.MelAPIFeign.SERVICE_PREFIX;

@FeignClient(url = "${merisApiGateway}", name = "mel",path = SERVICE_PREFIX )
// @FeignClient(url = "${melUrl}", name = "mel")
public interface MelAPIFeign {

    String SERVICE_PREFIX = "/api-mel";
    /**
     * 模板获取
     * @param serviceId 
     * @param code
     * @param sourceType
     * @return
     */
    @GetMapping("/dataModel/{serviceId}/{code}")
    JSONObject dataModel(@PathVariable("serviceId") String serviceId, @PathVariable("code") String code, @RequestParam("sourceType") String sourceType);

    /**
     * 保存模板数据
     * @param modelId
     * @param data
     * @param submitter
     * @param authToken
     * @param sync
     * @return
     */
    /*@PostMapping(value="/logdata/{modelId}/submit",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    JSONObject save(@PathVariable("modelId") Integer modelId, 
        @RequestHeader("authToken") String authToken, 
        @RequestParam("data") String data, 
        @RequestParam("submitter") String submitter, 
        @RequestParam("sync") Boolean sync);*/
    @PostMapping(value="/logdata/{modelId}/submit",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    JSONObject save(@PathVariable("modelId") Integer modelId, 
        @RequestHeader("authToken") String authToken, 
        @RequestBody String data, 
        @RequestParam("submitter") String submitter, 
        @RequestParam("sync") Boolean sync);
    
    /**
     * 分页查询
     * @param serviceId
     * @param title
     * @param sourceType
     * @param submitter
     * @param startTime
     * @param endTime
     * @param code
     * @param keyword
     * @param page
     * @param limit
     * @return
     */
    @PostMapping(value="/data/retrieve",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    JSONObject retrieve(@RequestParam("serviceId") String serviceId, 
        @RequestParam("title") String title, 
        @RequestParam("sourceType") String sourceType, 
        @RequestParam("submitter") String submitter, 
        @RequestParam("startTime") Long startTime, 
        @RequestParam("endTime") Long endTime, 
        @RequestParam("code") String code, 
        @RequestParam("keyword") String keyword, 
        @RequestParam("page") Integer page, 
        @RequestParam("limit") Integer limit);

}
