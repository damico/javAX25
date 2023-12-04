package org.jdamico.javax25;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import org.jdamico.javax25.soundcard.Soundcard;

public class GuiApp extends JPanel implements ActionListener {
	
	private JTextArea textArea = new JTextArea(40, 80);
	private JComboBox devicesComboBox = null; 
	private JButton decodeBtn = null;
	private JButton resetBtn = null;
	private JLabel audioLevelLabel = null;
	private JLabel audioLevelValue = null;
	private Thread guiDecoderThread;

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });		
	}
	
	public GuiApp() {
        super(new BorderLayout());
        
        JPanel northPanel = new JPanel();  // **** to hold buttons
 
        List<String> lst = Soundcard.getInputDevicesLst();
        String[] deviceArray = new String[lst.size()];
        for (int i = 0; i < deviceArray.length; i++) {
			deviceArray[i] = lst.get(i);
		}
        devicesComboBox = new JComboBox(deviceArray);
        northPanel.add(devicesComboBox);
        
        decodeBtn = new JButton("Decode Audio");
        decodeBtn.addActionListener(this);
        northPanel.add(decodeBtn);
        
        resetBtn = new JButton("Reset");
        
        
        resetBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Soundcard.running = false;
				guiDecoderThread.interrupt();
				devicesComboBox.setEnabled(true);
				decodeBtn.setEnabled(true);
				resetBtn.setEnabled(false);
			}
		});
        
        resetBtn.setEnabled(false);
        
        northPanel.add(resetBtn);
        
        audioLevelLabel = new JLabel("Audio Level: ");
        northPanel.add(audioLevelLabel);
        
        audioLevelValue = new JLabel("000");
        audioLevelValue.setForeground(Color.red);
        northPanel.add(audioLevelValue);
        
        add(northPanel, BorderLayout.PAGE_START);
        
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);   
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        add(scrollPane, BorderLayout.CENTER);
        
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    }
	
	private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("JavaX25 Decoder v.0.0.1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon img = new ImageIcon("dist/icon.png");
        frame.setIconImage(img.getImage());
 
        //Create and set up the content pane.
        JComponent newContentPane = new GuiApp();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

	@Override
	public void actionPerformed(ActionEvent arg0) {
		decode();
		
	}
	
	private void decode() {
		decodeBtn.setEnabled(false);
		devicesComboBox.setEnabled(false);
		resetBtn.setEnabled(true);
		String input = (String) devicesComboBox.getSelectedItem();
		Soundcard.jTextArea = textArea;
		Soundcard.audioLevelValue = audioLevelValue;
		guiDecoderThread = new GuiDecoderThread(input);
		guiDecoderThread.start();
	}

}
