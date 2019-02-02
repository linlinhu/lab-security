
package cn.cdyxtech.lab.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import cn.cdyxtech.lab.config.FeignMultipartSupportConfig;

@FeignClient(url = "${merisApiGateway}", name = "fileApiFeign", configuration = FeignMultipartSupportConfig.class, path = FileApiFeign.SERVICE_PREFIX)
public interface FileApiFeign {
	String SERVICE_PREFIX = "/api-storage";

	/**
	 * 图片文件上传（会对图片进行压缩）
	 * 
	 * @param file
	 *            上传的图片文件
	 * @return
	 */
	@RequestMapping(value = "/storage/upload/img", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	JSONObject upload(@RequestPart(value = "file") MultipartFile file);

	/**
	 * 文件上传（不会对文件进行压缩，不限制文件格式）
	 * 
	 * @param ecmId
	 *            商场id或者主体id
	 * @param file
	 *            上传的文件
	 * @return
	 */
	@RequestMapping(value = "/storage/{ecmId}/upload/uploadFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	JSONObject universalUpload(@PathVariable(value = "ecmId") Long ecmId, @RequestPart(value = "file") MultipartFile file);

}
