package com.example.todo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.paint.Color; // javaFX 색상 클래스
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class MainApp extends Application {

  private List<String> plans = new ArrayList<>();     // 계획 데이터를 저장하는 리스트

  @Override
  public void start(Stage primaryStage) throws Exception {
    // 폰트 로드
    Font.loadFont(getClass().getResourceAsStream("/font/Pacifico-Regular.ttf"), 85);
    Font.loadFont(getClass().getResourceAsStream("/font/D2CodingBold-Ver1.3.2-20180524.ttf"), 40);
    Font.loadFont(getClass().getResourceAsStream("/font/Jalnan2TTF.ttf"), 14);
    // 시작 화면 fxml 로드
    Parent root = FXMLLoader.load(getClass().getResource("/fxml/start.fxml"));
    primaryStage.setTitle("To-Do");   // 창 제목
    primaryStage.setScene(new Scene(root, 800, 600)); // 창 크기
    primaryStage.setResizable(false); // 창 크기를 변경하지 못하도록 고정
    primaryStage.show();
    // 로그인 및 회원가입 버튼 설정
    Button loginButton = (Button) root.lookup("#loginButton");  // 로그인 버튼을 ID로 찾음
    Button signButton = (Button) root.lookup("#signButton");
    // 로그인 버튼 클릭 이벤트
    loginButton.setOnAction(event -> {
      try {
        showLoginWindow(primaryStage);    // 로그인 창으로 전환
      } catch (Exception e) {
        e.printStackTrace();    // 예외 발생 시 콘솔에 출력
      }
    });
    // 회원가입 버튼 클릭 이벤트
    signButton.setOnAction(event -> {
      try {
        showSignWindow(primaryStage);     // 회원가입 창으로 전환
      } catch (Exception e) {
        e.printStackTrace();    // 예외 발생 시 콘솔에 출력
      }
    });
  } // start
  // 로그인 창 전환
  private void showLoginWindow(Stage currentStage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));   // 로그인 화면 fxml 로드
      Parent loginRoot = loader.load();   // 로그인 fxml파일 로드
      currentStage.setScene(new Scene(loginRoot, 800, 600));    // 현재 stage에 새로운 scene 설정
      // 로그인 처리
      Button loginButton = (Button) loginRoot.lookup("#loginButton");   // 로그인 버튼을 ID로 찾음
      loginButton.setOnAction(event -> {
        // 사용자 입력 필드 가져오기
        TextField usernameField = (TextField) loginRoot.lookup("#id");  // 사용자 id입력 필드
        TextField passwordField = (TextField) loginRoot.lookup("#passwd");  // 사용자 password입력 필드
        // 사용자 입력 값 가져오기
        String username = usernameField.getText().trim();   // id
        String password = passwordField.getText().trim();   // password
        // 입력값 검증
        if (username.isEmpty() || password.isEmpty()) {   // 경고 알림 표시 (입력값 누락)
          Alert warningAlert = new Alert(Alert.AlertType.WARNING);
          warningAlert.setTitle("경고");
          warningAlert.setContentText("사용자 이름 또는 비밀번호를 입력하세요.");
          warningAlert.showAndWait();
        } else {    // 사용자 인증 (users 테이블에서 사용자 확인)
          int userId = DBconnection.validateUser(username, password);
          if (userId != -1) {
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);  // 로그인 성공 표시
            successAlert.setTitle("로그인 성공");
            successAlert.setContentText("로그인에 성공했습니다.");
            successAlert.showAndWait();
            showMainWindow(currentStage, userId); // 메인 화면으로 이동, 사용자 ID 전달
          } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);  // 로그인 실패 표시
            errorAlert.setTitle("로그인 실패");
            errorAlert.setContentText("잘못된 사용자 이름 또는 비밀번호입니다.");
            errorAlert.showAndWait();
          }
        }
      });
      // 회원가입 화면으로 이동 버튼 설정
      Button gotoSign = (Button) loginRoot.lookup("#gotoSign");
      gotoSign.setOnAction(event -> {
        try {
          showSignWindow(currentStage);   // 회원가입 화면으로 이동
        } catch (Exception e) {
          e.printStackTrace();    // 예외 발생 시 콘솔에 출력
        }
      });
    } catch (Exception e) {
      e.printStackTrace();  // 예외 처리 : fxml로드 또는 다른 에러 발생 시 콘솔에 출력
    }
  } // showLogin
  // 회원가입 창 전환
  private void showSignWindow(Stage currentStage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sign.fxml"));    // 회원가입 화면 fxml 로드
      Parent signRoot = loader.load();    // 회원가입 fxml파일 로드
      currentStage.setScene(new Scene(signRoot, 800, 600));   // 현재 stage에 새로운 scene설정
      // 회원가입 처리 버튼
      Button enterButton = (Button) signRoot.lookup("#enter");
      enterButton.setOnAction(event -> {
        // 사용자 입력 필드 가져오기
        TextField usernameField = (TextField) signRoot.lookup("#id");
        TextField passwordField = (TextField) signRoot.lookup("#passwd");
        TextField confirmPasswordField = (TextField) signRoot.lookup("#repasswd");
        // 사용자 입력 값 가져오기
        String username = usernameField.getText().trim();   // id
        String password = passwordField.getText().trim();   // password
        String confirmPassword = confirmPasswordField.getText().trim();   // repassword
        // 입력 값 검증
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {    // 경고 알림 (입력값 누락)
          Alert alert = new Alert(Alert.AlertType.WARNING);
          alert.setTitle("경고");
          alert.setContentText("모든 필드를 입력하세요.");
          alert.showAndWait();
        } else if (!password.equals(confirmPassword)) {    // 비밀번호 불일치 경고
          Alert alert = new Alert(Alert.AlertType.WARNING);
          alert.setTitle("경고");
          alert.setContentText("비밀번호가 일치하지 않습니다.");
          alert.showAndWait();
        } else if (DBconnection.isUsernameTaken(username)) {    // 중복 아이디 경고
          Alert alert = new Alert(Alert.AlertType.WARNING);
          alert.setTitle("경고");
          alert.setContentText("이미 사용 중인 아이디입니다. 다른 아이디를 입력하세요");
          alert.showAndWait();
        } else {    // 회원가입 성공 or 실패
          boolean success = DBconnection.insertUser(username, password);    // users table에 사용자 추가
          if (success) {     // 회원가입 성공 및 로그인 화면으로 이동
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("회원가입 성공");
            successAlert.setContentText("회원가입에 성공했습니다.");
            successAlert.showAndWait();
            showLoginWindow(currentStage);    // 로그인 화면으로 전환
          } else {    // 회원가입 실패
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("회원가입 실패");
            errorAlert.setContentText("회원가입에 실패했습니다.");
            errorAlert.showAndWait();
          }
        }
      });
      // 로그인 화면으로 이동 버튼 설정
      Button gotoLogin = (Button) signRoot.lookup("#gotoLogin");
      gotoLogin.setOnAction(event -> {
        try {
          showLoginWindow(currentStage);    // 로그인 화면으로 이동
        } catch (Exception e) {
          e.printStackTrace();    // 예외 발생 시 콘솔에 출력
        }
      });
    } catch (Exception e) {
      e.printStackTrace();  // 예외 처리 : fxml로드 또는 다른 에러 발생 시 콘솔에 출력
    }
  } // shwosign
  // 메인 화면으로 전환
  private void showMainWindow(Stage currentStage, int userId) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));    // 메인화면 fxml로드
      Parent mainRoot = loader.load();    // 메인화면 fxml파일 로드
      currentStage.setScene(new Scene(mainRoot, 800, 600));   // 현재 stage에 새로운 scene 설정
      // 메인화면 버튼 가져오기
      Button gotoPlanPlus = (Button) mainRoot.lookup("#gotoPlanPlus");    // 계획 추가
      Button gotoAllPlan = (Button) mainRoot.lookup("#gotoAllPlan");      // 모든 일정 보기 & 관리
      Button gotoway = (Button) mainRoot.lookup("#gotoway");      // 사용방법
      // ImageView 이벤트 핸들러 설정
      ImageView imageView = (ImageView) mainRoot.lookup("#imageView");
      imageView.setOnMouseClicked(this::handleImageClick); // 이미지 클릭 시 핸들러 호출
      // 계획 추가 화면으로 전환
      gotoPlanPlus.setOnAction(event -> {
        try {
          showPlanWindow(currentStage, userId); // 사용자 ID 전달하며 계획 추가 화면으로 전환
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      // 모든 일정 보기 & 관리 화면으로 전환
      gotoAllPlan.setOnAction(event -> {
        try {
          showAllPlanWindow(currentStage, userId); // 사용자 ID 전달하며 모든일정 화면으로 전환
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      // 사용방법 화면으로 전환
      gotoway.setOnAction(event -> {
        try {
          showwayWindow(currentStage, userId); // 사용자 ID 전달하며 사용방법 화면으로 전환
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    } catch (Exception e) {
      e.printStackTrace(); // 예외 처리
    }
  } // showMain
  // 이미지 클릭 시 호출되는 메서드
  public void handleImageClick(MouseEvent event) {
    // 무작위 위치 계산
    double randomX = Math.random() * 600; // 0~600 범위 내 랜덤 X 좌표
    double randomY = Math.random() * 400; // 0~400 범위 내 랜덤 Y 좌표

    // 랜덤으로 선택할 이미지 파일 경로 배열
    String[] imagePaths = {
        "file:C:/work/java_projects/img/b1.png",   // 첫 번째 이미지
        "file:C:/work/java_projects/img/b2.png",   // 두 번째 이미지
        "file:C:/work/java_projects/img/b3.png"    // 세 번째 이미지
    };

    // 0, 1, 2 중 랜덤 인덱스 선택
    int randomIndex = (int) (Math.random() * imagePaths.length);

    // 랜덤 이미지 선택
    Image randomImage = new Image(imagePaths[randomIndex]);

    // 새로운 이미지뷰 생성
    ImageView newImage = new ImageView(randomImage);
    newImage.setFitHeight(120);
    newImage.setFitWidth(120);
    newImage.setLayoutX(randomX);
    newImage.setLayoutY(randomY);

    // FXML에서 루트 AnchorPane을 찾는 방식으로 수정
    AnchorPane root = (AnchorPane) ((ImageView) event.getSource()).getParent(); // ImageView의 부모인 AnchorPane을 찾음
    root.getChildren().add(newImage); // 랜덤 위치에 이미지 추가

    // 1초 후에 이미지를 제거하는 Timeline 생성
    Timeline timeline = new Timeline(
        new KeyFrame(Duration.seconds(1), e -> root.getChildren().remove(newImage))
    );
    timeline.setCycleCount(1); // 1번만 실행
    timeline.play(); // 타임라인 실행
  } // handleImageClick
  // 계획 추가 화면으로 전환
  private void showPlanWindow(Stage currentStage, int userId) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/planplus.fxml"));    // 계획 추가 화면 fxml로드
      Parent planRoot = loader.load();    // 계획 추가 화면 fxml파일 로드
      currentStage.setScene(new Scene(planRoot, 800, 600));     // 현재 stage에 새로운 scene설정
      // 뒤로가기 버튼
      Button back = (Button) planRoot.lookup("#Back");    // 뒤로가기 버튼을 ID로 찾기
      back.setOnAction(event -> {
        try {
          showMainWindow(currentStage, userId); // 사용자 ID 전달하며 메인화면으로 전환
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      // 계획 저장 버튼 처리
      Button enterButton = (Button) planRoot.lookup("#enterButton");    // 저장 버튼
      enterButton.setOnAction(event -> {
        // 입력 필드 가져오기
        TextField titleField = (TextField) planRoot.lookup("#titleField");    // 제목
        TextArea contentArea = (TextArea) planRoot.lookup("#contentArea");    // 내용
        TextField dateField = (TextField) planRoot.lookup("#dateField");      // 날짜
        // 입력 값 가져오기
        String title = titleField.getText().trim();     // 제목
        String content = contentArea.getText().trim();  // 내용
        String date = dateField.getText().trim();       // 날짜
        // 유효성 검사
        if (title.isEmpty() || content.isEmpty() || date.isEmpty()) {   // 누락 값 체크
          Alert alert = new Alert(Alert.AlertType.WARNING);
          alert.setTitle("경고");
          alert.setContentText("모든 필드를 입력하세요.");
          alert.showAndWait();
        } else {    // plans 테이블에 저장
          boolean success = DBconnection.insertPlan(userId, title, content, date);    // 계획 저장 메서드 호출
          if (success) {    // 저장 성공 및 모든계획 화면으로 전환
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("성공");
            successAlert.setContentText("계획이 저장되었습니다.");
            successAlert.showAndWait();
            showAllPlanWindow(currentStage, userId); // 모든 계획 보기 화면으로 전환
          } else {    // 저장 실패 시 오류 표시
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
  } // showPlan
  // 모든 계획 보기 & 관리 화면으로 전환
  private void showAllPlanWindow(Stage currentStage, int userId) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/allplan.fxml"));   // 모든계획 화면 fxml 로드
      Parent allPlanRoot = loader.load();   // 모든계획 화면 fxml 파일 로드
      currentStage.setScene(new Scene(allPlanRoot, 800, 600));    // 현재 stage에 새로운 scene 설정
      // 화면 요소 초기화
      ListView<HBox> planListView = (ListView<HBox>) allPlanRoot.lookup("#planListView");   // 계획 ListView를 ID로 찾기
      Button plusButton = (Button) allPlanRoot.lookup("#plusButton");   // 추가 버튼을 ID로 찾기
      Button planDeleteButton = (Button) allPlanRoot.lookup("#planDelete");  // 삭제 버튼
      // plans 테이블에서 사용자 계획 리스트를 가져와 ListView에 추가
      List<String> plans = DBconnection.getPlansByUserId(userId); // user_id로 계획 조회
      planListView.getItems().clear();    // 기존 항목 초기화
      // 각 계획에 대해 체크박스 추가
      for (String plan : plans) {
        HBox hbox = new HBox(10);             // HBox 생성, 간격
        CheckBox checkBox = new CheckBox(plan);  // 체크박스를 생성하고
        checkBox.setFont(Font.font("Jalnan 2 TTF", 14));    // 체크박스 폰트 설정
        // 체크박스 상태 변경 이벤트
        checkBox.setOnAction(event -> {
          if (checkBox.isSelected()) {  // 체크된 상태에서 텍스트 색상을 파란색으로 설정
            checkBox.setTextFill(Color.BLUE);
          } else {  // 체크 해제된 상태에서 텍스트 색상을 기본 검정으로 설정
            checkBox.setTextFill(Color.BLACK);
          }
        });
        hbox.getChildren().add(checkBox);         // 체크박스를 HBox에 추가
        hbox.setAlignment(Pos.CENTER_LEFT);       // HBox 안의 요소들을 왼쪽 정렬
        planListView.getItems().add(hbox);        // ListView에 추가
      }
      // 삭제 버튼 클릭 이벤트 처리
      planDeleteButton.setOnAction(event -> {
        // 삭제할 항목들을 찾기 위한 리스트
        List<HBox> itemsToRemove = new ArrayList<>();
        // ListView에서 각 HBox에 대해 체크박스를 확인하고, 체크된 항목을 삭제
        for (HBox hbox : planListView.getItems()) {
          CheckBox checkBox = (CheckBox) hbox.getChildren().get(0);  // 첫 번째 자식 (체크박스) 가져오기
          if (checkBox.isSelected()) {
            String planTitle = checkBox.getText();  // 체크박스의 텍스트 (계획 제목)
            // DB에서 계획 삭제
            DBconnection.deletePlan(planTitle, userId);
            // 삭제할 항목을 리스트에 추가
            itemsToRemove.add(hbox);
          }
        }
        // ListView에서 체크된 항목 제거
        planListView.getItems().removeAll(itemsToRemove);
      });
      // ListView의 셀 팩토리 설정 (각 셀에 대해 HBox를 렌더링)
      planListView.setCellFactory(param -> {
        return new javafx.scene.control.ListCell<HBox>() {
          @Override
          protected void updateItem(HBox item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
              setGraphic(item); // HBox를 셀에 추가
            } else {
              setGraphic(null); // 항목이 비어있을 경우
            }
          }
        };
      });
      // 추가 버튼 클릭 이벤트 처리
      plusButton.setOnAction(event -> {
        try {
          showPlanWindow(currentStage, userId); // 계획 추가 화면으로 이동
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      // 뒤로가기 버튼 이벤트
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
  // 사용 방법 화면으로 전환
  private void showwayWindow(Stage currentStage, int userId) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/way.fxml"));   // 사용방법 화면에서 fxml로드
      Parent wayRoot = loader.load();
      currentStage.setScene(new Scene(wayRoot, 800, 600));
      // 뒤로 가기 버튼 처리
      Button backButton = (Button) wayRoot.lookup("#backButton");
      backButton.setOnAction(event -> {
        try {
          showMainWindow(currentStage, userId); // 메인 화면으로 이동
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      // 로그아웃 버튼 처리
      Button logoutButton = (Button) wayRoot.lookup("#logoutButton");
      logoutButton.setOnAction(event -> {
        // 로그아웃 확인 표시
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("로그아웃");
        confirmationAlert.setHeaderText(null);    // 헤더 제거
        confirmationAlert.setContentText("로그아웃 하시겠습니까?");
        // 사용자의 선택 처리
        Optional<ButtonType> result = confirmationAlert.showAndWait();    // 사용자 선택 대기
        if (result.isPresent() && result.get() == ButtonType.OK) {    // 확인 버튼 클릭 시
          try {
            showLoginWindow(currentStage); // 로그아웃 성공, 로그인 화면으로 이동
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
      // 회원탈퇴 버튼 처리
      Button deleteAccountButton = (Button) wayRoot.lookup("#deleteAccountButton");   // 회원탈퇴 버튼을 ID로 찾기
      deleteAccountButton.setOnAction(event -> {
        // 비밀번호 입력창 생성
        Dialog<String> passwordDialog = new Dialog<>();
        passwordDialog.setTitle("회원탈퇴");
        passwordDialog.setHeaderText("비밀번호를 입력해주세요.");    // 알림창 헤더 설정
        // 비밀번호 입력 필드 추가
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("비밀번호 입력");   // 힌트 텍스트 설정
        passwordDialog.getDialogPane().setContent(passwordField);   // 입력 필드를 알림창에 추가
        passwordDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);   // OK 및 취소 버튼 추가
        // OK 버튼 클릭 시 입력값을 user 테이블에서 가져오기
        passwordDialog.setResultConverter(dialogButton -> {
          if (dialogButton == ButtonType.OK) {
            return passwordField.getText();   // 입력된 비밀번호 반환
          }
          return null;
        });
        // 사용자 입력 처리
        Optional<String> result = passwordDialog.showAndWait();   // 알림창 표시 및 입력 값 대기
        result.ifPresent(password -> {
          // 비밀번호 확인 및 탈퇴 처리
          if (DBconnection.checkPassword(userId, password)) { // users 테이블에서 비밀번호 확인
            boolean success = DBconnection.deleteUser(userId); // 회원탈퇴 메서드 호출
            if (success) {    // 성공 알림
              Alert alert = new Alert(Alert.AlertType.INFORMATION);
              alert.setTitle("회원탈퇴 성공");
              alert.setContentText("회원탈퇴가 완료되었습니다.");
              alert.showAndWait();
              showLoginWindow(currentStage); // 로그인 화면으로 이동
            }
          } else {    // 경고 알림, 회원탈퇴 실패
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("오류");
            alert.setContentText("비밀번호가 올바르지 않습니다.");
            alert.showAndWait();
          }
        });
      });
    } catch (Exception e) {
      e.printStackTrace(); // 예외 처리
    }
  } // showway

  public static void main(String[] args) {
    launch(args);
  } // main
} // Application