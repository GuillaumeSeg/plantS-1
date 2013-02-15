package app;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import renderer.JOGLRenderer;

import com.jogamp.opengl.util.FPSAnimator;
 
 
public class JOGLWindow {
 
	private JFrame m_Frame;
	private GLCanvas m_Canvas;
	private GLCapabilities m_Capabilities;
	private GLProfile m_Profile;	
	private FPSAnimator m_Animator;
	private JOGLRenderer m_Renderer;
 
	int currentMouseButtonPressed, currentKeyPressed;
	private float TpreviousPositionX = 0;
	private float TpreviousPositionY = 0;
	private float TpositionX = 0;
	private float TpositionY = 0;
	private float mouseX = 0;
	private float mouseY = 0;
	private float nextMouseX = 0;
	private float nextMouseY = 0;
 
	private float RpreviousPositionX = 0;
	private float RpreviousPositionY = 0;
	private float RpositionX = 0;
	private float RpositionY = 0;
	
	private int WINDOWS_WIDTH = 900;
	private int WINDOWS_HEIGHT = 800;
	
	public JOGLWindow(String name){
 
		m_Frame = new JFrame(name);
		m_Frame.setSize(WINDOWS_WIDTH, WINDOWS_HEIGHT);
		
		m_Profile = GLProfile.getDefault();
		m_Capabilities = new GLCapabilities(m_Profile);
		m_Canvas = new GLCanvas(m_Capabilities);
		
		currentMouseButtonPressed = 0;
		
		/* LISTENERS */
		
		m_Frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				m_Frame.dispose();
				System.exit(0);
			}
		});
		
		m_Canvas.addMouseWheelListener(new MouseWheelListener() {

			public void mouseWheelMoved(MouseWheelEvent e) {
				
				if(e.getWheelRotation() == -1) {
					if(m_Renderer.getTCamera().getDistance() > 0.1) {
						m_Renderer.getTCamera().moveFront(0.05f);
					}
				} else {
					m_Renderer.getTCamera().moveFront(-0.01f);
				}

				if(e.getWheelRotation() == 1) {
					if(m_Renderer.getTCamera().getDistance() > 0.1) {
						m_Renderer.getTCamera().moveFront(-0.05f);
					}
				} else {
					m_Renderer.getTCamera().moveFront(0.01f);
				}
			}
		});
 
		m_Canvas.addMouseListener(new MouseListener() {
 
			public void mouseReleased(MouseEvent e) {
				currentMouseButtonPressed = 0;
			}
 
			public void mousePressed(MouseEvent e) {
				currentMouseButtonPressed = e.getButton();
				RpreviousPositionX = e.getX();
				RpreviousPositionY = e.getY();
				TpreviousPositionX = e.getX();
				TpreviousPositionY = e.getY();
			}
 
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}
			public void mouseClicked(MouseEvent e) {}
		});
 
		m_Canvas.addMouseMotionListener(new MouseMotionListener() {
 
			public void mouseMoved(MouseEvent e) {
				
				nextMouseX = e.getX();
				nextMouseY = e.getY();
				float xRel = (nextMouseX - mouseX >= 0) ? ((nextMouseX - mouseX == 0) ? 0f : -2f) : 2f;
				float yRel = (nextMouseY - mouseY >= 0) ? ((nextMouseY - mouseY == 0) ? 0f : -2f) : 2f;
				m_Renderer.getFPSCamera().rotateUp(yRel);
				m_Renderer.getFPSCamera().rotateLeft(xRel);
				mouseX = nextMouseX;
				mouseY = nextMouseY;
				xRel = yRel = 0;
				
			}
			
			public void mouseDragged(MouseEvent e) {
				/*switch(currentMouseButtonPressed) {
					case 2 :
						TpositionX = e.getX();
						TpositionY = e.getY();
						float tx = 0.004f*(TpositionX - TpreviousPositionX);
						float ty = -0.004f*(TpositionY - TpreviousPositionY);
						m_Renderer.getTCamera().setTarget(tx, ty);
						TpreviousPositionX = TpositionX;
						TpreviousPositionY = TpositionY;
					break;
					case 3 :
						RpositionX = e.getX();
						RpositionY = e.getY();
						float rLeft = -0.01f *(RpositionX - RpreviousPositionX);
						float rUp = -0.01f * (RpositionY - RpreviousPositionY);
						m_Renderer.getTCamera().rotateUp(rUp);
						m_Renderer.getTCamera().rotateLeft(rLeft);
						RpreviousPositionX = RpositionX;
						RpreviousPositionY = RpositionY;
					break;
				}*/
			}
		});
		
		m_Canvas.addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e) {
				
				switch(e.getKeyChar()) {
					case 'f' : case 'F' :
						//m_Renderer.getTCamera().goToFrontView();
					break;
					case 't' : case 'T' :
						//m_Renderer.getTCamera().goToTopView();
					break;
					case 'l' : case 'L' :
						//m_Renderer.getTCamera().goToLeftView();
					break;
					case 'r' : case 'R' :
						//m_Renderer.getTCamera().goToRightView();
					break;
					case 'z' : case 'Z' : 
						m_Renderer.getFPSCamera().moveFront(1f);
					break;
					case 's' : case 'S' : 
						m_Renderer.getFPSCamera().moveFront(-1f);
					break;
					case 'q' : case 'Q' : 
						m_Renderer.getFPSCamera().moveLeft(1f);
					break;
					case 'd' : case 'D' : 
						m_Renderer.getFPSCamera().moveLeft(-1f);
					break;
					case 'n' : case 'N' :
						m_Renderer.createNewTree();
					break;
					
				}
			}
			
			public void keyReleased(KeyEvent e) {}	
			public void keyPressed(KeyEvent e) {}
			
		});
		
		
		m_Renderer = new JOGLRenderer(WINDOWS_WIDTH, WINDOWS_HEIGHT);
		m_Canvas.addGLEventListener(m_Renderer);
		
		JPanel viewBarre = new JPanel();
		viewBarre.setLayout(new FlowLayout());
		
		JButton treebutton = new JButton();
		ImageIcon treeicon = new ImageIcon("src/icon/tree.png");
		treebutton.setIcon(treeicon);
		treebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_Renderer.createNewTree();
			}
		});

		viewBarre.add(treebutton);
		viewBarre.setBackground(Color.BLACK);
		
		m_Canvas.setBackground(Color.BLACK);
		
		m_Frame.getContentPane().add(viewBarre, BorderLayout.SOUTH);
		m_Frame.getContentPane().add(m_Canvas, BorderLayout.CENTER);
 
		m_Animator = new FPSAnimator(m_Canvas, 60);
		m_Animator.add(m_Canvas);
		
		Timer fpsTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String framename = m_Renderer.updateFPS();
                m_Frame.setTitle(framename);
            }
        });
		
		fpsTimer.setRepeats(true);
        fpsTimer.start();
		
		m_Animator.start();
		m_Frame.setVisible(true);	
	}
	
	public int getWidth() {
		return WINDOWS_WIDTH;
	}
	
	public int getHeight() {
		return WINDOWS_HEIGHT;
	}
	
}
