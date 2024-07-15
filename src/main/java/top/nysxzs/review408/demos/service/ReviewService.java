package top.nysxzs.review408.demos.service;


import top.nysxzs.review408.demos.pojo.ReviewInit;
import top.nysxzs.review408.demos.pojo.WdQuestion;
import top.nysxzs.review408.demos.pojo.review408;

import java.util.List;

public interface ReviewService {
    List<review408> getQuestions(String subject, String username);
    review408 getTargetQuestion(Integer id,String username);
    Integer updateWrongTimes(Integer id,Integer wrongTimes,String username);
    String updateReplyStatus(Integer id,String status,Long nextReviewTime,Integer isTodayDone,Integer wrongTimes,Integer successiveRightDays,String username);
    Integer insertReview(String subject,String question,String answer,Integer optionType,String optionA,
                         String optionB,String optionC,String optionD,String explain,String username);
    Integer updateReview(Integer id,String subject,String question,String answer,Integer optionType,String optionA,
                         String optionB,String optionC,String optionD,String explain,String username);
    String checkAnswer(Integer id,String answer,String username);
    ReviewInit initData(String username);
    Integer labelKnowed(Integer id,String username);
    List<WdQuestion> getWdQuestions(String subject);
    String badWordsFilter(String text);
}
