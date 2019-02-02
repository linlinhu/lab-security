package cn.cdyxtech.lab.config;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

@ControllerAdvice
public class WACTExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(WACTExceptionHandler.class);

    /***
     * 判断接口的注解是否是ResponseBody,是,那么返回json,而不是跳转错误页面
     * @param stackTraceElement
     * @return
     */
    public static boolean isControllerAction(StackTraceElement stackTraceElement) {

        String className = stackTraceElement.getClassName();
        if (className.endsWith("Controller")) {
            try {
                Class<?> controllerClass = Class.forName(className);
                Method[] methods = controllerClass.getDeclaredMethods();
                for (Method method : methods){
                    if(method.getName().equals(stackTraceElement.getMethodName()) && method.getAnnotation(ResponseBody.class)!=null){
                        return true;
                    }
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /***
     * 判断接口的注解是否是ResponseBody,是,那么返回json,而不是跳转错误页面<br />
     * 限制最多循环4次,否则影响性能
     * @param stackTraceElements
     * @return
     */
    public static boolean isControllerAction(StackTraceElement[] stackTraceElements) {
        int length = stackTraceElements.length;
      /*  if (length > 6) {
            length = 6;
        }*/
        for (int i = 0; i < length; i++) {
            StackTraceElement stackTraceElement = stackTraceElements[i];
            if (isControllerAction(stackTraceElement)) {
                return true;
            }
        }
        return false;
    }


    @ExceptionHandler({EminException.class})
    @ResponseBody
    public ModelAndView eminExceptionHandler(EminException e,HttpServletRequest request, HttpServletResponse response) throws IOException {
        e.printStackTrace();
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        boolean responseJson = isControllerAction(stackTraceElements);
        if(responseJson){
            JSONObject json = new JSONObject();
            json.put("success", false);
            json.put("message", e.getLocalizedMessage());
            response.addHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write(json.toJSONString());
            return null;
        }
        ModelAndView mv = new ModelAndView("500");
        mv.addObject("message",e.getLocalizedMessage());
        return mv;
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ModelAndView exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        e.printStackTrace();
        boolean responseJson = isControllerAction(e.getStackTrace());
        if(responseJson){
            JSONObject json = new JSONObject();
            json.put("success", false);
            json.put("message", e.getMessage());
            response.addHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write(json.toJSONString());
            return null;
        }
        ModelAndView mv = new ModelAndView("500");
        mv.addObject("message",e.getMessage());
        return mv;
    }


}
