package memorysimulator;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Application.Parameters;
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
import memorysimulator.UIStarter.SimulSpeed;

public class UIStarter extends Application {
	
	
	@Override
	public void start(Stage primaryStage) {
		try {			
			// Parameter list inputs
			Parameters params = getParameters();
			List<String> inputArg = params.getRaw();//input arguments from command line
			
			//Initialize memory slots
			SimulSpeed simulationSpeed = new SimulSpeed();
			
			// Create parent panes
			FlowPane panemaster = new FlowPane(); //Master pane for entire scene
			panemaster.setOrientation(Orientation.VERTICAL);
			
			HBox paneTopComponents = new HBox(); // pane for top components - memory area, list of processes, PCB information etc
			paneTopComponents.setPrefSize(650,600); ///sizing
			
			HBox paneCenterComponents = new HBox(); //pane for center - input arg selection and simulation speed control
			paneCenterComponents.setPrefSize(650,50);
			
			VBox paneBottomComponents = new VBox(); //pane for bottom component - start/stop/pause buttons
			paneBottomComponents.setPrefSize(650,150);
			
			HBox startStopButtons = new HBox(); //pane for bottom component - start/stop/pause buttons
			startStopButtons.setPrefSize(650,50);
		
			
			//Panes or Containers in bottom component
			VBox controlsLeftB = new VBox();
			VBox controlsRightB = new VBox();
			controlsLeftB.setPrefSize(325, 50);
			controlsRightB.setPrefSize(325, 50);
			
			
			// Pane to hold check boxes for input arguments
			HBox inputArgs = new HBox();
			inputArgs.setPrefSize(320, 200);//set size
			
			
			//Create Children Panes
			BorderPane smallPane = new BorderPane(); //Container for right controls, slider and switch
			VBox processGen = new VBox(); //container for process generation toggling equivalent to a pause
			BorderPane btnSnap = new BorderPane();// Container for snapshot button - shows analysis at that point in time, make plot
			BorderPane reset = new BorderPane(); // reset button for check boxes
			btnSnap.setPrefSize(650,100);
			reset.setPrefSize(150, 200);
			smallPane.setPrefSize(170, 200);
			
			
			//Container for each input check box
			BorderPane firstCenterx = new BorderPane();
			BorderPane worstCenterx = new BorderPane();
			BorderPane nextCenterx = new BorderPane();
			BorderPane bestCenterx = new BorderPane();
			
		
			//Sizing check box container
			firstCenterx.setPrefSize(80, 200);
			worstCenterx.setPrefSize(80, 200);
			bestCenterx.setPrefSize(80, 200);
			nextCenterx.setPrefSize(80, 200);
			
			//Create buttons  
			Button snapShot = new Button("TERMINATE PROGRAM"); // Terminate Program
			Button interrupt = new Button("RESET");//Button to reset check boxes
			snapShot.setMaxWidth(Double.MAX_VALUE);
			interrupt.setMaxWidth(Double.MAX_VALUE);
			
			//Create Slider to manipulate time
			Slider timeMultiplier = new Slider();
			
			//Toggle groups to control pause, stop
			ToggleGroup group = new ToggleGroup();
			ToggleGroup pauseSim = new ToggleGroup();
			ToggleButton paused = new ToggleButton();
			ToggleButton notPaused = new ToggleButton();
			
			paused.setToggleGroup(pauseSim);
			notPaused.setToggleGroup(pauseSim);
			notPaused.setSelected(true);
			
			//Radio Buttons for cont(-inuing) and shut(-ting) down simulation
			RadioButton cont = new RadioButton("ON");
			RadioButton shut = new RadioButton("OFF");
			
			//Set options
			cont.setSelected(false);
			shut.setSelected(true);
			cont.setToggleGroup(group);
			shut.setToggleGroup(group);
			
			//Check boxes for algorithm selection
			CheckBox firstFit = new CheckBox("First Fit");
			CheckBox bestFit = new CheckBox("Best Fit");
			CheckBox worstFit = new CheckBox("Worst Fit");
			CheckBox nextFit = new CheckBox("Next Fit");
			
			TextArea statusInfo = new TextArea("Status Info:");
			
			//Set Padding for check boxes
			int checkBoxSize = 5;
			firstFit.setPadding(new Insets(checkBoxSize,checkBoxSize,checkBoxSize,checkBoxSize));
			bestFit.setPadding(new Insets(checkBoxSize,checkBoxSize,checkBoxSize,checkBoxSize));
			worstFit.setPadding(new Insets(checkBoxSize,checkBoxSize,checkBoxSize,checkBoxSize));
			nextFit.setPadding(new Insets(checkBoxSize,checkBoxSize,checkBoxSize,checkBoxSize));
			
			//Create some font styles
			Font fontBoldItalic = Font.font("Georgia",FontWeight.BOLD, FontPosture.ITALIC, 12);
			Font fontBold = Font.font("Georgia", FontWeight.BOLD, FontPosture.REGULAR, 18);
			
			Button startSim = new Button("START");
			Button stopSim = new Button("STOP"); //Stop Button - becomes visible when start button is pressed
			startSim.setMaxWidth(Double.MAX_VALUE);
			stopSim.setMaxWidth(Double.MAX_VALUE);
			
			ListView<String> procRun = new ListView<String>(); //List view to hold processes
			
			//Styling areas (besides memory slots - done above)
			paneCenterComponents.setStyle("-fx-background-color:lightgray;");
			paneBottomComponents.setStyle("-fx-background-color:darkgray;");
			
			//Color buttons
			startSim.setStyle("-fx-background-color:#32de84;");
			stopSim.setStyle("-fx-background-color:#de3163;");
			
			//Slider for simulation speed
			timeMultiplier.setOrientation(Orientation.HORIZONTAL);
			timeMultiplier.setShowTickLabels(true);
			timeMultiplier.setShowTickMarks(true);
			timeMultiplier.setValue(50);
			
			// Putting components into children panes
			btnSnap.setCenter(snapShot);
			processGen.getChildren().addAll(cont,shut);
			smallPane.setCenter(timeMultiplier);
			reset.setCenter(interrupt);
			firstCenterx.setCenter(firstFit);
			bestCenterx.setCenter(bestFit);
			worstCenterx.setCenter(worstFit);
			nextCenterx.setCenter(nextFit);
			inputArgs.getChildren().addAll(firstCenterx,bestCenterx, worstCenterx,nextCenterx);
			
			//Putting children panes into Parent Panes/containers
			controlsLeftB.getChildren().add(startSim);
			controlsRightB.getChildren().add(stopSim);
			startStopButtons.getChildren().addAll(controlsLeftB,controlsRightB);
			paneBottomComponents.getChildren().addAll(startStopButtons, statusInfo, btnSnap);
			paneCenterComponents.getChildren().addAll(inputArgs, reset, smallPane);


			SimulatorUI[] multUI = new SimulatorUI[4]; //max of 4 can run at once
			Stage[] allStages = new Stage[4];
			
			//Master pane insertion
			panemaster.getChildren().addAll(paneCenterComponents, paneBottomComponents);// Top and bottom components into panemaster
			
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
	        
	        EventHandler<ActionEvent> checkBoxBestHandler = new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent e)
	            {	
	        		int index = setMultUI(multUI, allStages, bestFit);
	        		allStages[index].show();
	            }
	        };
	        
