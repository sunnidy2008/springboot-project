package me.springboot.fwk.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Pattern;


public class StringUtils extends org.apache.commons.lang3.StringUtils {

	public StringUtils() {
	}

	public static boolean isNotEmpty(String str) {
		if (str == null || str.equals(""))
			return false;
		return true;
	}

	public static boolean isEmpty(String str) {
		return !isNotEmpty(str);
	}

	/**
	 * 
	 * @param clObj
	 *            Collection
	 * @param separator
	 *            String
	 * @param limiter
	 *            String
	 * @return String
	 */
	public static String linkObjectWithoutRepeat(Collection clObj,
			String separator, String limiter) {
		Hashtable table = new Hashtable();
		Iterator it = clObj.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj != null && StringUtils.isNotEmpty(obj.toString())) {
				table.put(obj, obj);
			}
		}
		it = table.keySet().iterator();
		String rs = "";
		while (it.hasNext()) {
			if (!rs.equals(""))
				rs += separator + " ";
			rs += "" + limiter + it.next().toString().trim() + limiter;
		}
		// if(StringTool.isEmpty(rs)){
		// rs=limiter+limiter;
		// }
		return rs;
	}

	public static String str2ISO(String value) throws Exception {
		if (value == null || value.trim().equals(""))
			return value;
		else
			return new String(value.getBytes("GBK"), "iso8859-1");
	}

	public static String iso2GBK(String value) throws Exception {
		if (value == null || value.trim().equals(""))
			return value;
		else
			return new String(value.getBytes("iso8859-1"), "GBK");
	}

	public static String[] split(String value, String subStr) {
		ArrayList out = new ArrayList();
		boolean loop = true;
		int index = 0;
		int start = 0;
		int length = subStr.length();
		if (value == null || value.trim().equals("") || subStr == null
				|| subStr.trim().equals(""))
			return null;
		do {
			index = value.indexOf(subStr, start);
			if (index > -1) {
				out.add(value.substring(start, index));
				start = index + length;
			} else {
				out.add(value.substring(start, value.length()));
				loop = false;
			}
		} while (loop);
		String strs[] = new String[out.size()];
		return (String[]) (String[]) out.toArray(strs);
	}

	public static String parseStr(String value) {
		if (value == null)
			value = "";
		return value;
	}

	public static String parseStr(String value, String toValue) {
		if (value == null)
			return toValue;
		else
			return value;
	}

	public static byte[] convertPass(char ac[]) {
		byte abyte0[] = null;
		try {
			if (ac == null) {
				abyte0 = new byte[0];
			} else {
				ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
				OutputStreamWriter outputstreamwriter = new OutputStreamWriter(
						bytearrayoutputstream, "UTF-8");
				outputstreamwriter.write(ac);
				outputstreamwriter.flush();
				abyte0 = bytearrayoutputstream.toByteArray();
				outputstreamwriter.close();
			}
		} catch (UnsupportedEncodingException unsupportedencodingexception) {
		} catch (IOException ioexception) {
			throw new IllegalArgumentException("Error reading password "
					+ ioexception.toString());
		}
		return abyte0;
	}

	public static byte[] sha1_digest(byte abyte0[]) {
		try {
			MessageDigest sha1 = null;
			sha1 = MessageDigest.getInstance("SHA");
			return sha1.digest(abyte0);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] md5_digest(byte abyte0[]) {
		try {
			MessageDigest md5 = null;
			md5 = MessageDigest.getInstance("MD5");
			return md5.digest(abyte0);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String replace(String s, String s1, String s2) {
		StringBuffer stringbuffer = new StringBuffer();
		int i = s1.length();
		for (int j = 0; (j = s.indexOf(s1)) != -1;) {
			stringbuffer.append(s.substring(0, j) + s2);
			s = s.substring(j + i);
		}

		stringbuffer.append(s);
		return stringbuffer.toString();
	}

	public static String replaceTag(String value, String tagStr, String subStr) {
		StringBuffer strBuffer = new StringBuffer();
		int count = 1;
		int len = tagStr.length();
		if (value == null || tagStr == null || subStr == null)
			return value;
		for (int j = 0; (j = value.indexOf(tagStr)) != -1;) {
			if (count % 2 == 0)
				strBuffer.append(subStr);
			else
				strBuffer.append(value.substring(0, j));
			value = value.substring(j + len);
			count++;
		}

		strBuffer.append(value);
		return strBuffer.toString();
	}

	public static final String getTagContent(String value, String tag) {
		String out = "";
		if (value != null && !value.equals("")) {
			int indexNum = value.indexOf(tag);
			if (indexNum != -1) {
				value = value
						.substring(indexNum + tag.length(), value.length());
				indexNum = value.indexOf(tag);
				if (indexNum != -1)
					out = value.substring(0, indexNum);
				else
					out = value;
			}
		}
		return out;
	}

	public static final String formatTag(String tag) {
		String returnTag = tag;
		if (returnTag != null)
			returnTag = "<!--%" + returnTag + "%-->";
		return returnTag;
	}

	public static final String htmlEncode(String value) {
		StringBuffer out = new StringBuffer();
		if (value == null)
			return "";
		for (int i = 0; i < value.length(); i++)
			out.append(emitQuote(value.charAt(i)));

		return out.toString();
	}


	private static String emitQuote(char c) {
		String ls_Out = null;
		if (c == '&')
			ls_Out = "&amp;";
		else if (c == '"')
			ls_Out = "&quot;";
		else if (c == '<')
			ls_Out = "&lt;";
		else if (c == '>')
			ls_Out = "&gt;";
		else
			ls_Out = String.valueOf(c);
		return ls_Out;
	}

	public static Number toNumber(String value, Number number) {
		if (value == null || value.trim().equals(""))
			return number;
		try {
			NumberFormat.getInstance().parse(value);
		} catch (ParseException e) {
			return number;
		}
		return number;
	}

	public static Date toDate(String value, String pattern) {
		try {
			SimpleDateFormat format;
			if (pattern != null && !pattern.trim().equals(""))
				format = new SimpleDateFormat(pattern);
			else
				format = new SimpleDateFormat();
			return format.parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//**************************************************************************
	/**
	 */
	//**************************************************************************
	public static boolean isInt(String str) {
		if(str == null) return false;
		Pattern pattern = Pattern.compile("[-+]{0,1}[0-9]+");
		return pattern.matcher(str).matches();
	}
	
	/**
	 */
	public static int strToInt(String sInt) {
		if(!isInt(sInt)) {
			return 0;
		} else {
			return Integer.parseInt(sInt);
		}
	}

    public static Integer strToInteger(String sInt) {
    	Integer iRtn=0;
        if(sInt==null || "".equals(sInt))
          return 0;
        try{
          iRtn = new Integer(sInt);
        }catch(Exception e){}
        return iRtn;
    }
    /**
     * 
     * @param amount
     * @return
     */
    public static String money2Upper(double amount){
    	try{
    	  return MoneyUtils.toUpper(amount);
    	}catch(Exception e){}
    	return "";
    }
    
    public static String money2Upper(String amount){
    	try{
    	  if(amount==null ||"".equals(amount.trim()))
    		 return "";
    	  int idxComma = amount.indexOf(".");    	  
    	  if(idxComma>-1&& (idxComma+2)<amount.length()){
    		  amount = amount.substring(0,idxComma+3);
    	  }    	  
    	  return MoneyUtils.toUpper(amount);
    	}catch(Exception e){e.printStackTrace();}
    	return "";
    }
    public static void main(String[] arg){
    	System.err.println("==>"+money2Upper(102379384.9091));
    	System.err.println("==>"+money2Upper("384.19"));
    }
 }
