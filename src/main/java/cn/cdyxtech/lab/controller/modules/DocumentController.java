package cn.cdyxtech.lab.controller.modules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;

import cn.cdyxtech.lab.config.FileTypeConfig.FileResourceType;
import cn.cdyxtech.lab.controller.HeaderCommonController;

import cn.cdyxtech.lab.feign.DocumentApiFeign;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.util.UserClaim;

import java.util.Map;

@Controller
@RequestMapping("/document")
public class DocumentController extends HeaderCommonController{
	@Autowired
	DocumentApiFeign documentApiFeign;
	
	@Autowired
    MelAPIFeign melAPIFeign;
	
	@Autowired
	FileResourceType fileResourceType;

    @GetMapping("/index")
    public ModelAndView index(Map<String,Object> data){
    	UserClaim userClaim = this.validateAuthorizationToken();
		Long highestEcmId = userClaim.getHighestEcmId();
    	ModelAndView mv = new ModelAndView("modules/document/manage");
    	mv.addObject("moculeCode","document");
    	mv.addObject("ecmId",highestEcmId);
        return mv;
    }
    
    /**
     * 模板获取
     * @param serviceId 
     * @param code
     * @param sourceType
     * @return
     */
    @GetMapping("/get-mel")
	@ResponseBody
	public JSONObject getMel(String serviceId, String code, String sourceType) {
		JSONObject res = melAPIFeign.dataModel(serviceId, code, sourceType);
		this.dealException(res);
		return res;
	}
    
    /**
	 * 文件列表查询
	 * @param paramsStr 查询参数,json格式的字符串
	 * @param sort 按照某个属性进行排序,多个属性按照’,’英文逗号隔开,同理与order属性一起
	 * @param order 按照什么类型排序,可选值:asc(默认,升序),desc(降序),多个属性按照’,’英文逗号隔开,同理与sort属性一起,多个属性则有多个排序值
	 * @param flockId 角色id
	 */
	@RequestMapping("/getPage")
	@ResponseBody
	public JSONObject getPage(String keyword, String order, Long pid, Boolean isContainSelf,Long nodeType, Integer nodeDomain) {
		UserClaim userClaim = this.validateAuthorizationToken();
		JSONObject res;
		Long ecmId = userClaim.getHighestEcmId();
		JSONObject paramsStr = new JSONObject();
		String order_o = order;
		Integer page = getPageRequestData().getCurrentPage();
		Integer limit = getPageRequestData().getLimit();
		if(order == null){
			order = "desc";
		}
		order = "asc," + order;
		paramsStr.put("name", keyword);
		paramsStr.put("ecmId", ecmId);
		if(pid!=null){
			paramsStr.put("pid", pid);
		}
		if(nodeType!=null){
			paramsStr.put("nodeType", nodeType);
		}
		paramsStr.put("nodeDomain", nodeDomain);
		res = documentApiFeign.querypage(paramsStr.toJSONString(), "nodeType,lastModifyTime", order, page, limit);
		if (!res.getBooleanValue("success")) {
			throw new EminException(res.getString("code"));
		}
		JSONObject result = res.getJSONObject("result");
		
		if(result!=null){
			JSONArray array = result.getJSONArray("resultList");
			int size = array.size();
			for (int i = 0; i < size; i++) {
				JSONObject entity = array.getJSONObject(i);
				if(!entity.containsKey("fileType")){
					continue;
				}
				String fileType = entity.getOrDefault("fileType","obj").toString();
				String viewFileType = fileResourceType.get(fileType);
				entity.put("viewFileType",viewFileType );
				array.set(i, entity);
			}
			result.put("resultList", array);
			res.put("result", result);
		}
		if(isContainSelf == null) {
			isContainSelf = true;
		}
		if(pid==null){
			JSONObject root = documentApiFeign.root();
			if (!root.getBooleanValue("success")) {
				throw new EminException(root.getString("code"));
			}
			root = root.getJSONObject("result");
			pid=root.getLong("id");
		}
		JSONObject path = documentApiFeign.path(pid.intValue(), isContainSelf);
		if (!res.getBooleanValue("success")) {
			throw new EminException(res.getString("code"));
		}
		res.put("path", path.getJSONArray("result"));
		res.put("name", keyword);
		res.put("order",order_o);
		return res;
	}
	
	/**
     * 查询领域根节点
     *
     * @param
     */
    @GetMapping("/queryRoots")
    @ResponseBody
    public JSONObject queryRoots(String domainIsSelected) {
    	UserClaim userClaim = this.validateAuthorizationToken();
		Long highestEcmId = userClaim.getHighestEcmId();
        JSONObject params = new JSONObject();
        int[] noInNodeDomains = {1};
        params = new JSONObject();
        params.put("noInNodeDomains", noInNodeDomains);//排除1
        params.put("pid", -1);//查询全部
        params.put("nodeDomain", -1);//查询全部
        params.put("sort", "createTime");
        params.put("order", "asc");
        params.put("domainIsSelected", domainIsSelected);
        params.put("ecmId", highestEcmId);
        
        JSONObject res = documentApiFeign.queryRoots(params.toJSONString());
        this.dealException(res);
        return res;
    }
	
