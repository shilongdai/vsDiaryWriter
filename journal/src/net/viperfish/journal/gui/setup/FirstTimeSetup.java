package net.viperfish.journal.gui.setup;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FirstTimeSetup extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5531765124830505855L;
	private JPanel contentPane;
	public JButton btnNext;

	/**
	 * Create the frame.
	 */
	public FirstTimeSetup() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("inset 0, fill", "[grow]", "[grow][]"));

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, "cell 0 1,alignx right,growy");
		panel_1.setLayout(new MigLayout("inset 0, fill", "[55px][55px]", "[23px]"));

		JButton btnBack = new JButton("Back");
		btnBack.setEnabled(false);
		panel_1.add(btnBack, "cell 0 0,alignx right,aligny top");

		btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doCont = true;
			}
		});
		btnNext.setEnabled(false);
		panel_1.add(btnNext, "cell 1 0,alignx right,aligny top");
		loadConfiguration();
	}

	public ConfigView view;

	public void loadConfiguration() {
		new Thread(new Runnable() {
			public void run() {
				SimpleOrExpert simpleOrExpert = new SimpleOrExpert(FirstTimeSetup.this);
				setView(simpleOrExpert);
				// SIMPLE/Required
				
				if(simpleOrExpert.rdbtnExpert.isSelected()){
					// EXPERT/Optional
				}
				
			}
		}).start();
	}
	
	private boolean canContinue = false;
	private boolean doCont = false;

	public void setView(ConfigView view) {
		if (view != null) {
			contentPane.remove(view);
		}
		this.view = view;
		contentPane.add(view, "cell 0 0,grow");
		contentPane.updateUI();
		canContinue = false;
		btnNext.setEnabled(canContinue);
		doCont = false;
		while(!canContinue || !doCont){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void cont(){
		canContinue = true;
		btnNext.setEnabled(canContinue);
	}

}
