package camera;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import matrix.GLMatrixTransform;

public class FreeFlyCamera {
	
	Vector3f m_Position;
	float m_fPhi;
	float m_fTheta;
	Vector3f m_FrontVector;
	Vector3f m_LeftVector;
	Vector3f m_UpVector;
	Matrix4f m_MVP;

	public FreeFlyCamera() {
		
		m_Position = new Vector3f(-3.0f, 20.0f, 10.0f);
		m_fPhi = -5.6f;
		m_fTheta = 0.02f;
		
		m_MVP = new Matrix4f();
		m_FrontVector = new Vector3f();
		m_UpVector = new Vector3f(0.0f, 1.0f, 0.0f);
		m_LeftVector = new Vector3f();
		
		computeDirectionVectors();
	}
	
	public Vector3f position() {
		return m_Position;
	}
	
	private void computeDirectionVectors() {
		
		float cosTheta = (float)Math.cos(m_fTheta);
		
		m_FrontVector.x = cosTheta*(float)Math.sin(m_fPhi);
		m_FrontVector.y = (float)Math.sin(m_fTheta);
		m_FrontVector.z = cosTheta*(float)Math.cos(m_fPhi);
	
		m_LeftVector.x = (float)Math.sin(m_fPhi + Math.PI/2);
		m_LeftVector.y = 0.0f;
		m_LeftVector.z = (float)Math.cos(m_fPhi + Math.PI/2);
		
		m_UpVector.cross(m_FrontVector, m_LeftVector);
		
	}
	
	public void moveLeft(float t) {
		
		m_Position.x += t*m_LeftVector.x;
		m_Position.y += t*m_LeftVector.y;
		m_Position.z += t*m_LeftVector.z;
	}
	
	public void moveFront(float t) {
		m_Position.x += t*m_FrontVector.x;
		m_Position.y += t*m_FrontVector.y;
		m_Position.z += t*m_FrontVector.z;
	}
	
	public void rotateLeft(float degrees) {
		m_fPhi += degrees/360 * 2 * Math.PI;
		computeDirectionVectors();
	}
	
	public void rotateUp(float degrees) {
		m_fTheta += degrees/360 * 2 * Math.PI;
		computeDirectionVectors();
	}
	
	public Matrix4f getViewMatrix() {
		Vector3f view = new Vector3f();
		view.add(m_Position, m_FrontVector);
		return GLMatrixTransform.LookAt(m_Position, view, m_UpVector);
	}

}


