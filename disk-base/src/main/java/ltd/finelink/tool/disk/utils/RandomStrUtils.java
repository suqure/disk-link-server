package ltd.finelink.tool.disk.utils;

import java.util.Random;

public class RandomStrUtils {
	
	private static char[] charList = {'0','1','2','3','4','5','6','7','8','9',
            'A','B','C','D','E','F','G','H','I','J','K','L','M',
            'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
            'a','b','c','d','e','f','g','h','i','j','k','l','m',
            'n','o','p','q','r','s','t','u','v','w','x','y','z'};

    //指定长度整数字符串
    public static String randomIntStr(int n){
    	return String.valueOf((int) ((Math.random() * 9 + 1) * Math.pow(10, n - 1)));
    }

    //获取指定长度的字符串大小
    public static String randomStr(int length){ 
        char[] rev = new char[length];
        Random f = new Random();
        for (int i = 0; i < length; i++)
        {
            rev[i] = charList[Math.abs(f.nextInt(127)) % charList.length];
        }
        return new String(rev);
    }

}
