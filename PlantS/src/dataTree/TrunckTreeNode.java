package dataTree;

import javax.media.opengl.GL3;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import matrix.GLMatrix;
import drawable.Cylinder;

public class TrunckTreeNode extends PlantsTreeNode {
	
	public boolean renderable = true;
	
	public float length;
	Vector3f axe;
	float radius;
	float alpha;

	Matrix4f MPcp, MPcb;
	Matrix4f SC = new Matrix4f();
	Matrix4f SP = new Matrix4f();
	Matrix4f SB = new Matrix4f();
	Cylinder cylinder;

	public Matrix4f MV = new Matrix4f(
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f
	);
	
	public TrunckTreeNode(float length, Vector3f axe, float rad, float radP, float radB, Matrix4f PASSAGEchildParent, Matrix4f PASSAGEchildBrother, boolean uniqueChild) {
		
		this.length = length;
		this.axe = axe;
		this.radius = rad;
		
		SC = new Matrix4f(
			rad,	0,		0,		0,
			0,		1,		0,		0,
			0,		0,		rad,	0,
			0,		0,		0,		1
		);
		//System.out.println("SC : " + SC);
		
		SP = new Matrix4f(
			radP,	0,		0,		0,
			0,		1,		0,		0,
			0,		0,		radP,	0,
			0,		0,		0,		1
		);
		//System.out.println("SP : " + SP);
		
		SB = new Matrix4f(
			radB,	0,		0,		0,
			0,		1,		0,		0,
			0,		0,		radB,	0,
			0,		0,		0,		1
		);
		//System.out.println("SB : " + SB);
		
		float normeAxe = axe.length();
		this.alpha = (float) Math.acos(axe.x/normeAxe);
		if(axe.y < 0) {
			this.alpha = -this.alpha;
		}
		
		MPcp = new Matrix4f(PASSAGEchildParent);
		MPcb = new Matrix4f(PASSAGEchildBrother);	
		
		cylinder = new Cylinder(length, 8, 8, alpha, MPcp, MPcb, uniqueChild);
		
		MV.setIdentity();
		
	}
	
	public TrunckTreeNode() {
		this.length = 0;
		this.axe = new Vector3f(0, 1, 0);
		this.alpha = 1.0f;
		renderable = false;
		Matrix4f identity = new Matrix4f();
		identity.setIdentity();
		cylinder = new Cylinder(0, 1, 1, 0, identity, identity, true);
	}

	public Matrix4f getRotationMatrix() {
		
		Vector3f v = new Vector3f(axe);
		v.normalize();
		
		Vector3f X = new Vector3f(1.0f, 0.0f, 0.0f);
		Vector3f w = new Vector3f();
		
		if(Math.abs(X.dot(v)) > 0.8) {
			X.x = 0.0f;
			X.y = 1.0f;
			X.z = 0.0f;
			w.cross(v, X);
			w.normalize();
		} else {
			w.cross(X, v);
			w.normalize();
		}
		
		Vector3f u = new Vector3f();
		u.cross(v, w);
		u.normalize();
		
		Matrix4f Mrotate = new Matrix4f(
				u.x, v.x, w.x, 0.0f,
				u.y, v.y, w.y, 0.0f,
				u.z, v.z, w.z, 0.0f,
				0.0f, 0.0f, 0.0f, 1f
		);
		
		Mrotate.transpose();
		
		return Mrotate;
	}
	
	public void setMV(Matrix4f mv) {
		MV = mv;
	}
	
	public Matrix4f getMV() {
		return MV;
	}

	public String toString() {
		return ("TRUNCK - MV = " + MV);
	}
	
	public void render(GL3 gl, int MVcLocation, int MVpLocation, Matrix4f MVp, int MVbLocation, Matrix4f MVb, int SCLocation, int SPLocation, int SBLocation) {
		if(renderable) {
	        gl.glUniformMatrix4fv(MVcLocation, 1, false, GLMatrix.parseToFloatArray(MV), 0);
	        gl.glUniformMatrix4fv(MVpLocation, 1, false, GLMatrix.parseToFloatArray(MVp), 0);
	        gl.glUniformMatrix4fv(MVbLocation, 1, false, GLMatrix.parseToFloatArray(MVb), 0);
	        gl.glUniformMatrix4fv(SCLocation, 1, false, GLMatrix.parseToFloatArray(SC), 0);
	        gl.glUniformMatrix4fv(SPLocation, 1, false, GLMatrix.parseToFloatArray(SP), 0);
	        gl.glUniformMatrix4fv(SBLocation, 1, false, GLMatrix.parseToFloatArray(SB), 0);
	        //System.out.println("MV :\n"+MV);
	        cylinder.draw(gl);
		}
	}

	public float getLength() {
		return this.length;
	}

	public String getType() {
		return "trunck";
	}
}