
package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import static cn.cdyxtech.lab.feign.SecurityCheckAPI.SERVICE_PREFIX;

@FeignClient(url = "${labApiGateway}", name = "securityCheck", path = SERVICE_PREFIX)
public interface SecurityCheckAPI {
	String SERVICE_PREFIX="/api-lab-security-check";

	/**
	 * 安全检查分页查询
	 * @param ecmId 查看范围
	 * @param status 0 待整改 1 已整改
	 * @param checkType 检查类型
	 * @param hiddenDangerGrade 隐患等级
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param keyword 关键字
	 * @param page
	 * @param limit
	 * @return
	 */
	@GetMapping("/check/getPage")
    JSONObject inspectPage(@RequestParam("ecmId") Integer ecmId,
		@RequestParam("rectificationStatus") Integer status,
		@RequestParam("checkType") String checkType,
		@RequestParam("unqualifiedProGrade") String hiddenDangerGrade,
		@RequestParam("createTimeStart") Long startTime,
		@RequestParam("createTimeEnd") Long endTime,
        @RequestParam("keyword") String keyword,
        @RequestParam("page") Integer page,
		@RequestParam("limit") Integer limit,
		@RequestParam("unqualifiedGrade") String checkGrade);
		
	/**
	 * 安全检查列表
	 * @param ecmId 查看范围
	 * @param checkType 检查类型
	 * @param hiddenDangerGrade 隐患等级
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param keyword 关键字
	 * @param page
	 * @param limit
	 * @return
	 */
	@GetMapping("/rectification/getPage")
	JSONObject changedPage(@RequestParam("ecmId") Integer ecmId,
		@RequestParam("checkType") String checkType,
		@RequestParam("unqualifiedGrade") String hiddenDangerGrade,
		@RequestParam("createTimeStart") Long startTime,
		@RequestParam("createTimeEnd") Long endTime,
		@RequestParam("keyword") String keyword,
		@RequestParam("page") Integer page,
		@RequestParam("limit") Integer limit);

	/**
	 * 复查记录查询
	 * @param ecmId 查看范围
	 * @param reviewStatus 是否通过
	 * @param checkType 检查类型
	 * @param hiddenDangerGrade 隐患等级
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param keyword 关键字
	 * @param page
	 * @param limit
	 * @return
	 */
	@GetMapping("/review/getPage")
	JSONObject redoPage(@RequestParam("ecmId") Integer ecmId,
		@RequestParam("reviewStatus") Boolean reviewStatus,
		@RequestParam("checkType") String checkType,
		@RequestParam("unqualifiedGrade") String hiddenDangerGrade,
		@RequestParam("createTimeStart") Long startTime,
		@RequestParam("createTimeEnd") Long endTime,
		@RequestParam("keyword") String keyword,
		@RequestParam("page") Integer page,
		@RequestParam("limit") Integer limit);

		
	@GetMapping("/check/queryCheckDetail/{id}")
	JSONObject inspectDetail(@PathVariable("id") Integer id);

	/**
	 * 问题统计-按检查项统计(树形)
	 *
	 * @param ecmId
	 * @param page
	 * @param limit
	 * @return
	 */
	@GetMapping(value = "/check/getPageStatisticsByUnqualified/{ecmId}")
	JSONObject getPageStatisticsByUnqualified(@PathVariable(value = "ecmId") Integer ecmId,
											  @RequestParam(value = "page") Integer page,
											  @RequestParam(value = "limit") Integer limit);

	/**
	 * 问题统计-按问题等级(树形)
	 *
	 * @param ecmId
	 * @return
	 */
	@GetMapping(value = "/check/getStatisticsByProblemGrade/{ecmId}")
	JSONObject getStatisticsByProblemGrade(@PathVariable(value = "ecmId") Integer ecmId);

    /**
     * 整改统计-整改占比统计
     *
     * @param ecmId
     * @return
     */
    @GetMapping(value = "/check/count/rectification")
    JSONObject rectification(@RequestParam(value = "ecmId") Integer ecmId);

    /**
     * 整改统计-复查通过占比统计
     *
     * @param ecmId
     * @return
     */
	@GetMapping(value = "/review/count/reviewStatus")
	JSONObject reviewStatus(@RequestParam(value = "ecmId") Integer ecmId);
	
	/**
	 * 事故分頁查詢
	 * @param ecmId
	 * @param page
	 * @param limit
	 * @param keyword
	 * @return
	 */
    @GetMapping(value = "/accident/queryAccPage")
	JSONObject accidentPage(@RequestParam(value = "ecmId") Integer ecmId,
		@RequestParam(value = "page") Integer page,
		@RequestParam(value = "limit") Integer limit,
		@RequestParam(value = "accName") String keyword);

		
	/**
	 * 检查统计
	 * @param ecmId
	 * @return 本月检查总数 本年检查总数
	 */
    @GetMapping("/check/getStatisticsSum/{ecmId}")
	JSONObject inspectStatis(@PathVariable("ecmId") Integer ecmId);
	
	/**
	 * 整改统计
	 * @param ecmId
	 * @return 本月整改总数，本年整改总数，复查率
	 */
    @GetMapping("/rectification/getStatisticsRectificationSum/{ecmId}")
	JSONObject changedStatis(@PathVariable("ecmId") Integer ecmId);
	
	/**
	 * 问题统计
	 * @param ecmId
	 * @return 按问题等级统计个数
	 */
    @GetMapping("/check/getStatisticsByProblemGrade/{ecmId}")
	JSONObject problemStatis(@PathVariable("ecmId") Integer ecmId);
	
	/**
	 * 事故统计
	 * @param ecmId
	 * @return
	 */
    @GetMapping("/accident/getSafetyAccidentsStatistics/{ecmId}")
	JSONObject accidentStatis(@PathVariable("ecmId") Integer ecmId);

	/**
	 * 保存事故详情阶段记录
	 * @param json
	 * @return
	 */
    @PostMapping("/accident/createOrUpdateRecord")
    JSONObject saveAccidentRecord(@RequestBody JSONObject json);

	/**
	 * 事故详情阶段记录
	 * @param id
	 * @param stage
	 * @return
	 */
    @GetMapping("/accident/queryRecode")
    JSONObject accidentRecord(@RequestParam("accId")Long id,@RequestParam("stage")String stage);

    @GetMapping("/accident/queryRecordDetail")
	JSONObject accidentRecordDetail(@RequestParam("recordId") Long id);

    @GetMapping("/accident/isTeamGroup")
    JSONObject isAccidentTeamMember(@RequestParam("id")Long id,@RequestParam("userId")Long userId);

    @PostMapping("/accident/changeAccStatus")
    JSONObject changeAccidentStatus(@RequestParam("accId")Long id,@RequestParam("status")Integer status);

    @GetMapping("/accident/queryAccStatusCode")
	JSONObject getAccidentStatus(@RequestParam("accId")Long id);
}
