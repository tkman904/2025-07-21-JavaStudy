package com.sist.dao;
import java.util.*;
import java.sql.*;
// 기능 => 우편번호 검색 / 로그인 여부 확인 / 회원가입
public class MemberDAO {
	private Connection conn;
	private PreparedStatement ps;
	private final String URL="jdbc:oracle:thin:@localhost:1521:XE";
	private static MemberDAO dao;
	
	// 1. driver 등록
	public MemberDAO() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}
		catch(Exception ex) {}
	}
	
	// 2. 싱글톤
	public static MemberDAO newInstance() {
		if(dao==null)
			dao=new MemberDAO();
		return dao;
	}
	
	// 3. 연결
	public void getConnection() {
		try {
			conn=DriverManager.getConnection(URL, "hr", "happy");
		}
		catch(Exception ex) {}
	}
	
	// 4. 연결해제
	public void disConnection() {
		try {
			if(ps!=null)
				ps.close();
			/*
			 *  PreparedStatement : OutputStream / BufferedReader
			 */
			if(conn!=null)
				conn.close();
			/*
			 *  Connection : Socket
			 */
		}
		catch(Exception ex) {}
	}
	
	/* 기능 */
	// 1. 우편번호 검색
	public List<ZipcodeVO> postFind(String dong) {
		List<ZipcodeVO> list=new ArrayList<ZipcodeVO>();
		try {
			getConnection();
			String sql="SELECT zipcode,sido,gugun,dong,NVL(bunji,' ') "
					+ "FROM zipcode "
					+ "WHERE dong "
					+ "LIKE '%'||?||'%'";
			// 전송
			ps=conn.prepareStatement(sql);
			
			// ? 값을 설정
			ps.setString(1, dong);
			
			// 실행결과
			ResultSet rs=ps.executeQuery();
			while(rs.next()) {
				ZipcodeVO vo=new ZipcodeVO();
				vo.setZipcode(rs.getString(1));
				vo.setSido(rs.getString(2));
				vo.setGugun(rs.getString(3));
				vo.setDong(rs.getString(4));
				vo.setBunji(rs.getString(5));
				list.add(vo);
			}
			rs.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			disConnection();
		}
		return list;
	}
	
	// 2. ID 체크
	// 3. 회원가입
	public int memberJoin(MemberVO vo) {
		int result=0;
		try {
			getConnection();
			String sql="INSERT INTO member VALUES(?,?,?,?,?,?,0)";
			// 전송
			ps=conn.prepareStatement(sql);
			
			// 실행요청전 ? 값 채우기
			ps.setString(1, vo.getId());
			ps.setString(2, vo.getPwd());
			ps.setString(3, vo.getName());
			ps.setString(4, vo.getPost());
			ps.setString(5, vo.getAddr1());
			ps.setString(6, vo.getAddr2());
			
			// 실행
			result=ps.executeUpdate();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			disConnection();
		}
		return result;
	}
	
	// 4. 로그인
	public MemberVO memberLogin(String id,String pwd) {
		MemberVO vo=new MemberVO();
		try {
			// 1. 연결
			getConnection();
			
			// 2. SQL문장
			String sql="SELECT COUNT(*) FROM member WHERE id=?";
			
			// 3. 결과값
			ps=conn.prepareStatement(sql);
			
			// 4. ?가 있는 경우에는 반드시 ?에 값을 채운다
			ps.setString(1, id);
			ResultSet rs=ps.executeQuery();
			rs.next();
			int count=rs.getInt(1);
			rs.close();
			
			if(count==0) {
				vo.setMsg("아이디가 없습니다");
			}
			else {
				sql="SELECT pwd,name,address FROM member WHERE id=?";
				ps=conn.prepareStatement(sql);
				ps.setString(1, id);
				rs=ps.executeQuery();
				rs.next();
				String db_pwd=rs.getString(1);
				String name=rs.getString(2);
				String addr1=rs.getString(3);
				rs.close();
				
				if(db_pwd.equals(pwd)) {
					vo.setMsg("로그인 성공");
					vo.setName(id);
					vo.setAddr1(addr1);
					vo.setName(name);
				}
				else {
					vo.setMsg("비밀번호가 틀렸습니다");
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			disConnection();
		}
		return vo;
	}
}
