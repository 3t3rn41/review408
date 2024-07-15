package top.nysxzs.review408.demos.service;


import top.nysxzs.review408.demos.pojo.Token;
import top.nysxzs.review408.demos.pojo.User;

public interface UserService {
    //查询所有用户
    public User listUser(String username, String password);
    public Token checkToken(String username);
    void updateLoginTime(Long lastLoginTime, String username,String token);
    User checkRegister(String username);
    void toRegister(String username,String password);
    void updateTaskList(String taskList);
    void updateTargetId(Integer targetId);
}
