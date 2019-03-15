
package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static cn.cdyxtech.lab.feign.BasicInfoAPI.SERVICE_PREFIX;

@FeignClient(url = "${labApiGateway}",name = "basicInfo", path = SERVICE_PREFIX)
public interface BasicInfoAPI {

    String SERVICE_PREFIX = "/api-lab-basicinformation-extension";

    /**
     * 查询学校详情
     *
     * @param ecmId
     * @return
     */
    @GetMapping("/wholeUniversity/wholeUniversityDetail")
    JSONObject schoolInfo(@RequestParam("ecmId") Integer ecmId);

    /**
     * 根据学校编号查询学院列表
     *
     * @param ecmId   学校主体编号
     * @param page
     * @param limit
     * @param keyword
     * @return
     */
    @GetMapping("/wholeCollege/getPage")
    JSONObject sbranchPage(@RequestParam("universityId") Integer ecmId,
                           @RequestParam("page") Integer page,
                           @RequestParam("limit") Integer limit,
                           @RequestParam("keyword") String keyword);


    /**
     * 查询学院详情
     *
     * @param ecmId 学院主体编号
     * @return
     */
    @GetMapping("/wholeCollege/wholeCollegeDetail")
    JSONObject branchInfo(@RequestParam("collegeId") Integer ecmId);

    /**
     * 删除学院
     *
     * @param ecmId
     * @return
     */
    @GetMapping("/college/deleteByEcmId")
    JSONObject removeBranch(@RequestParam("ecmId") Integer ecmId);


    /**
     * 学校或者安全中心统计实验室列表
     *
     * @param ecmId         学校编号
     * @param page
     * @param limit
     * @param keyword       关键字
     * @param categoryName  实验室分类
     * @param securityLevel 实验室等级
     * @return
     */
    @GetMapping("/wholeLaboratory/getLabPageStatistics/{ecmId}")
    JSONObject labsPageBySchoolId(@RequestParam("ecmId") Integer ecmId,
                                  @RequestParam("page") Integer page,
                                  @RequestParam("limit") Integer limit,
                                  @RequestParam("keyword") String keyword,
                                  @RequestParam("categoryName") String categoryName,
                                  @RequestParam("securityLevel") String securityLevel);

    /**
     * 学院实验室列表
     *
     * @param ecmId        查看范围主体编号
     * @param page
     * @param limit
     * @param keyword
     * @param categoryName 实验室分类名称，或者危险源名称
     * @return
     */
    @GetMapping("/wholeLaboratory/getPage")
    JSONObject labPage(@RequestParam("collegeId") Integer ecmId,
                       @RequestParam("page") Integer page,
                       @RequestParam("limit") Integer limit,
                       @RequestParam("keyword") String keyword,
                       @RequestParam("categoryName") String categoryName);

    /**
     * 实验室详情
     *
     * @param ecmId 实验室主体编号
     * @return
     */
    @GetMapping("/wholeLaboratory/wholeLaboratoryDetail")
    JSONObject labInfo(@RequestParam("laboratoryId") Integer ecmId);

    /**
     * 删除实验室
     *
     * @param ecmId
     * @return
     */
    @GetMapping("/laboratory/deleteByEcmId")
    JSONObject removeLab(@RequestParam("ecmId") Integer ecmId);

    /**
     * 查询安全中心信息
     *
     * @param ecmId 学校主体编号
     * @return
     */
    @GetMapping("/wholeSecurityCenter/wholeSecurityCenterDetailByEcmId")
    JSONObject scInfo(@RequestParam("ecmId") Long ecmId);

    /**
     * 判断实验室是否存在二维码打印申请
     *
     * @param ecmId
     * @return
     */
    @GetMapping("/laboratoryQrApply/existApply")
    JSONObject existPrintApply(@RequestParam("laboratoryId") Integer ecmId);

    /**
     * 实验室申请打印二维码
     *
     * @param ecmId
     * @param operationUserId
     * @return
     */
    @PostMapping("/laboratoryQrApply/apply")
    JSONObject printApply(@RequestParam("laboratoryId") Integer ecmId,
                          @RequestParam("operationUserId") Long operationUserId);


    /**
     * 查询安全中心申请二维码打印列表
     *
     * @param ecmId        学校主体编号
     * @param verifyUserId
     * @param page
     * @param limit
     * @param keyword
     * @return
     */
    @GetMapping("/laboratoryQrApply/page")
    JSONObject printApplyPage(@RequestParam("ecmId") Integer ecmId,
                              @RequestParam("verifyUserId") Integer verifyUserId,
                              @RequestParam("page") Integer page,
                              @RequestParam("limit") Integer limit,
                              @RequestParam("keyword") String keyword);

    /**
     * 根据实验室编号生成实验室二维码图片及信息
     *
     * @param sns 实验室编号 字符串数组
     * @return
     */
    @GetMapping("/wholeLaboratory/generateQRCode")
    JSONObject generateQrcode(@RequestParam("sns") String[] sns);

