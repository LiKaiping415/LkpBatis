package com.lkp.batis;

import com.lkp.batis.connection.Executor;
import com.lkp.batis.connection.Encoder;
import com.lkp.batis.connection.Packager;
import com.lkp.batis.connection.RoundTripper;
import com.lkp.batis.connection.Transporter;
import java.net.Socket;

public class Test {
    public static void main(String[] args) throws Exception {
        Socket socket=new Socket("127.0.0.1",9999);
        Encoder encoder = new Encoder();
        Transporter transporter = new Transporter("a","0");
        Packager packager = new Packager(transporter, encoder);
        RoundTripper roundTripper = new RoundTripper(packager);
        Executor executor = new Executor(roundTripper);
        String response = executor.execute("create table test_table id int32,value int32 (index id)");
        System.out.println(response);/*😊🤪*/
    }
}
