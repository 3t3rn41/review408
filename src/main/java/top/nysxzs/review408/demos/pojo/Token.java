package top.nysxzs.review408.demos.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    private Integer enScore;
    private Integer sqlScore;
    private Integer pcoScore;
    private Boolean isAdmin;
}
