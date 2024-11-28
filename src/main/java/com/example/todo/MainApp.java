package com.example.todo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

  // 계획 데이터를 저장하는 리스트
  private List<String> plans = new ArrayList<>();

  @Override
  public void start(Stage primaryStage) throws Exception {
    Font.loadFont(getClass().getResourceAsStream("/font/Pacifico-Regular.ttf"), 85);
    Font.loadFont(getClass().getResourceAsStream("/font/D2CodingBold-Ver1.3.2-20180524.ttf"), 40);
    Font font = Font.loadFont(getClass().getResourceAsStream("/font/Jalnan2TTF.ttf"), 14);

    // 시작 화면 로드
    Parent root = FXMLLoader.load(getClass().getResource("/fxml/start.fxml"));
    primaryStage.setTitle("To-Do");
    primaryStage.setScene(new Scene(root, 800, 600));
    primaryStage.setResizable(false);
    primaryStage.show();

    // 로그인 및 회원가입 버튼 설정
    Button loginButton = (Button) root.lookup("#loginButton");
    Button signButton = (Button) root.lookup("#signButton");

    loginButton.setOnAction(event -> {
      try {
        showLoginWindow(primaryStage);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    signButton.setOnAction(event -> {
      try {
        showSignWindow(primaryStage);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  } // start

  // 로그인 창 전환
  private void showLoginWindow(Stage currentStage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
      Parent loginRoot = loader.load();
      currentStage.setScene(new Scene(loginRoot, 800, 600));

      // 로그인 처리
      Button loginButton = (Button) loginRoot.lookup("#loginButton");
      loginButton.setOnAction(event -> {
        TextField usernameField = (TextField) loginRoot.lookup("#id");
        TextField passwordField = (TextField) loginRoot.lookup("#passwd");

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
          Alert warningAlert = new Alert(Alert.AlertType.WARNING);
          warningAlert.setTitle("경고");
          warningAlert.setContentText("사용자 이름 또는 비밀번호를 입력하세요.");
          warningAlert.showAndWait();
        } else {
          int userId = DBconnection.validateUser(username, password);
          if (userId != -1) {
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("로그인 성공");
            successAlert.setContentText("로그인에 성공했습니다.");
            successAlert.showAndWait();
            showMainWindow(currentStage, userId); // 사용자 ID 전달
          } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("로그인 실패");
            errorAlert.setContentText("잘못된 사용자 이름 또는 비밀번호입니다.");
            errorAlert.showAndWait();
          }
        }
      });


      // 회원가입 화면으로 이동
      Button gotoSign = (Button) loginRoot.lookup("#gotoSign");
      gotoSign.setOnAction(event -> {
        try {
          showSignWindow(currentStage);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    } catch (Exception e) {
      e.printStackTrace();  // 예외 처리
    }
  } // showLogin

  // 회원가입 창 전환
  private void showSignWindow(Stage currentStage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sign.fxml"));
      Parent signRoot = loader.load();
      currentStage.setScene(new Scene(signRoot, 800, 600));

      // 회원가입 처리
      Button enterButton = (Button) signRoot.lookup("#enter");
      enterButton.setOnAction(event -> {
        TextField usernameField = (TextField) signRoot.lookup("#id");
        TextField passwordField = (TextField) signRoot.lookup("#passwd");
        TextField confirmPasswordField = (TextField) signRoot.lookup("#repasswd");

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
          Alert alert = new Alert(Alert.AlertType.WARNING);
          alert.setTitle("경고");
          alert.setContentText("모든 필드를 입력하세요.");
          alert.showAndWait();
        } else if (!password.equals(confirmPassword)) {
          Alert alert = new Alert(Alert.AlertType.WARNING);
          alert.setTitle("경고");
          alert.setContentText("비밀번호가 일치하지 않습니다.");
          alert.showAndWait();
        } else {
          boolean success = DBconnection.insertUser(username, password);
          if (success) {
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("회원가입 성공");
            successAlert.setContentText("회원가입에 성공했습니다.");
            successAlert.showAndWait();
            showLoginWindow(currentStage);
          } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("회원가입 실패");
            errorAlert.setContentText("회원가입에 실패했습니다.");
            errorAlert.showAndWait();
          }
        }
      });

      // 로그인 화면으로 돌아가기 버튼
      Button gotoLogin = (Button) signRoot.lookup("#gotoLogin");
      gotoLogin.setOnAction(event -> {
        try {
          showLoginWindow(currentStage);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    } catch (Exception e) {
      e.printStackTrace();  // 예외 처리
    }
  } // showSign

  // 메인 화면으로 전환
  private void showMainWindow(Stage currentStage, int userId) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
      Parent mainRoot = loader.load();
      currentStage.setScene(new Scene(mainRoot, 800, 600));

      Button gotoPlanPlus = (Button) mainRoot.lookup("#gotoPlanPlus");
      Button gotoAllPlan = (Button) mainRoot.lookup("#gotoAllPlan");

      gotoPlanPlus.setOnAction(event -> {
        try {
          showPlanWindow(currentStage, userId); // 사용자 ID 전달
        } catch (Exception e) {
          e.printStackTrace();
        }
      });

      gotoAllPlan.setOnAction(event -> {
        try {
          showAllPlanWindow(currentStage, userId); // 사용자 ID 전달
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    } catch (Exception e) {
      e.printStackTrace(); // 예외 처리
    }
  } // showMain

  private void showPlanWindow(Stage currentStage, int userId) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/planplus.fxml"));
      Parent planRoot = loader.load();
      currentStage.setScene(new Scene(planRoot, 800, 600));

      Button back = (Button) planRoot.lookup("#Back");
      back.setOnAction(event -> {
        try {
          showMainWindow(currentStage, userId); // 사용자 ID 전달
        } catch (Exception e) {
          e.printStackTrace();
        }
      });

      Button enterButton = (Button) planRoot.lookup("#enterButton");
      enterButton.setOnAction(event -> {
        TextField titleField = (TextField) planRoot.lookup("#titleField");
        TextArea contentArea = (TextArea) planRoot.lookup("#contentArea");
        TextField dateField = (TextField) planRoot.lookup("#dateField");

        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        String date = dateField.getText().trim();

        if (title.isEmpty() || content.isEmpty() || date.isEmpty()) {
          Alert alert = new Alert(Alert.AlertType.WARNING);
          alert.setTitle("경고");
          alert.setContentText("모든 필드를 입력하세요.");
          alert.showAndWait();
        } else {
          boolean success = DBconnection.insertPlan(userId, title, content, date);
          if (success) {
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("성공");
            successAlert.setContentText("계획이 저장되었습니다.");
            successAlert.showAndWait();
            showAllPlanWindow(currentStage, userId); // 계획 목록으로 이동
          } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("오류");
            errorAlert.setContentText("계획 저장에 실패했습니다.");
            errorAlert.showAndWait();
          }
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  // showPlan


  private void showAllPlanWindow(Stage currentStage, int userId) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/allplan.fxml"));
      Parent allPlanRoot = loader.load();
      currentStage.setScene(new Scene(allPlanRoot, 800, 600));

      ListView<String> planListView = (ListView<String>) allPlanRoot.lookup("#planListView");
      Button plusButton = (Button) allPlanRoot.lookup("#plusButton");

      List<String> plans = DBconnection.getPlansByUserId(userId); // 사용자 계획 조회
      planListView.getItems().clear();
      planListView.getItems().addAll(plans);

      plusButton.setOnAction(event -> {
        try {
          showPlanWindow(currentStage, userId); // 계획 추가 화면으로 이동
        } catch (Exception e) {
          e.printStackTrace();
        }
      });

      Button back = (Button) allPlanRoot.lookup("#Back");
      back.setOnAction(event -> {
        try {
          showMainWindow(currentStage, userId); // 메인 화면으로 이동
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  } // showAllPlan



  public static void main(String[] args) {
    launch(args);
  } // main
} // Application