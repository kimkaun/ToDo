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
    Connection conn = null;
    String username = "root";
    String password = "070506";
    String dbUrl = "jdbc:mysql://localhost:3306/todo_db?useSSL=false&serverTimezone=UTC"; // 추가된 옵션

    try {
      // JDBC 드라이버 로드
      Class.forName("com.mysql.cj.jdbc.Driver");
      System.out.println("JDBC 드라이버 로드 성공");

      // 데이터베이스 연결
      conn = DriverManager.getConnection(dbUrl, username, password);
      System.out.println("데이터베이스 연결 성공");
    } catch (ClassNotFoundException e) {
      System.out.println("JDBC 드라이버 로드 실패: " + e.getMessage());
    } catch (SQLException e) {
      System.out.println("데이터베이스 연결 실패: " + e.getMessage());
    }

    return conn; // 연결 객체 반환
  }

  // 회원 데이터 삽입 메서드
  public static boolean insertUser(String username, String password) {
    String query = "INSERT INTO users (username, password) VALUES (?, ?)";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

      // 값 바인딩
      pstmt.setString(1, username);
      pstmt.setString(2, password);

      // 쿼리 실행
      int result = pstmt.executeUpdate();
      return result > 0; // 1 이상의 값이 반환되면 성공
    } catch (SQLException e) {
      System.out.println("회원 데이터 삽입 중 오류 발생: " + e.getMessage());
      return false;
    }
  }

  // 계획 저장 메서드
  public static boolean insertPlan(int userId, String title, String content, String date) {
    String query = "INSERT INTO plans (user_id, title, content, date) VALUES (?, ?, ?, ?)";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

      pstmt.setInt(1, userId);
      pstmt.setString(2, title);
      pstmt.setString(3, content);
      pstmt.setString(4, date);

      int result = pstmt.executeUpdate();
      return result > 0;
    } catch (SQLException e) {
      System.out.println("계획 추가 중 오류 발생: " + e.getMessage());
      return false;
    }
  }

  // 사용자별 계획 조회 메서드
  public static List<String> getPlansByUserId(int userId) {
    String query = "SELECT title, content, date FROM plans WHERE user_id = ?";
    List<String> plans = new ArrayList<>();

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

      pstmt.setInt(1, userId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String title = rs.getString("title");
        String content = rs.getString("content");
        String date = rs.getString("date");
        plans.add("제목 : " + title + "\n내용 : " + content + "\n날짜 : " + date);
      }
    } catch (SQLException e) {
      System.out.println("계획 조회 중 오류 발생: " + e.getMessage());
    }
    return plans;
  }
  public static boolean validateUserExistence(String username, String password) {
    String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, username);
      stmt.setString(2, password);

      ResultSet rs = stmt.executeQuery();
      return rs.next(); // 사용자가 존재하면 true 반환
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static int validateUser(String username, String password) {
    String sql = "SELECT user_id FROM users WHERE username = ? AND password = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, username);
      stmt.setString(2, password);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getInt("user_id"); // 로그인 성공 시 user_id 반환
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1; // 로그인 실패 시 -1 반환
  }
  // 중복 아이디 체크 메서드
  public static boolean isUsernameTaken(String username) {
    String query = "SELECT COUNT(*) FROM users WHERE username = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getInt(1) > 0; // 아이디가 존재하면 true 반환
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false; // 예외 발생 시 false 반환
  }
  // 비밀번호 확인 메서드
  public static boolean checkPassword(int userId, String password) {
    String query = "SELECT * FROM users WHERE user_id = ? AND password = ?";
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
  }

  // 회원탈되 메서드
  public static boolean deleteUser(int userId) {
    String deletePlansQuery = "DELETE FROM plans WHERE user_id = ?";
    String deleteUserQuery = "DELETE FROM users WHERE user_id = ?";
    Connection conn = null; // conn 변수를 메서드 범위 내에서 선언

    try {
      conn = getConnection(); // DB 연결 생성
      conn.setAutoCommit(false); // 트랜잭션 시작

      // plans 테이블 데이터 삭제
      try (PreparedStatement deletePlansStmt = conn.prepareStatement(deletePlansQuery)) {
        deletePlansStmt.setInt(1, userId);
        deletePlansStmt.executeUpdate();
      }

      // users 테이블 데이터 삭제
      try (PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserQuery)) {
        deleteUserStmt.setInt(1, userId);
        deleteUserStmt.executeUpdate();
      }

      conn.commit(); // 트랜잭션 커밋
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      if (conn != null) { // conn이 null이 아닐 때만 롤백 시도
        try {
          conn.rollback();
        } catch (SQLException rollbackEx) {
          rollbackEx.printStackTrace();
        }
      }
    } finally {
      if (conn != null) { // conn이 null이 아닐 때만 닫기
        try {
          conn.close();
        } catch (SQLException closeEx) {
          closeEx.printStackTrace();
        }
      }
    }
    return false; // 실패 시 false 반환
  }


} // DBconnetion class