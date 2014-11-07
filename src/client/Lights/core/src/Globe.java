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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class Globe
{

	private class VertexData
	{
		public VertexData(Vector3 position, Vector2 texture, Vector2 offset)
		{
			this.offset = offset;
			this.position = position;
			this.texture = texture;
		}

		public Vector3 position;
		public Vector2 texture;
		public Vector2 offset;
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
	byte colors[];

	public void PrintColors()
	{

		ByteBuffer bytes = pixmap.getPixels();


   	try 
   	{ 
		Files.write(Paths.get("/Users/Dave/Dropbox/Classes/115/blinkylights/src/client/Lights/core/src/test.txt"), colors);
    } 
    catch (IOException e) 
    { 
    	System.out.println("Failed to write...");
	}
}

	public void SetColor(int r, int g, int b, int a)
	{
		for (int i =0; i < rows; i++ ) 
		{
			for (int j = 0; j < columns; j++ ) 
			{
				SetColorAt(j, i,r, g, b, a);
			}
		}

	}

	public void SetColorAt(int column, int row, int r, int g, int b, int a)
	{


		byte bb = pixmap.getPixels().get((row + column * rows)*4+0);

		pixmap.getPixels().put((row + column * rows)*4, (byte)r);
		pixmap.getPixels().put((row + column * rows)*4+1, (byte)g);
		pixmap.getPixels().put((row + column * rows)*4+2, (byte)b);
		pixmap.getPixels().put((row + column * rows)*4+3, (byte)a);

		colors[(row + column * rows)*3 + 0] = (byte)r;
		colors[(row + column * rows)*3 + 1] = (byte)g;
		colors[(row + column * rows)*3 + 2] = (byte)b;

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
		colors = new byte[rows * columns*3];;

		shader = new ShaderProgram
		(
      		Gdx.files.internal("PositionTextureColor.vs").readString(),
      		Gdx.files.internal("PositionTextureColor.fs").readString()
      	);


 		int components = 7; //position 3 + texture 2 + offset 2 (for smooth shading)
	 	mesh = new Mesh(true, rows*columns*components, rows*columns*6,
	 		new VertexAttribute(Usage.Position, 3, "a_position"),
	 		new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"),
	 		new VertexAttribute(Usage.Position, 2, "a_offset"));       
 


		pixmap = new Pixmap( rows, columns, Format.RGBA8888 );
		pixmap.setColor( 0, 0, 0, 0);
		pixmap.fill();


		SetColor(255, 255, 255, 255);
		SetColorAt(0, 15, 255, 255, 255, 255);
		SetColorAt(0, 14, 255, 0, 0, 255);
		SetColorAt(0, 13, 0, 255, 0, 255);
		SetColorAt(0, 11, 0, 0, 255, 255);


		texture = new Texture( pixmap );



		//Generate the vertex data for a sphere
	 	VertexData verts[][][] = new VertexData[rows][columns][4];
	 	for (int r = 0; r < rows; r++)
	 	{
	 		for (int c = 0; c < columns; c++)
	 		{

				float angle1 = c/(float)columns*3.1416f*2.0f;
				float angle2 = (c+1)/(float)columns*3.1416f*2.0f;

				//Find the radius of this band using x^2 + y^2 = r^2 r = 1 since its a unit circle.
				float radius1 =  (float)Math.sqrt(1.0f - 4 * ((r)/(float)rows-.5f) * ((r)/(float)rows-.5f));
				float radius2 =  (float)Math.sqrt(1.0f - 4 * ((r+1)/(float)rows -.5f) * ((r+1)/(float)rows-.5f));


				Vector3 p1 = new Vector3(radius1*(float)Math.cos(angle1), r/(float)rows*2, radius1*(float)Math.sin(angle1));
				Vector3 p2 = new Vector3(radius1*(float)Math.cos(angle2), r/(float)rows*2, radius1*(float)Math.sin(angle2));

				Vector3 p3 = new Vector3(radius2*(float)Math.cos(angle1), (r+1)/(float)rows*2, radius2*(float)Math.sin(angle1));
				Vector3 p4 = new Vector3(radius2*(float)Math.cos(angle2), (r+1)/(float)rows*2, radius2*(float)Math.sin(angle2));

				Vector2 textureCoord = new Vector2((float)r/(float)rows + .5f/rows, (float)c/(float)columns + .5f/columns);

	
				verts[r][c][0] = new VertexData(p1, textureCoord, new Vector2(-1, -1));
				verts[r][c][1] = new VertexData(p2, textureCoord, new Vector2(-1, 1));
				verts[r][c][2] = new VertexData(p3, textureCoord, new Vector2(1, -1));
				verts[r][c][3] = new VertexData(p4, textureCoord, new Vector2(1, 1));
	 		}
	 	}

	 	//6 indeicies for 2 triangles to draw 1 quad.
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

 		//Flatten the data into a single block to upload... fuck java's type system
 		float t[] = new float[rows*columns*components*4];
 		for (int r = 0; r < rows; r++) 
 		for (int i = 0; i < columns; i++) 
 		{
 			for (int k = 0; k < 4; k++)
 			{ 
 				t[r*columns*4*components + i*4*components + k*components + 0] = verts[r][i][k].position.x;
 				t[r*columns*4*components + i*4*components + k*components + 1] = verts[r][i][k].position.y;
 				t[r*columns*4*components + i*4*components + k*components + 2] = verts[r][i][k].position.z;

 				t[r*columns*4*components + i*4*components + k*components + 3] = verts[r][i][k].texture.x;
 				t[r*columns*4*components + i*4*components + k*components + 4] = verts[r][i][k].texture.y;

 				t[r*columns*4*components + i*4*components + k*components + 5] = verts[r][i][k].offset.x;
 				t[r*columns*4*components + i*4*components + k*components + 6] = verts[r][i][k].offset.y;
 			}
 		}
      	mesh.setVertices(t);
      	mesh.setIndices(indicies);   

	}

	public Coord GetCoord(int screenX, int screenY)
	{

		Matrix4 mm = new Matrix4().translate(new Vector3(0, -1.f*.85f, 1.f)).rotate(0, 1,0, 0).scale(.85f, .85f,.85f);

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

			Vector3 v = new Vector3(x,y,0);
			v= v.prj(p.inv());


			float radius = (float)Math.sqrt(1.0 - (v.y - 1.0)*(v.y - 1.0));


			float angle = (float)Math.acos(v.x/radius);
			System.out.format("%f\n", angle/3.1415*180);

			int column = (int)((360 - angle/3.1415*180 + rotation)/2.0) % columns;

			int row = (int)(v.y*rows)/2;
			row = row >= 28 ? 27 : row;
			row = rows - 1  - row;

			System.out.format("%d  %d\n", column, row);
			return new Coord(column, row, true);

		}

		return new Coord(-1, -1, false);

	}
	public void render() 
	{

		rotation += .1;
		Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

     
        if (dirty)
        {
       		texture = new Texture( pixmap );	
       		dirty = false;
        }

		Matrix4 v = new Matrix4().translate(new Vector3(0, -1.f*.85f, 1.f)).rotate(0, 1,0, rotation).scale(.85f, .85f,.85f);

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
