package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONObject;
import static cn.cdyxtech.lab.feign.ExaminationAPIFeign.SERVICE_PREFIX;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;


/*@FeignClient(value = "zuul")*/
@FeignClient(url = "${labApiGateway}", name = "examination", path = SERVICE_PREFIX)
public interface ExaminationAPIFeign {

	String SERVICE_PREFIX="/api-lab-basicinformation-extension";

	/**
     * 申请列表
     * @param keyword 关键字:申请人姓名、申请加入主体名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param applyUserId 申请人Id
     * @param verifyUserId 审核人Id
     * @param isAgree 是否通过审核:true通过，false拒绝;不传此参数，表示查询verifyUserId待审核的申请
     * @param sort 按照某个属性进行排序,多个属性按照’,’英文逗号隔开,同理与order属性一起
     * @param order 按照什么类型排序,可选值:asc(默认,升序),desc(降序),多个属性按照’,’英文逗号隔开,同理与sort属性一起,多个属性则有多个排序值
     */
    @GetMapping(value ="/userFlockApplyApi/page")
    JSONObject querypage(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "startTime") Long startTime,
            @RequestParam(value = "endTime") Long endTime,
            @RequestParam(value = "applyUserId") Long applyUserId,
            @RequestParam(value = "verifyUserId") Long verifyUserId,
            @RequestParam(value = "isAgree") Boolean isAgree,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "order") String order,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "limit") Integer limit);

   
    /**
     * 审核
     *
     * @param applyId  审核id
     * @param verifyUserId 审核人的id
     * @param isAgree 是否同意 1同意 2拒绝
     */
    @GetMapping(value ="/userFlockApplyApi/verify")
    JSONObject verify(
            @RequestParam(value = "applyId") Long applyId,
            @RequestParam(value = "verifyUserId") Long verifyUserId,
            @RequestParam(value = "isAgree") Long isAgree);   
}
