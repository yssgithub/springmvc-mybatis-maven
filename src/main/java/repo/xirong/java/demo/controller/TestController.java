package repo.xirong.java.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import repo.xirong.java.demo.websocket.WebSocketServer;

import javax.websocket.server.PathParam;

@Controller
@RequestMapping("/main")
public class TestController {

    @Autowired
    WebSocketServer webSocketServer;

    @RequestMapping(value="/users")
    public ModelAndView users(Model model) {
        ModelAndView mv =new ModelAndView();
        mv.setViewName("index");

        return mv;
    }

    @RequestMapping(value="/home")
    public String home(Model model) {
        //返回界面home
        return "home";
    }

    @RequestMapping(value="/homedata")
    @ResponseBody
    public String homedata(Model model) {
        //返回字符串home
        return "home";
    }

    @RequestMapping(value="/index")
    public String index(Model model) {
//        ModelAndView mv =new ModelAndView();
//        mv.setViewName("index");

        return "index";
    }

    @RequestMapping(value = "/socket/{userId}")
    public void socket(@PathParam("userId") String userId) {
        webSocketServer.sendMessage(userId, "你好"+userId+",老铁666");
    }

}