	 /**
     * 保存领域节点信息
     *
     * @param rootModifyDto 领域节点信息
     */
    @PostMapping("/createDomainRoot")
    @ResponseBody
    public JSONObject createDomainRoot(String rootModifyDto) {
        UserClaim userClaim = this.validateAuthorizationToken();
		Long highestEcmId = userClaim.getHighestEcmId();
        JSONObject rootModifyDtoObj = JSONObject.parseObject(rootModifyDto); 
        rootModifyDtoObj.put("operationUserId", userClaim.getId());
        rootModifyDtoObj.put("operationUserName", userClaim.getRealName());
        rootModifyDtoObj.put("isRoot", true);
        rootModifyDtoObj.put("ecmId", highestEcmId);
        JSONObject res = documentApiFeign.createDomainRoot(rootModifyDtoObj.toJSONString());
        this.dealException(res);
        return res;
    }
    
    /**
     * 新建或者编辑文件夹(编辑分类也用这个方法)
     *
     * @param dirDto 文件夹信息
     */
    @PostMapping("/folderCreateOrUpdate")
    @ResponseBody
    public JSONObject folderCreateOrUpdate(String dirDto,Boolean isRoot) {
    	UserClaim userClaim = this.validateAuthorizationToken();
        JSONObject dirDtoObj = JSONObject.parseObject(dirDto); 
        JSONObject folderDetail;
        if (dirDtoObj.getLong("id") == null) {
        	dirDtoObj.put("operationUserId", userClaim.getId());
        	dirDtoObj.put("operationUserName", userClaim.getRealName());
        	dirDtoObj.put("ecmId", userClaim.getHighestEcmId());
        	
        } else {
        	folderDetail = documentApiFeign.folderDetail(dirDtoObj.getLong("id"));
            this.dealException(folderDetail);
            folderDetail = folderDetail.getJSONObject("result");
            folderDetail.put("name", dirDtoObj.getString("name"));
            folderDetail.remove("nodeType");
            dirDtoObj = folderDetail;
            if(isRoot) {
            	dirDtoObj.put("isRoot", true);
            }
        }
        JSONObject res = documentApiFeign.folderCreateOrUpdate(dirDtoObj.toJSONString());
        this.dealException(res);
        return res;
    }
    
    @PostMapping("/delete")
	@ResponseBody
	public JSONObject remove(Integer[] ids) {
		JSONObject res = documentApiFeign.delete(ids);
		this.dealException(res);
		return res;
	}
    /**
     * 新建或者编辑文件
     *
     * @param fileDto 文件信息
     */
	@RequestMapping("/fileCreateOrUpdate")
	@ResponseBody
	public JSONObject fileCreateOrUpdate(String fileDto,String tagParams, String tagLibParams) {
		UserClaim userClaim = this.validateAuthorizationToken();
		JSONObject res;
		JSONObject fileDtoObj = JSON.parseObject(fileDto);
		if(fileDtoObj.getLong("id") == null) {
			fileDtoObj.put("operationUserId", userClaim.getId());
			fileDtoObj.put("operationUserName", userClaim.getRealName());
			fileDtoObj.put("ecmId", userClaim.getHighestEcmId());
		}
		res = documentApiFeign.fileCreateOrUpdate(fileDtoObj.toJSONString());
		this.dealException(res);
		return res;
	}
	
	/**
	 * 移动文件或者文件夹
	 * @param id 文件或者文件夹的id
	 * @param mvPid 新的父节点id
	 */
	@RequestMapping("/move")
	@ResponseBody
	public JSONObject move(Integer[] ids, Integer mvPid) {
		JSONObject res =  new JSONObject();
		if(ids == null) {
			res.put("success", false);
			res.put("message", "参数不完整");
		} else {
			for(int i = 0; i < ids.length; i++) {
				res = documentApiFeign.move(ids[i], mvPid);
				this.dealException(res);
			}
		}
		return res;
	}
	/**
	 * 验证某个文件下是否含有某些节点名,存在相同名返回true
	 * @param targetDirId 目标目录的id
	 * @param nodeNames
	 */
	@RequestMapping("/existNodeFirLvlName")
	@ResponseBody
	public JSONObject existNodeFirLvlName(Long targetDirId, String[] nodeNames) {
		JSONObject res = documentApiFeign.existNodeFirLvlName(targetDirId, nodeNames);
		this.dealException(res);
		return res;
	}
	/**
     * 判断当前文档是否可以预览
     * @param viewChannel 预览的渠道方式
     * @param filePath 网络文档地址
     * @param previewFileName 预览文件的名称
     */
	@GetMapping("/supportPreview")
	@ResponseBody
	public JSONObject supportPreview(String viewChannel, String filePath, String previewFileName) {
		JSONObject res = documentApiFeign.supportPreview(viewChannel, filePath, previewFileName);
		this.dealException(res);
		return res;
	}
	/**
     * 预览文档
     * @param viewChannel 预览的渠道方式
     * @param filePath 网络文档地址
     * @param previewFileName 预览文件的名称
     */
	@PostMapping("/preview")
	@ResponseBody
	public JSONObject preview(String viewChannel, String filePath, String previewFileName) {
		JSONObject res = documentApiFeign.preview(viewChannel, filePath, previewFileName);
		this.dealException(res);
		return res;
	}  
}
