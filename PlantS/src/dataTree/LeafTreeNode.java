package dataTree;

import javax.media.opengl.GL3;
import javax.vecmath.Matrix4f;

import matrix.GLMatrix;

import drawable.Square;

public class LeafTreeNode extends PlantsTreeNode {

	int nbVertices;
	Matrix4f MV;
	public float rho, theta, phi, scale, height, radiusTrunck, lengthTrunck, orientation;
	int type;
	Square square= new Square();
	
	public LeafTreeNode(float rho, float theta, float phi, float scale, float height, float radiusTrunck, float lengthTrunck, float orientation, int type) {
		this.rho = rho;
		this.theta = theta;
		this.phi = phi;
		this.scale = scale;
		this.height = height;
		this.radiusTrunck = radiusTrunck;
		this.lengthTrunck = lengthTrunck;
		this.orientation = orientation;
		this.type = type;
	}
	
	public void render(GL3 gl, int MVcLocation) {
		gl.glUniformMatrix4fv(MVcLocation, 1, false, GLMatrix.parseToFloatArray(MV), 0);
		square.draw(gl);
	}

	public void setMV(Matrix4f mv) {
		MV = mv;
	}
	
	public Matrix4f getMV() {
		return MV;
	}
	
	public String toString() {
		return ("LEAF : \n " + MV.toString());
	}
	
	public Matrix4f getRotationMatrix() {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		
		Matrix4f XRotate = new Matrix4f(
			1.0f, 0.0f, 					0.0f, 					0.0f,
			0.0f, (float)Math.cos(rho), 	(float)-Math.sin(rho), 	0.0f,
			0.0f, (float)Math.sin(rho), 	(float)Math.cos(rho), 	0.0f,
			0.0f, 0.0f, 					0.0f, 					1.0f
		);
		XRotate.transpose();
		
		Matrix4f YRotate = new Matrix4f(
			(float)Math.cos(theta),		0.0f,	(float)Math.sin(theta),		0.0f,
			0.0f, 						1.0f,	0.0f,						0.0f,
			-(float)Math.sin(theta),	0.0f,	(float)Math.cos(theta),		0.0f,
			0.0f,						0.0f,	0.0f,						1.0f	
		);
		YRotate.transpose();
		
		Matrix4f ZRotate = new Matrix4f(
			(float)Math.cos(phi),		-(float)Math.sin(phi),		0.0f,		0.0f,
			(float)Math.sin(phi), 	(float)Math.cos(phi),		0.0f,		0.0f,
			0.0f,						0.0f,						1.0f,		0.0f,
			0.0f,						0.0f,						0.0f,		1.0f	
		);
		ZRotate.transpose();
		
		matrix.mul(XRotate);
		matrix.mul(YRotate);
		matrix.mul(ZRotate);
			
		return matrix;
	}

	public String getType() {
		return "leaf";
	}
	
}
