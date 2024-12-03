package com.example.todo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBconnection {
  // 데이터베이스 연결 메서드
  public static Connection getConnection() {
    Connection conn = null;   // DB 연결 객체 초기화
    String username = "root";   // 사용자 이름
    String password = "070506";   // 사용자 비밀번호
    String dbUrl = "jdbc:mysql://localhost:3306/todo_db?useSSL=false&serverTimezone=UTC"; // DB (URL) 설정
    try {   // JDBC 드라이버 로드
      Class.forName("com.mysql.cj.jdbc.Driver");
      System.out.println("JDBC 드라이버 로드 성공");
      // 데이터베이스 연결
      conn = DriverManager.getConnection(dbUrl, username, password);
      System.out.println("데이터베이스 연결 성공");
    } catch (ClassNotFoundException e) {    // JDBC 드라이버가 존재하지 않을 경우 예외 처리
      System.out.println("JDBC 드라이버 로드 실패: " + e.getMessage());
    } catch (SQLException e) {    // DB 연결 중 오류가 발생한 경우 예외 처리
      System.out.println("데이터베이스 연결 실패: " + e.getMessage());
    }
    return conn; // 연결 객체 반환
  } // Coonection
  // 회원 데이터 삽입 메서드
  public static boolean insertUser(String username, String password) {
    String query = "INSERT INTO users (username, password) VALUES (?, ?)";   // SQL INSERT쿼리 정의
    // try-with-resources 구문을 사용하여 데이터베이스 연결 및 PreparedStatement 객체 생성
    try (Connection conn = getConnection();   // DB 연결 객채 생성
         PreparedStatement pstmt = conn.prepareStatement(query)) {  // SQL 쿼리를 준비
      // 값 바인딩
      pstmt.setString(1, username);   // 첫 번째 매개변수에 사용자 이름 설정
      pstmt.setString(2, password);   // 두 번째 매개변수에 비밀번호 설정
      // 쿼리 실행 및 결과 반환
      int result = pstmt.executeUpdate();   // 데이터베이스에 쿼리를 실행하고 삽입된 행 수를 반환
      return result > 0; // 1 이상의 값이 반환되면 성공
    } catch (SQLException e) {    // SQLException 발생 시 오류 메시지 출력
      System.out.println("회원 데이터 삽입 중 오류 발생: " + e.getMessage());
      return false;   // 삽입 실패 시 false 반환
    }
  }  // insertUser
  // 계획 저장 메서드
  public static boolean insertPlan(int userId, String title, String content, String date) {
    // SQL INSERT쿼리 정의 : 테이블에 ID, 제목, 내용, 날짜를 삽입
    String query = "INSERT INTO plans (user_id, title, content, date) VALUES (?, ?, ?, ?)";
    // try-with-resources 구문을 사용하여 데이터베이스 연결 및 PreparedStatement 객체 생성
    try (Connection conn = getConnection();   // 데이터베이스 연결 객체 생성
         PreparedStatement pstmt = conn.prepareStatement(query)) {   // SQL 쿼리를 준비
      // 값 바인딩
      pstmt.setInt(1, userId);      // user_id
      pstmt.setString(2, title);    // title (제목)
      pstmt.setString(3, content);  // content (내용)
      pstmt.setString(4, date);     // date (날짜)
      // 쿼리 실행 및 결과 반환
      int result = pstmt.executeUpdate();    // 데이터베이스에 쿼리를 실행하고 삽입된 행 수를 반환
      return result > 0;
    } catch (SQLException e) {    // SQLException 발생 시 오류 메시지 출력
      System.out.println("계획 추가 중 오류 발생: " + e.getMessage());
      return false;
    }
  } // insertPlan
  // 사용자별 계획 조회 메서드
  public static List<String> getPlansByUserId(int userId) {
    String query = "SELECT title, content, date FROM plans WHERE user_id = ? ORDER BY date ASC";    // 사용자 ID에 해당하는 계획을 조회하는 SQL 쿼리 (가장 오래된 날짜부터)
    List<String> plans = new ArrayList<>();   // 계획을 저장할 리스트 생성
    // 데이터베이스 연결 및 쿼리 실행을 위한 try-with-resources 구문
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
      // 사용자 ID를 쿼리의 자리 표시자에 바인딩
      pstmt.setInt(1, userId);
      // 쿼리 실행 후 결과셋을 반환3
      ResultSet rs = pstmt.executeQuery();
      // 결과셋에서 데이터를 읽어와 plans 리스트에 추가
      while (rs.next()) {
        String title = rs.getString("title");   // 제목 조회
        String content = rs.getString("content");   // 내용 조회
        String date = rs.getString("date");   // 날짜 조회
        plans.add("제목 : " + title + "\n내용 : " + content + "\n날짜 : " + date);  // 리스트에 각 계획을 "제목", "내용", "날짜" 형식으로 추가
      }
    } catch (SQLException e) {
      System.out.println("계획 조회 중 오류 발생: " + e.getMessage());
    }
    return plans;
  } // getPlansByUserId
  // 사용자 인증 메서드 : 로그인 시 사용자 아이디를 반환하는 메서드
  public static int validateUser(String username, String password) {
    String sql = "SELECT user_id FROM users WHERE username = ? AND password = ?";   // 사용자 이름과 비밀번호를 이용해 user_id를 조회하는 SQL 쿼리
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      stmt.setString(2, password);
      ResultSet rs = stmt.executeQuery();
      // 결과셋에서 첫 번째 행이 존재하면 로그인 성공
      if (rs.next()) {
        return rs.getInt("user_id"); // 로그인 성공 시 user_id 반환
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1; // 로그인 실패 시 -1 반환
  } //  validateUser
  // 중복 아이디 체크 메서드
  public static boolean isUsernameTaken(String username) {
    String query = "SELECT COUNT(*) FROM users WHERE username = ?";   // 주어진 사용자 이름으로 존재하는 아이디 수를 세는 SQL 쿼리
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setString(1, username); // 사용자 이름을 쿼리의 자리 표시자에 설정
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getInt(1) > 0; // 아이디가 존재하면 true 반환
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false; // 예외 발생 시 false 반환
  } // isUsernameTaken
  // 비밀번호 확인 메서드: 주어진 사용자 ID와 비밀번호가 데이터베이스에 저장된 비밀번호와 일치하는지 확인하는 메서드
  public static boolean checkPassword(int userId, String password) {
    String query = "SELECT * FROM users WHERE user_id = ? AND password = ?";    // 사용자 ID와 비밀번호를 사용하여 해당 사용자가 존재하는지 확인하는 SQL 쿼리
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
      stmt.setInt(1, userId);
      stmt.setString(2, password);

      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next(); // 결과가 있으면 비밀번호 일치
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false; // 쿼리가 실패하거나 결과가 없으면 false
  } // checkPassword
  // 회원탈되 메서드
  public static boolean deleteUser(int userId) {
    String deletePlansQuery = "DELETE FROM plans WHERE user_id = ?";    // plans 테이블에서 사용자 ID에 해당하는 계획을 삭제하는 쿼리
    String deleteUserQuery = "DELETE FROM users WHERE user_id = ?";     // users 테이블에서 사용자 ID에 해당하는 사용자를 삭제하는 쿼리
    Connection conn = null;   // DB 연결 객체
    try {
      conn = getConnection(); // DB 연결 생성
      conn.setAutoCommit(false); // 트랜잭션 시작
      // plans 테이블에서 사용자 ID에 해당하는 데이터 삭제
      try (PreparedStatement deletePlansStmt = conn.prepareStatement(deletePlansQuery)) {
        deletePlansStmt.setInt(1, userId);   // 사용자 ID를 쿼리에 바인딩
        deletePlansStmt.executeUpdate();    // 계획 데이터 삭제 실행
      }
      // users 테이블에서 사용자 ID에 해당하는 데이터 삭제
      try (PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserQuery)) {
        deleteUserStmt.setInt(1, userId);
        deleteUserStmt.executeUpdate();
      }
      conn.commit(); // 트랜잭션 커밋
      return true;    // 삭제 성공 시 true
    } catch (SQLException e) {
      e.printStackTrace();    // 오류 메세지 출력
      if (conn != null) { // conn이 null이 아닐 때만 롤백 시도
        try {
          conn.rollback();    // 트랜잭션 롤백
        } catch (SQLException rollbackEx) {
          rollbackEx.printStackTrace();   // 롤백 오류 메세지 출력
        }
      }
    } finally {
      if (conn != null) { // conn이 null이 아닐 때만 닫기
        try {
          conn.close();   // DB 연결 종료
        } catch (SQLException closeEx) {
          closeEx.printStackTrace();    // 연결 종료 오류 메세지 출력
        }
      }
    }
    return false; // 실패 시 false 반환
  } // deleteUser
} // DBconnetion class