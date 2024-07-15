package top.nysxzs.review408.demos.pojo.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class insertReviewQuery {//subject,question,answer,optionType,optionA,optionB,optionC,optionD,explain
    private String subject;
    private String question;
    private String answer;
    private Integer optionType;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String explain;
}
