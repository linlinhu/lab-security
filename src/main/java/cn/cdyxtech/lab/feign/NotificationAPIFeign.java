package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.cdyxtech.lab.feign.NotificationAPIFeign.SERVICE_PREFIX;

/**
 * 通知服务相关API
 */
@FeignClient(url="${labApiGateway}",name="notification",path = SERVICE_PREFIX)
public interface NotificationAPIFeign {

    String SERVICE_PREFIX = "/api-lab-message-notice";

    /**
     * 通知配置保存
     */
    @PostMapping("/unqualifiedProGradeNoticeFlow/createOrUpdate")
    JSONObject saveNotificationConfig(@RequestBody JSONArray configs);

    @GetMapping("/unqualifiedProGradeNoticeFlow/findAll")
    JSONObject findAllNotificationConfig();

    /**
     * 我的消息
     * @param keyword 关键字
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userId 用户id
     * @param messageType 消息类型 1:检查通知 2：整改通知 3：复查通知 4：系统公告 5：申请通知
     * @param sort 按照某个属性进行排序,多个属性按照’,’英文逗号隔开,同理与order属性一起
     * @param order 按照什么类型排序,可选值:asc(默认,升序),desc(降序),多个属性按照’,’英文逗号隔开,同理与sort属性一起,多个属性则有多个排序值
     */
    @GetMapping(value ="/noticePerson/pageMyMessage")
    JSONObject querypage(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "startTime") Long startTime,
            @RequestParam(value = "endTime") Long endTime,
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "messageType") Long messageType,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "order") String order,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "limit") Integer limit);

   
    /**
     * 我的未读消息数量
     * @param userId  用户id
     */
    @GetMapping(value ="/noticePerson/myNotReadMessageCount")
    JSONObject myNotReadMessageCount(
            @RequestParam(value = "userId") Long userId);
    
    /**
     * 我的未读消息简单列表
     * @param userId  用户id
     * @param count 数量
     * @param readStatus 阅读状态：0 未读 1已读
     */ 
    @GetMapping(value ="/message/queryMySimpleMessage")
    JSONObject queryMySimpleMessage(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "count") Long count,
            @RequestParam(value = "readStatus") Long readStatus);
    
    /**
     * 获取消息详情
     * @param userId  用户id
     * @param id 消息记录id
     * @param isNeedReceiver 是否需要发送范围
     */
    @GetMapping(value ="/message/queryDetail/{id}")
    JSONObject detail(
    		@PathVariable(value = "id") Long id,
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "isNeedReceiver") Boolean isNeedReceiver);
    
    /**
     * 删除
     * @param userId  用户id
     * @param messageId 消息记录id
     */
    @PostMapping(value ="/noticePerson/delMyMessage")
    JSONObject delMyMessage(
    		@RequestParam(value = "messageId") Long messageId,
            @RequestParam(value = "userId") Long userId);
    /**
     * 置为已读
     * @param userId  用户id
     * @param messageId 消息记录id
     */
    @GetMapping(value ="/noticePerson/read/{messageId}/{userId}")
    JSONObject setRead(
    		@PathVariable(value = "messageId") Long messageId,
    		@PathVariable(value = "userId") Long userId);
    
    /**
     * 分页加载检查通知或者公告
     * @param keyword 关键字
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param messageType 消息类型 1:检查通知 2：整改通知 3：复查通知 4：系统公告 5：申请通知
     * @param sort 按照某个属性进行排序,多个属性按照’,’英文逗号隔开,同理与order属性一起
     * @param order 按照什么类型排序,可选值:asc(默认,升序),desc(降序),多个属性按照’,’英文逗号隔开,同理与sort属性一起,多个属性则有多个排序值
     * @param ecmId 学校id
     * @param isDraft 是不是草稿
     */
    @GetMapping(value ="/message/pageMessage")
    JSONObject pageMessage(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "startTime") Long startTime,
            @RequestParam(value = "endTime") Long endTime,
            @RequestParam(value = "messageType") Integer messageType,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "order") String order,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "limit") Integer limit,
            @RequestParam(value = "ecmId") Long ecmId,
            @RequestParam(value = "isDraft") Boolean isDraft);
    
    /**
     * 分页加载整改通知
     * @param keyword 关键字
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param messageType 消息类型 1:检查通知 2：整改通知 3：复查通知 4：系统公告 5：申请通知
     * @param sort 按照某个属性进行排序,多个属性按照’,’英文逗号隔开,同理与order属性一起
     * @param order 按照什么类型排序,可选值:asc(默认,升序),desc(降序),多个属性按照’,’英文逗号隔开,同理与sort属性一起,多个属性则有多个排序值
     * @param ecmId 学校/学院/实验室di
     */
    @GetMapping(value ="/rectificationMessage/pageMessage")
    JSONObject pageModification(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "startTime") Long startTime,
            @RequestParam(value = "endTime") Long endTime,
            @RequestParam(value = "messageType") Integer messageType,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "order") String order,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "limit") Integer limit,
            @RequestParam(value = "ecmId") Long ecmId);
    /**
     * 分页加载复查通知
     * @param keyword 关键字
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param messageType 消息类型 1:检查通知 2：整改通知 3：复查通知 4：系统公告 5：申请通知
     * @param sort 按照某个属性进行排序,多个属性按照’,’英文逗号隔开,同理与order属性一起
     * @param order 按照什么类型排序,可选值:asc(默认,升序),desc(降序),多个属性按照’,’英文逗号隔开,同理与sort属性一起,多个属性则有多个排序值
     * @param ecmId 学校/学院/实验室di
     */
    @GetMapping(value ="/reviewMessage/pageMessage")
    JSONObject pageReview(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "startTime") Long startTime,
            @RequestParam(value = "endTime") Long endTime,
            @RequestParam(value = "messageType") Integer messageType,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "order") String order,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "limit") Integer limit,
            @RequestParam(value = "ecmId") Long ecmId);
    
    /**
     * 查询所有岗位角色
     * @param keyword 关键字
     */
    @GetMapping(value ="/noticePerson/getAllJobRoles")
    JSONObject getAllJobRoles(@RequestParam(value = "keyword") String keyword);
    
    /**
     * 查询消息通知学校架构数据
     * @param keyword 搜索关键字
     * 
     */
    @GetMapping(value ="/noticePerson/getSecurityTree")
    JSONObject getSecurityTree(@RequestParam(value = "keyword") String keyword);
    
    /**
     * 查询下级实验室
     * @param collegeId 学院主体ID
     * @param keyword 关键字
     */
    @GetMapping(value ="/noticePerson/getLowerLevelLabs/{collegeId}")
    JSONObject getLowerLevelLabs(@PathVariable(value = "collegeId") Long collegeId,
    		@RequestParam(value = "keyword") String keyword);
    /**
    * 删除通知：只删除通知本身
    * @param messageId 通知id
    */
   @PostMapping(value ="/message/delete")
   JSONObject deleteNotification(@RequestParam(value = "messageId") Long messageId);
}
