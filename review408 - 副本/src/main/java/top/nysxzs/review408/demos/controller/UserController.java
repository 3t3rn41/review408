package top.nysxzs.review408.demos.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.nysxzs.review408.demos.config.JwtAuthenticationManager;
import top.nysxzs.review408.demos.config.JwtConfig;
import top.nysxzs.review408.demos.pojo.User;
import top.nysxzs.review408.demos.pojo.query.Return;
import top.nysxzs.review408.demos.pojo.query.UserQuery;
import top.nysxzs.review408.demos.service.CaptchaService;
import top.nysxzs.review408.demos.service.CaptchaServiceImpl;
import top.nysxzs.review408.demos.service.ReviewService;
import top.nysxzs.review408.demos.service.UserService;

import javax.servlet.http.HttpSession;

@CrossOrigin
@RestController//返回页面用Controller,返回数据用RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;
    @Autowired

    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CaptchaService captchaService;
    public Return checkToken(String token){
        JwtAuthenticationManager jwtAuthenticationManager=new JwtAuthenticationManager();
        return jwtAuthenticationManager.verifyJWT(token);
    }
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Object login(@RequestBody UserQuery userQuery){
        Return ret=new Return();
        if(userQuery.getPassword()==null||userQuery.getUsername()==null){
            ret.setCode(3);
            ret.setMessage("账号密码不能为空");
            return ret;
        }
        if(userQuery.getVerifyCode()==null||userQuery.getVerifyCode().equals(""))
        {
            ret.setCode(3);
            ret.setMessage("验证码不能为空");
            return ret;
        }
        String verifyResult= captchaService.verifyCode(userQuery.getUsername(), userQuery.getVerifyCode());
        if(!verifyResult.equals("验证码正确")) {
            ret.setCode(4);
            ret.setMessage(verifyResult);
            return ret;
        }
        if(userService.listUser(userQuery.getUsername(),userQuery.getPassword())!=null)
        {
            ret.setCode(0);
            ret.setMessage("登录成功");
            JwtConfig jwtConfig=new JwtConfig();
            User user=userService.listUser(userQuery.getUsername(),userQuery.getPassword());
            user.setToken(jwtConfig.generateJwt(user.getId(),user.getUsername()));
            ret.setData(user);
        }
        else{
            ret.setCode(1);
            ret.setMessage("用户名或密码错误");
        }
        return ret;
    }
    @RequestMapping(value = "/checkRegister",method = RequestMethod.POST)
    public Object checkRegister(@RequestBody UserQuery userQuery){
        Return ret=new Return();
        if(!userQuery.getUsername().equals("")&&!userQuery.getPassword().equals("")) {
            if(userQuery.getVerifyCode()==null||userQuery.getVerifyCode().equals(""))
            {
                ret.setCode(3);
                ret.setMessage("验证码不能为空");
                return ret;
            }
            String verifyResult= captchaService.verifyCode(userQuery.getUsername(), userQuery.getVerifyCode());
            if(!verifyResult.equals("验证码正确")) {
                ret.setCode(4);
                ret.setMessage(verifyResult);
                return ret;
            }
            if (userService.checkRegister(userQuery.getUsername()) != null) {
                ret.setCode(6);
                ret.setMessage("用户名已存在!");
            }
            else if(reviewService.badWordsFilter(userQuery.getUsername()).contains("*"))
            {
                ret.setCode(-1);
                ret.setMessage("用户名不符合要求！");
                return ret;
            }
            else {
                ret.setCode(0);
                userService.toRegister(userQuery.getUsername(), userQuery.getPassword());
                ret.setMessage("注册成功!");
            }
        }
        else {
            ret.setCode(1);
            ret.setMessage("用户名和密码不能为空!");
        }
        return ret;
    }
    @GetMapping("/getUser")
    public User getUsers(@RequestParam String username, @RequestParam String password) {
        return userService.listUser(username, password);
    }
    @RequestMapping(value = "/initData",method = RequestMethod.GET)
    public Object initData(@RequestHeader(value = "Authentication",required = false) String token){
        Return ret=new Return();
        ret.setCode(0);
        if(token==null||token.isEmpty())
        {
            ret.setCode(-1);
            ret.setMessage("请先登录");

            return ret;
        }
        Return res=checkToken(token);
        if(res.getMessage().equals("登录过期")||res.getMessage().equals("用户名或密码错误"))
            ret.setMessage(res.getMessage());
        else//登录正常
        {
            ret.setMessage("登录状态正常");
            ret.setData(userService.checkToken(res.getMessage()));
        }

        //Token user = userService.checkToken(token);
//        if(user == null) {
//            ret.setCode(3);
//            ret.setMessage("登录信息异常");
//            return ret;
//        }
//            if (userService.checkToken(token).getTimeout() != null) {
//                Date date = new Date();
//                if (userService.checkToken(token).getTimeout() > date.getTime()) {
//                    ret.setCode(0);
//                    ret.setMessage("登录状态正常");
//                    ret.setData(userService.checkToken(token));
//                } else {
//                    ret.setCode(2);
//                    ret.setMessage("登录过期");
//                }
//
//            }
        return ret;
    }
    public void saveString(String key, String value) {

        stringRedisTemplate.opsForValue().set(key, value);

    }

    public String getString(String key) {

        return stringRedisTemplate.opsForValue().get(key);

    }
}
