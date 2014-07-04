package jp.biglobe.camel.router;

import jp.biglobe.camel.eip.MyAggregationStrategy;
import jp.biglobe.camel.eip.MySplitter;
import jp.biglobe.camel.processor.MyFacebookProcessor;
import jp.biglobe.camel.processor.MyInstagramProcessor;
import jp.biglobe.camel.processor.MyProcessor;
import jp.biglobe.camel.processor.MyTwitterProcessor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

import java.beans.Expression;

import static org.apache.camel.builder.ExpressionBuilder.beanExpression;

/**
 * Created by sane on 2014/07/01.
 */
@Component
public class MyRouteBuilder extends SpringRouteBuilder {


    public static final String DIRECT_START = "direct:start";
    public static final String DIRECT_FACEBOOK = "direct:facebook";
    public static final String DIRECT_TWITTER = "direct:Twitter";
    public static final String DIRECT_INSTAGRAM = "direct:Instagram";

    public static final String ACTIVEMQ_MESSAGESTART = "activemq:MessageStart";
    public static final String ACTIVEMQ_SPLITTED = "activemq:MessageSpliteed";
    public static final String ACTIVEMQ_POSTPROCESS = "activemq:MessagePostProcess";
    public static final String ACTIVEMQ_AGGREGATED = "activemq:MessageAggregated";

    @Override
    public void configure() throws Exception {

        from(DIRECT_START).to(ACTIVEMQ_MESSAGESTART);

        from(ACTIVEMQ_MESSAGESTART).split(beanExpression(new MySplitter(), "splitMessage")).to(ACTIVEMQ_SPLITTED);

        from(ACTIVEMQ_SPLITTED).choice()
                .when(body().startsWith("Facebook")).to(DIRECT_FACEBOOK)
                .when(body().startsWith("Twitter")).to(DIRECT_TWITTER)
                .when(body().startsWith("Instagram")).to(DIRECT_INSTAGRAM);

        from(DIRECT_FACEBOOK).process(new MyFacebookProcessor()).to(ACTIVEMQ_POSTPROCESS);
        from(DIRECT_TWITTER).process(new MyTwitterProcessor()).to(ACTIVEMQ_POSTPROCESS);
        from(DIRECT_INSTAGRAM).process(new MyInstagramProcessor()).to(ACTIVEMQ_POSTPROCESS);

        from(ACTIVEMQ_POSTPROCESS).aggregate(body(), new MyAggregationStrategy()).completionSize(3).to(ACTIVEMQ_AGGREGATED);

        from(ACTIVEMQ_AGGREGATED).process(new MyProcessor());
    }
}