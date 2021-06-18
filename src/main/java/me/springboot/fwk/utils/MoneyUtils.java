package me.springboot.fwk.utils;

import java.text.DecimalFormat;

/**
 * 功能：
 * 
 * @author wangbinbin email: 
 * @date 2008 8 4
 */
public class MoneyUtils {
	
	public static String toUpper(double amount){
		DecimalFormat df = new  DecimalFormat("#0.00");		
		return toUpper(df.format(amount));
	}
	/**
	 * 小写转换大写金额
	 */
	public static String toUpper(String amount) {		
		StringBuffer result = new StringBuffer("");
		String[] tmp = amount.replaceAll(",", "").split("\\.");
		String integer = tmp[0];
		final int LEN = integer.length();
		if (LEN > 12) {
			return amount;
		}
		for (int k = 12; k > LEN; k--) {
			integer = "0" + integer;
		}
		int part1 = Integer.parseInt(integer.substring(0, 4));
		int part2 = Integer.parseInt(integer.substring(4, 8));
		int part3 = Integer.parseInt(integer.substring(8, 12));

		if (part1 != 0) {
			result.append(parsePrefix(part1) + "亿");
		}

		if (part2 != 0) {
			if (part2 < 1000){
				if(result.length()>0)
				   result.append("零");
			}
			result.append(parsePrefix(part2) + "万");
		} else {
		  if(result.length()>0)
			result.append("零");
		}

		if (part3 != 0) {
			if (part3 < 1000){
			  if(result.length()>0)
				result.append("零");
			}
			result.append(parsePrefix(part3));
		}
		result.append("元");
		if (tmp.length == 2) {
			result.append(parseSuffix(tmp[1]));
		}
		return result.toString();
	}

	/**
	 * 
	 * 功能:解析四位整数转换为中文金额大写
	 * @param i
	 * @return
	 */
	private static String parsePrefix(int i) {
		String[] num = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
		String result = "";
		int tmp = i;
		if (tmp / 1000 != 0) {
			result += num[tmp / 1000] + "仟";
			tmp = tmp - (tmp / 1000) * 1000;
		}
		if (tmp / 100 != 0) {
			result += num[tmp / 100] + "佰";
			tmp = tmp - (tmp / 100) * 100;
		}
		if (tmp / 10 != 0) {
			result += num[tmp / 10] + "拾";
			tmp = tmp - (tmp / 10) * 10;
		}
		if (tmp != 0)
			result += num[tmp];
		return result;
	}

	/**
	 * 
	 * 功能:解析小数部分
	 * @param sStr
	 * @return
	 */
	private static String parseSuffix(String sStr) {
		String[] num = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "七", "捌", "玖" };
		String[] unit = { "角", "分" };
		String result = "";
		final int LEN = sStr.length();
		for (int i = 0; i < LEN; i++) {
			String tmp = sStr.substring(i, i + 1);
			int k = Integer.parseInt(tmp);
			result += num[k] + unit[i];
		}
		return result;
	}
	
	public static void main(String[] args) {
		String s = toUpper("122123456000.05");
	}
}
