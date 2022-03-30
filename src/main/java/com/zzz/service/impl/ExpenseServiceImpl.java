package com.zzz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzz.Util.Result;
import com.zzz.mapper.ExpenseMapper;
import com.zzz.pojo.entity.Expense;
import com.zzz.service.ExpenseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 花销表 服务实现类
 * </p>
 *
 * @author zzz
 * @since 2022-03-29
 */
@Service
public class ExpenseServiceImpl extends ServiceImpl<ExpenseMapper, Expense> implements ExpenseService {

    @Override
    public Result getExpense(Long studentId) {
        QueryWrapper<Expense> wrapper = new QueryWrapper<Expense>().eq("student_id", studentId).orderByDesc("expense_id");
        List<Expense> expenses = baseMapper.selectList(wrapper);
        return Result.success(expenses);
    }

    @Override
    public Result getExpensePage(Long studentId, Integer page) {
        Page<Expense> expensePage = new Page<>(page, 10);
        QueryWrapper<Expense> wrapper = new QueryWrapper<Expense>().eq("student_id", studentId).orderByDesc("expense_id");
        Page<Expense> result = baseMapper.selectPage(expensePage, wrapper);
        return Result.success(result);
    }
}
