package cn.cdyxtech.lab.tag;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import freemarker.core.Environment;
import freemarker.template.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class Object2JSONTag implements TemplateDirectiveModel {
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {

        Writer writer = env.getOut();
        if (params.get("obj") != null) {
            Class clazz = params.get("obj").getClass();
            String convertJsonStr = "";
            if (clazz.isAssignableFrom(SimpleSequence.class)) {
                SimpleSequence ss = (SimpleSequence) params.get("obj");
                convertJsonStr = JSONArray.toJSONString(ss.toList());
            }

            if (clazz.isAssignableFrom(SimpleHash.class)) {
                SimpleHash sh = (SimpleHash) params.get("obj");
                convertJsonStr = JSONObject.toJSONString(sh.toMap());
            }
            writer.write(convertJsonStr);

        } else {
            writer.write("");
        }

        if(body!=null) {
            body.render(writer);
        }
    }
}
