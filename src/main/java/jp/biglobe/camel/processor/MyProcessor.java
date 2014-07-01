package jp.biglobe.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by sane on 2014/07/01.
 */
public class MyProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        body = body + " & MyProcessor, Test.";
        exchange.getIn().setBody(body);
    }
}
