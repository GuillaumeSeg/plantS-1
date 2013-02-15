package matrix;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class GLMatrixTransform {

	public static Matrix4f LookAt(Vector3f eye, Vector3f center, Vector3f up) {
		
		Vector3f w = new Vector3f(eye);
		w.sub(center);
		w.normalize();
		
		Vector3f u = new Vector3f();
		u.cross(up, w);
		u.normalize();
		
		Vector3f v = new Vector3f();
		v.cross(w, u);
		
		Matrix4f rot = new Matrix4f(
			u.x, v.x, w.x, 0.0f,
			u.y, v.y, w.y, 0.0f,
			u.z, v.z, w.z, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f
		);
		rot.transpose();
		
		Matrix4f t = new Matrix4f(
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			-eye.x, -eye.y, -eye.z, 1.0f
		);
		t.transpose();
		
		rot.mul(t);
		return rot;
	}
	
	public static Matrix4f Perspective(float l, float r, float b, float t, float n, float f) {
		Matrix4f P = new Matrix4f(
				2*n/(r-l),		0.0f,			0.0f,			0.0f,
				0,				2*n/(t-b),		0.0f,			0.0f,
				(r+l)/(r-l),	(t+b)/(t-b),	-(f+n)/(f-n),	-1.0f,
				0.0f,			0.0f,			-2*f*n/(f-n),	0.0f
		);
		P.transpose();
		return P;
	}
	
	public static Matrix4f Perspective(float fovy, float aspect, float near, float far) {
		
		float range = (float)Math.tan((fovy/2) * 2 * Math.PI / 360) * near;
		float l = -range * aspect;
		float r = range * aspect;
		float b = -range;
		float t = range;
		
		Matrix4f P = new Matrix4f(
				2*near/(r-l),		0.0f,			0.0f,			0.0f,
				0,				2*near/(t-b),		0.0f,			0.0f,
				(r+l)/(r-l),	(t+b)/(t-b),	-(far+near)/(far-near),	-1.0f,
				0.0f,			0.0f,			-2*far*near/(far-near),	0.0f
		);
		P.transpose();
		return P;
	}

	public static Matrix4f Rotation(Vector3f dir, float angle) {
		dir.normalize();
		angle = angle * 2.f * (float)Math.PI / 360.f;
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		
		float a11 = dir.x*dir.x + (1-dir.x*dir.x)*cos;
		float a12 = dir.x*dir.y*(1-cos) - dir.z*sin;
		float a13 = dir.x*dir.z*(1-cos) + dir.y*sin;
		float a14 = 0;
		float a21 = dir.x*dir.y*(1-cos) + dir.z*sin;
		float a22 = dir.y*dir.y + (1-dir.y*dir.y)*cos;
		float a23 = dir.y*dir.z*(1-cos) - dir.x*sin;
		float a24 = 0;
		float a31 = dir.x*dir.z*(1-cos) - dir.y*sin;
		float a32 = dir.y*dir.z*(1-cos) + dir.x*sin;
		float a33 = dir.z*dir.z + (1-dir.z*dir.z)*cos;
		float a34 = 0;
		float a41 = 0;
		float a42 = 0;
		float a43 = 0;
		float a44 = 1;
		
		Matrix4f rotate = new Matrix4f(a11, a12, a13, a14, a21, a22, a23, a24, a31, a32, a33, a34, a41, a42, a43, a44);
		//rotate.transpose();
		return rotate;
	}

	public static Matrix4f Translation(Vector3f t) {
		Matrix4f translate = new Matrix4f(
				1, 0, 0, t.x,
				0, 1, 0, t.y,
				0, 0, 1, t.z,
				0, 0, 0, 1
		);
		//translate.transpose();
		return translate;
	}
	
	public static Vector4f multMat4Vec4(Matrix4f mat, Vector4f vec) {
		//mat.transpose();
		Vector4f newvec = new Vector4f(
			mat.m00*vec.x + mat.m01*vec.y + mat.m02*vec.z + mat.m03*vec.w,
			mat.m10*vec.x + mat.m11*vec.y + mat.m12*vec.z + mat.m13*vec.w,
			mat.m20*vec.x + mat.m21*vec.y + mat.m22*vec.z + mat.m23*vec.w,
			mat.m30*vec.x + mat.m31*vec.y + mat.m32*vec.z + mat.m33*vec.w
		);
		return newvec;
	}
}
