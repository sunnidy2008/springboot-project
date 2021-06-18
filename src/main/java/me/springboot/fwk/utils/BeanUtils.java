package me.springboot.fwk.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;


/**
 * 功能说明：提供Bean的反射操作方法 特殊约定：如果Bean存在基本类型的名为"value"的属性，该属性为ValueAttibute
 * <p>
 * Title: 数据访问引擎
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangdongxiao
 * @version 1.2
 * 
 * 版本1.1说明：增加对于泛型的反射
 * 
 * getAttributeParameterType: 属性的参数类型 getBeanGernaricParmeterType： Bean生成类的参数类型
 * 
 * 版本1.2说明：增加JSON字符串的处理 修改日期：2007-01-07 基于JSon-lib(net.sf)实现 方法： beanToJSon
 * jSonToBean
 * 
 * 版本1.2.5说明：扩展对Class属性的方法 修改日期：2007-01-14
 * 
 * 方法： public static String[] getAtrributeNames(Class c,Class superClass) public
 * static Class getAttributeType(Class c,Class superClass,String name)
 */

@Slf4j
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {
	// 基本类型
	public static String DATA_BASE_TYPE = "int;java.lang.Integer;short;java.lang.Short;float;java.lang.Float;double;java.lang.Double;char;java.lang.Char;long;java.lang.Long;boolean;java.lang.Boolean;Sjava.lang.String;byte;java.lang.Byte;";
	// 特殊的Bean属性，名称为"value"
	public static String ATTRIBUTE_VALUE = "value";
	private final static String SUPER_NAME = "com.isoftstone.";



	public static Object invokeMethod(Object bean, String methodName,
			Object[] parmObj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method[] mtds = bean.getClass().getMethods();
		for (int i = 0; i < mtds.length; i++) {
			if (mtds[i].getName().equals(methodName)) {
				Type[] type = mtds[i].getGenericParameterTypes();
				return mtds[i].invoke(bean, parmObj);
				// type[i].getClass().getName().equals(anObject)
			}
		}
		return null;
	}

	/**
	 * 获取属性表达式的第一个属性结点名称
	 * 
	 * @param expr
	 *            String
	 * @return String
	 */
	public static String getExprFirstAttrName(String expr) {
		String[] tmp = StringUtils.split(expr, ".");
		return tmp[0];
	}

	public static String getExprEndAttr(String expr) {
		String[] tmp = StringUtils.split(expr, ".");
		if (tmp.length > 1) {
			return tmp[tmp.length - 1];
		}
		return expr;
	}

	public static boolean isBeanExpr(String expr) {
		return StringUtils.split(expr, ".").length > 1;
	}

	public static void setBeanAttrBySampleExpr(Object varBean, String varExpr,
			Object calBean, String calExpr) throws Exception {
		String msg = "变量对象：" + varBean + " 变量表达式： " + varExpr + " 计算对象："
				+ calBean + "  计算表达式：" + calExpr;
		// 获取计算表达式结果对象
		if (calExpr != null && (!calExpr.equals(""))) {
			calBean = BeanUtils.getSmapleExpressVlaue(calBean, calExpr);
		}
		// 变量表达式为空
		if (varExpr == null || varExpr.equals("")) {
			if (varBean.getClass().isInstance(calBean)) {
				varBean = calBean;
			} else {
				throw new Exception("参数表达式赋值错误,类型不匹配" + msg);
			}
		}

		String[] tmp = StringUtils.split(varExpr, ".");
		// 获取叶子结点属性名称
		String varEntAttr = tmp[tmp.length - 1];
		// 获取中间结点属性路径
		String varPath = "";
		for (int i = 0; i < tmp.length - 1; i++) {
			if (i != 0 && i != (tmp.length - 1)) {
				varPath = varPath + ".";
			}
			varPath += tmp[i];
		}
		Object subBean = varBean;
		if (!varPath.equals("")) {
			subBean = BeanUtils.getSmapleExpressVlaue(varBean, varPath);
		}

		Class c = BeanUtils.getAttributeType(subBean, varEntAttr);
		if (c.isInstance(calBean)) {// 设置属性值
			BeanUtils.setAttributeValue(subBean, varEntAttr, calBean);

		} else if (calBean instanceof String) {// 转换String值
			BeanUtils.setAttributeByString(subBean, varEntAttr, (String) calBean);
		} else {
			// String msg=" " +
			throw new Exception("参数表达式赋值错误,类型不匹配" + msg);

		}
	}

	/**
	 * 一个类与它的祖先类之间的申明的属性，包括给类与祖先类所申明的属性
	 * 
	 * @param c
	 *            ：子类
	 * @param superClass
	 *            ：祖先类
	 * @return ：申明的属性名称的集合
	 * @throws Exception
	 */
	public static String[] getAtrributeNames(Class c, Class superClass)
			throws Exception {
		Collection cl = new ArrayList();
		Class p = c;
		String names[] = null;
		while (BeanUtils.isSubClass(p, superClass) || p == superClass) {
			names = getAtrributeNames(p);
			for (String attr : names) {
				cl.add(attr);
			}
			p = p.getSuperclass();
		}
		names = new String[cl.size()];
		cl.toArray(names);

		return names;
	}

	/**
	 * 获得Bean的所有属性名称
	 * 
	 * @param bean
	 *            Object : Bean实例
	 * @return String[] : 属性名称数组
	 * @throws Exception
	 */
	public static String[] getAtrributeNames(Object bean) throws Exception {
		return getAtrributeNames(bean.getClass());
	}

	/**
	 * 获取表达式的值 规则：
	 * 
	 * @param obj
	 *            Object 数据对象
	 * @param expr
	 *            String 实体对象表达式
	 * @return Object
	 * @throws Exception
	 *             说明： 例如： userVo@acd123 对象实例， expr="dept.name" 表达式解释：
	 *             userVo里的属性为: dept(DeptVo类型)，DeptVo的属性 name
	 */
	public static Object getSmapleExpressVlaue(Object obj, String expr)
			throws Exception {
		String[] attr = StringUtils.split(expr, ".");

		for (int i = 0; i < attr.length; i++) {
			if (obj == null)
				return null;
			if (obj instanceof Map) {
				obj = ((Map) obj).get(attr[i]);
			}
			obj = BeanUtils.getAttributeValue(obj, attr[i]);
		}
		return obj;
	}

	/**
	 * 断言Bean属性的可访问属性
	 * 
	 * @param bean
	 *            Object : Bean实例对象
	 * @param attrName
	 *            String : 属性名称
	 * @return boolean : 如果该属性为public或存在getter方法，则返回true,否则返回false;
	 */
	public static boolean canAccess(Object bean, String attrName) {
		if (bean == null || !StringUtils.isNotEmpty(attrName))
			return false;
		Object value = "";
		try {
			Field field = bean.getClass().getField(attrName);
			value = field.get(bean).toString();
			return true;
		} catch (Exception e) {
			Method mtd = getGetterMethod(bean, attrName);
			if (mtd == null) {
				mtd = getIsMethod(bean, attrName);
				if (mtd != null)
					return true;

				return false;
			}
		}
		return true;
	}

	/**
	 * 获取Bean的属性值
	 * 
	 * @param bean
	 *            Object : bean实例对象
	 * @param attrName
	 *            String : 属性名称
	 * @return Object ：属性值
	 * @throws Exception
	 */
	public static Object getAttributeValue(Object bean, String attrName)
			throws Exception {
		if (!StringUtils.isNotEmpty(attrName))
			return null;

		Object value = "";
		try {// 按public属性读取
			Field field = bean.getClass().getField(attrName);
			value = field.get(bean).toString();
		} catch (Exception e) {// 读取失败，获取Getter方法，读取属性值
			Method mtd = null;
			if (BeanUtils.getAttributeType(bean.getClass(), attrName) == boolean.class) {
				mtd = getIsMethod(bean, attrName);
			}
			if (mtd == null) {
				mtd = getGetterMethod(bean, attrName);
			}
			if (mtd == null) {
				String msg = "反射获取Bean的属性值失败！　" + bean.getClass().getName()
						+ " 的属性“" + attrName + "” 不是 Public类型，也没有getter方法："
						+ createGetterName(attrName);
				throw new Exception(msg);
			}
			value = mtd.invoke(bean, null);
		}
		return value;
	}

	/**
	 * 断言类之间的继承关系
	 * 
	 * @param base
	 * @param parent
	 * @return
	 */
	public static boolean isSubClass(Class base, Class parent) {
		return parent.isAssignableFrom(base);
	}

	public static String getAttributeValueAsString(Object bean, String attrName) throws Exception {
		if (!StringUtils.isNotEmpty(attrName))
			return null;
		Object value = getAttributeValue(bean, attrName);
		//修复当后台往前台压数据时，由于没有精度，导致数据丢失的问题
		if(value instanceof Number) {
			java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			nf.setMaximumIntegerDigits(30);
			nf.setMaximumFractionDigits(8);
			//nf.setMinimumFractionDigits(2);
			return nf.format(value);
		}
		return object2Str(value,null);
	}

	public static String object2Str(Object value) {
		return object2Str(value,null);
	}
	
	public static String object2Str(Object value,String pre) {
		if (value == null)
			return "";
		if (value instanceof java.util.Date) {
			return DateUtils.dateToStr((Date) value);
		} else if (value instanceof Number) {
			try {
				DecimalFormat format = (DecimalFormat) NumberFormat.getPercentInstance();
				
				//修复之前Integer.parse(pre)抛出异常而未处理的BUG
				//如果是Double类型，返回String.valueOf(value)则可能返回科学计数法的数字
				if(pre != null && !"".equals(pre)) {
					int p = StringUtils.strToInt(pre);
					String pp = "";//pp = "####"
					if (pre != null && !pre.equals("")) {
						for (int i=0;i<p;i++) {
							pp += "0";//pp += "#";
						}
					}
					//2010年12月2日 解决Double类型 precision="0"时返回 值带小数点问题
					String pattern = "";
					if (pp.equals("")) {
						pattern = "0";
					} else {
						pattern = "0." + pp;
					}
					
					//2010年12月24日 解决四舍五入的问题，之前由于在内存中保存数据1.125时由于二进制精度问题成为1.124999999
					BigDecimal num = new BigDecimal(String.valueOf(value)).setScale(p, BigDecimal.ROUND_HALF_UP);
					format.applyPattern(pattern);
					return format.format(num);
				} else {//dengfc 2011-06-03 如果在DW中没有设置precision，或者precision设置为空，则对数值不做精度截取处理，尽量还原其原始值
					java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
					nf.setGroupingUsed(false);
					nf.setMaximumIntegerDigits(30);
					nf.setMaximumFractionDigits(8);
					return nf.format(value);
				}
			} catch (Exception e) {
				return String.valueOf(value);
			}
		}
		return value.toString();
	}
	
	//**************************************************************************
	/**
	 * 根据精度，将数值格式化为字符串 <br><pre>
	 * 方法number2StrByPre的详细说明 <br>
	 * 编写者：ferris.deng
	 * 创建时间：2011-6-3 下午01:08:21 </pre>
	 * @return String 说明
	 * @throws
	 */
	//**************************************************************************
	public static String number2StrByPre(Object number, String pre) {
		if (number == null)
			return "";
		if (number instanceof Number) {
			try {
				if(pre != null && !"".equals(pre)) { //如果有设置precision精度属性
					DecimalFormat format = (DecimalFormat) NumberFormat.getPercentInstance();
					String pp = "";
					String pattern = "";
					
					int p = StringUtils.strToInt(pre);
					for (int i=0;i<p;i++) {
						pp += "0";
					}
					if (pp.equals("")) {
						pattern = "0";
					} else {
						pattern = "0." + pp;
					}
					
					//解决之前由于在内存中保存数据1.125时由于二进制精度问题成为1.124999999
					BigDecimal num = new BigDecimal(String.valueOf(number)).setScale(p, BigDecimal.ROUND_HALF_UP);
					format.applyPattern(pattern);
					return format.format(num);
				} else {//dengfc 2011-06-03 如果在DW中没有设置precision，或者precision设置为空，则对数值不做精度截取处理，尽量还原其原始值
					java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
					nf.setGroupingUsed(false);
					nf.setMaximumIntegerDigits(30);
					nf.setMaximumFractionDigits(8);
					return nf.format(number);
				}
			} catch (Exception e) {
				return String.valueOf(number);
			}
		}
		return number.toString();
	}

	public static Object String2BaseTypeData(String value, Class type)
			throws Exception {

		throw new Exception("没有实现的方法");
	}

	/**
	 * 断言类存在为参数构造函数
	 * 
	 * @param beanClass
	 * @param parmType
	 * @return
	 */
	public static boolean hasCons(Class beanClass, Class[] parmType) {
		try {
			beanClass.getConstructor(parmType);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 设置Bean的属性值
	 *
	 */
	public static void setAttributeValue(Object bean, String attrName,
			Object beanValue) throws Exception {

		if (beanValue == null || bean == null)
			return;
		if (StringUtils.isEmpty(attrName))
			return;
		if (!BeanUtils.canAccess(bean, attrName))
			return;

		Class c = BeanUtils.getAttributeType(bean, attrName);
		Object value = null;

		if (beanValue instanceof java.lang.Number && c != beanValue.getClass()) {
			value = ((java.lang.Number) beanValue).toString();
			BeanUtils.setAttributeByString(bean, attrName, (String) value);
			return;
		}
		if (c != beanValue.getClass()) {
			if (BeanUtils.isBeseType(c)
					&& BeanUtils.isBeseType(beanValue.getClass())) {
				value = beanValue.toString();
				BeanUtils.setAttributeByString(bean, attrName, (String) value);
				return;

			} else {
				value = beanValue;
			}

		}

		else {
			value = beanValue;
		}

		invokeSetterAttr(bean, attrName, value);
	}

	private static void invokeSetterAttr(Object bean, String attrName,
			Object attrValue) throws Exception {
		Object value = attrValue;
		try {
			Class c = BeanUtils.getAttributeType(bean.getClass(), attrName);
			if (c == String.class) {
				if (value != null)
					value = attrValue.toString();
			}

			Field field = bean.getClass().getField(attrName);
			field.set(bean, value);
			value = field.get(bean).toString();
		} catch (Exception e) {
			Method mtd = getSetterMethod(bean, attrName);
			if (mtd == null) {
				String msg = "反射获取Bean的属性值失败！　" + bean.getClass().getName()
						+ " 的属性“" + attrName + "” 不是 Public类型，也没有setter方法："
						+ createGetterName(attrName);
				throw new Exception(msg);
			}
			try {
				mtd.invoke(bean, new Object[] { value });
			} catch (Exception ex) {
				String msg = "反射获取Bean的属性值失败！　" + bean.getClass().getName()
						+ " 属性名: " + attrName + " 值： " + value;

				throw new Exception(msg, ex);
			}
		}

	}

	public static Object createBeseTypeInstance(Class c, String value)
			throws Exception {
		BeanUtils tool = new BeanUtils();
		if (c == String.class)
			return value;

		Object ts = tool.getBaseTypeTranslator(c);
		Class[] cs = new Class[1];
		cs[0] = String.class;

		Method md = ts.getClass().getMethod("getValueByString", cs);
		Object ps[] = new Object[1];
		ps[0] = value;
		Object data = null;
		try {

			data = md.invoke(ts, ps);

		} catch (Exception ex) {
			throw new Exception("字符串生成基本数据类型对象实例失败  , 数据类型=" + c.getName()
					+ " 值=" + value.toString(), ex);
		}
		return data;
	}

	/**
	 * 将字符串的值赋予Bean的指定属性
	 * 
	 * @param bean
	 *            Object ： Bean对象是实例
	 * @param attrName
	 *            String : 属性名称
	 * @param value
	 *            Object ： 属性值
	 * @throws Exception
	 */
	public static void setAttributeByString(Object bean, String attrName, String value) throws Exception {
		if (bean == null) {
			throw new Exception("Bean属性赋值失败 Bean 为空");
		}
		if (StringUtils.isEmpty(attrName)) {
			throw new Exception("属性名称为空！");
		}

		Class<?> c = BeanUtils.getAttributeType(bean, attrName);
		if (c == null) {
			throw new Exception("Bean " + bean.getClass().getName() + " 不存在属性 " + attrName);
		}
		String msg = "int;short;float;double;char;long;boolean;byte;Long;";
		if(msg.indexOf(c.getName()) >= 0) {
			//ferris.deng 修改 如果参数为空
			if("".equals(value)) {
				value = "0";
			}
			BeanUtils tool = new BeanUtils();
			Object ts = tool.getBaseTypeTranslator(c);
			Class<?>[] cs = new Class[1];
			cs[0] = String.class;

			Method md = ts.getClass().getMethod("getValueByString", cs);//调用自定义的私有类的getValueByString方法
			Object ps[] = new Object[1];
			ps[0] = value;

			try {
				Object data = md.invoke(ts, ps);
				invokeSetterAttr(bean, attrName, data);
			} catch (Exception ex) {
				throw new Exception("Bean属性赋值失败 Bean =" + bean.toString() + " 属性名称=" + attrName + ", 数据类型=" + c.getName() + " 值=" + value.toString(), ex);
			}
		} else {
			Object ov = null;
			if (c == Date.class || c == java.sql.Time.class || c == java.sql.Timestamp.class) {
				//ferris.deng 修改 如果参数为空
				try {
					ov = DateUtils.strToDate(value.toString());
				}catch(Exception e){
					ov = null;
				}
			} else {
				Class cp[] = new Class[1];
				cp[0] = String.class;

				Constructor cn = c.getConstructor(cp);
				Object ps[] = new Object[1];
				ps[0] = value;
				try {
					ov = cn.newInstance(ps);
				} catch(Exception e) {
					ov = null;
				}
			}

			try {
				invokeSetterAttr(bean, attrName, ov);
			} catch (Exception ex) {
				throw new Exception("Bean属性赋值失败 Bean =" + bean.toString() + " Attribute=" + attrName + " value=" + value.toString(), ex);
			}
		}
		/*
		String msg = "int;short;float;double;char;long;boolean;byte;Long;";

		// 为基本数据类型赋值
		if (msg.indexOf(c.getName()) >= 0) {
			//ferris.deng 修改 如果参数为空
			if("".equals(value)) {
				value = "0";
			}
			BeanUtils tool = new BeanUtils();
			Object ts = tool.getBaseTypeTranslator(c);
			Class[] cs = new Class[1];
			cs[0] = String.class;

			Method md = ts.getClass().getMethod("getValueByString", cs);
			Object ps[] = new Object[1];
			ps[0] = value;

			try {

				Object data = md.invoke(ts, ps);
				// tool.setAttributeValue(bean, attrName, data);
				invokeSetterAttr(bean, attrName, data);
			} catch (Exception ex) {
				throw new Exception("Bean属性赋值失败 Bean =" + bean.toString()
						+ " 属性名称=" + attrName + ", 数据类型=" + c.getName() + " 值="
						+ value.toString(), ex);
			}
		} else {
			Object ov = null;
			if (c == Date.class || c == java.sql.Time.class
					|| c == java.sql.Timestamp.class) {
				
				//ferris.deng 修改 如果参数为空
				try{
					ov = DateUtils.strToDate(value.toString());
				}catch(Exception e){
					ov = null;
				}
			} else {

				Class cp[] = new Class[1];
				cp[0] = String.class;

				Constructor cn = c.getConstructor(cp);
				Object ps[] = new Object[1];
				ps[0] = value;
				try{
					ov = cn.newInstance(ps);
				}catch(Exception e){
					ov = null;
				}
			}

			try {
				// setAttributeValue(bean, attrName, ov);
				invokeSetterAttr(bean, attrName, ov);
			} catch (Exception ex) {
				throw new Exception("Bean属性赋值失败 Bean =" + bean.toString()
						+ " Attribute=" + attrName + " value="
						+ value.toString(), ex);
			}

		}
		*/
	}

	/**
	 * 
	 * 
	 * @param dateValue
	 *            String
	 * @return String
	 */
	private static String getDateFormat(String dateValue) {
		dateValue = dateValue.trim();
		String tmp[] = StringUtils.split(dateValue, " ");
		if (tmp.length == 1)
			return DateUtils.DATE_CONSTANT;
		else {
			return DateUtils.DATA_TIME_CONSTANT;
		}
	}

	/**
	 * 获取Bean对象实例的Getter方法
	 * 
	 * @param bean
	 *            Object
	 * @param attrName
	 *            String
	 * @return Method
	 */
	public static Method getGetterMethod(Object bean, String attrName) {
		Method[] mtds = bean.getClass().getMethods();
		String[] mtdName = createGetterName(attrName);
		for (int i = 0; i < mtds.length; i++) {
			if (mtds[i].getName().equals(mtdName[0])||mtds[i].getName().equals(mtdName[1]))
				return mtds[i];
		}
		return null;
	}

	public static Method getIsMethod(Object bean, String attrName) {
		Method[] mtds = bean.getClass().getMethods();
		String mtdName = createIsName(attrName);
		for (int i = 0; i < mtds.length; i++) {
			if (mtds[i].getName().equals(mtdName))
				return mtds[i];
		}
		return null;
	}

	/**
	 * 获取Bean实例的Setter方法
	 * 
	 * @param bean
	 *            Object
	 * @param attrName
	 *            String
	 * @return Method
	 */
	public static Method getSetterMethod(Object bean, String attrName) {
		Method[] mtds = bean.getClass().getMethods();
		String mtdName[] = createSetterName(attrName);
		for (int i = 0; i < mtds.length; i++) {
			if (mtds[i].getName().equals(mtdName[0]) || mtds[i].getName().equals(mtdName[1]))
				return mtds[i];
		}
		return null;
	}

	/**
	 * 创建Bean的属性的Getter方法字符传
	 * 
	 * @param attrName
	 *            String
	 * @return String
	 */
	private static String[] createGetterName(String attrName) {
		String first = ("" + attrName.charAt(0)).toUpperCase();
		String getter = "get" + first
				+ attrName.substring(1, attrName.length());
		return new String[]{getter,"get"+attrName};
	}

	private static String createIsName(String attrName) {
		String first = ("" + attrName.charAt(0)).toUpperCase();
		String getter = "is" + first + attrName.substring(1, attrName.length());
		return getter;
	}

	/**
	 * 创建Bean的属性的Setter方法字符传
	 * 
	 * @param attrName
	 *            String
	 * @return String
	 */
	private static String[] createSetterName(String attrName) {
		String first = ("" + attrName.charAt(0)).toUpperCase();
		String setter = "set" + first
				+ attrName.substring(1, attrName.length());
		return new String[]{setter,"set"+attrName};
	}

	/**
	 * 给集合中的所有对象的属性赋值
	 * 
	 * @param cl
	 *            Collection 对象集合
	 * @param attr
	 *            String 属性名称
	 * @param value
	 *            Object 值对象
	 * @throws Exception
	 */
	public static void setCollectionBeanAttr(Collection cl, String attr,
			Object value) throws Exception {
		if (cl == null || StringUtils.isEmpty(attr))
			return;

		Iterator it = cl.iterator();
		while (it.hasNext()) {
			setAttributeValue(it.next(), attr, value);
		}
	}

	/**
	 * 断言对象是基本数据类型：基本数据烈性见DATA_BASE_TYPE
	 * 
	 * @param attr
	 *            Object 对象实例
	 * @return boolean 是基本基本数据类型返回true,否则返回false
	 */
	public static boolean isBeseType(Object attr) {
		return isBeseType(attr.getClass());
	}

	/**
	 * 断言对象是基本数据类型：基本数据烈性见DATA_BASE_TYPE
	 * 
	 * @Class c 对象类型
	 * @return boolean 是基本基本数据类型返回true,否则返回false
	 */

	public static boolean isBeseType(Class c) {
		if (BeanUtils.isSubClass(c, java.lang.Number.class)) {
			return true;
		}

		String type = c.getName() + ";";
		if (BeanUtils.DATA_BASE_TYPE.indexOf(type) > -1)
			return true;
		return false;
	}

	/**
	 * 
	 * @param name
	 *            String
	 * @return boolean
	 */
	public static boolean isValueAttibute(String name) {
		if (name.equals(ATTRIBUTE_VALUE))
			return true;
		return false;
	}

	public static Field getFieldByName(Class beanClass, String name) {
		Field[] field = beanClass.getDeclaredFields();
		for (int i = 0; i < field.length; i++) {
			if (field[i].getName().equals(name.trim())) {

				return field[i];
			}
		}
		return null;
	}

	public static List<Method> getMethodByName(Class beanClass, String name) {
		Method[] ms = beanClass.getMethods();
		List<Method> list = new ArrayList<Method>();

		for (Method mth : list) {
			if (mth.getName().equalsIgnoreCase(name.trim())) {
				list.add(mth);
			}
		}
		return list;
	}

	/**
	 * 获取一个类与其祖先类之间的属性的类型
	 * 
	 * @param c :
	 *            基本类
	 * @param superClass :
	 *            祖先类， 可以为null, 如果为空，函数内部作为Object.class，不建议这么做，影响性能
	 * @param name :
	 *            属性名称
	 * @return ：属性类型
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static Class getAttributeType(Class c, Class superClass, String name)
			throws SecurityException, NoSuchFieldException {
		superClass = superClass == null ? Object.class : superClass;

		Collection cl = new ArrayList();
		Class p = c;
		String names[] = null;

		while (BeanUtils.isSubClass(p, superClass) || p == superClass) {
			Field[] field = p.getDeclaredFields();
			for (int i = 0; i < field.length; i++) {
				if (field[i].getName().equals(name.trim())) {

					return field[i].getType();
				}
			}
			p = p.getSuperclass();
		}
		return null;
	}

	public static String getClassNameWithoutPackage(Object bean) {

		String name = bean.getClass().getName();
		// name=name.replace('.','&');
		// String tmp[]=name.split("&");
		String tmp[] = StringUtils.split(name, ".");
		return tmp[tmp.length - 1];
	}

	public static Object createInstanceByName(String name) throws Exception {
		return Class.forName(name).newInstance();// .newInstace();
	}

	private Object getBaseTypeTranslator(Class c) {
		String name = c.getName();
		Object ts = null;
		if (name.equals("int")) {
			ts = new intTranslator();
		} else if (name.equals("short")) {
			ts = new shortTranslator();
		} else if (name.equals("float")) {
			ts = new floatTranslator();
		} else if (name.equals("double")) {
			ts = new doubleTranslator();
		} else if (name.equals("boolean")) {
			ts = new booleanTranslator();
		} else if (name.equals("char")) {
			ts = new charTranslator();
		} else if (name.equals("byte")) {
			ts = new byteTranslator();
		} else if (name.equals("long")) {// 2005-12-26 补充
			ts = new longTranslator();
		}

		return ts;
	}

	private class intTranslator {
		public int getValueByString(String s) {
			return new Integer(s).intValue();
		};
	}

	private class floatTranslator {
		public float getValueByString(String s) {
			return new Float(s).floatValue();
		};
	}

	private class shortTranslator {
		public short getValueByString(String s) {
			return new Short(s).shortValue();
		};
	}

	private class booleanTranslator {
		public boolean getValueByString(String s) {
			s = s.trim().toLowerCase();
			if (s.equals("true") || s.equals("1")) {
				return true;
			}
			return false;
		};
	}

	private class longTranslator {
		public long getValueByString(String s) {
			return new Long(s).longValue();
		};
	}

	private class doubleTranslator {
		public double getValueByString(String s) {
			return new Double(s).doubleValue();
		};
	}

	private class byteTranslator {
		public byte getValueByString(String s) {
			return s.getBytes()[0];
		};
	}

	private class charTranslator {
		public char getValueByString(String s) {
			return s.charAt(0);
		};
	}

	/**
	 * =========================================================================
	 * Version 1.1
	 * 
	 * ===========================================================================
	 */
	/**
	 * 获取属性的参数类型
	 * 
	 * @param beanClass
	 * @param index
	 * @return
	 */
	public static Class getBeanGernaricParmeterType(Class beanClass, int index) {

		if (beanClass.getGenericSuperclass() instanceof ParameterizedType) {
			ParameterizedType ptype = (ParameterizedType) beanClass
					.getGenericSuperclass();
			Class rclas = (Class) ptype.getRawType();

			log.debug("==================="+ ptype.getRawType().getClass().getName());
			Type[] actuals = ptype.getActualTypeArguments();
			if (actuals.length > index) {
				Object obj = ((TypeVariable) actuals[index])
						.getGenericDeclaration();
				if (obj instanceof Class) {
					return (Class) obj;
				}
			}

		}

		return null;
	}

	/**
	 * 获取属性的参数类型
	 * 
	 * @param beanClass
	 * @param attrName
	 * @param index
	 * @return
	 */
	public static Class getAttributeParmeterType(Class beanClass,
			String attrName, int index) {

		Field field = BeanUtils.getFieldByName(beanClass, attrName);
		if (field == null)
			return null;

		if (field.getGenericType() instanceof ParameterizedType) {
			ParameterizedType ptype = (ParameterizedType) field
					.getGenericType();

			Type[] actuals = ptype.getActualTypeArguments();
			if (actuals.length > index) {
				Object obj = actuals[index];
				if (actuals[index] instanceof Class) {
					return (Class) actuals[index];
				}
			}
		}

		return null;
	}

	public static final boolean isOverdue(String msg) throws Exception {
		Date dt = DateUtils.strToDate("2007-06-01");
		Date cr = new Date(System.currentTimeMillis());

		if (dt.after(cr)) {

		} else {
			if (StringUtils.isEmpty(msg))
				msg = "该";
			throw new Exception(msg + "功能试用过期  ;  " + cr.toGMTString());
		}

		return false;
	}

	/**
	 * 将值对象的属性值以字符串的格式保存在Map中
	 * 
	 * @param srcBean
	 * @param map
	 */
	public static void bean2Map(Object srcBean, Map map) throws Exception {
		String names[] = BeanUtils.getAtrributeNames(srcBean.getClass());
		for (int i = 0; i < names.length; i++) {
			if (BeanUtils.canAccess(srcBean, names[i])) {
				map.put(names[i], BeanUtils.getAttributeValueAsString(srcBean, names[i]));
			}
		}
	}

	/**
	 * 将Map中的属性值还原到Bean对应的字段中
	 * 
	 * @param srcMap
	 * @param bean
	 */
	public static void map2Bean(Map srcMap, Object bean) throws Exception {
		String names[] = BeanUtils.getAtrributeNames(bean.getClass());
		for (int i = 0; i < names.length; i++) {
			if(names[i].equals("tBirthday")){
			}
			if (BeanUtils.canAccess(bean, names[i])) {
				Object value = srcMap.get(names[i]);
				//修复当后台往前台压数据时，由于没有精度，导致数据丢失的问题
				if(value instanceof Number) {//浮点数精度进行兼容
					java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
					nf.setGroupingUsed(false);
					nf.setMaximumIntegerDigits(30);
					nf.setMaximumFractionDigits(8);
					//nf.setMinimumFractionDigits(2);
					BeanUtils.setAttributeByString(bean, names[i], nf.format(value));
					continue;
				}
				// bluelan modified 20080908
				// if(value!=null){
				if (value != null && !"".equals(value)) {
					String strValue = BeanUtils.object2Str(value,null);
					BeanUtils.setAttributeByString(bean, names[i], strValue);
				}
			}
		}
		/*
		for(int i = 0; i < names.length; i++) {
			Object value = srcMap.get(names[i]);
			Class<?> cls = BeanUtils.getAttributeType(bean, names[i]);
			if(Number.class.isAssignableFrom(cls)) {//如果是数值类型
				java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
				nf.setGroupingUsed(false);
				nf.setMaximumIntegerDigits(30);
				nf.setMaximumFractionDigits(8);
				BeanUtils.setAttributeByString(bean, names[i], nf.format(value));
				continue;
			} else if(Date.class.isAssignableFrom(cls)) {//如果是日期类型
				continue;
			}
		}
		*/
	}

	/**
	 * 获取Bean或者Map对象指定属性的值
	 * 
	 * @param bean
	 * @param attrName
	 * @return
	 * @throws Exception
	 */
	public static Object getValueByAttr(Object bean, String attrName)
			throws Exception {
		if (bean instanceof java.util.Map) {
			return ((Map) bean).get(attrName);
		}
		return BeanUtils.getAttributeValue(bean, attrName);

	}

	public static String changeDataToJSon(List list) throws Exception {
		StringBuffer sb = new StringBuffer("[");

		List<String> nameList = new ArrayList();

		for (Object obj : list) {
			if (obj instanceof java.util.Map) {
				Set set = ((Map) obj).keySet();
				nameList = new ArrayList(set);
			} else {

				String[] names = BeanUtils.getAtrributeNames(obj.getClass());
				for (String tmp : names)
					nameList.add(tmp);
			}
			if (nameList.size() > 0)
				sb.append("{");
			for (int i = 0; i < nameList.size(); i++) {
				String name = nameList.get(i);
				// {name:'3f8181d819ea76ed0119ea7d177e0004',value:'ba'}{name:'3f8181d819e671980119e6725df70001'value:'7'}
				Object value = BeanUtils.getValueByAttr(obj, name);
				String sValue = value == null ? "" : value.toString();
				sb.append(name + ":'" + sValue + "'");
				if (i == 0 && i != (list.size() - 1) && list.size() > 1) {
					sb.append(",");
				}

			}
			if (nameList.size() > 0)
				sb.append("},");

		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]");
		return sb.toString();

	}

	/** ********************************************************************** */
	/** ************************修改为递归找属性以及属性的定义类型****************** */
	/** ********************************************************************** */
	/**
	 * 获得Bean的所有属性名称
	 * 
	 *            Object : Bean实例
	 * @return String[] : 属性名称数组
	 * @throws Exception
	 */
	public static String[] getAtrributeNames(Class cls) throws Exception {
		/*
		 * String[] names=new String[c.getDeclaredFields().length]; for(int
		 * i=0;i<names.length;i++){
		 * names[i]=c.getDeclaredFields()[i].getName(); } return names;
		 */// bluelan modify 20080704
		String names[] = null;
		List<String> lstAtrrib = new ArrayList<String>();

		recursiveClassAtrrib(cls, lstAtrrib);

		names = new String[lstAtrrib.size()];
		lstAtrrib.toArray(names);
		return names;
	}

	/**
	 * 
	 * 功能:递归基类的所有属性(com.isoftstone.开头的基类)
	 * 
	 * @param cls
	 * @param lstAtrrib
	 * @return
	 * @throws Exception
	 */
	public static List recursiveClassAtrrib(Class cls, List lstAtrrib)
			throws Exception {
		for (int i = 0; i < cls.getDeclaredFields().length; i++) {
			lstAtrrib.add(cls.getDeclaredFields()[i].getName());
		}
		String superName = cls.getSuperclass().getName();
		if (superName.startsWith(SUPER_NAME)) {
			recursiveClassAtrrib(cls.getSuperclass(), lstAtrrib);
		}
		return lstAtrrib;
	}

	/**
	 * 根据属性名，得到属性的定义类型 (递归的所有基类(com.isoftstone.开头的基类))
	 * 
	 * @param bean
	 *            Object : Bean对象实例
	 * @param name
	 *            String ： 属性名称
	 * @return Class ： 类型
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static Class getAttributeType(Object bean, String name)
			throws SecurityException, NoSuchFieldException {
		/*
		 * Field[] field= bean.getClass().getDeclaredFields(); for(int i=0;i<field.length;i++){
		 * if( field[i].getName().equals(name.trim())){
		 * 
		 * return field[i].getType(); } }
		 */// bluelan modify 20080704
		return getAttributeType(bean.getClass(), name);
	}

	public static Class getAttributeType(Class cls, String name)
			throws SecurityException, NoSuchFieldException {
		Field[] field = cls.getDeclaredFields();
		for (int i = 0; i < field.length; i++) {
			if (field[i].getName().equals(name.trim())) {
				return field[i].getType();
			}
		}
		// bluelan modify 20080704
		return recursiveAtrribType(cls.getSuperclass(), name);
	}

	/**
	 * 
	 * 功能:根据属性名，得到属性的定义类型 (递归的所有基类(com.isoftstone.开头的基类))
	 * 
	 * @param cls
	 *            类
	 * @param atrrName
	 *            属性名
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static Class recursiveAtrribType(Class cls, String atrrName)
			throws SecurityException, NoSuchFieldException {
		Field[] field = cls.getDeclaredFields();
		for (int i = 0; i < field.length; i++) {
			if (field[i].getName().equals(atrrName.trim())) {
				return field[i].getType();
			}
		}
		String superName = cls.getSuperclass().getName();
		if (superName.startsWith(SUPER_NAME)) {
			return recursiveAtrribType(cls.getSuperclass(), atrrName);
		}
		return null;
	}
	
	/**
	 * 深度比较两个对象的差异，
	 * 将差异存放在<String, String>结构中
	 * @param oldObj
	 * @param newObj
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> compareVO(Object oldObj, Object newObj) throws Exception {
		Map<String, String> diffMap = new HashMap<String, String>();
		Class<?> oldClass = oldObj.getClass();
		Class<?> newClass = newObj.getClass();
		Class<?> targetClass = null;
		if(oldClass != newClass) {
			if(!oldClass.isAssignableFrom(newClass) && !newClass.isAssignableFrom(oldClass)) {
				throw new RuntimeException("两个对象不是同一类别，也没有继承关系，没法比较是否相同，请使用相同类对象。");
			}
		}
		String[] attrNames = null;
		if(oldClass.isAssignableFrom(newClass)) {//旧类是新类的父类
			attrNames = getAtrributeNames(oldObj);
			targetClass = oldClass;
		} else if(newClass.isAssignableFrom(oldClass)) {//新类是旧类的父类
			attrNames = getAtrributeNames(newObj);
			targetClass = newClass;
		}
		for(int i=0; i<attrNames.length; i++) {
			Object oldValue = getAttributeValue(oldObj, attrNames[i]);
			Object newValue = getAttributeValue(newObj, attrNames[i]);
			if(oldValue != null && newValue != null) {
				String oldStr = null, newStr = null;
				Class<?> type = getAttributeType(oldObj, attrNames[i]);
				if(isBeseType(type)) {//如果是基本类型
					oldStr = getAttributeValueAsString(oldObj, attrNames[i]);
					newStr = getAttributeValueAsString(newObj, attrNames[i]);
					if(newStr != null && !newStr.equals(oldStr)) {
						diffMap.put(targetClass + "." + attrNames[i], "oldValue:" + oldStr + "," + "newValue:" + newStr);
					} else if(oldStr != null && !oldStr.equals(newStr)) {
						diffMap.put(targetClass + "." + attrNames[i], "oldValue:" + oldStr + "," + "newValue:" + newStr);
					}
				} else {
					compareVO(oldValue, newValue, diffMap);
				}
			} else {
				if(oldValue != newValue) {
					diffMap.put(targetClass + "." + attrNames[i], "oldValue:" + oldValue + "," + "newValue:" + newValue);
				}
			}
		}
		return diffMap;
	}
	
	/**
	 * 递归比较两个对象的差异，
	 * 将差异存放在<String, String>结构中
	 * @param oldObj
	 * @param newObj
	 * @param diffMap
	 * @throws Exception
	 */
	public static void compareVO(Object oldObj, Object newObj, Map<String, String> diffMap) throws Exception {
		if(diffMap == null) {
			diffMap = new HashMap<String, String>();
		}
		Class<?> oldClass = oldObj.getClass();
		Class<?> newClass = newObj.getClass();
		Class<?> targetClass = null;
		if(oldClass != newClass) {
			if(!oldClass.isAssignableFrom(newClass) && !newClass.isAssignableFrom(oldClass)) {
				throw new RuntimeException("两个对象不是同一类别，也没有继承关系，没法比较是否相同，请使用相同类对象。");
			}
		}
		String[] attrNames = null;
		if(oldClass.isAssignableFrom(newClass)) {//旧类是新类的父类
			attrNames = getAtrributeNames(oldObj);
			targetClass = oldClass;
		} else if(newClass.isAssignableFrom(oldClass)) {//新类是旧类的父类
			attrNames = getAtrributeNames(newObj);
			targetClass = newClass;
		}
		for(int i=0; i<attrNames.length; i++) {
			Object oldValue = getAttributeValue(oldObj, attrNames[i]);
			Object newValue = getAttributeValue(newObj, attrNames[i]);
			String oldStr = null, newStr = null;
			Class<?> type = getAttributeType(oldObj, attrNames[i]);
			if(isBeseType(type)) {//如果是基本类型
				oldStr = getAttributeValueAsString(oldObj, attrNames[i]);
				newStr = getAttributeValueAsString(newObj, attrNames[i]);
				if(newStr != null && !newStr.equals(oldStr)) {
					diffMap.put(targetClass + "-->" + attrNames[i], "oldValue:" + oldStr + "," + "newValue:" + newStr);
				} else if(oldStr != null && !oldStr.equals(newStr)) {
					diffMap.put(targetClass + "-->" + attrNames[i], "oldValue:" + oldStr + "," + "newValue:" + newStr);
				}
			} else {
				compareVO(oldValue, newValue, diffMap);
			}
		}
	}
	
	public static void main(String[] args) {
//		TestBean bean1 = new TestBean();
//		TestBean2 b1 = new TestBean2();
//		b1.setNum(1000);
//		bean1.setBean2(b1);
//		TestBean2 b2 = new TestBean2();
//		b2.setDesc("sdfsdfsfs");
//		bean1.setAge(100);
//		TestBean bean2 = new TestBean();
//		bean2.setBean2(b2);
//		try {
//			Map<String, String> diffMap = compareVO(bean1, bean2);
//			diffMap.clear();
//			diffMap.put("age", "3");
//			diffMap.put("salary", "6800.970");
//			diffMap.put("name", "Ferris");
//			BeanUtils.map2Bean(diffMap, bean2);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		/*
		Double qqq = new Double(178999.12345);
		Double value2 = new Double("25.12");
		if(value2 instanceof Double) {
			String numStr = value2.toString();
			java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			nf.setMaximumIntegerDigits(30);
			nf.setMaximumFractionDigits(8);
			//nf.setMinimumFractionDigits(2);
		}
		//if(true)return;
		Double value = new Double("46.000000");
		//java.math.BigDecimal value=new BigDecimal(value1);
 
		java.math.BigDecimal b=new BigDecimal(value.doubleValue());
		//java.math.BigInteger bI=new BigInteger(value.doubleValue()+"");
		String strBValue=b.toString();
		
		String numStr =value.toString();//b.toString();// value.toString();
//		Double value2 = new Double(numStr);
//		if(value2.doubleValue()==value.doubleValue()){
//		}else{
//		}
		//if(1==1)return;
		int pos=numStr.indexOf('E');
		if(null!=numStr&&pos>0){
			String size=numStr.substring(pos+1,numStr.length());
			
			if(value.longValue()==value.doubleValue()){
				pos=pos-1;
			}
			if((pos-2)<Integer.parseInt(size)){
				
			}else{
				
			}
			int fSize=pos-2-Integer.parseInt(size);
			
			if(value.doubleValue()!=new Double(value.toString())){
			}
			if(fSize>0){
			}else{
			}
			if(1==1)return;
			 
			int  len=numStr.substring(0, pos).length();
			String tmp=strBValue.substring(0,len);
			// return;
		}
		
		//if(1 == 1)return;
		
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		nf.setMaximumIntegerDigits(30);
		
		nf.setMaximumFractionDigits(8);
		nf.setMinimumFractionDigits(2);
		String s= nf.format(value);

		
		
		//if(1==1)return;

		String pre = "4";
		String str = object2Str(value, "2");

		nf = java.text.NumberFormat.getInstance();
		nf.setMaximumFractionDigits(8);
		nf.setGroupingUsed(false);

		Object ob = new Double(123.12);
		if(ob instanceof java.lang.Number) {
			nf = java.text.NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			nf.setMaximumIntegerDigits(30);
			nf.setMaximumFractionDigits(8);
		}
		class VOTest {
			Double value = 12345678901237.15;
			
			public Double getValue() {
				return value;
			}
		}
		
		VOTest vo = new VOTest();
		try {
			String aa = BeanUtils.getAttributeValueAsString(vo, "value");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		BigDecimal val = new BigDecimal("123456789123456789.05756756757651");

		Double dbl = new Double(1.125);
		DecimalFormat format = (DecimalFormat) NumberFormat.getPercentInstance();
		format.applyPattern("0.0000");
		float f = (float) 1.125;

		double d = 123456789012345678L;
		long l = Double.doubleToLongBits(d);

		Date dt = new Date();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date dt2 = sdf.parse("2011-08-25 11:20:22.234");
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		nf.setMaximumIntegerDigits(30);
		nf.setMaximumFractionDigits(8);
		//nf.setMinimumFractionDigits(2);
		String str = nf.format(new Double("12345.1234"));
	}
}
//class TestBean {
//	int age = 0;
//	Double salary = 0.0;
//	String name = "";
//	Date birthday = null;
//
//	TestBean2 bean2 = null;
//	public int getAge() {
//		return age;
//	}
//	public void setAge(int age) {
//		this.age = age;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public Date getBirthday() {
//		return birthday;
//	}
//	public void setBirthday(Date birthday) {
//		this.birthday = birthday;
//	}
//	public TestBean2 getBean2() {
//		return bean2;
//	}
//	public void setBean2(TestBean2 bean2) {
//		this.bean2 = bean2;
//	}
//	public Double getSalary() {
//		return salary;
//	}
//	public void setSalary(Double salary) {
//		this.salary = salary;
//	}
//}
//class TestBean2 {
//	int num = 0;
//	String desc = "";
//	public int getNum() {
//		return num;
//	}
//	public void setNum(int num) {
//		this.num = num;
//	}
//	public String getDesc() {
//		return desc;
//	}
//	public void setDesc(String desc) {
//		this.desc = desc;
//	}
//}