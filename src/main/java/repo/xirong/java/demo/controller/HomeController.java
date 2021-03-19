package repo.xirong.java.demo.controller;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import repo.xirong.java.demo.model.User;
import repo.xirong.java.demo.service.TestService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xirong on 15/1/28.
 */
@Controller
public class HomeController {
    @Autowired
    private TestService service;
//
//    @Autowired
//    private OrderStatisticService orderStatisticService;

    @RequestMapping(value="/")
    public ModelAndView index(Model model)
    {
        ModelAndView mv =new ModelAndView();
        mv.setViewName("home");

//        ArrayList<User> users=service.getAllUsers();
//        mv.addObject("userList",users);

        return mv;
    }

    @RequestMapping(value="/test")
    public ModelAndView test(Model model) {
        ModelAndView mv =new ModelAndView();
        mv.setViewName("test");
        return mv;
    }

    @RequestMapping(value="/home")
    public ModelAndView home(Model model) {
        ModelAndView mv =new ModelAndView();
        mv.setViewName("home");
        return mv;
    }

    @RequestMapping(value="/homepage")
    public String homepage(Model model) {
//        ModelAndView mv =new ModelAndView();
//        mv.setViewName("home");
        return "home";
    }

    @ResponseBody
    @RequestMapping("/hello")
    public String hello() throws Exception {
        String url = "http://localhost:8080/pcs/payCenter/payInfo";
        Map<String,Object> parameters = new HashMap<String, Object>();
        parameters.put("payOrderId", "02000000012021030320937");
        parameters.put("code", "");
        parameters.put("tradeType", "02");
        return doPost(url, parameters);
//        return "hello";
    }

    public static String doPost(String url, Map<String,Object> parameters) throws Exception{
        HttpClient client = new HttpClient();
        PostMethod getMethod = new PostMethod(url);
        client.getHttpConnectionManager().getParams().setConnectionTimeout(40 * 1000);// 连接超时
        client.getHttpConnectionManager().getParams().setSoTimeout(40 * 1000);// 数据读取超时
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        if(parameters!=null&&parameters.size()>0){
            for(String key:parameters.keySet()){
                Object obj = parameters.get(key);
                if(obj!=null)getMethod.setParameter(key, obj.toString());
            }
        }
        client.executeMethod(getMethod);
        return getMethod.getResponseBodyAsString();
    }
}
