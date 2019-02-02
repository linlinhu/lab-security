package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONObject;

import static cn.cdyxtech.lab.feign.DocumentApiFeign.SERVICE_PREFIX;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


/*@FeignClient(value = "zuul")*/
@FeignClient(url = "${merisApiGateway}", name = "document", path = SERVICE_PREFIX)
public interface DocumentApiFeign {

	String SERVICE_PREFIX="/api-doc";

	/**
     * 文件列表查询
     * @param paramsStr 查询参数,json格式的字符串
     * @param sort      按照某个属性进行排序,多个属性按照’,’英文逗号隔开,同理与order属性一起
     * @param order     按照什么类型排序,可选值:asc(默认,升序),desc(降序),多个属性按照’,’英文逗号隔开,同理与sort属性一起,多个属性则有多个排序值
     */
    @RequestMapping(value ="/node/page",method = RequestMethod.GET,headers={"Authorization=cn.cdyxtech.super.token"})
    JSONObject querypage(
            @RequestParam(value = "paramsStr") String paramsStr,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "order") String order,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "limit") Integer limit);

    /**
     * 按照当前查询获取当前文档库某一节点信息,若存在多个,则返回错误码
     *
     * @param paramsStr 查询参数,json格式的字符串
     */
    @RequestMapping(value ="/node/getUniqueByParams", method = RequestMethod.GET)
    JSONObject nodeDetail(
            @RequestParam(value = "paramsStr") String paramsStr);

    /**
     * 获取文档库的根节点
     */
    @RequestMapping(value ="/node/root", method = RequestMethod.GET)
    JSONObject root();


    /**
     * 新建或者编辑文件夹
     *
     * @param dirDto 文件夹信息
     */
    @RequestMapping(value ="/node/createOrUpdateDir", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    JSONObject folderCreateOrUpdate(@RequestBody String dirDto);

    /**
     * 新建或者编辑文件
     *
     * @param fileDto 文件信息
     */
    @RequestMapping(value ="/node/createOrUpdateFile", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    JSONObject fileCreateOrUpdate(@RequestBody String fileDto);

    /**
     * 移动文件或者文件夹
     *
     * @param id    文件或者文件夹的id
     * @param mvPid 新的父节点id
     */
    @RequestMapping(value ="/node/mv/{id}/to/{mvPid}", method = RequestMethod.POST)
    JSONObject move(
            @PathVariable(value = "id") Integer id,
            @PathVariable(value = "mvPid") Integer mvPid);

    /**
     * 根据id查询路径
     *
     * @param id            文件或者文件夹的id
     * @param isContainSelf 当前路径是否包含自己节点：false:不包含， true：包含
     */
    @RequestMapping(value ="/node/path/{id}", method = RequestMethod.GET)
    JSONObject path(
            @PathVariable(value = "id") Integer id,
            @RequestParam(value = "isContainSelf") Boolean isContainSelf);

    /**
     * 根据id删除文件或者文件夹
     *
     * @param ids id集合
     */
    @RequestMapping(value ="/node/rms", method = RequestMethod.POST)
    JSONObject delete(
            @RequestParam(value = "ids") Integer[] ids);

    /**
     * 根据id查询文件夹的详情
     *
     * @param id 文件夹id
     */
    @RequestMapping(value ="/node/dir/{id}", method = RequestMethod.GET)
    JSONObject folderDetail(
            @PathVariable(value = "id") Long id);

    /**
     * 保存领域节点信息
     *
     * @param rootModifyDto 领域节点信息
     */
    @RequestMapping(value ="/node/createDomainRoot", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    JSONObject createDomainRoot(
            @RequestBody String rootModifyDto);

    /**
     * 查询领域根节点
     *
     * @param params 查询信息
     */
    @RequestMapping(value ="/node/root/query", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    JSONObject queryRoots(
            @RequestBody String params);

    /**
     * 验证某个文件下是否含有某些节点名,存在相同名返回true
     *
     * @param targetDirId 目标目录的id
     * @param nodeNames
     */
    @RequestMapping(value ="/node/existNodeFirLvlName", method = RequestMethod.GET)
    JSONObject existNodeFirLvlName(
            @RequestParam(value = "targetDirId") Long targetDirId,
            @RequestParam(value = "nodeNames") String[] nodeNames);

    /**
     * 获取当前文档库的文件夹节点
     * @param dirId 目录id
     * @return com.alibaba.fastjson.JSONObject
     */
    @GetMapping(value = "/node/dir/{id}")
    JSONObject getDirNodeById(@PathVariable("id") long dirId);

    /**
     * 查询子节点
     * @param pid         需要查询的节点id
     * @param isDeepQuery 是否采用深度查询,默认采用第一级节点,深度查询，消耗时间可能过大
     * @param isSimple    是否采用简单属性版本
     * @param paramsStr   查询参数,json格式的字符串
     */
    @GetMapping(value ="/node/{pid}/children")
    JSONObject children(
            @PathVariable("pid") Long pid,
            @RequestParam(value = "isDeepQuery",required = false, defaultValue = "false") Boolean isDeepQuery,
            @RequestParam(value = "isSimple",required = false, defaultValue = "true") Boolean isSimple,
            @RequestParam(value = "paramsStr",required = false, defaultValue = "{}") String paramsStr);
    
    /**
     * 判断当前文档是否可以预览
     * @param viewChannel 预览的渠道方式
     * @param filePath 网络文档地址
     * @param previewFileName 预览文件的名称
     */
    @GetMapping(value ="/doc-preview/supportPreview")
    JSONObject supportPreview(
            @RequestParam(value = "viewChannel",required = true, defaultValue = "yozosoft") String viewChannel,
            @RequestParam(value = "filePath",required = true) String filePath,
            @RequestParam(value = "previewFileName",required = false) String previewFileName);
    

    /**
     * 预览文档
     * @param viewChannel 预览的渠道方式
     * @param filePath 网络文档地址
     * @param previewFileName 预览文件的名称
     */
    @PostMapping(value ="/doc-preview/preview")
    JSONObject preview(
            @RequestParam(value = "viewChannel",required = true, defaultValue = "yozosoft") String viewChannel,
            @RequestParam(value = "filePath",required = true) String filePath,
            @RequestParam(value = "previewFileName",required = false) String previewFileName);
    
}
