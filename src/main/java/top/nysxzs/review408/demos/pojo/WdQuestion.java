package top.nysxzs.review408.demos.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WdQuestion {
    private int id;
    private int index;
    private String question;
    private String answer;
    private int optionType;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String subject;
    private String chapter;
}
