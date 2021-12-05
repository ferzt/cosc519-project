package memorysimulator;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
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
import javafx.stage.Stage;

public class UIStarter extends Application {
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			// Parameter list inputs
			Parameters params = getParameters();
			List<String> inputArg = params.getRaw();
			
			//Intialize memory slots
			int memslots = 0;	
			SimulSpeed simulationSpeed = new SimulSpeed();
			
			// Create parent panes
			FlowPane panemaster = new FlowPane(); //Master pane for entire scene
			panemaster.setOrientation(Orientation.VERTICAL);
			
			HBox paneTopComponents = new HBox(); // pane for top components - memory area, list of processes, PCB information etc
			paneTopComponents.setPrefSize(800,600); ///sizing
			
			HBox paneCenterComponents = new HBox(); //pane for center - input arg selection and simulation speed control
			paneCenterComponents.setPrefSize(800,50);
			
			HBox paneBottomComponents = new HBox(); //pane for bottom component - start/stop/pause buttons
			paneBottomComponents.setPrefSize(800,150);
		
			//Panes in top component
			VBox leftBox = new VBox(); //Queued Processes
			VBox middleBox = new VBox(); //container for memory area and label
			
			FlowPane centerBox = new FlowPane(); //Memory Boxes
			centerBox.setPrefSize(510, 500);
			
			VBox rightBox = new VBox(); //PCB info for both simulations
			Text text = new Text("Launched");
			
			ScrollPane procPane = new ScrollPane();
			procPane.setPrefSize(150, 500);
			
			VBox pcbHolder = new VBox(); // PCB informtaion
			pcbHolder.setPrefSize(120, 350);
			
			VBox systatus = new VBox();
			systatus.setPrefSize(140, 220);
			
			//Panes or Containers in bottom component
			VBox controlsLeftB = new VBox();
			VBox controlsRightB = new VBox();
			controlsLeftB.setPrefSize(400, 200);
			controlsRightB.setPrefSize(400, 200);
			
			
			// Panes or Containers for center components
			HBox inputArgs = new HBox();
			inputArgs.setPrefSize(800, 200);
			
			
			//Create Children Panes
			BorderPane smallPane = new BorderPane(); //Container for right controls, slider and switch
			VBox processGen = new VBox(); //container for process generation toggling equivalent to a pause
			Pane btnSnap = new Pane();// Container for snapshot button - shows analysis at that point in time, make plot
			
			//Create buttons and components for simulation 
			Button snapShot = new Button("Snapshot"); // Get analytic snapshot for both running simulations
			Button interrupt = new Button("Interrupt");
			Slider timeMultiplier = new Slider();
			
			ToggleGroup group = new ToggleGroup();
			ToggleGroup pauseSim = new ToggleGroup();
			ToggleButton paused = new ToggleButton();
			ToggleButton notPaused = new ToggleButton();
			
			RadioButton cont = new RadioButton("ON");
			RadioButton shut = new RadioButton("OFF");
			
			CheckBox firstFit = new CheckBox("First Fit");
			CheckBox bestFit = new CheckBox("Best Fit");
			CheckBox worstFit = new CheckBox("Worst Fit");
			CheckBox nextFit = new CheckBox("Next Fit");
			
			//Set Padding for checkboxes
			int checkBoxSize = 5;
			firstFit.setPadding(new Insets(checkBoxSize,checkBoxSize,checkBoxSize,checkBoxSize));
			bestFit.setPadding(new Insets(checkBoxSize,checkBoxSize,checkBoxSize,checkBoxSize));
			worstFit.setPadding(new Insets(checkBoxSize,checkBoxSize,checkBoxSize,checkBoxSize));
			nextFit.setPadding(new Insets(checkBoxSize,checkBoxSize,checkBoxSize,checkBoxSize));
			
			//Create some font styles
			Font fontBoldItalic = Font.font("Georgia",FontWeight.BOLD, FontPosture.ITALIC, 12);
			Font fontBold = Font.font("Georgia", FontWeight.BOLD, FontPosture.REGULAR, 18);
			
			Label memLabel = new Label("MEMORY"); //Label for memory area
			memLabel.setFont(fontBold);
			Label procQueueLabel = new Label("PROCESSES"); //Label for memory area
			procQueueLabel.setFont(fontBold);
			
			Button startSim = new Button("START");
			Button stopSim = new Button("STOP"); //Stop Button - becomes visible when start button is pressed
			
			ListView<String> procRun = new ListView<String>(); //Listview to hold processes
			
			Label pcbInfo = new Label("PCB");
			pcbInfo.setFont(fontBold);
			Label pcbBody = new Label("Time, Size");
			Label pcbTail = new Label("address");
			
