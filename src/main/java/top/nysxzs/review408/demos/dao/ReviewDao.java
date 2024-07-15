package top.nysxzs.review408.demos.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.nysxzs.review408.demos.pojo.ReviewInit;
import top.nysxzs.review408.demos.pojo.WdQuestion;
import top.nysxzs.review408.demos.pojo.review408;

import java.util.List;

@Mapper
@Repository
public interface ReviewDao {
    List<review408> getQuestions(@Param("subject") String subject, @Param("username") String username);
    review408 getTargetQuestion(@Param("id") Integer id,@Param("username") String username);
    Integer updateWrongTimes(@Param("id") Integer id,@Param("wrongTimes") Integer wrongTimes,@Param("username") String username);
    Integer updateReplyStatus(@Param("id") Integer id,@Param("nextReviewTime") Long nextReviewTime,@Param("isTodayDone") Integer isTodayDone,@Param("successiveRightDays") Integer successiveRightDays,@Param("username") String username);
    Integer insertReview(@Param("subject") String subject,@Param("question") String question,@Param("answer") String answer,
                         @Param("optionType") Integer optionType,@Param("optionA") String optionA,@Param("optionB") String optionB,@Param("optionC") String optionC,
                         @Param("optionD") String optionD,@Param("explain") String explain,@Param("username") String username);
    Integer updateReview(@Param("id") Integer id,@Param("subject") String subject,@Param("question") String question,@Param("answer") String answer,
                         @Param("optionType") Integer optionType,@Param("optionA") String optionA,@Param("optionB") String optionB,@Param("optionC") String optionC,
                         @Param("optionD") String optionD,@Param("explain") String explain,@Param("username") String username);
    ReviewInit initData(@Param("username") String username);
    Integer labelKnowed(@Param("id") Integer id);
    List<WdQuestion> getWdQuestions(@Param("subject") String subject);
}
