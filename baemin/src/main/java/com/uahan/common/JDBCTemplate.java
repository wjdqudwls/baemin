package com.uahan.common;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * JDBC 공통 로직(커넥션 획득, 닫기, 트랜잭션 등)을 처리하기 위한 유틸리티 클래스입니다.
 */
public class JDBCTemplate {

  /**
   * 데이터베이스와의 연결(Connection)을 획득합니다.
   * jdbc-config.properties 파일 설정을 읽어 MySQL 드라이버를 로드합니다.
   */
  public static Connection getConnection() {
    Properties prop = new Properties();
    Connection con = null;
    try {
      prop.load(JDBCTemplate.class.getClassLoader().getResourceAsStream("jdbc-config.properties"));
      String url = prop.getProperty("url");
      String user = prop.getProperty("user");
      String password = prop.getProperty("password");

      Class.forName("com.mysql.cj.jdbc.Driver");
      con = DriverManager.getConnection(url, user, password);

      // 트랜잭션 수동 제어를 위해 자동 커밋을 끕니다.
      con.setAutoCommit(false);

    } catch (SQLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return con;
  }

  /**
   * Connection 객체를 닫아 자원을 반납합니다 (메모리 누수 방지).
   */
  public static void close(Connection con) {
    try {
      if (con != null && !con.isClosed()) {
        con.close();
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * SQL 실행 객체(Statement/PreparedStatement)를 닫습니다.
   */
  public static void close(Statement stmt) {
    try {
      if (stmt != null && !stmt.isClosed()) {
        stmt.close();
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 결과 집합 객체(ResultSet)를 닫습니다.
   */
  public static void close(ResultSet rset) {
    try {
      if (rset != null && !rset.isClosed()) {
        rset.close();
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 변경 사항을 데이터베이스에 영구 반영(Commit)합니다.
   */
  public static void commit(Connection con) {
    try {
      if (con != null && !con.isClosed()) {
        con.commit();
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 오류 발생 시 작업 내용을 모두 취소(Rollback)합니다.
   */
  public static void rollback(Connection con) {
    try {
      if (con != null && !con.isClosed()) {
        con.rollback();
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
