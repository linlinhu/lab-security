/**
 * 
 */
package cn.cdyxtech.lab.config;

import cn.cdyxtech.lab.interceptor.WebInterceptor;
import cn.cdyxtech.lab.tag.Object2JSONTag;
import cn.cdyxtech.lab.tag.OperationValidationTag;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import cn.cdyxtech.lab.util.JWTUtil;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;
import feign.Feign;
import feign.codec.ErrorDecoder;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.io.IOException;
import java.util.Locale;

/**
 * @author jim.lee
 *
 */
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {




	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")  
        .allowCredentials(true)
        .allowedOrigins("*")
        .allowedHeaders("*")
        .allowedMethods("*");  
	}


	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
	    registry.addResourceHandler("/js/**")
	            .addResourceLocations("classpath:/static/js/");
		registry.addResourceHandler("/plugins/**")
				.addResourceLocations("classpath:/static/plugins/");
	    registry.addResourceHandler("/css/**")
	    		.addResourceLocations("classpath:/static/css/");
	    registry.addResourceHandler("/fonts/**")
				.addResourceLocations("classpath:/static/fonts/");
	    registry.addResourceHandler("/img/**")
				.addResourceLocations("classpath:/static/img/");
	    registry.addResourceHandler("swagger-ui.html")
				.addResourceLocations("classpath:/META-INF/resources/");
	    registry.addResourceHandler("**.html")
				.addResourceLocations("classpath:/META-INF/resources/");
	    registry.addResourceHandler("/webjars/**")
	    		.addResourceLocations("classpath:/META-INF/resources/webjars/");
	    super.addResourceHandlers(registry);
	}



	@Bean
	public Object2JSONTag object2JSONTag() {
		return new Object2JSONTag();
	}
	@Bean
	public OperationValidationTag operationValidationTag() {
		return new OperationValidationTag();
	}
	@Bean
	public CommandLineRunner customFreemarker(FreeMarkerViewResolver resolver){
    	return new CommandLineRunner() {
    		@Autowired
    		private freemarker.template.Configuration configuration;
	        @Override
	        public void run(String... strings) {
	        	configuration.setLogTemplateExceptions(false);
	        	configuration.setNumberFormat("#");
	        	configuration.setDateFormat("yyyy-MM-dd");
	        	configuration.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
				configuration.setSharedVariable("obj2json", object2JSONTag());
				configuration.setSharedVariable("codeValidation", operationValidationTag());
	        	configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
	            resolver.setViewClass(CustomFreeMarkerView.class);
	        }
	    };
	}


	@Bean
	public WebInterceptor webInterceptor() {
		return new WebInterceptor();
	}

	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(webInterceptor()).excludePathPatterns("/login",
				"/getValidImg");
		registry.addInterceptor(localeChangeInterceptor());
		super.addInterceptors(registry);

	}

	@Bean
	public Feign.Builder feignBuilder(){
		return Feign.builder().requestInterceptor(template -> {

			/*template.query("ecmId",JWTThreadLocalUtil.getEcmId().toString());
            template.header("ecmId",JWTThreadLocalUtil.getEcmId().toString());*/
			Long rootEcmId = JWTThreadLocalUtil.getRootEcmId();
			template.header("rootEcmId", rootEcmId!=null ? rootEcmId.toString() : null);
			template.header(JWTUtil.HEADER_STRING,JWTThreadLocalUtil.getJwt());

		});
	}

	@Bean
	public ErrorDecoder errorDecoder(){
		return (methodKey, response) -> {
			System.out.println(methodKey);
			EminException exception = new EminException(String.valueOf(response.status()));

			System.out.println(JSONObject.toJSONString(response));
			try {
				String body = IOUtils.toString(response.body().asInputStream(),"utf-8");
				System.out.println(body);
				JSONObject json = JSONObject.parseObject(body);
				if(json!=null && json.containsKey("code")){
					exception = new EminException(json.getString("code"));
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
            return exception;
        };
	}

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.CHINESE);
		return slr;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

}
