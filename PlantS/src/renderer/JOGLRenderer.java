package renderer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import matrix.GLMatrix;
import matrix.GLMatrixTransform;
import matrix.MatrixStack;

import org.jdom2.Element;

import utils.CST;
import xml.JDOMCreate;
import xml.JDOMHierarchy;
import camera.FreeFlyCamera;
import camera.TrackballCamera;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import dataTree.LeafTreeNode;
import dataTree.PlantsTreeNode;
import dataTree.TrunckTreeNode;
import drawable.Cube;
import drawable.HeightMap;
import drawable.Square;

public class JOGLRenderer implements GLEventListener {

	JOGLShaderProgram m_ShaderProgram;
	int m_iTreeProgram, m_iSkyboxProgram, m_iGroundProgram, m_iLeafProgram;
	ArrayList<DefaultMutableTreeNode> m_Trees;
	DefaultMutableTreeNode m_TreeRoot;

	MatrixStack m_Stack;
	int m_treeMVLocation, m_treePLocation, m_treeMVcLocation,
			m_treeMVpLocation, m_treeMVbLocation, m_treeScLocation,
			m_treeSpLocation, m_treeSbLocation, m_treeTexLocation;
	int m_skyMVLocation, m_skyPLocation, m_skyTexLocation;
	int m_groundMVLocation, m_groundPLocation, m_groundTexLocation,
			m_groundHmapLocation;
	int m_leafMVLocation, m_leafPLocation, m_leafTexLocation, m_leafMVcLocation;
	TrackballCamera m_TCamera;
	FreeFlyCamera m_FPSCamera;

	float angle, distance;
	Cube cube = new Cube();
	Square square = new Square();
	int idTexture;
	private Texture texSky, texGround, texTree, texHeightMap, texLeaf;
	HeightMap hmap;

	private int FPS;

	int WINDOWS_WIDTH;
	int WINDOWS_HEIGHT;
	GLU glu = new GLU();
	Random random = new Random();
	int DISPLAYTEXTURE_ID = GL.GL_TEXTURE1;
	int HMAPTEXTURE_ID = GL.GL_TEXTURE2;
	
	Matrix4f P;

	public JOGLRenderer(int width, int height) {
		WINDOWS_WIDTH = width;
		WINDOWS_HEIGHT = height;
	}

	public void createNewTree() {
		
		int i = m_Trees.size();
		String filename = "src/xml/tree" + i + ".xml";
		File f = new File(filename);
		f.delete();
		
		JDOMCreate treeCreator =  new JDOMCreate(filename);
		
		//Create JDOM Hierarchy
		JDOMHierarchy jdom = new JDOMHierarchy(new File(filename));
		
		// Init tree structure
		PlantsTreeNode root = new TrunckTreeNode();
		DefaultMutableTreeNode treeNodeRoot = new DefaultMutableTreeNode(root, true);

		// Fill Tree structure from JDOM Hierarchy
		fillTree(jdom.getRoot(), treeNodeRoot);
		
		// Compute Tree's ModelView Matrix
		MatrixStack stack = new MatrixStack();
		computeMVmatrix(treeNodeRoot, stack);
		m_Trees.add(treeNodeRoot);
		
	}
	
