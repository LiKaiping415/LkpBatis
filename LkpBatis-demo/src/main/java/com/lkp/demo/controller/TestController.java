package com.lkp.demo.controller;

import com.lkp.batis.connection.Executor;
import com.lkp.demo.dao.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    Executor executor;
    @Autowired
    TestDao testDao;
    @RequestMapping(value = "/sql/select",method = RequestMethod.GET)
    public String executeSelect()  {
        return testDao.getAns().toString();
    }
    @RequestMapping(value = "/sql/update",method = RequestMethod.GET)
    public String executeUpdate() {
        return String.valueOf(testDao.update());
    }
    @RequestMapping(value = "/sql/insert",method = RequestMethod.GET)
    public String executeInsert(){
        return String.valueOf(testDao.insert());
    }
    @RequestMapping(value = "/sql/delete",method = RequestMethod.GET)
    public String executeDelete(){
        return String.valueOf(testDao.delete());
    }
}
