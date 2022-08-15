package com.betvictor.websocketp2p.websocketp2p.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @Value("${betvictor.application-hostname}")
    private String applicationHostname;

    @Value("${betvictor.websocket.port:9000}")
    private Integer websocketPort;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("applicationHostname", applicationHostname);
        model.addAttribute("websocketPort", websocketPort);
        return "index";
    }
}
