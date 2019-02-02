package cn.cdyxtech.lab.util;

import com.emin.base.util.ThreadLocalUtil;


public class JWTThreadLocalUtil extends ThreadLocalUtil{

    private static final ThreadLocal<String> jwtLocal = new ThreadLocal();
    private static final ThreadLocal<Long> userIdLocal = new ThreadLocal();
    private static final ThreadLocal<Long> ecmLocal = new ThreadLocal();
    private static final ThreadLocal<Long> rootEcmIdLocal = new ThreadLocal();

    public static String getJwt() {
        return jwtLocal.get();
    }
    public static void setJwt(String jwt){
        jwtLocal.set(jwt);
    }

    public static Long getUserId(){
        return userIdLocal.get();
    }
    public static void setUserId(Long userId){
        userIdLocal.set(userId);
    }

    public static Long getEcmId(){
        return ecmLocal.get();
    }
    public static void setEcmId(Long ecmId){
        ecmLocal.set(ecmId);
    }
    
    public static Long getRootEcmId(){
        return ecmLocal.get();
    }
    public static void setRootEcmId(Long rootEcmId){
        ecmLocal.set(rootEcmId);
    }

    public static Boolean isPlatformUser(){
        return getEcmId()==null || getEcmId().longValue()==0l;
    }

}
