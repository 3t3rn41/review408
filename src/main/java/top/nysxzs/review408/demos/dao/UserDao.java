package top.nysxzs.review408.demos.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.nysxzs.review408.demos.pojo.Token;
import top.nysxzs.review408.demos.pojo.User;

@Mapper//告诉springboot这是一个mybatis的mapper类
@Repository//将userdao交由spring容器管理
public interface UserDao {
    //查询所有用户
    User listUser(@Param("username") String username, @Param("password") String password);
    Token checkToken(@Param("username") String username);
    void updateLoginTime(@Param("lastLoginTime") Long lastLoginTime,@Param("username") String username,@Param("token") String token);
    User checkRegister(@Param("username") String username);
    void toRegister(@Param("username") String username,@Param("password") String password);
    void updateTaskList(@Param("taskList") String taskList);
    void updateTargetId(@Param("targetId") Integer targetId);
}
