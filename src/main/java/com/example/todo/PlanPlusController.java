package com.example.todo;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class PlanPlusController {

  @FXML
  private TextField titleField;  // 제목 입력 필드
  @FXML
  private TextArea contentArea; // 내용 입력 필드
  @FXML
  private TextField dateField;  // 날짜 입력 필드

  @FXML
  private void onEnterButtonClicked() {
    try {
      // 입력 필드에서 데이터 가져오기
      String title = titleField.getText().trim();
      String content = contentArea.getText().trim();
      String date = dateField.getText().trim();

      if (title.isEmpty() || content.isEmpty() || date.isEmpty()) {
        System.out.println("모든 필드를 채워야 합니다.");
        return;
      }

      // 화면 전환 및 데이터 전달
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/allplan.fxml"));
      Parent root = loader.load();

      // AllPlanController 가져오기
      AllPlanController allPlanController = loader.getController();

      // 데이터를 AllPlanController로 전달
      String planDetails = "제목 : " + title + "\n내용 : " + content + "\n날짜 : " + date;
      allPlanController.addPlan(planDetails);

      // 현재 창 전환
      Stage stage = (Stage) titleField.getScene().getWindow();
      stage.setScene(new Scene(root));
      stage.show();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

