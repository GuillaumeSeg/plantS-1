package camera;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import matrix.GLMatrixTransform;

public class TrackballCamera {
	
	private float m_fDistance;
	private float m_fAngleX;
	private float m_fAngleY;
	private Vector3f m_Target;

	private int view;
	
	public TrackballCamera() {
		m_fDistance = 0.0f;
		m_fAngleX = 0.0f;
		m_fAngleY = 0.0f;
		m_Target = new Vector3f(0.0f, 0.0f, 0.0f);
	}
	
	public void setTarget(float x, float y) {
		m_Target.x += x;
		m_Target.y += y;
	}
	
	public void moveFront(float distance) {
		m_fDistance += -distance;
		if(m_fDistance <= 0.6) {
			m_fDistance = 0.6f;
		}
	}
	
	public void rotateUp(float degrees) {
		m_fAngleX += degrees;
		if(m_fAngleX <= -1) {
			m_fAngleX = -1;
		}
		if(m_fAngleX >= 0) {
			m_fAngleX = 0f;
		}
	}
	
	public void rotateLeft(float degrees) {
		m_fAngleY += degrees;
	}

	public float getDistance() {
		return m_fDistance;
	}
	
	public Vector3f getTarget() {
		return m_Target;
	}
	
	public float getLateralAngle() {
		return m_fAngleY;
	}
	
	public float getVerticalAngle() {
		return m_fAngleX;
	}
	
	public void goToFrontView() {
		m_fAngleX = 0;
		m_fAngleY = 0;
		m_fDistance = 5;
		m_Target.x = 0.08f;
		m_Target.y = -1.7f;
	}
	
	public void goToTopView() {
		m_fAngleX = -(float)Math.PI/2;;
		m_fAngleY = 0;
		m_fDistance = 8;
		m_Target.x = 0.0f;
		m_Target.y = 0.0f;
	}
	
	public void goToLeftView() {
		m_fAngleX = 0;
		m_fAngleY = -(float)Math.PI/2;
		m_fDistance = 5;
		m_Target.x = 0.08f;
		m_Target.y = -1.7f;
	}
	
	public void goToRightView() {
		m_fAngleX = 0;
		m_fAngleY = (float)Math.PI/2;
		m_fDistance = 5;
		m_Target.x = 0.08f;
		m_Target.y = -1.7f;
	}
	
	public Matrix4f getViewMatrix() {
		
		Matrix4f T = new Matrix4f(
				1.0f, 0.0f, 0.0f, m_Target.x,
				0.0f, 1.0f, 0.0f, m_Target.y,
				0.0f, 0.0f, 1.0f, m_Target.z,
				0.0f, 0.0f, 0.0f, 1.0f
			);
		
		/*Matrix4f V = new Matrix4f();
		V = GLMatrixTransform.LookAt(new Vector3f(0.0f, 0.0f, m_fDistance), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));*/
		
		T.mul(GLMatrixTransform.LookAt(new Vector3f(0.0f, 0.0f, m_fDistance), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f)));
		
		Matrix4f XRotate = new Matrix4f(
			1.0f, 0.0f, 						0.0f, 							0.0f,
			0.0f, (float)Math.cos(m_fAngleX), 	(float)-Math.sin(m_fAngleX), 	0.0f,
			0.0f, (float)Math.sin(m_fAngleX), 	(float)Math.cos(m_fAngleX), 	0.0f,
			0.0f, 0.0f, 						0.0f, 							1.0f
		);
		XRotate.transpose();
		
		Matrix4f YRotate = new Matrix4f(
			(float)Math.cos(m_fAngleY),		0.0f, (float)Math.sin(m_fAngleY),	0.0f,
			0.0f, 							1.0f, 0.0f,							0.0f,
			-(float)Math.sin(m_fAngleY),	0.0f, (float)Math.cos(m_fAngleY),	0.0f,
			0.0f,							0.0f, 0.0f,							1.0f	
		);
		YRotate.transpose();
		
		
		T.mul(XRotate);
		T.mul(YRotate);

		//System.out.println("m_fDistance : " + m_fDistance);
		//System.out.println("m_fAngleX : " + m_fAngleX);
		//System.out.println("m_fAngleY : " + m_fAngleY);
		//System.out.println("m_fTarget : " + m_Target.toString());
		
		return T;
	}

}
