package cn.cdyxtech.lab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.cdyxtech.lab.feign.MelAPIFeign;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/mel")
public class MelController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;

    @PostMapping("/save")
    @ResponseBody
    public JSONObject save(Integer modelId, String authToken, String data){
        
        String submitter = "18508220186";
        Boolean sync = true;
        JSONObject res = melApiFeign.save(modelId, authToken, data, submitter, sync);
        System.out.println(JSONObject.toJSONString(res));
        return res;
    }

}
