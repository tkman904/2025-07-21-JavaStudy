package com.sist.io;
/*
 *   객체 스트림 : 저장 => 객체 주소 저장
 *     ObjectOutputStream / ObjectInputStream
 */
import java.util.*;
import java.io.*;
public class 입출력_7 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Sawon> list=new ArrayList<Sawon>();
		list.add(new Sawon(1,"홍길동","개발부","사원","서울",3000));
		
		Sawon s=new Sawon();
		s.setSabun(2);
		s.setName("심청이");
		s.setDept("인사부");
		s.setJob("과장");
		s.setLoc("부산");
		s.setPay(4500);
		list.add(s);
		
		list.add(new Sawon(3,"이순신","자재부","대리","제주",4000));
		list.add(new Sawon(4,"강감찬","총무부","주임","서울",3500));
		list.add(new Sawon(5,"박문수","영업부","부장","서울",7500));
		list.add(new Sawon(6,"이철우","관리부","대표","인천",9000));
		list.add(new Sawon(7,"이성현","개발부","차장","경기",7000));
		
		try {
			File file=new File("c:\\upload\\sawon.txt");
			if(!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos=new FileOutputStream(file);
			
			// 객체 단위 저장
			ObjectOutputStream oos=new ObjectOutputStream(fos);
			oos.writeObject(list);
			
			oos.close();
			fos.close();
			System.out.println("객체 저장 완료");
		}
		catch(Exception ex) {}
	}

}
