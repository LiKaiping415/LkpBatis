package com.lkp.demo.dao;

import com.lkp.batis.annotation.*;
import com.lkp.demo.entity.TestEntity;

@MyMapper
public interface TestDao {
    @MySelect(sql = "select * from test_table")
    TestEntity getAns();
    @MyUpdate(sql = "update test_table set value=15 where id=3")
    int update();
    @MyInsert(sql = "insert into test_table values 2 4")
    int insert();
    @MyDelete(sql = "delete from test_table where id=2")
    int delete();
}
