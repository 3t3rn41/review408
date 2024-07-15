package top.nysxzs.review408.demos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.nysxzs.review408.demos.dao.ReviewDao;
import top.nysxzs.review408.demos.pojo.ReviewInit;
import top.nysxzs.review408.demos.pojo.WdQuestion;
import top.nysxzs.review408.demos.pojo.review408;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ReviewServiceImpl implements ReviewService{
    @Autowired
    private ReviewDao reviewDao;
    @Override
    public ReviewInit initData(String username)
    {
        return reviewDao.initData(username);
    }
    @Override
    public List<review408> getQuestions(String subject, String username)
    {
        List<review408> list= reviewDao.getQuestions(subject,username);
        ArrayList<review408> dataList = new ArrayList<>(list);
        Iterator<review408> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            review408 review = iterator.next();
            if (review.getNextReviewTime() != null) {
                long currentTimestamp=System.currentTimeMillis()/1000;
                if(review.getNextReviewTime()>currentTimestamp)//当前时间在预计下次复习时间之前
                    iterator.remove();
            }
        }
        review408[] randomData=dataList.toArray(new review408[0]);
        for (review408 randomDatum : randomData) {
            if(randomDatum.getOptionType()==1)
                randomDatum.shuffleOptionsAndUpdateAnswer();
        }
        return List.of(randomData);
    }
    @Override
    public review408 getTargetQuestion(Integer id,String username)
    {
        return reviewDao.getTargetQuestion(id,username);
    }
    public long getNextDayTimestamp()
    {
        LocalDateTime now = LocalDateTime.now();
        // 将当前日期时间调整到次日0时0分0秒
        LocalDateTime nextDayMidnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        // 选择时区，例如：中国时区
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        // 将LocalDateTime转换为ZonedDateTime
        ZonedDateTime zonedDateTime = nextDayMidnight.atZone(zoneId);
        // 将ZonedDateTime转换为时间戳（毫秒）
        long timestamp = zonedDateTime.toInstant().toEpochMilli();
        // 如果你需要秒级的时间戳，可以除以1000并转换为long类型
        return timestamp / 1000;
    }
    @Override
    @Transactional
    public String updateReplyStatus(Integer id,String status,Long nextReviewTime,Integer isTodayDone,Integer wrongTimes,Integer successiveRightDays,String username)
    {
        if(!status.equals("right")&&!status.equals("wrong"))
            return "参数错误";
        int intervalDays=0;
        if(successiveRightDays>=2&&successiveRightDays<4)
            intervalDays=1;
        else if(successiveRightDays>=4&&successiveRightDays<=5)
            intervalDays=3;
        else if(successiveRightDays>5)
            intervalDays=5;
        Integer updatedRows=0;
        if(isTodayDone==0)
        {
            if(status.equals("right"))
            {
                //加时间，标记完成，连续答对天数+1
                updatedRows = reviewDao.updateReplyStatus(id, getNextDayTimestamp()+86400*intervalDays, 1, successiveRightDays + 1,username);
            }
            else {
                //不标记完成，连续答对天数归零
                updatedRows = reviewDao.updateReplyStatus(id, nextReviewTime, 0,0,username);
                updateWrongTimes(id,wrongTimes+1,username);
            }
        }
        else
        {
            if(status.equals("wrong")) {
                //不标记完成，连续答对天数归零
                updatedRows = reviewDao.updateReplyStatus(id, nextReviewTime, 1,0,username);
                updateWrongTimes(id,wrongTimes+1,username);
            }
            else
                updatedRows=1;
        }
        if(updatedRows!=0)
            return "ok";
        else
            return "error";
    }
    @Override
    @Transactional
    public Integer updateWrongTimes(Integer id,Integer wrongTimes,String username)
    {
        return reviewDao.updateWrongTimes(id,wrongTimes,username);
    }
    @Override
    @Transactional
    public Integer insertReview(String subject,String question,String answer,Integer optionType,String optionA,String optionB,String optionC,String optionD,String explain,String username)
    {
        if (subject.isEmpty() || question.isEmpty() || answer.isEmpty() || optionType == null ||
                optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty() || explain.isEmpty()) {
            return 0;
        }
        return reviewDao.insertReview(subject,badWordsFilter(question),badWordsFilter(answer),optionType,badWordsFilter(optionA),
                badWordsFilter(optionB),badWordsFilter(optionC),badWordsFilter(optionD),badWordsFilter(explain),username);
    }
    @Override
    @Transactional
    public Integer updateReview(Integer id,String subject,String question,String answer,Integer optionType,String optionA,String optionB,String optionC,String optionD,String explain,String username)
    {
//        if (id == null || subject.isEmpty() || question.isEmpty() || answer.isEmpty() || optionType == null ||
//                optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty() || explain.isEmpty()) {
//            return 0;
//        }
        return reviewDao.insertReview(subject,badWordsFilter(question),badWordsFilter(answer),optionType,badWordsFilter(optionA),
                badWordsFilter(optionB),badWordsFilter(optionC),badWordsFilter(optionD),badWordsFilter(explain),username);
    }
    @Override
    public String checkAnswer(Integer id,String answer,String username)
    {
        review408 correctQuestion=getTargetQuestion(id,username);
        if(correctQuestion==null)
            return "error";
        String answerString="";
        String answerOption=correctQuestion.getAnswer();
        answerString = switch (answerOption) {
            case "A" -> correctQuestion.getOptionA();
            case "B" -> correctQuestion.getOptionB();
            case "C" -> correctQuestion.getOptionC();
            case "D" -> correctQuestion.getOptionD();
            default -> correctQuestion.getAnswer();
        };
        if(answerString.equals(answer)) {
            updateReplyStatus(correctQuestion.getId(),"right",correctQuestion.getNextReviewTime(),correctQuestion.getIsTodayDone(),correctQuestion.getWrongTimes(),correctQuestion.getSuccessiveRightDays(),username);
            return "right";
        }
        else {
            updateReplyStatus(correctQuestion.getId(),"wrong",correctQuestion.getNextReviewTime(),correctQuestion.getIsTodayDone(),correctQuestion.getWrongTimes(),correctQuestion.getSuccessiveRightDays(),username);
            return "wrong";
        }
    }
    @Override
    public Integer labelKnowed(Integer id,String username)
    {
        if(id != null&&id>0)
        {
            long seed = System.currentTimeMillis();
            int randomDays = (int) ((seed % 11) + 10);
            return reviewDao.updateReplyStatus(id,getNextDayTimestamp()+86400*randomDays,1,null,username);
        }
        else
            return 0;
    }
    @Override
    public List<WdQuestion> getWdQuestions(String subject)
    {
       if(subject==null||subject.equals(""))
           return null;
       else
           return reviewDao.getWdQuestions(subject);
    }
    @Override
    public String badWordsFilter(String text)
    {
        String newText=text;
        int flag=0;
        String[] wordsToFilter={"要过滤的词汇"};
        for (String s : wordsToFilter) {
            if (text.contains(s)) {
                newText = newText.replaceAll(s, "***");
                flag=1;
            }
        }
        if(flag==0)
            return text;
        else
            return newText;
    }
}