			TextArea statusInfo = new TextArea("Status Info:");
			
			
			//Create Memory Slots
			for (memslots = 0; memslots < 400; memslots++) {
				//Rectangle background with label stacked untop
				Rectangle memUnit = new Rectangle(0,0,20,20);
				StackPane memDivUnit = new StackPane();
				
				memUnit.setFill(Color.GOLD);
				memDivUnit.setPadding(new Insets(2));
				memDivUnit.getChildren().addAll(memUnit, new Label("~"));
				centerBox.getChildren().addAll(memDivUnit);
			}
			
			//Styling areas (besides memory slots - done above)
			paneCenterComponents.setStyle("-fx-background-color:lightgray;");
			paneBottomComponents.setStyle("-fx-background-color:darkgray;");
			
			//Set options and initialization for buttons and components
			snapShot.setMaxWidth(500);
			interrupt.setMaxWidth(Double.MAX_VALUE);
			startSim.setMaxWidth(Double.MAX_VALUE);
			stopSim.setMaxWidth(Double.MAX_VALUE);
			cont.setSelected(false);
			shut.setSelected(true);
			cont.setToggleGroup(group);
			shut.setToggleGroup(group);
			paused.setToggleGroup(pauseSim);
			notPaused.setToggleGroup(pauseSim);
			notPaused.setSelected(true);
			
			//Slider for simulation speed
			timeMultiplier.setOrientation(Orientation.VERTICAL);
			timeMultiplier.setShowTickLabels(true);
			timeMultiplier.setShowTickMarks(true);
			timeMultiplier.setValue(50);
			
			// Putting components into children panes
			btnSnap.getChildren().add(snapShot);
			processGen.getChildren().addAll(cont,shut);
			smallPane.setCenter(timeMultiplier);
			pcbHolder.getChildren().addAll(pcbInfo,pcbBody,pcbTail);
			inputArgs.getChildren().addAll(firstFit,bestFit, worstFit,nextFit);
			systatus.getChildren().add(statusInfo);
			
			//Putting children panes into Parent Panes/containers
			controlsLeftB.getChildren().add(startSim);
			controlsRightB.getChildren().add(stopSim);
			leftBox.getChildren().addAll(procQueueLabel, new ScrollPane(procRun), systatus); //interrupt, analysisAllAlgorithms
			middleBox.getChildren().addAll(memLabel,centerBox, snapShot);
			rightBox.getChildren().addAll(pcbHolder, smallPane);
			paneBottomComponents.getChildren().addAll(controlsLeftB,controlsRightB);
			paneCenterComponents.getChildren().addAll(inputArgs);
			paneTopComponents.getChildren().addAll(leftBox, middleBox, rightBox);//Left middle and right components into top
			
			//Master pane insertion
			panemaster.getChildren().addAll(paneTopComponents, paneCenterComponents, paneBottomComponents);// Top and bottom components into panemaster
			
			//Start/Resume, Pause and Stop parameters
			boolean start = false, pause=false, stop=false;
			
