package com.zzz.service;

import com.zzz.Util.Result;
import com.zzz.pojo.entity.Expense;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 花销表 服务类
 * </p>
 *
 * @author zzz
 * @since 2022-03-29
 */
public interface ExpenseService extends IService<Expense> {

    Result getExpense(Long studentId);

    Result getExpensePage(Long studentId, Integer page);
}
