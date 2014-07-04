package jp.biglobe.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * Created by sane on 2014/07/01.
 */
@Component
public class MyFacebookProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        String num = body.substring(body.length() - 3);
        String newBody = body + " MyFacebookProcessor process done!! " + num;
        System.out.println("## MyFacebookProcessor done.");
        exchange.getIn().setBody(newBody);
    }
}
