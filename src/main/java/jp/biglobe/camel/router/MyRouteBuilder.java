package jp.biglobe.camel.router;

import jp.biglobe.camel.processor.MyProcessor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

import java.beans.Expression;

/**
 * Created by sane on 2014/07/01.
 */
@Component
public class MyRouteBuilder extends SpringRouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:start").to("activemq:MessageQueue1");

        from("activemq:MessageQueue1").process(new MyProcessor()).to("activemq:MessageQueue2");

        from("activemq:MessageQueue2").split(body(String.class).tokenize(",")).to("activemq:MessageQueue3");

        from("activemq:MessageQueue3").to("stream:out");
    }
}