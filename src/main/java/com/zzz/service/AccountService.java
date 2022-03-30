package com.zzz.service;

import com.zzz.Util.Result;
import com.zzz.pojo.entity.Account;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 账户 服务类
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
public interface AccountService extends IService<Account> {

    Result transfer(Long buyer, Long seller, Double money);

    boolean saveAccount(Account account);

    boolean isLock(HttpServletRequest request);

    boolean isLock(Long studentId);

    Result addMoney(Long studentId, Double money,String password);

    Result reduceMoney(Long studentId, Double money,String password);
}
