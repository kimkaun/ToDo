package com.example.todo;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class AllPlanController {

  @FXML
  private ListView<String> planListView;  // 계획을 표시할 ListView

  // ListView에 데이터 추가
  public void addPlan(String planDetails) {
    // ListView의 셀을 커스터마이즈하여 폰트 적용
    planListView.setCellFactory(listView -> new ListCell<String>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          // 항목에 대한 텍스트 표시
          Text text = new Text(item);
          text.setFont(new Font("Jalnan 2 TTF", 15));  // 폰트 설정

          // 텍스트를 셀에 추가
          setGraphic(text);
        }
      }
    });

    // 입력된 계획을 ListView에 추가
    planListView.getItems().add(planDetails);
  }

  // Back 버튼 클릭 시 Main 화면으로 돌아가기
  @FXML
  private void onBackButtonClicked() {
    try {
      // Main 화면으로 돌아가기
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
      Parent root = loader.load();

      // 현재 Stage 가져오기
      Stage stage = (Stage) planListView.getScene().getWindow();
      stage.setScene(new Scene(root, 800, 600));
      stage.show();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
