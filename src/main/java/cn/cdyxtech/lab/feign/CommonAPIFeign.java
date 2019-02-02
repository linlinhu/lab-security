package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="common-service",url = "${merisApiGateway}",path = "/common-service")
public interface CommonAPIFeign {

    @GetMapping("/dateDetail/{year}/{month}/{day}")
    JSONObject getDateDetail(@PathVariable("year") int year, @PathVariable("month") int month, @PathVariable("day") int day);

    @GetMapping("/dd/group/isExistGroupCode")
    JSONObject configIsExists(@RequestParam("code") String code);

    @PostMapping("/dd/group/createOrUpdate")
    JSONObject createConfig(@RequestBody JSONObject config);

    @PostMapping("/dd/item/createOrUpdate")
    JSONObject createConfigItem(@RequestBody JSONObject item);

    @PostMapping("/dd/item/createOrUpdate_batch")
    JSONObject createConfigItems(@RequestBody JSONArray items);

    @GetMapping("/dd/item/{groupCode}/queryByCodePath/{matchType}")
    JSONObject getConfigItems(@PathVariable("groupCode") String groupCode, @RequestParam("codePath") String codePath, @PathVariable("matchType") String matchType);

    @GetMapping("/dd/item/{groupCode}/queryTreeByGroupCode")
    JSONObject getDefaultConfigItems(@PathVariable("groupCode") String groupCode);

    @GetMapping("/dd/item/{groupCode}/queryByCode/{itemCode}")
    JSONObject getConfigItemByCode(@PathVariable("groupCode") String groupCode,@PathVariable("itemCode") String itemCode);

    @GetMapping("/dd/group/getByCode")
    JSONObject getConfigGroupByCode(@RequestParam("code") String groupCode);

    @PostMapping("/dd/item/delete/{id}")
    JSONObject deleteConfigItem(@PathVariable("id") Long id);
}
