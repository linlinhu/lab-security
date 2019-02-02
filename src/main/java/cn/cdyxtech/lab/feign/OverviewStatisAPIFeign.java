package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.cdyxtech.lab.feign.OverviewStatisAPIFeign.SERVICE_PREFIX;


@FeignClient(url = "${labApiGateway}", name = "overviewStatis", path = SERVICE_PREFIX)
public interface OverviewStatisAPIFeign {

    String SERVICE_PREFIX = "/api-lab-security-check";

    /**
     * 按不符合项查询统计数据
     *
     * @param ecmId
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value = "/check/getPageStatisticsByUnqualified/{ecmId}", method = RequestMethod.GET)
    JSONObject getPageStatisticsByUnqualified(@PathVariable(value = "ecmId") Long ecmId,
                                              @RequestParam(value = "page") Integer page,
                                              @RequestParam(value = "limit") Integer limit);

    /**
     * 按隐患等级查询统计数据
     *
     * @param ecmId
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value = "/check/getPageStatisticsByUnqualifiedGrade/{ecmId}", method = RequestMethod.GET)
    JSONObject getPageStatisticsByUnqualifiedGrade(@PathVariable(value = "ecmId") Long ecmId,
                                                   @RequestParam(value = "page") Integer page,
                                                   @RequestParam(value = "limit") Integer limit);
}
