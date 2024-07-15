package top.nysxzs.review408.demos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.nysxzs.review408.demos.dao.UserDao;
import top.nysxzs.review408.demos.pojo.Token;
import top.nysxzs.review408.demos.pojo.User;

@Service//交由spring容器管理
public class UserServiceImpl implements UserService{
    @Autowired
    private UserDao userDao;
    @Override
    public User listUser(String username, String password){
        return  userDao.listUser(username,password);
    }
    @Override
    public Token checkToken(String username){
        return userDao.checkToken(username);
    };
    @Override
    public void updateLoginTime(Long lastLoginTime, String username,String token){
        userDao.updateLoginTime(lastLoginTime,username,token);
    }
    @Override
    public User checkRegister(String username){
        return userDao.checkRegister(username);
    }
    @Override
    public void toRegister(String username,String password){
        userDao.toRegister(username,password);
    }
    @Override
    public void updateTaskList(String taskList){
        userDao.updateTaskList(taskList);
    }
    @Override
    public void updateTargetId(Integer targetId){
        userDao.updateTargetId(targetId);
    }
}
