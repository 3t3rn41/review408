package top.nysxzs.review408.demos.pojo.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Return {
    private Integer code;
    private String message;
    private Object data;
}
