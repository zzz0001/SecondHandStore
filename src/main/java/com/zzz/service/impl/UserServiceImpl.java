package com.zzz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzz.Util.JwtUtils;
import com.zzz.Util.Result;
import com.zzz.mapper.AccountMapper;
import com.zzz.mapper.StoreMapper;
import com.zzz.mapper.UserMapper;
import com.zzz.pojo.entity.Account;
import com.zzz.pojo.entity.Store;
import com.zzz.pojo.entity.User;
import com.zzz.service.ImageService;
import com.zzz.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private ImageService imageService;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private JwtUtils jwtUtils;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result setPicture(MultipartFile file, HttpServletRequest request) {
        Long studentId = jwtUtils.getStudentId(request);
        User user = baseMapper.selectById(studentId);
        // 若头像不是初始头像，则删除原始头像
        if (!user.getPicture().equals("/user/123abc.jpeg")){
            String picture = "D:/IDEA workspace/secondhandstorevue/public"+user.getPicture();
            boolean delete = new File(picture).delete();
            if(!delete){
                return Result.fail("头像修改失败");
            }
        }
        String path = imageService.uploadFile(file);
        if (path == null){
            return Result.fail("头像设置失败");
        }
        user.setPicture(path);
        int update = baseMapper.updateById(user);
        if (update == 1){
            return Result.success("头像上传成功");
        }
        return Result.fail("头像上传失败");
    }

    @Override
    public Result updateStatus(Long userId) {
        User user = baseMapper.selectById(userId);
        Integer status = user.getStatus();
        if (status == 1){
            user.setStatus(user.getLastStatus());
        }else {
            user.setLastStatus(user.getStatus());
            user.setStatus(1);
        }
        int i = baseMapper.updateById(user);
        if (i==1){
            return Result.success("用户状态修改成功");
        }
        return Result.fail("用户状态修改失败");
    }

    @Override
    public Result getUsersAndStore(Integer page) {
        Page<User> userPage = new Page<>(page, 10);
        Page<User> users = baseMapper.selectPage(userPage, new QueryWrapper<User>().orderByDesc("student_id"));
        List<User> userList = users.getRecords();
        ArrayList<Object> usersResult = new ArrayList<>();
        userList.forEach(user -> {
            Long studentId = user.getStudentId();
            Store store = storeMapper.selectOne(new QueryWrapper<Store>().eq("student_id", studentId));
            Account account = accountMapper.selectById(studentId);
            HashMap<String, Object> map = new HashMap<>();
            map.put("user",user);
            map.put("store",store);
            map.put("account",account);
            usersResult.add(map);
        });
        userPage.setRecords(null);
        HashMap<String, Object> result = new HashMap<>();
        result.put("data",usersResult);
        result.put("page",userPage);
        return Result.success(result);
    }

    @Override
    public Result getByStudentId(Long studentId) {
        User user = baseMapper.selectById(studentId);
        if (user == null) {
            return Result.fail("不存在该用户");
        }
        Store store = storeMapper.selectOne(new QueryWrapper<Store>().eq("student_id", studentId));
        Account account = accountMapper.selectById(studentId);
        HashMap<String, Object> result = new HashMap<>();
        result.put("user",user);
        result.put("store",store);
        result.put("account",account);
        return Result.success(result);
    }

}

