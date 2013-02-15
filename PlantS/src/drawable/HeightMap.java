package drawable;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.media.opengl.GL3;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.jogamp.common.nio.Buffers;

import utils.CST;

public class HeightMap {

	private int[][] m_heightMap;
	int width;
	int height;
	
	private ArrayList<VertexShape> m_Vertices;
	private int m_NBvertices;
	private static int s_DrawingMode = GL3.GL_TRIANGLES;
	private FloatBuffer m_BufferPosition;
	private FloatBuffer m_BufferNormal;
	private FloatBuffer m_BufferTexcoords;
	
	public HeightMap(String filename, float precision) throws IOException {

		BufferedImage img = null;
		img = ImageIO.read(new File(filename));
		
		width = img.getWidth();
		height = img.getHeight();
		m_heightMap = new int[width][height];
		
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				int rgb = img.getRGB(i, j);
				m_heightMap[i][j] =  (rgb & 0xFF);
			}
		}
		
		int nbLateralVertices =  (int)(width * precision);
		int nbVerticalVertices = (int)(height * precision);
		System.out.println("Map with " + nbLateralVertices + " vertices length and " + nbVerticalVertices + " vertices height");
		
		// Fill all vertices
		float mapWidth = 1, mapHeight = 1; // map between -1 & 1
		ArrayList<VertexShape> allVertices = new ArrayList<VertexShape>();
		for(int i = 0; i < nbLateralVertices; i++) {
			for(int j = 0; j < nbVerticalVertices; j++) {
				float x = -mapWidth/2 + ((float)j/(float)nbLateralVertices) * mapWidth;
				float y = m_heightMap[i][j]*0.03f;
				float z = -mapHeight/2 + ((float)i/(float)nbVerticalVertices) * mapHeight;
				float tx = (float)j/(float)nbLateralVertices;
				float ty = (float)i/(float)nbVerticalVertices;
				allVertices.add(new VertexShape(x, y, z, 0f, 1f, 0f, tx, ty));
			}
		}
		
		// Regroup vertices into triangles
		// Each face is like : ( [i][j], [i+1][j], [i][j+1])  ([i][j+1], [i+1][j], [i+1][j+1]
		m_Vertices = new ArrayList<VertexShape>();
		for(int i = 0; i < nbLateralVertices - 1; i++) {
			for(int j = 0; j < nbVerticalVertices - 1; j++) {
				
				m_Vertices.add(allVertices.get(nbLateralVertices*i + j));
				m_Vertices.add(allVertices.get(nbLateralVertices*(i+1) + j));
				m_Vertices.add(allVertices.get(nbLateralVertices*i + j+1));
				
				m_Vertices.add(allVertices.get(nbLateralVertices*i + j+1));
				m_Vertices.add(allVertices.get(nbLateralVertices*(i+1) + j));
				m_Vertices.add(allVertices.get(nbLateralVertices*(i+1) + j + 1));
				
			}
		}
		
		m_NBvertices = m_Vertices.size();		

		m_BufferPosition = Buffers.newDirectFloatBuffer(getPositionFloatArray());
        m_BufferNormal = Buffers.newDirectFloatBuffer(getNormalFloatArray());
        m_BufferTexcoords = Buffers.newDirectFloatBuffer(getTexcoordFloatArray());
		
	}
	
	public void display() {
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				System.out.print(m_heightMap[i][j] + " ");
			}
			System.out.print("\n");
		}
	}
	
	public void draw(GL3 gl) {
		
		gl.glEnableVertexAttribArray(CST.GROUNDSHADER_POSITION_LOCATION);        
        gl.glVertexAttribPointer(CST.GROUNDSHADER_POSITION_LOCATION, 3, GL3.GL_FLOAT, false, 0, m_BufferPosition);
        
        gl.glEnableVertexAttribArray(CST.GROUNDSHADER_NORMAL_LOCATION);        
        gl.glVertexAttribPointer(CST.GROUNDSHADER_NORMAL_LOCATION, VertexShape.s_NB_COMPONENTS_NORMAL, GL3.GL_FLOAT, false, 0, m_BufferNormal);
        
        gl.glEnableVertexAttribArray(CST.GROUNDSHADER_TEXCOORDS_LOCATION);        
        gl.glVertexAttribPointer(CST.GROUNDSHADER_TEXCOORDS_LOCATION, VertexShape.s_NB_COMPONENTS_TEXCOORD, GL3.GL_FLOAT, false, 0, m_BufferTexcoords);
        
        gl.glDrawArrays(s_DrawingMode, 0, m_NBvertices);
        
        gl.glDisableVertexAttribArray(CST.GROUNDSHADER_POSITION_LOCATION); // Allow release of vertex memory
        gl.glDisableVertexAttribArray(CST.GROUNDSHADER_NORMAL_LOCATION); // Allow release of vertex position memory
        gl.glDisableVertexAttribArray(CST.GROUNDSHADER_TEXCOORDS_LOCATION); // Allow release of vertex position memory
	}
	
	public float[] getPositionFloatArray() {
		
		float[] positions = new float[m_NBvertices * 3];
		int i = 0;
		for(VertexShape v : m_Vertices) {
			positions[i] = v.position.x;
			++i;
			positions[i] = v.position.y;
			++i;
			positions[i] = v.position.z;
			++i;
		}
		return positions;
	}
	
	public float[] getNormalFloatArray() {
		
		float[] normals = new float[m_NBvertices * VertexShape.s_NB_COMPONENTS_NORMAL];
		int i = 0;
		for(VertexShape v : m_Vertices) {
			normals[i] = v.normal.x;
			++i;
			normals[i] = v.normal.y;
			++i;
			normals[i] = v.normal.z;
			++i;
		}
		return normals;
	}
	
	public float[] getTexcoordFloatArray() {
		
		float[] texcoords = new float[m_NBvertices * VertexShape.s_NB_COMPONENTS_TEXCOORD];
		int i = 0, index = 0;
		for(VertexShape v : m_Vertices) {
			/*if(index == 0) {
				texcoords[i] = 0f;
				++i;
				texcoords[i] = 0f;
				++i;
			}
			if(index%6 == 1 || index%6 == 4) {
				texcoords[i] = 1f;
				++i;
				texcoords[i] = 1f;
				++i;
			}
			if(index%6 == 2 || index%6 == 3) {
				texcoords[i] = 1f;
				++i;
				texcoords[i] = 1f;
				++i;
			}
			if(index%6 == 5) {
				texcoords[i] = 0f;
				++i;
				texcoords[i] = 0f;
				++i;
			}
			++index;*/
			texcoords[i] = v.texCoord.x; ++i;
			texcoords[i] = v.texCoord.y; ++i;
		}
		
		return texcoords;
	}
}
