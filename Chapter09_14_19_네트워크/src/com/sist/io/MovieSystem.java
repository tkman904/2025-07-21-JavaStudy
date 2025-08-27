package com.sist.io;
import java.util.*;
import java.io.*;

public class MovieSystem {
	private static ArrayList<MovieVO> mlist=new ArrayList<MovieVO>();
	static {
		try {
			FileReader fr=new FileReader("c:\\javaDev\\movie.txt");
			StringBuffer sb=new StringBuffer();
			int i=0;
			/*
			 *  int read(char[] buffer, int offst, int len)
			 *  ---- 읽은 글자수
			 *  int read()
			 *  ---- 문자
			 */
			
			while((i=fr.read())!=-1) {
				sb.append((char)i);
			}
			fr.close();
			
			String temp=sb.toString();
			String[] movies=temp.split("\n");
			
			for(String movie:movies) {
				String[] mm=movie.split("\\|");
				MovieVO m1=new MovieVO();
				m1.setMno(Integer.parseInt(mm[0]));
				m1.setTitle(mm[1]);
				m1.setGenre(mm[2]);
				m1.setPoster(mm[3]);
				m1.setActor(mm[4]);
				m1.setRegdate(mm[5]);
				m1.setGrade(mm[6]);
				m1.setDirector(mm[7]);
				mlist.add(m1);
			}
		}
		catch(Exception ex) {}
	}
	
	public List<MovieVO> moviListData(int page) {
		List<MovieVO> list=new ArrayList<MovieVO>();
		final int ROWSIZE=12;
		int start=(page*ROWSIZE)-ROWSIZE;
		int end=page*ROWSIZE;
		if(end>mlist.size())
			end=mlist.size();
		list=mlist.subList(start, end);
		return list;
	}
	
	public MovieVO movieDatailData(int mno) {
		return mlist.get(mno-1);
	}
	
	public List<MovieVO> movieFindData(String title) {
		List<MovieVO> list=new ArrayList<MovieVO>();
		for(MovieVO m:mlist) {
			if(m.getTitle().contains(title)) {
				list.add(m);
			}
		}
		return list;
	}
	
	public int movieTotalPage() {
		return (int)(Math.ceil(mlist.size()/12.0));
	}
}
