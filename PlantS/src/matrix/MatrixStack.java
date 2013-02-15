package matrix;

import java.util.Stack;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class MatrixStack {
	
	private Stack<Matrix4f> m_Stack;
	public int s_Count = 0;
	
	public MatrixStack() {
		m_Stack = new Stack<Matrix4f>();
		Matrix4f id = new Matrix4f();
		id.setIdentity();
		m_Stack.push(id);
	}
	
	public void push() {
		Matrix4f m = new Matrix4f(top());
		m_Stack.push(m);
		s_Count ++;
	}
	
	public void pop() {
		m_Stack.pop();
		s_Count --;
	}
	
	public Matrix4f top() {
		return m_Stack.peek();
	}
	
	public void set(Matrix4f mat) {
		m_Stack.pop();
		m_Stack.push(mat);
	}
	
	public void mult(Matrix4f mat) {
		top().mul(mat);
	}
	
	public void scale(Vector3f s) {
		Matrix4f scale = new Matrix4f(s.x, 0, 0, 0, 0, s.y, 0, 0, 0, 0, s.z, 0, 0, 0, 0, 1);
		mult(scale);
	}
	
	public void rotate(Vector3f dir, float angle) {
		Matrix4f rotate = GLMatrixTransform.Rotation(dir, angle);
		mult(rotate);
	}
	
	public void rotate(Matrix4f rotate) {
		mult(rotate);
	}
	
	public void translate(Vector3f t) {
		Matrix4f translate = GLMatrixTransform.Translation(t);
		mult(translate);
	}
}
