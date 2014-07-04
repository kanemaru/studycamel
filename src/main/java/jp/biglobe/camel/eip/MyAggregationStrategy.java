package jp.biglobe.camel.eip;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class MyAggregationStrategy implements AggregationStrategy {
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        String oldBody = oldExchange.getIn().getBody(String.class);
        String newBody = newExchange.getIn().getBody(String.class);
        String concatBody = oldBody.concat(newBody);
        // Set the body equal to a concatenation of old and new.
        oldExchange.getIn().setBody(concatBody);
        // Ignore the message headers!
        // (in a real application, you would probably want to do
        //  something more sophisticated with the header data).
        return oldExchange;
    }
}