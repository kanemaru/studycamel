package jp.biglobe.camel.eip;

import org.springframework.stereotype.Component;

/**
 * Created by sane on 2014/07/01.
 */
@Component
public class MyRouter {

    public String route(String message) {

        if (message.startsWith("Facebook")) {
            System.out.println("##route: channelFacebook");
            return "channelFacebook";
        }
        if (message.startsWith("Twitter")) {
            System.out.println("##route: channelTwitter");
            return "channelTwitter";
        }
        if (message.startsWith("Instagram")) {
            System.out.println("##route: channelInstagram");
            return "channelInstagram";
        }
        throw new RuntimeException("##no hit");
    }
}
