package top.nysxzs.review408.demos.controller;


import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.nysxzs.review408.demos.config.JwtAuthenticationManager;
import top.nysxzs.review408.demos.dao.ReviewDao;
import top.nysxzs.review408.demos.pojo.WdQuestion;
import top.nysxzs.review408.demos.pojo.query.Return;
import top.nysxzs.review408.demos.pojo.query.insertReviewQuery;
import top.nysxzs.review408.demos.pojo.query.reviewCheckQuery;
import top.nysxzs.review408.demos.pojo.review408;
import top.nysxzs.review408.demos.service.CaptchaService;
import top.nysxzs.review408.demos.service.CaptchaServiceImpl;
import top.nysxzs.review408.demos.service.ReviewServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@CrossOrigin
@RestController
public class ReviewController {
    @Autowired
    private ReviewServiceImpl reviewService;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private ReviewDao reviewDao;
    public Return checkToken(String token){
        JwtAuthenticationManager jwtAuthenticationManager=new JwtAuthenticationManager();
        return jwtAuthenticationManager.verifyJWT(token);
    }
    @RequestMapping(value = "/review408/initData")
    public Object initData(@RequestHeader(value = "Authentication",required = false) String token)
    {
        Return ret=new Return();
        Return checkTokenResult=checkToken(token);
        if (checkTokenResult.getCode()==0) {
            ret.setCode(0);
            ret.setMessage("登录状态正常");
            ret.setData(reviewService.initData(checkTokenResult.getMessage()));
        } else {
            ret.setMessage(checkToken(token).getMessage());
            ret.setCode(-1);
        }
        return ret;
    }
    @RequestMapping(value = "/review408/getQuestions")
    public Object getQuestions(@Param("subject") String subject,@RequestHeader(value = "Authentication",required = false)String token)
    {
        Return ret=new Return();
        Return checkTokenResult=checkToken(token);
        if (checkTokenResult.getCode()==0) {
            List<review408> dataList= reviewService.getQuestions(subject,checkTokenResult.getMessage());
            ArrayList<review408> newDataList=new ArrayList<>(dataList);
            Collections.shuffle(newDataList);
            ret.setData(newDataList);
            ret.setCode(0);
            ret.setMessage("ok");
        } else {
            ret.setMessage(checkToken(token).getMessage());
            ret.setCode(-1);
        }
        return ret;
    }
    @RequestMapping(value = "/review408/getTargetQuestion")
    public Object getTargetQuestions(@Param("id") Integer id,@RequestHeader(value = "Authentication",required = false)String token)
    {
        Return checkTokenResult=checkToken(token);
        Return ret=new Return();
        if(checkTokenResult.getCode()==0)
        {
            ret.setCode(0);
            ret.setMessage("ok");
            ret.setData(reviewService.getTargetQuestion(id,checkTokenResult.getMessage()));
        }
        else
        {
            ret.setCode(-1);
            ret.setMessage(checkTokenResult.getMessage());
        }
        return ret;
    }
    @PostMapping(value = "/review408/checkAnswer")
    public Object checkAnswer(@RequestBody reviewCheckQuery reviewCheckQuery, @RequestHeader(value = "Authentication",required = false) String token)
    {
        Return checkTokenResult=checkToken(token);
        Return ret = new Return();
        if(checkTokenResult.getCode()==0) {
            ret.setCode(0);
            ret.setMessage(reviewService.checkAnswer(reviewCheckQuery.getId(), reviewCheckQuery.getAnswer(),checkTokenResult.getMessage()));
        }
        else {
            ret.setCode(-1);
            ret.setMessage(checkTokenResult.getMessage());
        }
        return ret;
    }
    @RequestMapping(value = "/review408/insertReview",method = RequestMethod.POST)
    public Object insertReview(@RequestBody insertReviewQuery insertReviewQuery, @RequestHeader(value = "Authentication",required = false) String token)
    {
        Return ret=new Return();
        Return checkTokenResult=checkToken(token);
        if(checkTokenResult.getCode()==0) {
            ret.setCode(0);
            if (reviewService.insertReview(insertReviewQuery.getSubject(), insertReviewQuery.getQuestion(), insertReviewQuery.getAnswer(), insertReviewQuery.getOptionType(),
                    insertReviewQuery.getOptionA(), insertReviewQuery.getOptionB(), insertReviewQuery.getOptionC(), insertReviewQuery.getOptionD(), insertReviewQuery.getExplain(),checkTokenResult.getMessage()) == 1) {
                ret.setMessage("insert successfully!");
                ret.setData(null);
            } else {
                ret.setMessage("insert failed");
                ret.setData(null);
            }
        }
        else
        {
            ret.setCode(-1);
            ret.setMessage(checkTokenResult.getMessage());
        }
        return ret;
    }
    @RequestMapping(value = "/review408/updateReview",method = RequestMethod.POST)
    public Object updateReview(@RequestBody review408 review408,@RequestHeader(value = "Authentication",required = false) String token)
    {
        Return ret = new Return();
        Return checkTokenResult=checkToken(token);
        if(checkTokenResult.getCode()==0) {
            ret.setCode(0);
            if (reviewService.updateReview(review408.getId(), review408.getSubject(), review408.getQuestion(), review408.getAnswer(), review408.getOptionType(),
                    review408.getOptionA(), review408.getOptionB(), review408.getOptionC(), review408.getOptionD(), review408.getExplaination(),checkTokenResult.getMessage()) == 1)
                ret.setMessage("update successfully!");
            else
                ret.setMessage("update failed");
        }
        else
        {
            ret.setCode(-1);
            ret.setMessage(checkTokenResult.getMessage());
        }
        return ret;
    }
    @RequestMapping(value = "/review408/labelKnowed",method = RequestMethod.GET)
    public Object labelKnowed(@RequestParam("id") int id,@RequestHeader(value = "Authentication",required = false) String token)
    {
        Return ret=new Return();
        Return checkTokenResult=checkToken(token);
        if(checkTokenResult.getCode()==0) {
            ret.setCode(0);
            int affectedRows=reviewService.labelKnowed(id,checkTokenResult.getMessage());
            if(affectedRows==1)
                ret.setMessage("ok");
            else
                ret.setMessage("系统异常，请联系管理员");
        }
        else
        {
            ret.setCode(-1);
            ret.setMessage(checkTokenResult.getMessage());
        }
        return ret;
    }
    @RequestMapping(value = "/review408/getVerifyImg",method = RequestMethod.GET)
    public ResponseEntity<byte[]> getCaptcha(@RequestParam("u") String username) throws Exception {
        byte[] captchaImage = captchaService.generateCaptcha(username);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(captchaImage);
    }
    @RequestMapping(value = "/review408/getWdQuestions",method = RequestMethod.GET)
    public Object getWdQuestions(@RequestHeader(value = "Authentication",required = false) String token,@RequestParam("subject") String subject)
    {
        Return ret=new Return();
        Return checkTokenResult=checkToken(token);
        if(checkTokenResult.getCode()==0) {
            if(subject==null||!(subject.equals("数据结构")||subject.equals("操作系统")||subject.equals("计算机组成原理")||subject.equals("计算机网络"))) {
                ret.setMessage("参数不符合要求");
                ret.setCode(-2);
                return ret;
            }
            ret.setCode(0);
            ret.setMessage("ok");
            ret.setData(reviewService.getWdQuestions(subject));
            return ret;
        }
        else
        {
            ret.setCode(-1);
            ret.setMessage(checkTokenResult.getMessage());
        }
        return ret;
    }
    @RequestMapping(value = "/filter",method = RequestMethod.GET)
    public String filter(@RequestParam("text") String text)
    {
        return reviewService.badWordsFilter(text);
    }
}
