package top.nysxzs.review408.demos.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class review408 {
    private Integer id;
    private String question;
    private String answer;
    private Integer optionType;
    private Integer wrongTimes;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String subject;
    private Long createTime;
    private String explaination;
    private Long nextReviewTime;
    private Integer isTodayDone;
    private Integer successiveRightDays;
    private String username;
    public void shuffleOptionsAndUpdateAnswer() {
        String[] options = {optionA, optionB, optionC, optionD};
        String lastAnswer = "";
        for(int i=0;i<options.length;i++)
        {
            switch (answer) {
                case "A" -> lastAnswer = optionA;
                case "B" -> lastAnswer = optionB;
                case "C" -> lastAnswer = optionC;
                case "D" -> lastAnswer = optionD;
            }
        }
        // 打乱数组
        for (int i = options.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));
            String temp = options[index];
            options[index] = options[i];
            options[i] = temp;
        }

        // 更新optionA, optionB, optionC, optionD的值
        this.optionA = options[0];
        this.optionB = options[1];
        this.optionC = options[2];
        this.optionD = options[3];

        // 更新answer为正确答案
        //this.answer = findCorrectAnswer(options,lastAnswer);
        this.answer="";
    }

    // 根据打乱后的选项数组找到正确答案
    private String findCorrectAnswer(String[] options,String lastAnswer) {
        String[] answers={"A","B","C","D"};
        for (int i=0;i<options.length;i++)
            if(options[i].equals(lastAnswer))
                return answers[i];
        return null; // 如果没有找到正确答案，返回null
    }
}