    /**
     * 打印实验室二维码生成打印记录
     *
     * @param applyId 申请打印编号
     * @param userId
     * @return
     */
    @GetMapping("/laboratoryQrApply/print")
    JSONObject recordPrint(@RequestParam("applyId") String applyId,
                           @RequestParam("operationUserId") Integer userId);

    /**
     * 通知打印完成
     *
     * @param applyId 申请打印编号
     * @param userId
     * @return
     */
    @GetMapping("/laboratoryQrApply/notice")
    JSONObject noticePrinted(@RequestParam("applyId") String applyId,
                             @RequestParam("operationUserId") Integer userId);


    /**
     * 实验室统计-按学院
     *
     * @param ecmId 学校主体ID
     * @return
     */
    @GetMapping("/wholeLaboratory/getLabStatisticsByCollege/{ecmId}")
    JSONObject getLabStatisticsByCollege(@PathVariable("ecmId") Integer ecmId);
    
    /**
	 * * 根据主体ID集合查询所有类型，1：学校；2：学院；3：实验室
	 * @param ecmIds           
	 */
	@GetMapping("/user/findEcmWithTypeByEcmIds")
    JSONObject findEcmWithTypeByEcmIds(@RequestParam(value = "ecmIds") Long[] ecmIds);

    /**
     * 实验室统计-按分类
     *
     * @param ecmId 查看范围主体ID
     * @return
     */
    @GetMapping("/wholeLaboratory/getLabStatisticsByCategory/{ecmId}")
    JSONObject getLabStatisticsByCategory(@PathVariable("ecmId") Integer ecmId);

    /**
     * 实验室统计-按实验室等级
     *
     * @param ecmId 查看范围主体ID
     * @return
     */
    @GetMapping("/wholeLaboratory/getLabStatisticsByLevel/{ecmId}")
    JSONObject getLabStatisticsByLevel(@PathVariable("ecmId") Integer ecmId);

    /**
     * 危险源统计-按危险源总数
     *
     * @param ecmId 学校主体ID
     * @return
     */
    @GetMapping("/wholeLaboratory/getHazardSourceSumStatistics/{ecmId}")
    JSONObject getHazardSourceSumStatistics(@PathVariable("ecmId") Integer ecmId);

    /**
     * 危险源统计-按危险源分类
     *
     * @param ecmId 查看范围主体ID
     * @return
     */
    @GetMapping("/wholeLaboratory/getHazardSourceCategoryStatistics/{ecmId}")
    JSONObject getHazardSourceCategoryStatistics(@PathVariable("ecmId") Integer ecmId);
    
    /**
     * 查询安全体系组织树
     * @param universityId 学校ID
     * @param keyword 查询字段
     */
    @RequestMapping(value ="/wholeSecurityCenter/getSecurityTree",method = RequestMethod.GET,headers="Authorization=emin.smart.mall.super.token")
    JSONObject getSecurityTree(
            @RequestParam(value = "universityId") Long universityId,
            @RequestParam(value = "keyword") String keyword);


    /**
     * 实验室统计
     * @param ecmId
     * @return 实验室总数,存在危险源的实验室总数
     */
    @GetMapping("/wholeLaboratory/getUniversityLabCount/{ecmId}")
    JSONObject labsStatis(@PathVariable("ecmId") Integer ecmId);


    /**
     * 危险源统计
     * @param ecmId
     * @return 全校危险源总数
     */
    @GetMapping("/wholeLaboratory/getUniversityHSSum/{ecmId}")
    JSONObject hazardStatis(@PathVariable("ecmId") Integer ecmId);
    
    /**
     * 查询用户是否是安全中心的成员
     * @param userId 用户id
     * @param hEcmId 最高权限EcmId
     * @return 
     */
    @GetMapping("/user/getHighestType")
    JSONObject getHighestType(@RequestParam(value = "userId") Long userId,
            @RequestParam(value = "hEcmId") Long hEcmId);
            
    /**
     * 根据id查询用户详细信息
     * @param userId 用户id
     * @return 
     */
    @GetMapping(value = "/user/userLoginInfo", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<JSONObject> userLoginInfo(@RequestParam(value = "userId") Long userId);

     /**
     * 查询用户详情
     * @param id 用户id
     * @param highestEcmId   最高权限EcmId
     * @param userFlockType 最高权限的用户类别
     * @return 
     */
    @GetMapping("/user/findInfoById/{id}")
    JSONObject findInfoById(@PathVariable(value = "id") Long id,
            @RequestParam(value = "highestEcmId") Long highestEcmId,
            @RequestParam(value = "userFlockType") Long userFlockType);


     /**
     * 通过ecmId查询本ecmId及其下级主体：主要供数据权限类需求的前端控制选择查询数据范围使用
     * @param ecmId 主体id
     * @return 
     */
    @GetMapping("/common/getEcmAndChildByEcmId/{ecmId}")
    JSONObject getEcmAndChildByEcmId(@PathVariable(value = "ecmId") Long ecmId);
}
