package com.example.todo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class MainApp extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    // 폰트 로드
    Font.loadFont(getClass().getResourceAsStream("/font/Pacifico-Regular.ttf"), 85);
    Font.loadFont(getClass().getResourceAsStream("/font/D2CodingBold-Ver1.3.2-20180524.ttf"), 40);
    Font font = Font.loadFont(getClass().getResourceAsStream("/font/Jalnan2TTF.ttf"), 14);

    // 메인 화면 로드
    Parent root = FXMLLoader.load(getClass().getResource("/fxml/start.fxml"));
    primaryStage.setTitle("To-Do");
    primaryStage.setScene(new Scene(root, 800, 600));
    primaryStage.setResizable(false);
    primaryStage.show();

    // 로그인, 회원가입 버튼 이벤트 설정
    Button loginButton = (Button) root.lookup("#loginButton");
    Button signButton = (Button) root.lookup("#signButton");

    loginButton.setOnAction(event -> {
      try {
        showLoginWindow(primaryStage); // 로그인 화면으로 전환
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    signButton.setOnAction(event -> {
      try {
        showSignWindow(primaryStage); // 회원가입 화면으로 전환
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  private void showLoginWindow(Stage currentStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
    Parent loginRoot = loader.load();
    currentStage.setScene(new Scene(loginRoot, 800, 600));

    // 로그인 버튼 처리
    Button loginButton = (Button) loginRoot.lookup("#loginButton");
    loginButton.setOnAction(event -> {
      TextField usernameField = (TextField) loginRoot.lookup("#id");
      TextField passwordField = (TextField) loginRoot.lookup("#passwd");

      if (usernameField == null || passwordField == null) {
        System.out.println("텍스트 필드가 로드되지 않았습니다.");
        return; // 필드가 null이면 리턴하여 예외를 방지
      }

      String username = usernameField.getText().trim();
      String password = passwordField.getText().trim();

      // 유효성 검사
      if (username.isEmpty() || password.isEmpty()) {
        // 사용자 이름 또는 비밀번호가 비어 있을 경우 경고창 표시
        Alert warningAlert = new Alert(Alert.AlertType.WARNING);
        warningAlert.setTitle("경고");
        warningAlert.setHeaderText(null);
        warningAlert.setContentText("사용자 이름 또는 비밀번호를 입력하세요.");
        warningAlert.showAndWait();
      } else {
        // 데이터베이스에서 사용자 정보 검증
        boolean isValidUser = DBconnection.validateUser(username, password);
        if (isValidUser) {
          // 로그인 성공 시 알림 메시지
          Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
          successAlert.setTitle("로그인 성공");
          successAlert.setHeaderText(null);
          successAlert.setContentText("로그인에 성공했습니다.");
          successAlert.showAndWait();

          System.out.println("로그인 성공");
          try {
            showMainWindow(currentStage); // 로그인 성공 후 메인 화면으로 전환
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else {
          // 로그인 실패 시 알림 메시지
          Alert errorAlert = new Alert(Alert.AlertType.ERROR);
          errorAlert.setTitle("로그인 실패");
          errorAlert.setHeaderText(null);
          errorAlert.setContentText("잘못된 사용자 이름 또는 비밀번호입니다.");
          errorAlert.showAndWait();

          System.out.println("잘못된 사용자 이름 또는 비밀번호입니다.");
        }
      }
    });

    // 회원가입 화면으로 이동
    Button gotoSign = (Button) loginRoot.lookup("#gotoSign");
    gotoSign.setOnAction(event -> {
      try {
        showSignWindow(currentStage); // 회원가입 화면으로 전환
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  private void showSignWindow(Stage currentStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sign.fxml"));
    Parent signRoot = loader.load();
    currentStage.setScene(new Scene(signRoot, 800, 600));

    // 회원가입 버튼 처리
    Button enterButton = (Button) signRoot.lookup("#enter");
    enterButton.setOnAction(event -> {
      TextField usernameField = (TextField) signRoot.lookup("#id");
      TextField passwordField = (TextField) signRoot.lookup("#passwd");
      TextField confirmPasswordField = (TextField) signRoot.lookup("#repasswd");

      String username = usernameField.getText().trim();
      String password = passwordField.getText().trim();
      String confirmPassword = confirmPasswordField.getText().trim();

      // 유효성 검사
      if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
        // 모든 필드를 입력하지 않았을 경우
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("경고");
        alert.setHeaderText(null);
        alert.setContentText("모든 필드를 입력하세요.");
        alert.showAndWait();
      } else if (!password.equals(confirmPassword)) {
        // 비밀번호가 일치하지 않으면
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("경고");
        alert.setHeaderText(null);
        alert.setContentText("비밀번호가 일치하지 않습니다.");
        alert.showAndWait();
      } else {
        // 데이터베이스에 회원 정보 삽입
        boolean success = DBconnection.insertUser(username, password);
        if (success) {
          // 회원가입 성공 시 알림 메시지
          Alert successAlert = new Alert(AlertType.INFORMATION);
          successAlert.setTitle("회원가입 성공");
          successAlert.setHeaderText(null);
          successAlert.setContentText("회원가입에 성공했습니다.");
          successAlert.showAndWait();

          System.out.println("회원가입 성공");
          try {
            showLoginWindow(currentStage); // 회원가입 후 로그인 화면으로 전환
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else {
          // 회원가입 실패 시 알림 메시지
          Alert errorAlert = new Alert(AlertType.ERROR);
          errorAlert.setTitle("회원가입 실패");
          errorAlert.setHeaderText(null);
          errorAlert.setContentText("회원가입에 실패했습니다.");
          errorAlert.showAndWait();

          System.out.println("회원가입 실패");
        }
      }
    });

    // 로그인 화면으로 돌아가기 버튼
    Button gotoLogin = (Button) signRoot.lookup("#gotoLogin");
    gotoLogin.setOnAction(event -> {
      try {
        showLoginWindow(currentStage); // 로그인 화면으로 전환
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  private void showMainWindow(Stage currentStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
    Parent mainRoot = loader.load();
    currentStage.setScene(new Scene(mainRoot, 800, 600));

    // 메인 화면의 버튼 처리
    Button gotoPlanPlus = (Button) mainRoot.lookup("#gotoPlanPlus");
    gotoPlanPlus.setOnAction(event -> {
      try {
        showPlanWindow(currentStage); // 계획추가 화면으로 전환
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    Button gotoAllPlan = (Button) mainRoot.lookup("#gotoAllPlan");
    gotoAllPlan.setOnAction(event -> {
      try {
        showAllPlanWindow(currentStage); // 모든 계획 화면으로 전환
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  private void showPlanWindow(Stage currentStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/planplus.fxml"));
    Parent planRoot = loader.load();
    currentStage.setScene(new Scene(planRoot, 800, 600));

    Button back = (Button) planRoot.lookup("#Back");
    back.setOnAction(event -> {
      try {
        showMainWindow(currentStage); // 메인 화면으로 전환
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  private void showAllPlanWindow(Stage currentStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/allplan.fxml"));
    Parent allPlanRoot = loader.load();
    currentStage.setScene(new Scene(allPlanRoot, 800, 600));

    Button back = (Button) allPlanRoot.lookup("#Back");
    back.setOnAction(event -> {
      try {
        showMainWindow(currentStage); // 메인 화면으로 전환
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  public static void main(String[] args) {
    launch(args); // 애플리케이션 시작
  }
}
