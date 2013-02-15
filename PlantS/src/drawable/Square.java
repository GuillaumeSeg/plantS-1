package drawable;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL3;

import com.jogamp.common.nio.Buffers;

import utils.CST;

public class Square {

	private ArrayList<VertexShape> m_Vertices;
	private static int s_NBvertices = 4;
	private static int s_DrawingMode = GL3.GL_TRIANGLE_FAN;
	private FloatBuffer m_BufferPosition;
	private FloatBuffer m_BufferNormals;
	private FloatBuffer m_BufferTexcoords;
	
	public Square() {
		m_Vertices = new ArrayList<VertexShape>();
		m_Vertices.add(new VertexShape(-0.4f, 1.2f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f));
		m_Vertices.add(new VertexShape(0.4f, 1.2f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f));
		m_Vertices.add(new VertexShape(0.4f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f));
		m_Vertices.add(new VertexShape(-0.4f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f));
		
		m_BufferPosition = Buffers.newDirectFloatBuffer(getPositionFloatArray());
		m_BufferNormals = Buffers.newDirectFloatBuffer(getNormalFloatArray());
		m_BufferTexcoords = Buffers.newDirectFloatBuffer(getTexcoordFloatArray());
	}
	
	public void draw(GL3 gl) {
		
		gl.glEnableVertexAttribArray(CST.LEAFSHADER_POSITION_LOCATION);        
        gl.glVertexAttribPointer(CST.LEAFSHADER_POSITION_LOCATION, 3, GL3.GL_FLOAT, false, 0, m_BufferPosition);
        
        gl.glEnableVertexAttribArray(CST.LEAFSHADER_NORMAL_LOCATION);        
        gl.glVertexAttribPointer(CST.LEAFSHADER_NORMAL_LOCATION, VertexShape.s_NB_COMPONENTS_NORMAL, GL3.GL_FLOAT, false, 0, m_BufferNormals);
        
        gl.glEnableVertexAttribArray(CST.LEAFSHADER_TEXCOORDS_LOCATION);        
        gl.glVertexAttribPointer(CST.LEAFSHADER_TEXCOORDS_LOCATION, VertexShape.s_NB_COMPONENTS_TEXCOORD, GL3.GL_FLOAT, false, 0, m_BufferTexcoords);
        
        gl.glDrawArrays(s_DrawingMode, 0, s_NBvertices);
        
        gl.glDisableVertexAttribArray(CST.LEAFSHADER_POSITION_LOCATION); // Allow release of vertex memory
        gl.glDisableVertexAttribArray(CST.LEAFSHADER_NORMAL_LOCATION); // Allow release of vertex position memory
        gl.glDisableVertexAttribArray(CST.LEAFSHADER_TEXCOORDS_LOCATION); // Allow release of vertex position memory
	
	}
	
	// Parse la liste de vertex en tableau de floats et qui renvoie ce tableau
	public float[] getPositionFloatArray() {
		float[] positions = new float[s_NBvertices * VertexShape.s_NB_TOTAL_COMPONENTS];
		int i = 0;
		for(VertexShape v : m_Vertices) {
			positions[i] = v.position.x;	++i;
			positions[i] = v.position.y;	++i;
			positions[i] = v.position.z;	++i;
		}
		return positions;
	}
	
	public float[] getNormalFloatArray() {
		float[] normals = new float[s_NBvertices * VertexShape.s_NB_TOTAL_COMPONENTS];
		int i = 0;
		for(VertexShape v : m_Vertices) {
			normals[i] = v.normal.x;	++i;
			normals[i] = v.normal.y;	++i;
			normals[i] = v.normal.z;	++i;
		}
		return normals;
	}
	
	public float[] getTexcoordFloatArray() {
		float[] texcoords = new float[s_NBvertices * VertexShape.s_NB_TOTAL_COMPONENTS];
		int i = 0;
		for(VertexShape v : m_Vertices) {
			texcoords[i] = v.texCoord.x;	++i;
			texcoords[i] = v.texCoord.y;	++i;
		}
		return texcoords;
	}	
}
