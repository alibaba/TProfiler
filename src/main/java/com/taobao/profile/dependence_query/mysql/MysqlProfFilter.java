package com.taobao.profile.dependence_query.mysql;

import java.util.HashSet;
import java.util.Set;

import com.taobao.profile.config.ProfFilter;

/**
 * @author weigao
 * @since 15/6/1
 */
public class MysqlProfFilter extends ProfFilter {

    public final static String MysqlPath = "com/mysql/jdbc/ConnectionImpl";

    protected static Set<String> includeMysqlPackage = new HashSet<String>();
    
    private static MysqlProfFilter instance = new MysqlProfFilter();
    protected MysqlProfFilter() {
    	includeMysqlPackage.add(MysqlPath.toLowerCase());
    }

    public static MysqlProfFilter getInstance(){
        return instance;
    }
    
    /**
     * 为mysql重写isNeedInject方法
     * 
	 * 是否是需要注入的类
	 * 
	 * @param className
	 * @return
	 */
	public static boolean isNeedInjectMysql(String className) {
		String icaseName = className.toLowerCase().replace('.', '/');
		for (String v : includeMysqlPackage) {
			if (icaseName.startsWith(v)) {
				return true;
			}
		}
		return false;
	}
}
