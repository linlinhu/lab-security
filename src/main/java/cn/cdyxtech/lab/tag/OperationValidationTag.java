/**
 * 
 */
package cn.cdyxtech.lab.tag;

import freemarker.core.Environment;
import freemarker.template.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * @author jim.lee
 *
 */
public class OperationValidationTag implements TemplateDirectiveModel {

	/* (non-Javadoc)
	 * @see freemarker.template.TemplateDirectiveModel#execute(freemarker.core.Environment, java.util.Map, freemarker.template.TemplateModel[], freemarker.template.TemplateDirectiveBody)
	 */

	
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		
		Writer writer = env.getOut();
		
		String codes =((TemplateScalarModel) params.get("codes")).getAsString();
		
		String code = ((TemplateScalarModel) params.get("operationCode")).getAsString();
		
		if(body!=null) {

			if(codes.contains(code)) {
				body.render(writer);
			}
			
		}
	}

}
