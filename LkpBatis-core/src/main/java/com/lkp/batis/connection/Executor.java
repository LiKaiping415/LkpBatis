package com.lkp.batis.connection;


public class Executor {
    private RoundTripper rt;

    public Executor(RoundTripper rt) {
        this.rt = rt;
    }

    public String execute(String sql) throws Exception {
        Package aPackage = new Package(sql.getBytes(), null);
        Package resPackage = rt.roundTrip(aPackage);
        if (resPackage.getErr()!=null){
            throw resPackage.getErr();
        }
        return new String(resPackage.getData());
    }

    public void close(){
        try {
            rt.close();
        }catch (Exception e){

        }
    }
}
