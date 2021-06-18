package com.battcn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Test1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String pathname = "D:\\迅雷下载\\dytt\\master~\\Administrative-divisions-of-China-master\\dist\\pcas-code.json";
		StringBuffer sb = new StringBuffer();
		try (FileReader reader = new FileReader(pathname); BufferedReader br = new BufferedReader(reader)) {
			String line;
			// 网友推荐更加简洁的写法
			while ((line = br.readLine()) != null) {
				// 一次读入一行数据
//				System.out.println(line);
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JSONArray json = (JSONArray) JSONArray.parse(sb.toString());
//		System.out.println(json);
		
		JSONObject p ,c,a,s;
		JSONArray cArray;
		JSONArray aArray;
		JSONArray sArray;
//		StringBuffer psb = new StringBuffer();
//		StringBuffer csb = new StringBuffer();
//		StringBuffer asb = new StringBuffer();
//		StringBuffer ssb = new StringBuffer();
		
		StringBuffer sb1 = new StringBuffer();
		int count=0;
		int countp=0,countc=0,counta=0,counts=0;
		List<String> list = new ArrayList<String>();
		String prefix = "insert into t_sys ('c_code','c_name','c_parent','c_type') values ";
		for(int i=0;i<json.size();i++){
			countp=countp+1;
			count=count+1;
			p = json.getJSONObject(i);
			sb1.append("('"+p.getString("code")+"','"+p.getString("name")+"','0','province')");
//			if(i!=json.size()-1){
//				sb1.append(",");
//			}
			if(count%100==0){
				list.add(prefix+sb1.toString()+";");
				sb1=new StringBuffer();
			}else{
				sb1.append(",");
			}
			
			cArray = p.getJSONArray("children");
			for(int j=0;j<cArray.size();j++){
				countc=countc+1;
				count=count+1;
				c = cArray.getJSONObject(j);
				sb1.append("('"+c.getString("code")+"','"+c.getString("name")+"','"+p.getString("code")+"','city')");
//				if(j!=cArray.size()-1 || cArray.size()==1){
//					sb1.append(",");
//				}
				if(count%100==0 || count==46942){
					list.add(prefix+sb1.toString()+";");
					sb1=new StringBuffer();
				}else{
					sb1.append(",");
				}
				
				aArray = c.getJSONArray("children");
				for(int m=0;m<aArray.size();m++){
					counta=counta+1;
					count=count+1;
					a = aArray.getJSONObject(m);
					sb1.append("('"+a.getString("code")+"','"+a.getString("name")+"','"+c.getString("code")+"','area')");
//					if(m!=aArray.size()-1 || aArray.size()==1){
//						sb1.append(",");
//					}
					if(count%100==0 || count==46942){
						list.add(prefix+sb1.toString()+";");
						sb1=new StringBuffer();
					}else{
						sb1.append(",");
					}
					
					sArray = a.getJSONArray("children");
					if(sArray!=null){
						for(int n=0;n<sArray.size();n++){
							counts=counts+1;
							count=count+1;
							s = sArray.getJSONObject(n);
							sb1.append("('"+s.getString("code")+"','"+s.getString("name")+"','"+a.getString("code")+"','street')");
//							if(n!=sArray.size()-1 || sArray.size()==1){
//								sb1.append(",");
//							}
							if(count%100==0 || count==46942){
								list.add(prefix+sb1.toString()+";");
								sb1=new StringBuffer();
							}else{
								sb1.append(",");
							}
						}
					}
				}
			}
		}
		
		System.out.println(countp+","+countc+","+counta+","+counts);
//		System.out.println("insert into t_sys_province ('c_code','c_name','c_parent','c_type') values "+sb.toString()+";");
//		System.out.println("insert into t_sys_city ('c_code','c_name','c_parent','c_type') values "+sb1.toString()+";");
//		System.out.println("insert into t_sys_area ('c_code','c_name','c_parent','c_type') values "+sb1.toString()+";");
//		System.out.println("insert into t_sys_street ('c_code','c_name','c_parent','c_type') values "+sb1.toString()+";");
		for(int x=0;x<list.size();x++){
			System.out.println(list.get(x));
		}
	}

}
