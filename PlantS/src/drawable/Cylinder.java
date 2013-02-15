package drawable;
 
import java.nio.FloatBuffer;
import java.util.ArrayList;
 
import javax.media.opengl.GL3;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;
 
import matrix.GLMatrixTransform;
 
import com.jogamp.common.nio.Buffers;
 
import utils.CST;

public class Cylinder {
 
	private ArrayList<VertexShape> m_AllVertices;
	private int m_NBtotalVertices;
 
	private static int s_DrawingMode = GL3.GL_TRIANGLES; // le mode de dessin
	private FloatBuffer m_BufferPositionC;
	private FloatBuffer m_BufferPositionP;
	private FloatBuffer m_BufferPositionB;
	private FloatBuffer m_BufferNormals;
	private FloatBuffer m_BufferTexcoords;
 
	public Cylinder(float height, int discLat, int discHeight, float iangle, Matrix4f MPcp, Matrix4f MPcb, boolean uniqueChild) {

		float rcpLat = 1.f / discLat, rcpH = 1.f / discHeight;
        float dPhi = 2 * (float)Math.PI * rcpLat, dH = height * rcpH;
        
        ArrayList<VertexShape> distinctVertices = new ArrayList<VertexShape>();
        
        // Construit l'ensemble des vertex
        for(int j = 0; j < discHeight; ++j) {
        	for(int i = 0; i < discLat; ++i) {
        		
                VertexShape v = new VertexShape();
                
                v.texCoord.x = i * rcpLat;
                v.texCoord.y = j * rcpH;
                
                v.normal.x = (float)Math.sin(i * dPhi);
                v.normal.y = 0;
                v.normal.z = (float)Math.cos(i * dPhi);
                
            	v.position.x = (float)Math.sin(i*dPhi);
                v.position.y = j * dH;
                v.position.z = (float)Math.cos(i * dPhi);                
                
                distinctVertices.add(v);
            }
        }
        
     // Repliement pour la pose.
        float dy = height/discHeight;
        
        for(int i = 0; i < discHeight/4; ++i) {
        	
        	float weight = (1-(float)4*i/discHeight);
        	float angle = (float)Math.PI/4;
        	
        	float ponderateAngle = weight*angle;
        	if(uniqueChild) {
    			ponderateAngle = - ponderateAngle;
    		}
        	
    		for(int j = 0; j < discLat/2; ++j) {
        		float tmpx = distinctVertices.get(j + discLat*i).position.x;
        		float tmpy = distinctVertices.get(j + discLat*i).position.y;
        		
        		tmpy = tmpy - i*dy;
        		
        		float newx = (tmpx * (float)Math.cos(ponderateAngle) - tmpy * (float)Math.sin(ponderateAngle));
        		float newy = (tmpx * (float)Math.sin(ponderateAngle) + tmpy * (float)Math.cos(ponderateAngle));
        		float newz = distinctVertices.get(j  + discLat*i).position.z;
        		newy = newy + i*dy;
        		float normalx = distinctVertices.get(j + discLat*i).normal.x;
        		float normaly = distinctVertices.get(j + discLat*i).normal.y;
        		float normalz = distinctVertices.get(j + discLat*i).normal.z;
        		float tx = distinctVertices.get(j + discLat*i).texCoord.x;
        		float ty = distinctVertices.get(j + discLat*i).texCoord.y;
        		
        		distinctVertices.set(j  + discLat*i, new VertexShape(newx, newy, newz, normalx, normaly, normalz, tx, ty));
    		}
    		
    		ponderateAngle =  (float) (weight*(angle - (float)Math.PI/2));
    		
    		for(int j = discLat/2; j < discLat; ++j) {

    			float tmpx = distinctVertices.get(j + discLat*i).position.x;
        		float tmpy = distinctVertices.get(j + discLat*i).position.y;
        		
        		tmpy = tmpy - i*dy;
        		
        		float newx = (tmpx * (float)Math.cos(ponderateAngle) - tmpy * (float)Math.sin(ponderateAngle));
        		float newy = (tmpx * (float)Math.sin(ponderateAngle) + tmpy * (float)Math.cos(ponderateAngle));
        		float newz = distinctVertices.get(j  + discLat*i).position.z;
        		newy = newy + i*dy;
        		float normalx = distinctVertices.get(j + discLat*i).normal.x;
        		float normaly = distinctVertices.get(j + discLat*i).normal.y;
        		float normalz = distinctVertices.get(j + discLat*i).normal.z;
        		float tx = distinctVertices.get(j + discLat*i).texCoord.x;
        		float ty = distinctVertices.get(j + discLat*i).texCoord.y;
        		
        		distinctVertices.set(j  + discLat*i, new VertexShape(newx, newy, newz, normalx, normaly, normalz, tx, ty));
        	}
        }
        //-------------------------------------------------------------------------------
        int k = 0;
        
        //----------------- Setup des poids -------------------------//
        
        float start = -discHeight/6;
        float a = start;
        float H = (float)discHeight/2;
        float pas = (-2*start)/H;
        
        float wc; // local weight
    	float wp; // Parent weight
    	float wb; // Brother weight
        
    	float B; // Pondération latérale
    	float X; // Variation de Hauteur
    	
    	
    	for(int i = 0; i < discHeight; ++i) {
            	X = (float)Math.sqrt(i);
            	
            	// First semi-circle
            	for(int j = 0; j < discLat/2; ++j) {
            		
            		B = ((float)16.0f/((discLat)*(discLat)))*(j - ((float)(discLat)/4))*(j - ((float)(discLat)/4));
            		//initialWp = (16/((discLat)*(discLat)))*(j - ((3*(discLat))/4)*(j - ((3*(discLat))/4)));
            		//initialWc = initialWb = (1 - initialWp)/2;
            		
            		if(i < H) {
            			
            			/*if(!uniqueChild){
    	        			//wc = (1-(float)(4*i*i)/(discHeight*discHeight))*(initialWc) + (float)(4*i*i)/(float)(discHeight*discHeight);
    	        			//wb = wp = (1 - wc)/2;
    	        			
            				//Pondération polynomiale
            					//père :
            				wp = B -(B/((float)Math.sqrt(H)))*X;
            					//courante :
            				wc = ((1-B)/2) + ((B+1)/(2*(float)Math.sqrt(H)))*X;
            					//frèrot :
            				wb = ((1-B)/2) + ((B-1)/(2*(float)Math.sqrt(H)))*X;
            				
            				
            			} else {
            				wc = (float)((float)Math.sqrt(i))/((float)Math.sqrt(H));
                			wp = (1.0f - (float)((float)Math.sqrt(i))/((float)Math.sqrt(H)));
                			wb = 0.0f;
            			}*/
            			
            			// Triple reverse ultime ponderation
            			/*if(!uniqueChild){
    	        			wc = (1-(float)(4*i*i)/(discHeight*discHeight))*(initialWc) + (float)(4*i*i)/(float)(discHeight*discHeight);
    	        			wb = wp = (1 - wc)/2;
    	        			
            			} else {
            				wc = (float)(4*i*i)/(discHeight*discHeight);
                			wp = (1.0f - (float)(4*i*i)/(discHeight*discHeight)); // ->  linéaire
                			wb = 0.0f;
            			}*/
            			
                		// Quadratique Ponderation
                		wc = (float)(Math.sqrt(i)/Math.sqrt(H));
            			wp = 1.0f - (float)(Math.sqrt(i)/Math.sqrt(H));
            			wb = 0.0f;
                		
            			// Arctan ponderation
            			//wc = (float) (1.176f*((Math.atan(a/4)/Math.PI) + 0.49f));
            			//wp = 1 - wc;
            			//wp = (float) (0.5f - 1.176f*((Math.atan(a/4)/Math.PI) + 0.24f));
            			//wb = 0.0f;
            			
            			distinctVertices.get(j  + discLat*i).fixWeight(wc, wp, wb);
            			
            		} else {
            			wp = 0.0f;
            			wb = 0.0f;
            			wc = 1.0f;
            			distinctVertices.get(j  + discLat*i).fixWeight(wc, wp, wb);
            		}
            	}
            	
            	// Second semi-circle
            	for(int j = discLat/2; j < discLat; ++j) {
            		
            		//initialWp = (16/((discLat)*(discLat)))*(j - ((3*(discLat))/4)*(j - ((3*(discLat))/4)));
            		//initialWc = initialWb = (1 - initialWp)/2;
            		
            		B = ((float)16.0f/((discLat)*(discLat)))*(j - ((float)(3*(discLat))/4))*(j - ((float)(3*(discLat))/4));
            		
            		if(i < H) {
            		
            			// Triple reverse ultime ponderation
            			/*if(!uniqueChild){
    	        			//wc = (1-(float)(4*i*i)/(discHeight*discHeight))*(initialWc) + (float)(4*i*i)/(float)(discHeight*discHeight);
    	        			//wb = wp = (1 - wc)/2;
    	        			
            				//Pondération polynomiale
            					//père :
            				wp = B -(B/((float)Math.sqrt(H)))*X;
            					//courante :
            				wc = ((1-B)/2) + ((B+1)/(2*(float)Math.sqrt(H)))*X;
            					//frèrot :
            				wb = ((1-B)/2) + ((B-1)/(2*(float)Math.sqrt(H)))*X;
            				
            				
            			} else {
            				wc = (float)((float)Math.sqrt(i))/((float)Math.sqrt(H));
                			wp = (1.0f - (float)((float)Math.sqrt(i))/((float)Math.sqrt(H)));
                			wb = 0.0f;
            			}*/
            			// Quadratique Ponderation
                		wc = (float)(Math.sqrt(i)/Math.sqrt(H));
            			wp = 1.0f - (float)(Math.sqrt(i)/Math.sqrt(H));
            			wb = 0.0f;
            			//Arctan Ponderation
            			//wc = (float) (1.176f*((Math.atan(a/4)/Math.PI) + 0.49f));
            			//wp = 1 - wc;
            			//wp = (float) (0.5f - 1.176f*((Math.atan(a/4)/Math.PI) + 0.24f));
            			//wb = 0.0f;
            			
            			//Linear Ponderation
            			//wp = (0.5f - (float)i/discHeight);//--> linéaire
            			//wc = (float)2*i/discHeight;
            			//wb = (0.5f - (float)i/discHeight);
            			
            			//Double quadratique ponderation
            			//wc = (i*i)/(float)Math.pow(discHeight/2, 2);
            			//wp = wb = (1 - wc)/2;
            			
            			//Pondération polynomiale
            			//wp = B + w*X -(2*t + (1/H*H) + (w/H))*X*X;
            			//wc =((1-B)/2) - (H*t + ((1-B)/2) + w)*X + ((1/H*H) + t + (w/H))*X*X;
            			//wb = ((1-B)/2) - (H*t + ((1-B)/2*H))*X + t*X*X;
            			//wb = ((1-B)/2) - (H*t + ((1-B)/2))*X + t*X*X;
            			
            			distinctVertices.get(j  + discLat*i).fixWeight(wc, wp, wb);
            		}
            		else {
            			wp = 0.0f;
            			wb = 0.0f;
            			wc = 1.0f;
            			distinctVertices.get(j  + discLat*i).fixWeight(wc, wp, wb);
            		}
            	}
            	a = a + pas;
            }
        
        for(VertexShape v : distinctVertices) {
        	
            Vector4f coordsRP = new Vector4f(GLMatrixTransform.multMat4Vec4(MPcp, new Vector4f(v.position.x, v.position.y, v.position.z, 1)));
            v.positionRP.x = coordsRP.x/coordsRP.w;
            v.positionRP.y = coordsRP.y/coordsRP.w;
            v.positionRP.z = coordsRP.z/coordsRP.w;
            
            Vector4f coordsRB = new Vector4f(GLMatrixTransform.multMat4Vec4(MPcb, new Vector4f(v.position.x, v.position.y, v.position.z, 1)));
            v.positionRB.x = coordsRB.x/coordsRB.w;
            v.positionRB.y = coordsRB.y/coordsRB.w;
            v.positionRB.z = coordsRB.z/coordsRB.w;
            
        }
        // regroup vertices into triangles
        
        m_AllVertices = new ArrayList<VertexShape>();
        
        for(int j = 0; j < discHeight-1; ++j) {
        	int offset = j * discLat;
        	for(int i = 0; i < discLat; ++i) {
        		m_AllVertices.add(new VertexShape(distinctVertices.get(offset + i)));
        		m_AllVertices.add(new VertexShape(distinctVertices.get(offset + (i + 1)%discLat)));
        		m_AllVertices.add(new VertexShape(distinctVertices.get(offset + discLat + (i + 1)%discLat)));
        		m_AllVertices.add(new VertexShape(distinctVertices.get(offset + i)));
        		m_AllVertices.add(new VertexShape(distinctVertices.get(offset + discLat + (i + 1)%discLat)));
        		m_AllVertices.add(new VertexShape(distinctVertices.get(offset + i + discLat)));
            }
        }
        
        
 
        m_NBtotalVertices = m_AllVertices.size();
        m_BufferPositionC = Buffers.newDirectFloatBuffer(getPositionCFloatArray());
        m_BufferPositionP = Buffers.newDirectFloatBuffer(getPositionPFloatArray());        
        m_BufferPositionB = Buffers.newDirectFloatBuffer(getPositionBFloatArray());
        m_BufferNormals = Buffers.newDirectFloatBuffer(getNormalFloatArray());
        m_BufferTexcoords = Buffers.newDirectFloatBuffer(getTexcoordFloatArray());
        
	}
 