	        EventHandler<ActionEvent> checkBoxFirstHandler = new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent e)
	            {
	        		int index = setMultUI(multUI, allStages, firstFit);
	        		allStages[index].show();
	            }
	        };
	        
	        EventHandler<ActionEvent> checkBoxWorstHandler = new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent e)
	            {      
	        		int index = setMultUI(multUI, allStages, worstFit);
	        		allStages[index].show();
	            }
	        };
	        
	        EventHandler<ActionEvent> checkBoxNextHandler = new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent e)
	            {
	        		int index = setMultUI(multUI, allStages, nextFit);
	        		allStages[index].show();
	            }
	        };
	        
	        interrupt.setOnAction(e -> {
	        	CheckBox[] allBoxes = {bestFit, worstFit, nextFit, firstFit};
	        	reset(allStages, multUI, allBoxes);
	        });
	        
	        timeMultiplier.valueProperty().addListener(ov -> {
		        simulationSpeed.setSimSpeed(timeMultiplier.getValue() * 2000 / timeMultiplier.getMax());
	        });
			
			//Actions from buttons and other event handlers
			startSim.setOnAction(startEvent);
			stopSim.setOnAction(e -> {
				shut.setSelected(true);
                cont.setSelected(false);
                startSim.setDisable(true);
				stopSim.setDisable(true);
			});
			
			bestFit.setOnAction(checkBoxBestHandler);
			worstFit.setOnAction(checkBoxWorstHandler);
			nextFit.setOnAction(checkBoxNextHandler);
			firstFit.setOnAction(checkBoxFirstHandler);
			
			//termination call
			snapShot.setOnAction(e -> {
				CheckBox[] allBoxes = {bestFit, worstFit, nextFit, firstFit};
				reset(allStages, multUI, allBoxes);
				terminate(primaryStage);
			});
			
			//Create scene and place on stage
			Scene scene = new Scene(panemaster,650,200);
			primaryStage.setX(800);
			primaryStage.setY(600);
			primaryStage.setTitle("Memory Simulator COSC 519 - GROUP 3"); // Set the stage title
			primaryStage.setScene(scene); // Place the scene in the stage
			primaryStage.show(); // Display the stage

			//Create Thread to run simulation
			Thread simThread = new Thread(new Runnable() {
				public void run(){

					//Simulation options
					System.out.println("Running..................");

					//run simulation
					while(group.getSelectedToggle() != cont) {} //To Begin Simulation
					
					int numOfSims = 0;
					
					for(int i = 0; i < multUI.length; i++) {
						if(multUI[i] != null) {
							numOfSims++;
						}
					}
					
					System.out.println(numOfSims);
					//set simulation
					MultiSimulator multsim = new MultiSimulator(numOfSims);
					
					// leaving this one as "sim" in order to not change too much -pweems
					for(int i = 0; i < multsim.sims.length; i++) {
						System.out.println(multUI[i].runningAlgo);
						multsim.getSim(i).setSlotAlgorithm(multUI[i].runningAlgo);
						multUI[i].memSim = multsim.getSim(i);
					}
					
					
					while (group.getSelectedToggle() != shut) {
						if(pauseSim.getSelectedToggle() != paused) {
							multsim.timeStep();
			    			multsim.sims[0].printMemory();			    			
						
							for(int i = 0; i < multsim.sims.length; i++) {
								obtainSimInfo(multUI[i], "prep");
							}
			    			
			    			//Function to update UI
			    			Platform.runLater(new Runnable() {
			    				
								public void run(){
	
									for(int i = 0; i < multsim.sims.length; i++) {
										obtainSimInfo(multUI[i], "ui");
									}
									
									statusInfo.setText("Status Info: ");
									statusInfo.setText(statusInfo.getText() + " \n" + "Simulation Time Elapsed: " + multsim.sims[0].currentTime);
									statusInfo.setText(statusInfo.getText() + " \n" + "Simulation Speed: " + simulationSpeed.getSimSpeed());
				
								}					
							});
			    			try {
			    				Thread.sleep((long)simulationSpeed.getSimSpeed());
			    			} catch (InterruptedException e) {
			    				
			    			}
		    			
						}
					
					}
					
					
				}			
			});
			
			simThread.start();	
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//Utility function to obtain simulator info
	public void obtainSimInfo(SimulatorUI myPane, String point) {
		
		MemorySimulator sim = myPane.memSim;
		
		if(point == "prep") {
			
			ArrayList<Process> processes = sim.processes;
			ArrayList<Process> remove = sim.processesDone;
			int arraySize = processes.size();
			String[] procNames = new String[arraySize];
			int insertNames = 0;
			for(Process p: processes) {
				procNames[insertNames] = (String) "" + p.getPname();
				insertNames++;
			}
			
			try {
				myPane.messages.add("Removed Process " + remove.get(0).pname );
			} catch(Exception e) {
				myPane.messages.add("Added process " + processes.get(processes.size()-1).pname);
			}
		}
		if(point == "ui") {
			//Update Main UI components
			updateUI(myPane.middleBox, sim.processes, sim.processesDone);

			//Update Info individual to each simulator
			myPane.updateSimInfo();
		}
	}
	
	//Utility function to retrieve PCB info
	public int setMultUI(SimulatorUI[] multUI, Stage[] allStages, CheckBox btn) {
		
		int index = -1;
		
		for(int i = 0; i < multUI.length; i++) {

			index++;
			if(multUI[i] == null) {
				multUI[i] = new SimulatorUI();
				break;
			}
		}
		
		String algo = btn.getText().toLowerCase();
		int spaceLoc = algo.indexOf(" ");
		algo = algo.substring(0, spaceLoc);
		
		multUI[index].runningAlgo = algo;
		
		Stage primStage = viewSim(multUI[index].mainComponent(), btn.getText());
		
		int setPos = 650;
		
		primStage.setX(setPos*index);
		primStage.setY(index);
		
		allStages[index] = primStage;

		//loop through input args to set checkboxes in UI
		btn.setDisable(true);
		btn.setSelected(true);
		
		return index;
		
	}
	
	//Display second simulator
	public Stage viewSim(Pane otherSim, String algo) {
		Pane simMasterPane = new Pane();
		simMasterPane.getChildren().add(otherSim);
		Scene scene = new Scene(simMasterPane, 650, 620);
		Stage primaryStage = new Stage();
		primaryStage.setTitle("Simulator: " + algo); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage

		return primaryStage;
	}
	
	//Utility function to reset check boxes
	public void reset(Stage allStages[], SimulatorUI allSims[], CheckBox[] options) {
		
		for(int i = 0; i < allStages.length; i++) {
			if(allStages[i] != null) {
				allStages[i].close();
				allStages[i] = null;
				allSims[i] = null;
			}
		}
		
		for(int i = 0; i < options.length; i++) {
			options[i].setDisable(false);
			options[i].setSelected(false);
		}
	}
	
	public static void main(String[] args) {
		//Use arguments from command line to initialize UI corresponding fields
		SimulationParameters simP = new SimulationParameters();
		simP.input = args;
		launch(args);
	}
	
	//Simulation speed control
	public class SimulSpeed {
		double simSpeed = 1000;
		
		public void setSimSpeed(double val) {
			this.simSpeed = val;
		}
		
		public double getSimSpeed() {
			return this.simSpeed;
		}
	}
	
	//For each process in processes(Running processes including reserved memory, alter boxes in UI)
	public void updateUI(VBox middleBox, ArrayList<Process> processes, ArrayList<Process> remove) {
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
	}
	
	//terminate program
	public void terminate(Stage boss) {
		boss.close();
	}
}
