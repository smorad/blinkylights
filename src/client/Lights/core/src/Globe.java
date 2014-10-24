package com.Lights;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.InputAdapter;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.*;
import java.nio.*;


public class Globe
{

	private class VertexData
	{
		public float x,y,z;
		public float ox, oy;
		public float tx, ty;
	}

	public class Coord
	{
		public Coord(int column, int row, boolean valid)
		{
			this.valid = valid;
			this.column = column;
			this.row = row;
		}
		boolean valid;
		public int column,row;
	}


	private Mesh mesh;
    private ShaderProgram shader;
	private int columns;
	private int rows;
	private float rotation = 90;
	private boolean dirty = true;
	float step;
	Texture texture;

	Pixmap pixmap;
	public void SetColorAt(int column, int row, int r, int g, int b, int a)
	{

		pixmap.getPixels().put((row + column * rows)*4, (byte)r);
		pixmap.getPixels().put((row + column * rows)*4+1, (byte)g);
		pixmap.getPixels().put((row + column * rows)*4+2, (byte)b);
		pixmap.getPixels().put((row + column * rows)*4+3, (byte)a);
		dirty = true;
	}

	public void SetColorAt(Coord coord, int r, int g, int b, int a)
	{

		SetColorAt(coord.column, coord.row, r, g, b, a);
	}


	public Globe(int columns, int rows, float step)
	{

		this.columns = columns;
		this.rows = rows;
		this.step = step;

		shader = new ShaderProgram
		(
                Gdx.files.internal("PositionTextureColor.vs").readString(),
                Gdx.files.internal("PositionTextureColor.fs").readString()
                
        );

	 	mesh = new Mesh(
	 		true, rows*columns*7, rows*columns*6,
	 		new VertexAttribute(Usage.Position, 3, "a_position"),
	 		new VertexAttribute(Usage.Position, 2, "a_offset"),
	 		new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));          
 


		pixmap = new Pixmap( rows, columns, Format.RGBA8888 );
		pixmap.setColor( 0, 1, 0, 1.0f);
		pixmap.fill();

		SetColorAt(0, 15, 255, 255, 255, 255);
		SetColorAt(1, 15, 255, 255, 255, 255);


		texture = new Texture( pixmap );


	 	VertexData verts[][][] = new VertexData[rows][columns][4];


	 	for (int r = 0; r < rows; r++)

 		for (int i = 0; i < columns; i++)
 		{

			float radius = (float)Math.cos(((float)r/rows - .5f)*2.0);

 			for (int j = 0; j < 4; j++)
 			{

	 			VertexData v = new VertexData(); 
				v.x = (float)Math.cos(i/(double)columns * Math.PI * 2.0) * radius;
				v.z = (float)Math.sin(i/(double)columns * Math.PI * 2.0) * radius;
				v.y = (r/30.0f*1.5f) - .75f;
				v.tx = (float)r/(float)rows + .5f/rows;
				v.ty = (float)i/(float)columns + .5f/columns;

				float offset = .01f;
				if (j % 4 == 0)
			 	{
			 		v.ox = -offset;
	 				v.oy = -offset;
			 	}
			 	else if (j % 4 == 1)
			 	{
			 		v.ox= -offset;
	 				v.oy = offset;
			 	}
			 	else if (j % 4 == 2)
			 	{

			 		v.ox = offset;
	 				v.oy = -offset;
			 	}
			 	else
			 	{
					v.ox = offset;
	 				v.oy = offset;
	 			}

	 			verts[r][i][j] = v;
 			}
 		}

 		short indicies[] = new short[columns*6*rows];

 		int j = 0;
		for (short i = 0; i < columns*6*rows; i+= 6, j++)
 		{

 			indicies[i+0] = (short)(j*4+0);
 			indicies[i+1] = (short)(j*4+1);
 			indicies[i+2] = (short)(j*4+2);

 			indicies[i+3] = (short)(j*4+1);
 			indicies[i+4] = (short)(j*4+2);
 			indicies[i+5] = (short)(j*4+3);

 		}



 		int e = 7;
 		float t[] = new float[rows*columns*e*4];

 		for (int r = 0; r < rows; r++) 
 		for (int i = 0; i < columns; i++) 
 		{
 			for (int k = 0; k < 4; k++)
 			{ 
 				t[r*columns*4*e + i*4*e + k*e + 0] = verts[r][i][k].x;
 				t[r*columns*4*e + i*4*e + k*e + 1] = verts[r][i][k].y;
 				t[r*columns*4*e + i*4*e + k*e + 2] = verts[r][i][k].z;
 				t[r*columns*4*e + i*4*e + k*e + 3] = verts[r][i][k].ox;
 				t[r*columns*4*e + i*4*e + k*e + 4] = verts[r][i][k].oy;
 				t[r*columns*4*e + i*4*e + k*e + 5] = verts[r][i][k].tx;
 				t[r*columns*4*e + i*4*e + k*e + 6] = verts[r][i][k].ty;

 			}
 		}

      	mesh.setVertices(t);     //top
      	mesh.setIndices(indicies);   

	}

	public Coord GetCoord(int screenX, int screenY)
	{

		Matrix4 mm = new Matrix4().translate(new Vector3(0, 0, 1.f)).rotate(0, 1,0, 0).scale(.85f, .85f,.85f);

				// Matrix4 p = (new Matrix4().setToProjection(.0001f, 1, 90, 1)).mul(mm);
	   	Matrix4 p = new Matrix4().mul(mm);

		ByteBuffer buffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder());

		Gdx.gl.glReadPixels( screenX, Gdx.graphics.getHeight()-screenY, 1, 1, GL20.GL_DEPTH_COMPONENT, GL20.GL_FLOAT, buffer);

		float depth = buffer.getFloat(0);


		float x = ((float)screenX/Gdx.graphics.getWidth() - 0.5f) * 2.0f;
		float y = ((float)screenY/Gdx.graphics.getHeight() - 0.5f) * 2.0f;
		float z = (float)Math.cos(x*3.1415f/2.0);

		if (depth != 1.0)
		{

			Vector3 v = new Vector3(x,y,depth);
			v= v.prj(p.inv());
			
			float radius = (float)Math.cos(v.y*3.1415f/2.0f);

			///System.out.format("%f\n", radius);
			//System.out.format("%f %f %f\n", v.x/radius, v.y, v.z*2.0/radius);

			v = new Vector3(v.x/radius, v.y, v.z*2.0f/radius);

			float angle = -(float)Math.atan2(v.z, v.x)* 180/3.145f;
			//System.out.format("%f\n", angle );

			int column = Math.abs(((360 -(int)angle+(int)rotation)/2))% columns;

			int row = (int)(-v.y/step*2.0 + 15);


			System.out.format("%d  %d\n", column, row);
			return new Coord(column, row, true);

		}

		return new Coord(-1, -1, false);

	}
	public void render() 
	{

		rotation += .1;
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        if (dirty)
        {
       		texture = new Texture( pixmap );	
       		dirty = false;
        }

		Matrix4 v = new Matrix4().translate(new Vector3(0, 0, 1.f)).rotate(0, 1,0, rotation).scale(.85f, .85f,.85f);

        //Matrix4 p = (new Matrix4().setToProjection(.0001f, 5, 90, 1)).mul(v);
        Matrix4 p = new Matrix4().mul(v);


        texture.bind();
		shader.begin();
		shader.setUniformMatrix(shader.getUniformLocation("viewProjection"), p);
		//sprite.draw(batch);
    	mesh.render(shader, GL20.GL_TRIANGLES, 0, rows*columns*6);
		shader.end();

	}


}