	public void draw(GL3 gl) {
 
		gl.glEnableVertexAttribArray(CST.TREESHADER_POSITIONC_LOCATION);
        gl.glVertexAttribPointer(CST.TREESHADER_POSITIONC_LOCATION, VertexShape.s_NB_COMPONENTS_POSITION, GL3.GL_FLOAT, false, 0, m_BufferPositionC);
 
        gl.glEnableVertexAttribArray(CST.TREESHADER_POSITIONP_LOCATION);        
        gl.glVertexAttribPointer(CST.TREESHADER_POSITIONP_LOCATION, VertexShape.s_NB_COMPONENTS_POSITION, GL3.GL_FLOAT, false, 0, m_BufferPositionP);
 
        gl.glEnableVertexAttribArray(CST.TREESHADER_POSITIONB_LOCATION);        
        gl.glVertexAttribPointer(CST.TREESHADER_POSITIONB_LOCATION, VertexShape.s_NB_COMPONENTS_POSITION, GL3.GL_FLOAT, false, 0, m_BufferPositionB);
        
        gl.glEnableVertexAttribArray(CST.TREESHADER_NORMAL_LOCATION);        
        gl.glVertexAttribPointer(CST.TREESHADER_NORMAL_LOCATION, VertexShape.s_NB_COMPONENTS_NORMAL, GL3.GL_FLOAT, false, 0, m_BufferNormals);
        
        gl.glEnableVertexAttribArray(CST.TREESHADER_TEXCOORDS_LOCATION);        
        gl.glVertexAttribPointer(CST.TREESHADER_TEXCOORDS_LOCATION, VertexShape.s_NB_COMPONENTS_TEXCOORD, GL3.GL_FLOAT, false, 0, m_BufferTexcoords);
        
        gl.glDrawArrays(s_DrawingMode, 0, m_NBtotalVertices);
 
        gl.glDisableVertexAttribArray(CST.TREESHADER_POSITIONC_LOCATION);
        gl.glDisableVertexAttribArray(CST.TREESHADER_POSITIONP_LOCATION);
        gl.glDisableVertexAttribArray(CST.TREESHADER_POSITIONB_LOCATION);
        gl.glDisableVertexAttribArray(CST.TREESHADER_NORMAL_LOCATION);
        gl.glDisableVertexAttribArray(CST.TREESHADER_TEXCOORDS_LOCATION);
	}
	
