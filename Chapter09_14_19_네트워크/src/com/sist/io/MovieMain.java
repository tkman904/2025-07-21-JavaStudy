package com.sist.io;
import java.util.*;

public class MovieMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MovieSystem ms=new MovieSystem();
		Scanner scan=new Scanner(System.in);
		// 페이지별 12개씩 목록 출력
//		System.out.println("페이지 입력(1~"+ms.movieTotalPage()+")");
//		int page=scan.nextInt();
//		List<MovieVO> list=ms.moviListData(page);
//		for(MovieVO vo:list) {
//			System.out.println(vo.getMno()+"."+vo.getTitle());
//		}
		
		// 상세보기
//		System.out.println("상세보기 할 영화 번호 입력(1~1938): ");
//		int mno=scan.nextInt();
//		MovieVO vo=ms.movieDatailData(mno);
//		System.out.println("영화번호: "+vo.getMno());
//		System.out.println("제목: "+vo.getTitle());
//		System.out.println("장르: "+vo.getGenre());
//		System.out.println("배우: "+vo.getActor());
//		System.out.println("등급: "+vo.getGrade());
//		System.out.println("감독: "+vo.getDirector());
//		System.out.println("개봉일: "+vo.getRegdate());
		
		// 검색
		System.out.println("검색어 입력: ");
		String title=scan.next();
		List<MovieVO> list=ms.movieFindData(title);
		for(MovieVO vo:list) {
			System.out.println(vo.getMno()+"."+vo.getTitle());
		}
	}

}
