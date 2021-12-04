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
			
			
			// Panes or Containers for center components
			HBox inputArgs = new HBox();
			inputArgs.setPrefSize(450, 200);
			
			
			//Create Children Panes
			BorderPane smallPane = new BorderPane(); //Container for right controls, slider and switch
			VBox processGen = new VBox(); //container for process generation toggling equivalent to a pause
			BorderPane btnSnap = new BorderPane();// Container for snapshot button - shows analysis at that point in time, make plot
			btnSnap.setPrefSize(650,100);
			
			//Create buttons and components for simulation 
			Button snapShot = new Button("VIEW SIMULATOR II"); // Get analytic snapshot for both running simulations
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
			
			Button startSim = new Button("START");
			Button stopSim = new Button("STOP"); //Stop Button - becomes visible when start button is pressed
			
			ListView<String> procRun = new ListView<String>(); //Listview to hold processes
			
			//Styling areas (besides memory slots - done above)
			paneCenterComponents.setStyle("-fx-background-color:lightgray;");
			paneBottomComponents.setStyle("-fx-background-color:darkgray;");
			
			//Set options and initialization for buttons and components
			snapShot.setMaxWidth(Double.MAX_VALUE);
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
			inputArgs.getChildren().addAll(firstFit,bestFit, worstFit,nextFit);

			
			//Putting children panes into Parent Panes/containers
			controlsLeftB.getChildren().add(startSim);
			controlsRightB.getChildren().add(stopSim);
			startStopButtons.getChildren().addAll(controlsLeftB,controlsRightB);
			paneBottomComponents.getChildren().addAll(startStopButtons, btnSnap);
			paneCenterComponents.getChildren().addAll(inputArgs, smallPane);

			SimulatorUI myPane = new SimulatorUI();
			SimulatorUI secondSim = new SimulatorUI();
			
			paneTopComponents.getChildren().addAll(myPane.mainComponent());
			
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
	        
	        EventHandler<ActionEvent> checkBoxBestHandler = new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent e)
	            {	  
    				if(!myPane.argSet && !secondSim.argSet) {
    					myPane.argSet = true;
    					myPane.runningAlgo = "best";
    					bestFit.setDisable(true);
    				} else {
    				if(!myPane.argSet || !secondSim.argSet) {
    					secondSim.argSet = true;
    					secondSim.runningAlgo = "best";
    					bestFit.setDisable(true);
    					firstFit.setDisable(true);
    					worstFit.setDisable(true);
    					nextFit.setDisable(true);
    				}
    				}
        			
	            }
	        };
	        
	        EventHandler<ActionEvent> checkBoxFirstHandler = new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent e)
	            {
	        		if(!myPane.argSet && !secondSim.argSet) {
    					myPane.argSet = true;
    					myPane.runningAlgo = "first";
    					firstFit.setDisable(true);
    				} else {
    				if(!myPane.argSet || !secondSim.argSet) {
    					secondSim.argSet = true;
    					secondSim.runningAlgo = "first";
    					firstFit.setDisable(true);
    					bestFit.setDisable(true);
    					worstFit.setDisable(true);
    					nextFit.setDisable(true);
    				}
    				}
	            }
	        };
	        
	        EventHandler<ActionEvent> checkBoxWorstHandler = new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent e)
	            {      
	        		if(!myPane.argSet && !secondSim.argSet) {
    					myPane.argSet = true;
    					myPane.runningAlgo = "worst";
    					worstFit.setDisable(true);
    				} else {
    				if(!myPane.argSet || !secondSim.argSet) {
    					secondSim.argSet = true;
    					secondSim.runningAlgo = "worst";
    					worstFit.setDisable(true);
    					firstFit.setDisable(true);
    					bestFit.setDisable(true);
    					nextFit.setDisable(true);
    				}
    				}
	            }
	        };
	        
	        EventHandler<ActionEvent> checkBoxNextHandler = new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent e)
	            {
	        		if(!myPane.argSet && !secondSim.argSet) {
    					myPane.argSet = true;
    					myPane.runningAlgo = "next";
    					nextFit.setDisable(true);
    				} else {
    				if(!myPane.argSet || !secondSim.argSet) {
    					secondSim.argSet = true;
    					secondSim.runningAlgo = "next";
    					nextFit.setDisable(true);
    					firstFit.setDisable(true);
    					worstFit.setDisable(true);
    					bestFit.setDisable(true);
    				}
    				}
	            }
	        };
	        
	        snapShot.setOnAction(e -> viewSim(secondSim.mainComponent()));
	        
	        timeMultiplier.valueProperty().addListener(ov -> {
		        simulationSpeed.setSimSpeed(timeMultiplier.getValue() * 2000 / timeMultiplier.getMax());
		        myPane.messages.add("current simulation speed" + simulationSpeed.getSimSpeed());
		        secondSim.messages.add("current simulation speed" + simulationSpeed.getSimSpeed());
	        });
			
			//Actions from buttons and other event handlers
			startSim.setOnAction(startEvent);
			stopSim.setOnAction(e -> {
				shut.setSelected(true);
                cont.setSelected(false);
			});
			
			bestFit.setOnAction(checkBoxBestHandler);
			worstFit.setOnAction(checkBoxWorstHandler);
			nextFit.setOnAction(checkBoxNextHandler);
			firstFit.setOnAction(checkBoxFirstHandler);
			
			//Start out with buttons greyed out
			startSim.setDisable(true);
			stopSim.setDisable(true);
			
			//Create scene and place on stage
			Scene scene = new Scene(panemaster,650,800);

			primaryStage.setTitle("Memory Simulator COSC 519 - GROUP 3"); // Set the stage title
			primaryStage.setScene(scene); // Place the scene in the stage
			primaryStage.show(); // Display the stage
			
			
			//Create Thread to run simulation
			Thread simThread = new Thread(new Runnable() {
				public void run(){
//					System.out.println(!checkArgsUI(bestFit, worstFit, firstFit, nextFit));
//					while(!checkArgsUI(bestFit, worstFit, firstFit, nextFit)) {};
					while(group.getSelectedToggle() != cont) {
						if(firstFit.isDisabled() && worstFit.isDisabled() && bestFit.isDisabled() && nextFit.isDisabled()) {
							startSim.setDisable(false);
							stopSim.setDisable(false);
						}
					} //To Begin Simulation
					//Simulation options
//					myPane.runningAlgo = "best";
//					paramsFromUI[1] = "first";
					System.out.println("Running.................."+ myPane.runningAlgo + " " + secondSim.runningAlgo);
					
					//set simulation
					String simName = myPane.runningAlgo;
					String simName2 = secondSim.runningAlgo;
					MemorySimulator sim = new MemorySimulator();
					MemorySimulator sim2 = new MemorySimulator();
					sim.setSlotAlgorithm(simName);
					sim2.setSlotAlgorithm(simName2);
					myPane.memSim = sim;
					secondSim.memSim = sim2;
					
					//run simulation
					System.out.println(group.getSelectedToggle() != shut);
					System.out.println(pauseSim.getSelectedToggle() != paused);
					while (group.getSelectedToggle() != shut) {
						System.out.println("Running..................");
						if(pauseSim.getSelectedToggle() != paused) {
							sim.timeStep();
							sim2.timeStep();
			    			sim.printMemory();	
			    			sim2.printMemory();	
						}
						ArrayList<Process> processes = sim.processes;
		    			ArrayList<Process> remove = sim.processesDone;
		    			myPane.messages.add("Simulation Time " + sim.currentTime);
		    			int arraySize = processes.size();
		    			String[] procNames = new String[arraySize];
		    			int insertNames = 0;
		    			for(Process p: processes) {
		    				procNames[insertNames] = (String) "" + p.getPname();
		    				insertNames++;
		    			}
		    			
		    			ArrayList<Process> processes2 = sim2.processes;
		    			ArrayList<Process> remove2 = sim2.processesDone;
		    			secondSim.messages.add("Simulation Time " + sim2.currentTime);
		    			int arraySize2 = processes2.size();
		    			String[] procNames2 = new String[arraySize2];
		    			int insertNames2 = 0;
		    			for(Process p: processes2) {
		    				procNames2[insertNames2] = (String) "" + p.getPname();
		    				insertNames2++;
		    			}
		    			
		    			//Function to update UI
		    			Platform.runLater(new Runnable() {
		    				//For each process in processes(Runnning processes including reserved memory, alter boxes in UI)
							public void run(){
								updateUI(myPane.middleBox, processes, remove);
								updateUI(secondSim.middleBox, processes2, remove2);
								
								//For each process in remove(processes pending removal, reset in UI)
								myPane.updateSimInfo();
								secondSim.updateSimInfo();

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
	public void viewSim(Pane otherSim) {
		Pane simMasterPane = new Pane();
		simMasterPane.getChildren().add(otherSim);
		Scene scene = new Scene(simMasterPane, 700, 700);
		Stage primaryStage = new Stage();
		primaryStage.setTitle("Current State"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage
	}
	
	public static void main(String[] args) {
		//If no arguments passed, set default arguments
		//Use arguments from command line to intialize ui corresponding fields
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
	
}