	public float[] getPositionCFloatArray() {
		float[] positionsC = new float[m_NBtotalVertices * VertexShape.s_NB_COMPONENTS_POSITION];
		int i = 0;
		for(VertexShape v : m_AllVertices) {
			positionsC[i] = v.position.x; ++i;
			positionsC[i] = v.position.y; ++i;
			positionsC[i] = v.position.z; ++i;
			positionsC[i] = v.position.w; ++i;
		}
		return positionsC;
	}
	
	public float[] getPositionPFloatArray() {
		float[] positionsP = new float[m_NBtotalVertices * VertexShape.s_NB_COMPONENTS_POSITION];
		int i = 0;
		for(VertexShape v : m_AllVertices) {
			positionsP[i] = v.positionRP.x; ++i;
			positionsP[i] = v.positionRP.y; ++i;
			positionsP[i] = v.positionRP.z; ++i;
			positionsP[i] = v.positionRP.w; ++i;
		}
		return positionsP;
	}
	
	public float[] getPositionBFloatArray() {
		float[] positionsB = new float[m_NBtotalVertices * VertexShape.s_NB_COMPONENTS_POSITION];
		int i = 0;
		for(VertexShape v : m_AllVertices) {
			positionsB[i] = v.positionRB.x; ++i;
			positionsB[i] = v.positionRB.y; ++i;
			positionsB[i] = v.positionRB.z; ++i;
			positionsB[i] = v.positionRB.w; ++i;
		}
		return positionsB;
	}
	
	public float[] getNormalFloatArray() {
		float[] normals = new float[m_NBtotalVertices * VertexShape.s_NB_COMPONENTS_NORMAL];
		int i = 0;
		for(VertexShape v : m_AllVertices) {
			normals[i] = v.normal.x; ++i;
			normals[i] = v.normal.y; ++i;
			normals[i] = v.normal.z; ++i;
		}
		return normals;
	}
	
	public float[] getTexcoordFloatArray() {
		float[] texcoords = new float[m_NBtotalVertices * VertexShape.s_NB_COMPONENTS_TEXCOORD];
		int i = 0;
		for(VertexShape v : m_AllVertices) {
			texcoords[i] = v.texCoord.x; ++i;
			texcoords[i] = v.texCoord.y; ++i;
		}
		return texcoords;
	} 
}