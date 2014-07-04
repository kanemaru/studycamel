package jp.biglobe.camel.eip;

import org.apache.camel.spi.ProcessorFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by sane on 2014/07/01.
 */
@Component
public class MySplitter {

    public List splitMessage(String payload) {

        String id = String.valueOf(new Random().nextInt(10));
        System.out.println("## Splitter: " + id);

        List result = new ArrayList<String>();
        result.add("Facebook " + payload + " (" + id + ")");
        result.add("Twitter " + payload + " (" + id + ")");
        result.add("Instagram " + payload + " (" + id + ")");
        return result;
    }
}
