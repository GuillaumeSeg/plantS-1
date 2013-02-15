package dataTree;

import javax.media.opengl.GL3;
import javax.vecmath.Matrix4f;

import matrix.MatrixStack;

public abstract class PlantsTreeNode {
	
	public PlantsTreeNode() {
		
	}

	//public abstract void render(GL3 gl, int MVcLocation, int MVpLocation, Matrix4f MVp, int MVbLocation, Matrix4f MVb, int SCLocation, int SPLocation, int SBLocation);
	public abstract Matrix4f getRotationMatrix();
	public abstract void setMV(Matrix4f mv);
	public abstract Matrix4f getMV();
	public abstract String getType();
	
	public String toString() {
		return ("TREE");
	}
	
}
