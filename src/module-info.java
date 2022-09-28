module memorysimulator {
	requires javafx.graphics;
	requires javafx.controls;
	requires javafx.base;
	
	opens memorysimulator to javafx.graphics, javafx.fxml;
}