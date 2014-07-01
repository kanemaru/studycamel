package jp.biglobe.camel.controller;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class IndexController {

    @RequestMapping(method = RequestMethod.GET)
    public String indexAction(@RequestParam(value = "message", required=false) String message, ModelMap model) throws Exception {

        ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
        CamelContext camelContext = (CamelContext) context.getBean("camel");
        ProducerTemplate template = camelContext.createProducerTemplate();

        String buildMessage = "Hello " + message + " !!!";
        template.sendBody("direct:start", buildMessage);

        return "index";
    }
}