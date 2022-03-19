package com.zzz.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.zzz.Util.JwtUtils;
import com.zzz.Util.Result;
import com.zzz.mapper.UserMapper;
import com.zzz.pojo.entity.Account;
import com.zzz.mapper.AccountMapper;
import com.zzz.pojo.entity.User;
import com.zzz.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 账户 服务实现类
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtUtils jwtUtils;

    @Override
    public Result addMoney(Long studentId, Double money,String password) {
        Account account = baseMapper.selectById(Long.valueOf(studentId));
        if (account.getStatus()==1){
            log.warn("账号 {} 被锁定，无法充值",studentId);
            return Result.fail("账户被锁定，无法充值");
        }
        if (!account.getPassword().equals(SecureUtil.md5(password))) {
            return Result.fail("密码错误，充值失败");
        }
        account.setMoney(account.getMoney()+money);
        int i = baseMapper.updateById(account);
        if (i==1){
            log.info("账号 {} 充值了 {} 元",studentId,money);
            return Result.success("充值成功");
        }
        return Result.fail("充值失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result transfer(Long buyer, Long seller, Double money) {
        Account buy = baseMapper.selectById(buyer);
        if(buy.getStatus() == 1){
            return Result.fail("账户被锁定，不允许付款操作");
        }
        if (buy.getMoney()<money){
            return Result.fail("账户余额不足，请先充值后付款");
        }
        buy.setMoney(buy.getMoney()-money);
        Account sell = baseMapper.selectById(seller);
        sell.setMoney(sell.getMoney()+money);
        int i = baseMapper.updateById(buy);
        int i2 = baseMapper.updateById(sell);
        if (i==1 && i2==1){
            return Result.success("转账成功");
        }
        return Result.fail("转账失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveAccount(Account account) {
        account.setCredit(1);
        account.setMoney(0D);
        account.setStatus(0);
        account.setDeleted(false);
        account.setPassword(SecureUtil.md5(account.getPassword()));
        int i = baseMapper.insert(account);
        User user = userMapper.selectById(account.getStudentId());
        user.setStatus(2);
        int i2 = userMapper.updateById(user);
        if (i == 1 && i2 == 1){
            return true;
        }
        return false;
    }

    @Override
    public boolean isLock(HttpServletRequest request) {
        Long studentId = jwtUtils.getStudentId(request);
        Account account = baseMapper.selectById(studentId);
        if (account.getStatus() == 1) {
            return true;
        }
        return false;
    }

    @Override
    public Result reduceMoney(Long studentId, Double money,String password) {
        Account account = baseMapper.selectById(studentId);
        if (account.getStatus()==1){
            log.warn("账号 {} 被锁定，无法提现",studentId);
            return Result.fail("账户被锁定，无法充值");
        }
        if (!account.getPassword().equals(SecureUtil.md5(password))) {
            return Result.fail("密码错误，提现失败");
        }
        Double money1 = account.getMoney();
        if (money1 < money){
            return Result.fail("账户余额不足，提现失败");
        }
        account.setMoney(money1-money);
        int update = baseMapper.updateById(account);
        if (update == 1){
            log.warn("账号 {} 提现成功，金额：{}",studentId,money);
            return Result.success("提现成功");
        }
        return Result.fail("提现失败");
    }


}
