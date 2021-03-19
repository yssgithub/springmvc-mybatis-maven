package repo.xirong.java.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/main")
public class TestController {

    @RequestMapping(value="/users")
    public ModelAndView users(Model model) {
        ModelAndView mv =new ModelAndView();
        mv.setViewName("index");

        return mv;
    }

    @RequestMapping(value="/index")
//    @ResponseBody
    public String home(Model model) {
//        ModelAndView mv =new ModelAndView();
//        mv.setViewName("index");

        return "index";
    }
}
