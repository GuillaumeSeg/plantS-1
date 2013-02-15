package drawable;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL3;

import utils.CST;

import com.jogamp.common.nio.Buffers;

public class Cube {
	
	private ArrayList<VertexShape> m_Vertices;
	private int m_NBvertices = 36;
	private static int s_DrawingMode = GL3.GL_TRIANGLES;
	private FloatBuffer m_BufferPosition;
	private FloatBuffer m_BufferTexcoords;
	
	public Cube() {
				
		m_Vertices = new ArrayList<VertexShape>();

		// bottom side
		m_Vertices.add(new VertexShape(-0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.25f, 1.0f));
		m_Vertices.add(new VertexShape(-0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.5f, 1.0f));
		m_Vertices.add(new VertexShape(0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.25f, 0.666666f));
		
		m_Vertices.add(new VertexShape(0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.5f, 0.666666f));
		m_Vertices.add(new VertexShape(0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.25f, 0.666666f));
		m_Vertices.add(new VertexShape(-0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.5f, 1.0f));
		
		// top side
		m_Vertices.add(new VertexShape(0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.25f, 0.333333f));
		m_Vertices.add(new VertexShape(-0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.5f, 0.0f));
		m_Vertices.add(new VertexShape(-0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.25f, 0.0f));
		
		m_Vertices.add(new VertexShape(-0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.5f, 0.0f));
		m_Vertices.add(new VertexShape(0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.25f, 0.333333f));
		m_Vertices.add(new VertexShape(0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.5f, 0.333333f));
		
		// left side
		m_Vertices.add(new VertexShape(-0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 0.25f, 0.333333f));
		m_Vertices.add(new VertexShape(-0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.666666f));	    		
		m_Vertices.add(new VertexShape(-0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f,  0.25f, 0.666666f));
		
		m_Vertices.add(new VertexShape(-0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.666666f));
		m_Vertices.add(new VertexShape(-0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 0.25f, 0.333333f));
		m_Vertices.add(new VertexShape(-0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.333333f));
		
		// right side
		m_Vertices.add(new VertexShape(0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.5f, 0.666666f));
		m_Vertices.add(new VertexShape(0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.75f, 0.666666f));
		m_Vertices.add(new VertexShape(0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.5f, 0.333333f));    		
		
		m_Vertices.add(new VertexShape(0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.75f, 0.333333f));
		m_Vertices.add(new VertexShape(0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.5f, 0.333333f));
		m_Vertices.add(new VertexShape(0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.75f, 0.666666f));
		
		// front side
		m_Vertices.add(new VertexShape(-0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.25f, 0.666666f));
		m_Vertices.add(new VertexShape(0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.5f, 0.666666f));
		m_Vertices.add(new VertexShape(-0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.25f, 0.333333f));
		
		m_Vertices.add(new VertexShape(0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.5f, 0.333333f));
		m_Vertices.add(new VertexShape(-0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.25f, 0.333333f));
		m_Vertices.add(new VertexShape(0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.5f, 0.666666f));
		
		// rear side
		m_Vertices.add(new VertexShape(-0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.333333f));
		m_Vertices.add(new VertexShape(0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.75f, 0.666666f));
		m_Vertices.add(new VertexShape(-0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.666666f));
		
		m_Vertices.add(new VertexShape(0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.75f, 0.666666f));
		m_Vertices.add(new VertexShape(-0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.333333f));
		m_Vertices.add(new VertexShape(0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.75f, 0.333333f));
		
		m_BufferPosition = Buffers.newDirectFloatBuffer(getPositionFloatArray());
		m_BufferTexcoords = Buffers.newDirectFloatBuffer(getTexcoordFloatArray());
	}
	
	public void draw(GL3 gl) {
		
		gl.glEnableVertexAttribArray(CST.SKYBOXHADER_POSITION_LOCATION);        
        gl.glVertexAttribPointer(CST.SKYBOXHADER_POSITION_LOCATION, 3, GL3.GL_FLOAT, false, 0, m_BufferPosition);
        
        gl.glEnableVertexAttribArray(CST.SKYBOXHADER_TEXCOORDS_LOCATION);        
        gl.glVertexAttribPointer(CST.SKYBOXHADER_TEXCOORDS_LOCATION, VertexShape.s_NB_COMPONENTS_TEXCOORD, GL3.GL_FLOAT, false, 0, m_BufferTexcoords);
        
        gl.glDrawArrays(s_DrawingMode, 0, m_NBvertices);
        
        gl.glDisableVertexAttribArray(CST.SKYBOXHADER_POSITION_LOCATION);
        gl.glDisableVertexAttribArray(CST.SKYBOXHADER_POSITION_LOCATION);
	}
	
	public float[] getPositionFloatArray() {
		
		float[] positions = new float[m_NBvertices * 3];
		int i = 0;
		for(VertexShape v : m_Vertices) {
			positions[i] = v.position.x; ++i;
			positions[i] = v.position.y; ++i;
			positions[i] = v.position.z; ++i;
		}
		return positions;
	}
	
	public float[] getNormalFloatArray() {
		float[] normals = new float[m_NBvertices * VertexShape.s_NB_COMPONENTS_NORMAL];
		int i = 0;
		for(VertexShape v : m_Vertices) {
			normals[i] = v.normal.x; ++i;
			normals[i] = v.normal.y; ++i;
			normals[i] = v.normal.z; ++i;
		}
		
		return normals;
	}

	public float[] getTexcoordFloatArray() {
		float[] texcoords = new float[m_NBvertices * VertexShape.s_NB_COMPONENTS_TEXCOORD];
		int i = 0;
		for(VertexShape v : m_Vertices) {
			texcoords[i] = v.texCoord.x; ++i;
			texcoords[i] = v.texCoord.y; ++i;
		}
		return texcoords;
	}

}
