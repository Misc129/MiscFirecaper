package miscfirecaper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GUI extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JTextArea instructions;
	
	JTextField foodIdField;
	//JTextField eatValueField;
	
	JPanel optionsPane;
	
	JScrollPane infoPane;
	
	JButton goButton;
	
	JTabbedPane panes;
	
	JCheckBox disablePaint;

	public GUI(){
		createContents();
		setSize(260,200);
		setTitle("V2.020");
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		createContents();
		setVisible(true);
	}
	
	public void createContents(){
		initComponents();
		
//		optionsPane.add(new JLabel("Eat percent:"));
//		optionsPane.add(eatSlider, BorderLayout.CENTER);
		
		optionsPane.add(new JLabel("Disable paint:"));
		optionsPane.add(disablePaint);
		
		optionsPane.add(new JLabel("        Food Id:"));
		optionsPane.add(foodIdField, BorderLayout.CENTER);
		
		//optionsPane.add(new JLabel("                                    Eat health:"));
		//optionsPane.add(eatValueField, BorderLayout.CENTER);
		//eatValueField.setText("3000");
		
		
		optionsPane.add(goButton, BorderLayout.SOUTH);
		
		panes.addTab("Options", optionsPane);
		panes.addTab("Instructions", infoPane);
		
		add(panes);
		
		Listener l = new Listener();
		goButton.addActionListener(l);
	}
	
	public void initComponents(){
		panes = new JTabbedPane();
		
		instructions = new JTextArea("Step 1:\n -Set up your inventory and gear.\n" +
				" -You MUST wear a WAND and SHIELD\n" +
				" -You MUST set a WATER spell autocast\n"+
				"\nStep 2:\n -Enter your food ID into the text field.\n" +
				" -Enter a health value to eat at (for safety purposes)\n"+
				"\nStep 3:\n -Be outside of the Fight Cave OR at any wave inside the caves.\n" +
				"\nStep 4:\n -Press Go.");
		instructions.setSize(new Dimension(250,300));
		instructions.setLineWrap(true);
		instructions.setWrapStyleWord(true);
		instructions.setEditable(false);
		
		foodIdField = new JTextField(7);
		//eatValueField = new JTextField(7);
		
		disablePaint = new JCheckBox();
		
		goButton = new JButton("Go");
		goButton.setPreferredSize(new Dimension(70,30));
		
		optionsPane = new JPanel();
		infoPane = new JScrollPane(instructions);
	}
	private class Listener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent a) {
			if(a.getSource() == goButton){
				try{
					MiscFireCaper.foodId = Integer.parseInt(foodIdField.getText());
					//MiscFireCaper.eatHealth = Integer.parseInt(eatValueField.getText());
				}catch(Exception ignored){
					System.out.println("invalid text field input");
				}
				MiscFireCaper.doPaint = !disablePaint.isSelected();
				MiscFireCaper.guiDone = true;
				setVisible(false);
				dispose();	
			}
		}
		
	}
}
