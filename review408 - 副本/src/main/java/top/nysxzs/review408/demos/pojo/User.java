package top.nysxzs.review408.demos.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String token;
    private Integer enScore;
    private Integer sqlScore;
    private Integer pcoScore;
    private Long lastLoginTime;
}
