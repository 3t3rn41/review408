package top.nysxzs.review408.demos.pojo.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserQuery {
    private String username;
    private String password;
    private String verifyCode;
}
