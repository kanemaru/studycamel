package jp.biglobe.bchat.controller;

import jp.biglobe.bchat.handler.ChatHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class IndexController {
    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome(ModelMap model) throws Exception {
        return "index";
    }
}