 	public void display(GLAutoDrawable drawable) {

		GL3 gl = drawable.getGL().getGL3();

		gl.glClearColor(0.f, 0.f, 0.f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		if (glu.gluErrorString(gl.glGetError()) != "no error") {
			System.err.println(glu.gluErrorString(gl.glGetError()));
			System.err.println("End of program");
			System.exit(0);
		}
		
		m_Stack.push();

			m_Stack.mult(m_FPSCamera.getViewMatrix());
	
			// RENDER SKYBOX
			m_Stack.push();
				gl.glUseProgram(m_iSkyboxProgram);
				gl.glActiveTexture(DISPLAYTEXTURE_ID);
				texSky.enable(gl);
				texSky.bind(gl);
				gl.glUniform1i(m_skyTexLocation, 1);
				m_Stack.translate(m_FPSCamera.position());
				m_Stack.scale(new Vector3f(1, 1, 1));
				gl.glUniformMatrix4fv(m_skyMVLocation, 1, false, GLMatrix.parseToFloatArray(m_Stack.top()), 0);
				cube.draw(gl);
			m_Stack.pop();
	
			// RENDER GROUND
			m_Stack.push();
				gl.glUseProgram(m_iGroundProgram);
				gl.glActiveTexture(DISPLAYTEXTURE_ID);
				texGround.enable(gl);
				texGround.bind(gl);
				gl.glUniform1i(m_groundTexLocation, 1);
				gl.glActiveTexture(HMAPTEXTURE_ID);
				texHeightMap.enable(gl);
				texHeightMap.bind(gl);
				gl.glUniform1i(m_groundHmapLocation, 2);
				m_Stack.translate(new Vector3f(0.0f, -5f, 0.0f));
				m_Stack.scale(new Vector3f(200, 1, 200));
				gl.glUniformMatrix4fv(m_groundMVLocation, 1, false, 	GLMatrix.parseToFloatArray(m_Stack.top()), 0);
				hmap.draw(gl);
			m_Stack.pop();		
	
		m_Stack.pop();

		// RENDER TREES
		random.setSeed(0);
		m_Stack.push();
			for(DefaultMutableTreeNode tree : m_Trees) {	
				m_Stack.translate(new Vector3f((random.nextFloat()*100)-50, 0.0f, (random.nextFloat()*100)-50));
				render(tree, gl);
			}
		m_Stack.pop();
		++FPS;
	}

	public void dispose(GLAutoDrawable drawable) {

	}

	public void init(GLAutoDrawable drawable) {
 
		// Init variables
		angle = 0;
		distance = 0;
		FPS = 0;
		
		// Get openGL context
		GL3 gl = drawable.getGL().getGL3();		
		
		// Init openGL
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_TEXTURE_2D);

		// Load Shaders
		JOGLShaderProgram treeShaderProgram = new JOGLShaderProgram(drawable, new File("src/shaders/tree.vs.glsl"), new File("src/shaders/tree.fs.glsl"));
		m_iTreeProgram = treeShaderProgram.getProgram();
		JOGLShaderProgram skyboxShaderProgram = new JOGLShaderProgram(drawable, new File("src/shaders/skybox.vs.glsl"), new File("src/shaders/skybox.fs.glsl"));
		m_iSkyboxProgram = skyboxShaderProgram.getProgram();
		JOGLShaderProgram groundShaderProgram = new JOGLShaderProgram(drawable, new File("src/shaders/ground.vs.glsl"), new File("src/shaders/ground.fs.glsl"));
		m_iGroundProgram = groundShaderProgram.getProgram();
		JOGLShaderProgram leafShaderProgram = new JOGLShaderProgram(drawable, new File("src/shaders/leaf.vs.glsl"), new File("src/shaders/leaf.fs.glsl"));
		m_iLeafProgram = leafShaderProgram.getProgram();
		
		// Get uniform variables locations for tree shaders
		m_treeMVLocation = gl.glGetUniformLocation(m_iTreeProgram, "uniform_MV");
		m_treePLocation = gl.glGetUniformLocation(m_iTreeProgram, "uniform_P");
		m_treeMVcLocation = gl.glGetUniformLocation(m_iTreeProgram, "uniform_MVc");
		m_treeMVpLocation = gl.glGetUniformLocation(m_iTreeProgram, "uniform_MVp");
		m_treeMVbLocation = gl.glGetUniformLocation(m_iTreeProgram, "uniform_MVb");
		m_treeScLocation = gl.glGetUniformLocation(m_iTreeProgram, "uniform_Sc");
		m_treeSpLocation = gl.glGetUniformLocation(m_iTreeProgram, "uniform_Sp");
		m_treeSbLocation = gl.glGetUniformLocation(m_iTreeProgram, "uniform_Sb");
		m_treeTexLocation = gl.glGetUniformLocation(m_iTreeProgram, "uTex");
		
		// Get uniform variables locations for skybox shaders
		m_skyMVLocation = gl.glGetUniformLocation(m_iSkyboxProgram, "uniform_MV");
		m_skyPLocation = gl.glGetUniformLocation(m_iSkyboxProgram, "uniform_P");
		m_skyTexLocation = gl.glGetUniformLocation(m_iSkyboxProgram, "uTex");
		
		// Get uniform variables locations for ground shaders
		m_groundMVLocation = gl.glGetUniformLocation(m_iGroundProgram, "uniform_MV");
		m_groundPLocation = gl.glGetUniformLocation(m_iGroundProgram, "uniform_P");
		m_groundTexLocation = gl.glGetUniformLocation(m_iGroundProgram, "uTex");
		m_groundHmapLocation = gl.glGetUniformLocation(m_iGroundProgram, "uHmap");
		
		// Get uniform variables locations for ground shaders
		m_leafMVLocation = gl.glGetUniformLocation(m_iLeafProgram, "uniform_MV");
		m_leafPLocation = gl.glGetUniformLocation(m_iLeafProgram, "uniform_P");
		m_leafTexLocation = gl.glGetUniformLocation(m_iLeafProgram, "uTex");
		m_leafMVcLocation = gl.glGetUniformLocation(m_iLeafProgram, "uniform_MVc");
		
		// Create Pojection Matrix
		P = new Matrix4f(GLMatrixTransform.Perspective(70.0f, WINDOWS_WIDTH/(float)WINDOWS_HEIGHT, 0.1f, 1000.0f));
		
		// Send projection matrix to shaders
		gl.glUseProgram(m_iTreeProgram);
		gl.glUniformMatrix4fv(m_treePLocation, 1, false, GLMatrix.parseToFloatArray(P), 0);
		
		gl.glUseProgram(m_iSkyboxProgram);
		gl.glUniformMatrix4fv(m_skyPLocation, 1, false, GLMatrix.parseToFloatArray(P), 0);
		
		gl.glUseProgram(m_iGroundProgram);
		gl.glUniformMatrix4fv(m_groundPLocation, 1, false, GLMatrix.parseToFloatArray(P), 0);
		
		gl.glUseProgram(m_iLeafProgram);
		gl.glUniformMatrix4fv(m_leafPLocation, 1, false, GLMatrix.parseToFloatArray(P), 0);
		
		// Create Matrix Stack
		m_Stack = new MatrixStack();
		
		// Create & init Trackball Camera
		//m_TCamera = new TrackballCamera();
		//m_TCamera.goToFrontView();
		m_FPSCamera = new FreeFlyCamera();
 
		// Get JDOM Hierarchy from xml file
		m_Trees = new ArrayList<DefaultMutableTreeNode>();
		
		// Load textures
		try {
			texSky = TextureIO.newTexture(new File("src/img/skybox.jpg"), true);
			texGround = TextureIO.newTexture(new File("src/img/sand.jpg"), true);
			texHeightMap = TextureIO.newTexture(new File("src/img/hmap.png"), false);
			texTree = TextureIO.newTexture(new File("src/img/tex_tree4.jpg"), false);
			texLeaf = TextureIO.newTexture(new File("src/img/leaf.png"), true);
        } catch (IOException exc) {
            exc.printStackTrace();
            System.exit(1);
        } catch (GLException glexc) {
        	glexc.printStackTrace();
            System.exit(1);
		}
		
		// Set Textures parameters
		texSky.setTexParameterf(gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		texSky.setTexParameterf(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		texHeightMap.setTexParameterf(gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		texHeightMap.setTexParameterf(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		texTree.setTexParameterf(gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		texTree.setTexParameterf(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		texLeaf.setTexParameterf(gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		texLeaf.setTexParameterf(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		
		// Create HeightMap
		try {
			hmap = new HeightMap("src/img/hmap.png", 1f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, 	int height) {
		
		// Get openGL Context
		GL3 gl = drawable.getGL().getGL3();

		// Update frame size values
		WINDOWS_WIDTH = width;
		WINDOWS_HEIGHT = height;

		// Define new Projection matrix
		P = new Matrix4f(GLMatrixTransform.Perspective(70.0f, WINDOWS_WIDTH/(float) WINDOWS_HEIGHT, 0.1f, 1000.0f));

		// Send projection Matrix to shaders
		gl.glUseProgram(m_iTreeProgram);
		gl.glUniformMatrix4fv(m_treePLocation, 1, false, GLMatrix.parseToFloatArray(P), 0);
		gl.glUseProgram(m_iSkyboxProgram);
		gl.glUniformMatrix4fv(m_skyPLocation, 1, false, GLMatrix.parseToFloatArray(P), 0);
		gl.glUseProgram(m_iGroundProgram);
		gl.glUniformMatrix4fv(m_groundPLocation, 1, false, GLMatrix.parseToFloatArray(P), 0);
		gl.glUseProgram(m_iLeafProgram);
		gl.glUniformMatrix4fv(m_leafPLocation, 1, false, GLMatrix.parseToFloatArray(P), 0);
	}

	public void computeMVmatrix(DefaultMutableTreeNode root, MatrixStack stack) {
		
		if(((PlantsTreeNode)root.getUserObject()).getType() == "leaf") {
			
			float ty = -((LeafTreeNode)root.getUserObject()).lengthTrunck + ((LeafTreeNode)root.getUserObject()).lengthTrunck*(((LeafTreeNode)root.getUserObject()).height)/100.0f;
			float tx = ((LeafTreeNode)root.getUserObject()).radiusTrunck;
			float orientation = ((LeafTreeNode)root.getUserObject()).orientation;
			
			stack.translate(new Vector3f(0f, ty, 0f)); // translate h
			stack.rotate(new Vector3f(0f, 1f, 0f), orientation);
			stack.translate(new Vector3f(tx, 0f, 0f));
			stack.rotate(((PlantsTreeNode) root.getUserObject()).getRotationMatrix());
			stack.scale(new Vector3f(((LeafTreeNode)root.getUserObject()).scale, ((LeafTreeNode)root.getUserObject()).scale, 1f));
			((PlantsTreeNode) root.getUserObject()).setMV(stack.top());
		}
		
		if(((PlantsTreeNode)root.getUserObject()).getType() == "trunck") {
			stack.rotate(((PlantsTreeNode) root.getUserObject()).getRotationMatrix());
			((PlantsTreeNode) root.getUserObject()).setMV(stack.top());
			for (int i = 0; i < root.getChildCount(); i++) {
				stack.push();
					float length = ((TrunckTreeNode) root.getUserObject()).length;
					stack.translate(new Vector3f(0.0f, length, 0.0f));
					computeMVmatrix((DefaultMutableTreeNode) root.getChildAt(i), stack);
				stack.pop();
			}
		}
	}

	public void fillTree(Element JDOMelement, DefaultMutableTreeNode tree) {

		if (!JDOMelement.getChildren().isEmpty()) {

			List<Element> childrenList = JDOMelement.getChildren();
			Iterator<Element> itChildren = childrenList.iterator();

			// Get children axes
			ArrayList<Vector3f> axesList = new ArrayList<Vector3f>();
			float[] radiusList = new float[3];
			int q = 0;
			for (Element e : childrenList) {
				if (e.getName() == "trunck") {
					String axeXYZ = e.getAttributeValue("axe");
					String vect[] = axeXYZ.split(" ");
					float x = Float.parseFloat(vect[0]);
					float y = Float.parseFloat(vect[1]);
					float z = Float.parseFloat(vect[2]);
					axesList.add(new Vector3f(x, y, z));
					radiusList[q] = Float.parseFloat(e.getAttributeValue("radius"));
					++q;
				}
			}
			itChildren = childrenList.iterator();
			int numChild = 0;
			int nbChildren = axesList.size();

			while (itChildren.hasNext()) {

				Element JDOMchild = (Element) itChildren.next();
				DefaultMutableTreeNode treeChild = null;

				switch (JDOMchild.getName()) {

				case "trunck":
					
					float rad = Float.parseFloat(JDOMchild.getAttributeValue("radius"));
					float length = Float.parseFloat(JDOMchild.getAttributeValue("length"));
					
					float radp = rad;
					if(JDOMchild.getParentElement().getAttributeValue("radius") != null) {
						radp = Float.parseFloat(JDOMchild.getParentElement().getAttributeValue("radius"));
					}

					float radb = 1;
					if(nbChildren > 1) {
						if(numChild == 0) {
							radb = radiusList[1];
						} else {
							radb = radiusList[0];
						}	
					}

					String axeXYZ = JDOMchild.getAttributeValue("axe");
					String vect[] = axeXYZ.split(" ");
					float x = Float.parseFloat(vect[0]);
					float y = Float.parseFloat(vect[1]);
					float z = Float.parseFloat(vect[2]);


					
					float plength;
					if(JDOMchild.getParentElement() == null) {
						plength = 0;
					} else {
						plength = Float.parseFloat(JDOMchild.getParentElement().getAttributeValue("length"));
					}
					Vector3f v = new Vector3f(x, y, z);
					v.normalize();

					Matrix4f PASSAGEbrotherParent = new Matrix4f();
					PASSAGEbrotherParent.setIdentity();

					// Matrice de passage FILS -> PERE

					Vector3f X = new Vector3f(1.0f, 0.0f, 0.0f);
					Vector3f w = new Vector3f();
					Vector3f u = new Vector3f();

					if(Math.abs(X.dot(v)) > 0.8) {
						X.x = 0.0f;
						X.y = 1.0f;
						X.z = 0.0f;
						w.cross(v, X);
						w.normalize();
						u.cross(v, w);
						u.normalize();
					} else {
						w.cross(X, v);
						w.normalize();
						u.cross(v, w);
						u.normalize();
					}

					Matrix4f PASSAGEchildParent = new Matrix4f();

					if(plength == 0) {
						PASSAGEchildParent = new Matrix4f(
								1.0f,0.0f,0.0f,0.0f,
								0.0f,1.0f,0.0f,0.0f,
								0.0f,0.0f,1.0f,0.0f,
								0.0f,0.0f,0.0f,1.0f);
					}
					else if(v.x < 0) {
						PASSAGEchildParent = new Matrix4f(
								(float)Math.cos(Math.PI/4),(float)Math.sin(Math.PI/4), 0 , 0,
								-(float)Math.sin(Math.PI/4),(float)Math.cos(Math.PI/4), 0, 0,
								0					, 0	,					1.0f, 0,
								0					, 0	,					0, 1.0f);
					}
					else {
						PASSAGEchildParent = new Matrix4f(
							(float)Math.cos(Math.PI/4),-(float)Math.sin(Math.PI/4), 0 , 0,
							(float)Math.sin(Math.PI/4),(float)Math.cos(Math.PI/4), 0, 0,
							0					, 0	,					1.0f, 0,
							0					, 0	,					0, 1.0f);
					}

					if(nbChildren == 1) {
						PASSAGEchildParent.m13 = 0.87f*plength;
					} else {
						PASSAGEchildParent.m13 = 0.87f*plength;
					}

					Matrix4f PASSAGEchildBrother = new Matrix4f();

					if(nbChildren > 1) {

						// Matrice de passage FRERE -> PERE

						Vector3f brotherAxe = new Vector3f();

						if(numChild == 0) {
							brotherAxe = axesList.get(1);
						} else {
							brotherAxe = axesList.get(0);
						}

						if(Math.abs(brotherAxe.dot(X)) > 0.5) {
							u.cross(brotherAxe, new Vector3f(1, 0, 0));
						} else {
							u.cross(brotherAxe, X);
						}
						u.normalize();
						w = new Vector3f();
						w.cross(u, brotherAxe);
						w.normalize();

						if(v.x < 0) {
							PASSAGEchildBrother = new Matrix4f(
									(float)Math.cos(Math.PI/2),(float)Math.sin(Math.PI/2), 0 , 0,
									-(float)Math.sin(Math.PI/2),(float)Math.cos(Math.PI/2), 0, 0,
									0					, 0	,					1.0f, 0,
									0					, 0	,					0, 1.0f);
						}
						else {
							PASSAGEchildBrother = new Matrix4f(
								(float)Math.cos(Math.PI/2),-(float)Math.sin(Math.PI/2), 0 , 0,
								(float)Math.sin(Math.PI/2),(float)Math.cos(Math.PI/2), 0, 0,
								0					, 0	,					1.0f, 0,
								0					, 0	,					0, 1.0f);
						}

					}


					else {
						PASSAGEchildBrother.setIdentity();
					}

					boolean uniqueChild = (nbChildren==1)?true:false;

					treeChild = new DefaultMutableTreeNode(new TrunckTreeNode(length, v, rad, radp, radb, PASSAGEchildParent, PASSAGEchildBrother, uniqueChild));

					break;

				case "leaf":
					float rho = Float.parseFloat(JDOMchild.getAttributeValue("rho"));
					float theta = Float.parseFloat(JDOMchild.getAttributeValue("theta"));
					float phi = Float.parseFloat(JDOMchild.getAttributeValue("phi"));
					float scale = Float.parseFloat(JDOMchild.getAttributeValue("scale"));
					float height = Float.parseFloat(JDOMchild.getAttributeValue("height"));
					float currentRadius = Float.parseFloat(JDOMchild.getAttributeValue("radiusCurrent"));
					float currentLength = Float.parseFloat(JDOMchild.getAttributeValue("lengthCurrent"));
					float orientation = Float.parseFloat(JDOMchild.getAttributeValue("angleDiscLat"));
					int type = Integer.parseInt(JDOMchild.getAttributeValue("type"));
					treeChild = new DefaultMutableTreeNode(new LeafTreeNode(rho, theta, phi, scale, height, currentRadius, currentLength, orientation, type));
					break;

				default:
					System.err.println("Logic error in xml file");
					break;
				}

				fillTree(JDOMchild, treeChild);
				tree.add(treeChild);
				numChild++;
			}
		}
	}

	public void displayTree(DefaultMutableTreeNode tree) {
		System.out.println(tree.getUserObject().toString());
		for (int i = 0; i < tree.getChildCount(); ++i) {
			for (int j = 0; j < tree.getLevel() + 1; ++j) {
				System.out.print("   ");
			}
			// on caste le résultat de getChildAt qui renvoie un TreeNode et non
			// un DefaultMutableTreeNode
			displayTree((DefaultMutableTreeNode) tree.getChildAt(i));
		}
	}

	public void render(DefaultMutableTreeNode tree, GL3 gl) {
		
		Matrix4f MV = new Matrix4f();
		
		switch (((PlantsTreeNode)tree.getUserObject()).getType()) {
		
			case "trunck" :
				
			// Calculate Parent ModelView Matrix
			Matrix4f MVp = new Matrix4f();
			if (tree.getParent() == null) {
				MVp.setIdentity();
			} else {
				DefaultMutableTreeNode parent = ((DefaultMutableTreeNode) tree.getParent());
				MVp = new Matrix4f(((TrunckTreeNode) parent.getUserObject()).MV);
			}
	
			// Calculate Brother ModelView Matrix
			Matrix4f MVb = new Matrix4f();
			if (tree.getSiblingCount() == 2) {
				if (tree.getPreviousSibling() == null) {
					MVb = new Matrix4f(((TrunckTreeNode) tree.getNextSibling()
							.getUserObject()).MV);
				} else {
					MVb = new Matrix4f(((TrunckTreeNode) tree.getPreviousSibling()
							.getUserObject()).MV);
				}
			} else {
				MVb = new Matrix4f(((TrunckTreeNode) tree.getUserObject()).MV);
			}
			
			// Rend trunck
			
			gl.glUseProgram(m_iTreeProgram);
			gl.glActiveTexture(DISPLAYTEXTURE_ID);
			texTree.enable(gl);
			texTree.bind(gl);
			gl.glUniform1i(m_treeTexLocation, 1);
			MV = new Matrix4f(m_FPSCamera.getViewMatrix());
			MV.mul(m_Stack.top());
			gl.glUniformMatrix4fv(m_treeMVLocation, 1, false, GLMatrix.parseToFloatArray(MV), 0);
			
			((TrunckTreeNode) tree.getUserObject()).render(gl, m_treeMVcLocation, m_treeMVpLocation, MVp, m_treeMVbLocation, MVb, m_treeScLocation, m_treeSpLocation, m_treeSbLocation);
			// draw children
			for (int i = 0; i < tree.getChildCount(); ++i) {
				render((DefaultMutableTreeNode) tree.getChildAt(i), gl);
			}
			
			break;
			
			case "leaf" :
			
			// Rend leaf
			gl.glUseProgram(m_iLeafProgram);
			gl.glActiveTexture(DISPLAYTEXTURE_ID);
			texLeaf.enable(gl);
			texLeaf.bind(gl);
			gl.glUniform1i(m_leafTexLocation, 1);
			
			MV = new Matrix4f(m_FPSCamera.getViewMatrix());
			MV.mul(m_Stack.top());
			gl.glUniformMatrix4fv(m_treeMVLocation, 1, false, GLMatrix.parseToFloatArray(MV), 0);
			
			((LeafTreeNode)tree.getUserObject()).render(gl, m_leafMVcLocation);
				
			break;
		
		}
		
		
	}

	public TrackballCamera getTCamera() {
		return m_TCamera;
	}

	public FreeFlyCamera getFPSCamera() {
		return m_FPSCamera;
	}
	
	public String updateFPS() {
		String framename = "PlantS | FPS : " + FPS;
		FPS = 0;
		return framename;
	}

}
