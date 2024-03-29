package memorysimulator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class SimulatorUI {
	
	public SimulatorUI() {}
	
	public SimulatorUI(MemorySimulator memSim) {
		this.memSim = memSim;
	}
	
	public Queue<String> messages = new LinkedList<String>();
	public boolean argSet = false;
	
	public VBox middleBox = new VBox();
	
	public MemorySimulator memSim;
	
	public ListView<String> procRun = new ListView<String>(); //List view to hold processes
	
	public Label pcbBody = new Label("Time Size");
	public Label pcbTail = new Label(" Address");
	public Label diskQueue = new Label("On Disk");
	
	public TextArea statusInfo = new TextArea("Status Info:");
	
	public String runningAlgo;
	
	public Pane mainComponent() {
		
		//set text to wrap around
		statusInfo.setWrapText(true);
		
		//Simulation Area Sizing
		int simX = 650;
		int simY = 700;
		
		//center box and left box dimensions
		int centX = 500;
		int centY = 500;
		int pcbX = 400;
		int pcbY = 200;
		int diskX = centX-pcbX;
		
		//memory slot count 
		int memslots = 0;
		
		//Create parent panes		
		HBox paneTopComponents = new HBox(); // pane for top components - memory area, list of processes, PCB information etc
		paneTopComponents.setPrefSize(simX,simY); ///sizing
		
		HBox pcbBox = new HBox();
		pcbBox.setPrefSize(centX-diskX, pcbY);
		
		HBox queueLabel = new HBox();
		queueLabel.setPrefWidth(centX);
		
		HBox qtyBox = new HBox();
		qtyBox.setPrefSize(centX, pcbY);
		
		//Panes in top component
		VBox leftBox = new VBox(); //Queued Processes
		leftBox.setPrefSize(simX-centX, simY);
		
		
		FlowPane centerBox = new FlowPane(); //Memory Boxes
		centerBox.setPrefSize(centX, centY);
		
		
		VBox systatus = new VBox();
		systatus.setPrefSize(simX-centX, 250);
		
		//Create some font styles
		Font fontBoldItalic = Font.font("Georgia",FontWeight.BOLD, FontPosture.ITALIC, 12);
		Font fontBold = Font.font("Georgia", FontWeight.BOLD, FontPosture.REGULAR, 18);
		
		Label pcbInfo = new Label("PCB");
		Label diskInfo = new Label("DISK");
		pcbInfo.setFont(fontBold);
		pcbInfo.setPrefWidth(400);
		diskInfo.setFont(fontBold);
		
		Label memLabel = new Label("MEMORY"); //Label for memory area
		memLabel.setFont(fontBold);
		Label procQueueLabel = new Label("PROCESSES"); //Label for memory area
		procQueueLabel.setFont(fontBold);
		
		//Listener functions
		procRun.getSelectionModel().selectedItemProperty().addListener(ov -> pcbLocalAlter()
		);
		
		//Create Memory Slots
		for (memslots = 0; memslots < 400; memslots++) {
			//Rectangle background with label stacked on top
			Rectangle memUnit = new Rectangle(0,0,20,20);
			StackPane memDivUnit = new StackPane();
			
			memUnit.setFill(Color.GOLD);
			memDivUnit.setPadding(new Insets(2));
			memDivUnit.getChildren().addAll(memUnit, new Label("~"));
			centerBox.getChildren().addAll(memDivUnit);
		}
		
		systatus.getChildren().add(statusInfo);
		queueLabel.getChildren().addAll(pcbInfo, diskInfo);
		pcbBox.getChildren().addAll(pcbBody, pcbTail);
		qtyBox.getChildren().addAll(pcbBox, diskQueue);
		
		//Putting children panes into Parent Panes/containers
		leftBox.getChildren().addAll(procQueueLabel, new ScrollPane(procRun), systatus); //Queue and status information
		middleBox.getChildren().addAll(memLabel,centerBox,queueLabel,qtyBox); //memory and PCB information
		paneTopComponents.getChildren().addAll(leftBox, middleBox);//Left middle and right components into top
		
		return paneTopComponents;
	}
	
	public void pcbLocalAlter() {
		
		String[] selectedProc = new String[5];
		if(procRun.getSelectionModel().getSelectedItem() == null) {
			String recentProcess = "" + memSim.processes.get(memSim.processes.size()-1).pname;
			pcbBody.setText(" Process #: " + recentProcess);
			selectedProc = getPcbInfo(recentProcess, memSim.processes);
		} else {
			pcbBody.setText(" Process #: " + procRun.getSelectionModel().getSelectedItem());
			selectedProc = getPcbInfo(procRun.getSelectionModel().getSelectedItem(), memSim.processes);
		}

		pcbBody.setText(pcbBody.getText() + "\n" + " Time Added: " + selectedProc[1] + " ");
		pcbBody.setText(pcbBody.getText() + " " + " Size: " + selectedProc[2] + " ");
		pcbTail.setText("\n" + " PID: " + selectedProc[3] + "  ");
		try {
			pcbTail.setText(pcbTail.getText() + "   " + " Location: " + selectedProc[4] + "  ");
			pcbTail.setText(pcbTail.getText() + "   " + " Extinct: " + selectedProc[5] + "  ");
		} catch (Exception e) {}
		diskQueue.setText("Waiting: " + memSim.howManyWaiting());
		diskQueue.setText(diskQueue.getText()+ "\n" + "total wait: " + memSim.cumulativeStarvation);
	}
	
	//Utility function for obtaining PCB information
	public String[] getPcbInfo(String procName, ArrayList<Process> processes) {
		String [] procInfoGlob = new String[4];
		for(Process p : processes) {
			String procNameMatch = "" + p.getPname(); 
			if(procName != null) {
				if(procName.equals(procNameMatch)) {
					int procSize = p.getSize();
					int procPid = p.getPid();
					int procTimeAdd = p.getTimeAdded();
					int procLoc = p.getLocation();
					int procTimeDel = p.durationLeft;
					String[] procInfo = {procName, Integer.toString(procTimeAdd), Integer.toString(procSize), Integer.toString(procPid), 
							Integer.toString(procLoc), Integer.toString(procTimeDel)};
					return procInfo;
				}
			}
		}
		return procInfoGlob;
	}
	
	public void updateSimInfo() {
		int arraySize = memSim.processes.size();
		String[] procNames = new String[arraySize];
		int insertNames = 0;
		for(Process p: memSim.processes) {
			procNames[insertNames] = (String) "" + p.getPname();
			insertNames++;
		}
		procRun.setItems(FXCollections.observableArrayList(procNames));
		
		int numS = (int) Math.floor(statusInfo.getLength() + 12) ;
		while(!messages.isEmpty()) {
			statusInfo.setText(statusInfo.getText() + " \n" + messages.poll() + " ");
			statusInfo.positionCaret(numS);
		}
		pcbLocalAlter();

	}
			
}