			//Event handlers
			EventHandler<ActionEvent> startEvent = new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent e)
	            {
	            	if(startSim.getText() == "START") {
	            		startSim.setText("PAUSE");
		                shut.setSelected(false);
		                cont.setSelected(true);	            		
	            	} else if (startSim.getText() == "PAUSE") {
	            		startSim.setText("RESUME");
	            		paused.setSelected(true);
		                notPaused.setSelected(false);	 
	            	} else {
	            		startSim.setText("PAUSE");
	            		paused.setSelected(false);
		                notPaused.setSelected(true);
	            	}
	                
	            }
	        };
	        
	        EventHandler<ActionEvent> checkBoxInputHandler = new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent e)
	            {
	        		if (bestFit.isSelected() || worstFit.isSelected() || firstFit.isSelected() || nextFit.isSelected()) {
	        		
	        		}
	                
	            }
	        };
	        
	        timeMultiplier.valueProperty().addListener(ov -> {
		        simulationSpeed.setSimSpeed(timeMultiplier.getValue() * 2000 / timeMultiplier.getMax());
	        });
			
			//Actions from buttons and other event handlers
			snapShot.setOnAction(e -> result());
			startSim.setOnAction(startEvent);
			stopSim.setOnAction(e -> {
				shut.setSelected(true);
                cont.setSelected(false);
			});
			
			bestFit.setOnAction(checkBoxInputHandler);
			worstFit.setOnAction(checkBoxInputHandler);
			nextFit.setOnAction(checkBoxInputHandler);
			firstFit.setOnAction(checkBoxInputHandler);
			
			
			//Create scene and place on stage
			Scene scene = new Scene(panemaster,800,800);

			primaryStage.setTitle("Memory Simulator COSC 519 - GROUP 3"); // Set the stage title
			primaryStage.setScene(scene); // Place the scene in the stage
			primaryStage.show(); // Display the stage
			
			
			//Create Thread to run simulation
			Thread simThread = new Thread(new Runnable() {
				public void run(){
					//Simulation options
					System.out.println("Running.................."+ inputArg.get(0));

					//set simulation
					
					MultiSimulator multsim = new MultiSimulator(inputArg.size());
					multsim.setSlotAlgorithms(inputArg);
					
					// leaving this one as "sim" in order to not change too much -pweems
					MemorySimulator sim = multsim.getSim(0);
					
					//run simulation
					while(group.getSelectedToggle() != cont) {} //To Begin Simulation
					while (group.getSelectedToggle() != shut) {
						if(pauseSim.getSelectedToggle() != paused) {
							multsim.timeStep();
			    			sim.printMemory();			    			
						}
						ArrayList<Process> processes = sim.processes;
		    			ArrayList<Process> remove = sim.processesDone;
		    			int arraySize = processes.size();
		    			String[] procNames = new String[arraySize];
		    			int insertNames = 0;
		    			for(Process p: processes) {
		    				procNames[insertNames] = (String) "" + p.getPname();
		    				insertNames++;
		    			}
		    			
		    			procRun.getSelectionModel().selectedItemProperty().addListener(ov -> {
		    				pcbBody.setText("Process #: " + procRun.getSelectionModel().getSelectedItem());
		    				String[] selectedProc = getPcbInfo(procRun.getSelectionModel().getSelectedItem(), processes);
		    				
		    				pcbBody.setText(pcbBody.getText() + " \n" + " Time Added: \n" + selectedProc[1] + "\n");
		    				pcbBody.setText(pcbBody.getText() + " \n" + " Size: \n" + selectedProc[2] + "\n");
		    				pcbTail.setText(" \n" + " PID: \n" + selectedProc[3] + "\n");
		    				try {
		    				pcbTail.setText(pcbTail.getText() + " \n" + " Location: \n" + selectedProc[4] + "\n");
		    				} catch (Exception e) {}
		    			});
		    			
		    			
		    			
		    			//Function to update UI
		    			Platform.runLater(new Runnable() {
		    				//For each process in processes(Runnning processes including reserved memory, alter boxes in UI)
							public void run(){
								FlowPane memArea = (FlowPane) middleBox.getChildren().get(1);
								for(Process n: processes) { 
									for(int bounds = 0; bounds < n.getSize(); bounds++) {
										StackPane targetPane = (StackPane) memArea.getChildren().get(n.getLocation()+bounds);
										Rectangle nodeBg = (Rectangle) targetPane.getChildren().get(0);
										nodeBg.setFill(Color.AZURE);
										Label nodeMarker = (Label) targetPane.getChildren().get(1);
										nodeMarker.setText(n.pname + "");
									}
									System.out.println(n.getPname() + " " + n.getSize() + " " + n.getLocation());
								}
								
								//For each process in remove(processes pending removal, reset in UI)
								for(Process n: remove) { 
									
									for(int bounds = 0; bounds < n.getSize(); bounds++) {
										StackPane targetPane = (StackPane) memArea.getChildren().get(n.getLocation()+bounds);
										Rectangle nodeBg = (Rectangle) targetPane.getChildren().get(0);
										nodeBg.setFill(Color.GOLD);
										Label nodeMarker = (Label) targetPane.getChildren().get(1);
										nodeMarker.setText("~");
									}
									System.out.println(n.getPname() + " " + n.getSize());
								}
								
								//Insert code to interact with UI - set items on window
								procRun.setItems(FXCollections.observableArrayList(procNames));
								statusInfo.setText("Status Info: ");
								statusInfo.setText(statusInfo.getText() + " \n" + "Simulation Time Elapsed: " + sim.currentTime);
								statusInfo.setText(statusInfo.getText() + " \n" + "Simulation Speed: " + simulationSpeed.getSimSpeed());
							}					
						});
		    			try {
		    				Thread.sleep((long)simulationSpeed.getSimSpeed());
		    			} catch (InterruptedException e) {
		    				
		    			}
		    			
		    		}
					
					
				}			
			});
			
			simThread.start();	
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//Utility function
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
					int procTimeDel = procTimeAdd;
					String[] procInfo = {procName, Integer.toString(procTimeAdd), Integer.toString(procSize), Integer.toString(procPid), Integer.toString(procLoc)};
					return procInfo;
				}
			}
		}
		return procInfoGlob;
	}
	
	//Utility function to perform analysis calculations and summarize results
	public void result() {
		Button btnTest = new Button("Snapshot");
		Scene scene = new Scene(btnTest, 500, 500);
		Stage primaryStage = new Stage();
		primaryStage.setTitle("Current State"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage
	}
	
	public static void main(String[] args) {
		//If no arguments passed, set default arguments
		//Use arguments from command line to intialize ui corresponding fields
		SimulationParameters simP = new SimulationParameters();
		simP.input = args;
		launch(args);
	}
	
	public class SimulSpeed {
		double simSpeed = 1000;
		
		public void setSimSpeed(double val) {
			this.simSpeed = val;
		}
		
		public double getSimSpeed() {
			return this.simSpeed;
		}
	}
	
}
