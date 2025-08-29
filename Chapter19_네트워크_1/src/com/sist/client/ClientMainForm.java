package com.sist.client;
import java.util.List;
import java.util.StringTokenizer;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

import com.sist.commons.Function;
import com.sist.dao.*;
// login ==> "100|id|pwd" => String => login.jsp?id=aaa&pwd=1234 (웹)
public class ClientMainForm extends JFrame 
implements ActionListener,MouseListener,Runnable {
	CardLayout card=new CardLayout();
	// LOGIN / JOIN / WAIT / ROOM
	Login login=new Login();
	JoinForm join=new JoinForm();
	PostFindForm post=new PostFindForm();
	WaitRoom wr=new WaitRoom();
	
	/*
	 *   변수 => 네트워크와 관련 
	 */
	// 연결할 수 있는 기기 => 소프트웨어로 제작
	Socket s;
	// 서버와 송수신
	OutputStream out; // 서버에 요청
	BufferedReader in; // 서버로부터 응답을 받는다
	// 오라클 => PreparedStatement
	String myId;
	// => 모든 클라이언트는 서버의 명령을 받아서 처리
	// 서버 : 관리자, 클라이언트 : 노예
	int selectRow=-1;
	
	public ClientMainForm() {
		setLayout(card);
		add("login",login);
		add("join",join);
		add("wr",wr);
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		login.b1.addActionListener(this);
		login.b2.addActionListener(this);
		login.b3.addActionListener(this);
		login.tf.addActionListener(this);
		login.pf.addActionListener(this);
		
		join.b1.addActionListener(this);
		join.b2.addActionListener(this);
		join.b3.addActionListener(this);
		join.b4.addActionListener(this);
		
		post.b1.addActionListener(this);
		post.b2.addActionListener(this);
		post.tf.addActionListener(this);
		post.table.addMouseListener(this);
		
		wr.tf.addActionListener(this); // 대기실 채팅
		wr.b6.addActionListener(this); // 나가기
		wr.b5.addActionListener(this); // 정보 => ID
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				try {
					out.write((Function.CHATEND+"|\n").getBytes());
				}
				catch(Exception ex) {}
			}
		});
		
		wr.table2.addMouseListener(this);
		wr.b4.addActionListener(this);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
		}
		catch(Exception e) {}
		new ClientMainForm();
	} 
	
	//연결
	public void connection(String id,String name,String address) {
		try {
			// 서버와 연결
			s=new Socket("192.168.0.36",13579); //192.168.0.34
			//            서버 IP     서버에서 지정한 PORT
			// 서버는 고정 PORT, 클라이언트 자동 생성
			// 송수신
			// 서버에서 데이터 읽기 : 응답 => HttpServletResponse
			// InputStreamReadr => 보조 스트림
			// byte => char
			in=new BufferedReader(new InputStreamReader(s.getInputStream()));
			// 서버로 데이터 보내기 : 요청 => HttpServletRequest
			out=s.getOutputStream();
			
			// 서버로 로그인 요청
			out.write((Function.LOGIN+"|"+id+"|"+name+"|"+address+"\n").getBytes());
			// readLine => 반드시 마지막에 \n
		}
		catch(Exception ex) {}
		
		// 서버로부터 데이터를 읽기 시작
		new Thread(this).start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==login.b3) {
			dispose();
			System.exit(0);
		}
		else if(e.getSource()==login.b1||e.getSource()==login.tf||e.getSource()==login.pf) {
			String id=login.tf.getText();
			if(id.trim().length()<1) {
				JOptionPane.showMessageDialog(this, "아이디를 입력하세요");
				login.tf.requestFocus();
				return;
			}
			String pwd=String.valueOf(login.pf.getPassword());
			if(pwd.trim().length()<1) {
				JOptionPane.showMessageDialog(this, "비밀번호를 입력하세요");
				login.pf.requestFocus();
				return;
			}
			
			MemberDAO dao=MemberDAO.newInstance();
			MemberVO vo=dao.memberLogin(id, pwd);
			if(vo.getMsg().equals("아이디가 없습니다")) {
				JOptionPane.showMessageDialog(this, "아이디가 존재하지 않습니다");
				login.tf.setText("");
				login.pf.setText("");
				login.pf.requestFocus();
			}
			else if(vo.getMsg().equals("비밀번호가 틀렸습니다")) {
				JOptionPane.showMessageDialog(this, "비밀번호가 틀립니다");
				login.pf.setText("");
				login.pf.requestFocus();
			}
			else {
				// 서버 연결
				connection(vo.getId(), vo.getName(), vo.getAddr1());
			}
		}
		else if(e.getSource()==login.b2) {
			card.show(getContentPane(), "join");
		}
		else if(e.getSource()==join.b2) {
			post.tf.setText("");
			for(int i=post.model.getRowCount()-1;i>=0;i--) {
				post.model.removeRow(i);
			}
			post.setVisible(true);
		}
		/*
		 *   목록 => 페이징
		 *   입력
		 *   수정
		 *   삭제
		 *   상세보기
		 */
		else if(e.getSource()==join.b3) /* 회원가입 */{
			// 유효성 검사
			String id=join.tf1.getText();
			if(id.length()<1) {
				JOptionPane.showMessageDialog(this, "아이디를 입력하세요");
				join.tf1.requestFocus();
				return;
			}
			String pwd=String.valueOf(join.pf.getPassword());
			if(pwd.length()<1) {
				JOptionPane.showMessageDialog(this, "비밀번호를 입력하세요");
				join.pf.requestFocus();
				return;
			}
			String name=join.tf2.getText();
			if(name.length()<1) {
				JOptionPane.showMessageDialog(this, "이름을 입력하세요");
				join.tf2.requestFocus();
				return;
			}
			String post1=join.tf3.getText();
			if(post1.length()<1) {
				JOptionPane.showMessageDialog(this, "우편번호를 검색하세요");
				join.tf3.requestFocus();
				return;
			}
			String addr1=join.tf4.getText();
			String addr2=join.tf5.getText();
			
			MemberVO vo=new MemberVO();
			vo.setId(id);
			vo.setPwd(pwd);
			vo.setName(name);
			vo.setPost(post1);
			vo.setAddr1(addr1);
			vo.setAddr2(addr2);
			
			MemberDAO dao=MemberDAO.newInstance();
			int res=dao.memberJoin(vo);
			if(res==0) {
				JOptionPane.showMessageDialog(this, "회원가입 실패\n회원가입을 다시 진행해주세요");
			}
			else {
				JOptionPane.showMessageDialog(this, "🎉🎉회원가입을 축하드립니다🎉🎉\n로그인창으로 이동합니다");
				card.show(getContentPane(), "login");
			}
		}
		else if(e.getSource()==join.b4) {
			card.show(getContentPane(), "login");
		}
		else if(e.getSource()==post.b1 || e.getSource()==post.tf) {
			String dong=post.tf.getText();
			if(dong.length()<1) {
				JOptionPane.showMessageDialog(post, "동/읍/면을 입력하세요");
				post.tf.requestFocus();
				return;
			}
			// 입력시
			MemberDAO dao=MemberDAO.newInstance();
			List<ZipcodeVO> list=dao.postFind(dong);
			if(list.size()>0) {
				for(int i=post.model.getRowCount()-1;i>=0;i--) {
					post.model.removeRow(i);
				}
				
				for(ZipcodeVO vo:list) {
					String[] data= {
						vo.getZipcode(),vo.getAddress()
					};
					post.model.addRow(data);
				}
			}
			else {
				JOptionPane.showMessageDialog(post, "검색 결과가 없습니다");
				post.tf.setText("");
				post.tf.requestFocus();
			}
		}
		else if(e.getSource()==post.b2) {
			post.setVisible(false);
		}
		else if(e.getSource()==wr.tf) {
			String msg=wr.tf.getText();
			if(msg.length()<1) {
				return;
			}
			
			String color=wr.box.getSelectedItem().toString();
			try {
				// 서버로 채팅 전송 (요청 => Server에서 처리 후 응답)
				out.write((Function.WAITCHAT+"|"+msg+"|"+color+"\n").getBytes());
			}
			catch(Exception ex) {}
			//initStyle();
			//append(msg, color);
			wr.tf.setText("");
		}
		// 이벤트 처리(client) => 서버 전송 ==> 처리 ==> 응답
		// client = server = client
		// 나가기 요청
		else if(e.getSource()==wr.b6) {
			try {
				out.write((Function.CHATEND+"|\n").getBytes());
			}
			catch(Exception ex) {}
		}
		else if(e.getSource()==wr.b5) {
			if(selectRow==-1) {
				JOptionPane.showMessageDialog(this, "정보확인이 필요한 아이디를 선택하세요");
				return;
			}
			String id=wr.model2.getValueAt(selectRow, 0).toString();
			// id전송 => 정보를 보여달라 ...
			//JOptionPane.showMessageDialog(this, id+"님을 선택하였습니다");
			//selectRow=-1;
			try {
				out.write((Function.INFO+"|"+id+"\n").getBytes());
			}
			catch(Exception ex) {}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==post.table) {
			if(e.getClickCount()==2) {
				int row=post.table.getSelectedRow();
				String zip=post.model.getValueAt(row, 0).toString();
				String addr=post.model.getValueAt(row, 1).toString();
				join.tf3.setText(zip);
				join.tf4.setText(addr);
				post.setVisible(false);
			}
		}
		
		if(e.getSource()==wr.table2) {
			selectRow=wr.table2.getSelectedRow();
			String id=wr.model2.getValueAt(selectRow, 0).toString();
			if(id.equals(myId)) {
				wr.b3.setEnabled(false);
				wr.b4.setEnabled(false);
				wr.b5.setEnabled(false);
			}
			else {
				wr.b3.setEnabled(true);
				wr.b4.setEnabled(true);
				wr.b5.setEnabled(true);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	// 공통 적용
	public void initStyle() {
		   Style green=wr.pane.addStyle("green", null);
		   StyleConstants.setForeground(green, Color.green);
		   
		   Style yellow=wr.pane.addStyle("yellow", null);
		   StyleConstants.setForeground(yellow, Color.yellow);
		   
		   Style blue=wr.pane.addStyle("blue", null);
		   StyleConstants.setForeground(blue, Color.blue);
		   
		   Style pink=wr.pane.addStyle("pink", null);
		   StyleConstants.setForeground(pink, Color.pink);
		   
		   Style cyan=wr.pane.addStyle("cyan", null);
		   StyleConstants.setForeground(cyan, Color.cyan);
		   
		   Style magenta=wr.pane.addStyle("magenta", null);
		   StyleConstants.setForeground(magenta, Color.magenta);
	       
		   Style red=wr.pane.addStyle("red", null);
		   StyleConstants.setForeground(red, Color.red);
	   }
	   public void append(String msg,String color) {
		   try {
			   Document doc=wr.pane.getDocument();
			   doc.insertString(doc.getLength(), msg+"\n",
					   wr.pane.getStyle(color));
		   }
		   catch(Exception ex){}
	   }

	   @Override
	   public void run() {
		// TODO Auto-generated method stub
		// 서버에서 데이터 읽기
		   try {
			   while(true) {
				   String msg=in.readLine();
				   StringTokenizer st=new StringTokenizer(msg,"|");
				   int protocol=Integer.parseInt(st.nextToken());
				   
				   switch(protocol) {
				   		case Function.LOGIN: {
				   			// id, name, pos
				   			String[] data= {st.nextToken(),st.nextToken(),st.nextToken()};
				   			wr.model2.addRow(data);
				   		}
				   		break;
				   		
				   		case Function.MYLOG: {
				   			myId=st.nextToken();
				   			String name=st.nextToken();
				   			setTitle(name); // 윈도우 구분
				   			
				   			// 화면 이동 => Login => WaitRoom으로 변경
				   			card.show(getContentPane(), "wr");
				   		}
				   		break;
				   		
				   		case Function.WAITCHAT: {
				   			initStyle();
				   			wr.bar.setValue(wr.bar.getMaximum());
				   			append(st.nextToken(), st.nextToken());
				   		}
				   		break;
				   		
				   		case Function.MYEND: {
				   			dispose();
				   			System.exit(0);
				   		}
				   		break;
				   		
				   		case Function.CHATEND: {
				   			String id=st.nextToken();
				   			for(int i=0;i<wr.model2.getRowCount();i++) {
				   				String s=wr.model2.getValueAt(i, 0).toString();
				   				if(s.equals(id)) {
				   					wr.model2.removeRow(i);
				   					break;
				   				}
				   			}
				   		}
				   		break;
				   		
				   		case Function.INFO: {
				   			String s="아이디: "+st.nextToken()+"\n"+"이름: "+st.nextToken()+"\n"+"주소: "+st.nextToken();
				   			JOptionPane.showMessageDialog(this, s);
				   			selectRow=-1;
				   		}
				   		break;
				   }
			   }
		   }
		   catch(Exception ex) {}
	   }
}
