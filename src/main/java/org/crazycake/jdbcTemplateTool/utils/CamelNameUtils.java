package org.crazycake.jdbcTemplateTool.utils;


public class CamelNameUtils {
	
	/**
	 * 将驼峰命名转为下划线命名
	 * @return
	 */
	public static String camel2underscore(String camelName){
		//先把第一个字母大写
		camelName = capitalize(camelName);
		
		String regex = "([A-Z][a-z]+)";
		String replacement = "$1_";

		String underscoreName = camelName.replaceAll(regex, replacement);
		//output: Pur_Order_Id_ 接下来把最后一个_去掉，然后全部改小写
		
		underscoreName = underscoreName.toLowerCase().substring(0, underscoreName.length()-1);
		
		return underscoreName;
	}
	
	public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuilder(strLen)
            .append(Character.toTitleCase(str.charAt(0)))
            .append(str.substring(1))
            .toString();
    }
}
