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
import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class Globe
{
  private Mesh mesh;
  private ShaderProgram shader;
  private int n_columns;
  private int n_rows;
  private float rotation = 90;
  private boolean dirty = true;

  ArrayList<Layer> layer_array = new ArrayList<Layer>();
  Layer activeLayer;
  float step;
  Texture texture;

  Pixmap pixmap; //a raw image in memory as represented by pixels
  byte colors[];

  float speed = (float).1;
  String globe_text = "";
  
  
  //public void add_speed(double intput_speed) { speed += intput_speed; }
  
  
  
  public void add_speed(float rotation)
  {
    this.speed = (((rotation % 360) + 360) % 360);
  }
  
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
  
  public Layer layer_text(String text)
  {
    Layer layer = new Layer(n_columns, n_rows, text);
    
    layer_array.add(layer);
    
    return layer;
  }
  
  public void set_globe_text(String text)
  {
    globe_text = text;
    activeLayer = layer_text(globe_text);
    
    dirty = true;
  }
  
  //write to file. called on click or mouse movement.
  public void PrintColors()
  {
    // ByteBuffer bytes = pixmap.getPixels();

    // try 
    // { 
    //    Files.write(Paths.get("/Users/Reid/Desktop/test.txt"), colors);
    // } 
    // catch (IOException e) 
    // { 
    //    System.out.println("Failed to write...");
    // }
  }

  //set all the LEDs on the screen
  public void SetColor(int r, int g, int b, int a)
  {
    for (int i =0; i < n_rows; i++ ) 
    {
      for (int j = 0; j < n_columns; j++ ) 
      {
        SetColorAt(j, i,r, g, b, a);
      }
    }
  }

  //helper function for manual color setting.
  public void SetColorAt(int column, int row, int r, int g, int b, int a)
  {
    int rr = ((column + (int)activeLayer.x)%n_columns + n_columns)%n_columns;
    int cc = row + (int)activeLayer.y; 

    activeLayer.SetColorAt(rr, cc, r, g, b, a);
    dirty = true;
  }

  public float Mod(float lhs, float rhs)
  {

    return ((lhs % rhs) + rhs) % rhs;
  }

  public void FillColors()
  {
    int count = n_rows * n_columns * 4;

    for (int i =0; i < n_rows; i++ ) 
    {
      for (int j = 0; j < n_columns; j++ ) 
      {
        int at = ((int)Mod(j + layer_array.get(layer_array.size()-1).x, layer_array.get(layer_array.size()-1).width)  + (i+(int)layer_array.get(layer_array.size()-1).y) * n_columns)*4;

        int r = layer_array.get(layer_array.size()-1).GetPixelAt(((at+0)%count + count) % count);
        int g = layer_array.get(layer_array.size()-1).GetPixelAt(((at+1)%count + count) % count);
        int b = layer_array.get(layer_array.size()-1).GetPixelAt(((at+2)%count + count) % count);
        int a = layer_array.get(layer_array.size()-1).GetPixelAt(((at+3)%count + count) % count);

        pixmap.getPixels().put((n_rows-1-i + j * n_rows)*4, (byte)r);
        pixmap.getPixels().put((n_rows-1-i + j * n_rows)*4+1, (byte)g);
        pixmap.getPixels().put((n_rows-1-i + j * n_rows)*4+2, (byte)b);
        pixmap.getPixels().put((n_rows-1-i + j * n_rows)*4+3, (byte)255);

      }
    }
  }


  //manual color setting function on the globe.
  public void SetColorAt(Coord coord, int r, int g, int b, int a)
  {
    SetColorAt(coord.column, coord.row, r, g, b, a);
  }


  //definitions of the globe object.
  public Globe(int columns, int rows, float step)
  {

    this.n_columns = columns;
    this.n_rows = rows;
    this.step = step;

    shader = new ShaderProgram
    (
    Gdx.files.internal("PositionTextureColor.vs").readString(),
    Gdx.files.internal("PositionTextureColor.fs").readString()
    );


    int components = 7; //position 3 + texture 2 + offset 2 (for smooth shading)
    mesh = new Mesh(true, n_rows*n_columns*components, n_rows*n_columns*6,
    new VertexAttribute(Usage.Position, 3, "a_position"),
    new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"),
    new VertexAttribute(Usage.Position, 2, "a_offset"));          


    //A Pixmap is basically a raw image in memory as represented by pixels
    //Pixmap (row) wide, (column) height) using 8 bytes for Red, Green, Blue and Alpha channels
    pixmap = new Pixmap( n_rows, n_columns, Format.RGBA8888 );
    pixmap.setColor( 0, 0, 0, 0);   
    pixmap.fill(); //fills pixmap to all black. (should turn off all LED on actual globe.)



    texture = new Texture( pixmap );

    //Generate the vertex data for a sphere
    VertexData verts[][][] = new VertexData[n_rows][n_columns][4];
    for (int r = 0; r < n_rows; r++)
    {
      for (int c = 0; c < n_columns; c++)
      {

        float angle1 = c/(float)n_columns*3.1416f*2.0f;
        float angle2 = (c+1)/(float)n_columns*3.1416f*2.0f;

        //Find the radius of this band using x^2 + y^2 = r^2 r = 1 since its a unit circle.
        float radius1 =   (float)Math.sqrt(1.0f - 4 * ((r)/(float)n_rows-.5f) * ((r)/(float)n_rows-.5f));
        float radius2 =   (float)Math.sqrt(1.0f - 4 * ((r+1)/(float)n_rows -.5f) * ((r+1)/(float)n_rows-.5f));


        Vector3 p1 = new Vector3(radius1*(float)Math.cos(angle1), r/(float)n_rows*2, radius1*(float)Math.sin(angle1));
        Vector3 p2 = new Vector3(radius1*(float)Math.cos(angle2), r/(float)n_rows*2, radius1*(float)Math.sin(angle2));

        Vector3 p3 = new Vector3(radius2*(float)Math.cos(angle1), (r+1)/(float)n_rows*2, radius2*(float)Math.sin(angle1));
        Vector3 p4 = new Vector3(radius2*(float)Math.cos(angle2), (r+1)/(float)n_rows*2, radius2*(float)Math.sin(angle2));

        Vector2 textureCoord = new Vector2((float)r/(float)n_rows + .5f/n_rows, (float)c/(float)n_columns + .5f/n_columns);

        
        verts[r][c][0] = new VertexData(p1, textureCoord, new Vector2(-1, -1));
        verts[r][c][1] = new VertexData(p2, textureCoord, new Vector2(-1, 1));
        verts[r][c][2] = new VertexData(p3, textureCoord, new Vector2(1, -1));
        verts[r][c][3] = new VertexData(p4, textureCoord, new Vector2(1, 1));
      }
    }

    //6 indicies for 2 triangles to draw 1 quad.
    short indicies[] = new short[n_columns*6*n_rows];
    int j = 0;
    for (int i = 0; i < n_columns*6*n_rows; i+= 6, j++)
    {
      indicies[i+0] = (short)(j*4+0);
      indicies[i+1] = (short)(j*4+1);
      indicies[i+2] = (short)(j*4+2);

      indicies[i+3] = (short)(j*4+1);
      indicies[i+4] = (short)(j*4+2);
      indicies[i+5] = (short)(j*4+3);
    }

    //Flatten the data into a single block to upload... fuck java's type system
    float t[] = new float[n_rows*n_columns*components*4];
    for (int r = 0; r < n_rows; r++) 
    for (int i = 0; i < n_columns; i++) 
    {
      for (int k = 0; k < 4; k++)
      { 
        t[r*n_columns*4*components + i*4*components + k*components + 0] = verts[r][i][k].position.x;
        t[r*n_columns*4*components + i*4*components + k*components + 1] = verts[r][i][k].position.y;
        t[r*n_columns*4*components + i*4*components + k*components + 2] = verts[r][i][k].position.z;

        t[r*n_columns*4*components + i*4*components + k*components + 3] = verts[r][i][k].texture.x;
        t[r*n_columns*4*components + i*4*components + k*components + 4] = verts[r][i][k].texture.y;

        t[r*n_columns*4*components + i*4*components + k*components + 5] = verts[r][i][k].offset.x;
        t[r*n_columns*4*components + i*4*components + k*components + 6] = verts[r][i][k].offset.y;
      }
    }
    mesh.setVertices(t);
    mesh.setIndices(indicies);    
    
    activeLayer = layer_text("");
    
    SetColorAt(0, 15, 255, 255, 255, 255);
    SetColorAt(0, 14, 0, 255, 0, 255);
    SetColorAt(0, 13, 0, 0, 255, 255);
  }

  //returns the globe coordinates when the mouse is clicked.
  //this function is not called continuously, only when mouse is held down and coordinates change.
  public Coord GetCoord(int screenX, int screenY)
  {
    Matrix4 mm = new Matrix4().translate(new Vector3(0, -1.f*.85f, 1.f)).rotate(0, 1,0, 0).scale(.85f, .85f,.85f);

    // Matrix4 p = (new Matrix4().setToProjection(.0001f, 1, 90, 1)).mul(mm);
    Matrix4 p = new Matrix4().mul(mm);

    ByteBuffer buffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder());

    Gdx.gl.glReadPixels( screenX, Gdx.graphics.getHeight()-screenY, 1, 1, GL20.GL_DEPTH_COMPONENT, GL20.GL_FLOAT, buffer);

    float depth = buffer.getFloat(0);
    float horiz_degree = 360 / n_columns;

    float x = ((float)screenX/Gdx.graphics.getWidth() - 0.5f) * 2;
    float y = ((float)screenY/Gdx.graphics.getHeight() - 0.5f) * 2;
    float z = (float)Math.cos(x*3.1415f/2.0);

    if (depth != 1.0)
    {

      Vector3 v = new Vector3(x,y,0);
      v= v.prj(p.inv());


      float radius = (float)Math.sqrt(1.0 - (v.y - 1.0)*(v.y - 1.0));


      float angle = (float)Math.acos(v.x/radius);
      System.out.format("%f\n", angle/3.1415*n_columns);

      int column = (int)((((360 - angle/3.1415*n_columns + rotation)/2.0) % n_columns) + n_columns)%n_columns;

      // WTF lol???? whats this do
      int row = (int)(v.y*n_rows)/2;
      row = row >= n_rows ? n_rows-1 : row;
      row = n_rows - 1 - row;
      
      
      //Suppressing the OutOfBoundsIndex bug by setting any negative values to 0
      if (column < 0 || row < 0) { return new Coord(-1, -1, false); } //this produces other errors...

      System.out.format("%d   %d\n", column, row);
      return new Coord(column, row, true);

    }

    return new Coord(-1, -1, false);
  }
  
  //renders the globe
  public void render() 
  {

    rotation += speed * 5; //speed at which the globe turns.

    //activeLayer.x += .5;
    dirty = true;


    Gdx.gl.glClearColor(.25f, .25f, .25f, 1); //grey background.
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

    //Kevin doesn't know gl... This enables blending so transparency works...
    Gdx.gl20.glEnable(GL20.GL_BLEND);
    Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    
    if (dirty)
    {
      FillColors();
      texture = new Texture( pixmap );	
      dirty = false;
    }


    Matrix4 v = new Matrix4().translate(new Vector3(0, -1.f*.85f, 1.f)).rotate(0, 1,0, rotation).scale(.85f, .85f,.85f);
    Matrix4 p = new Matrix4().mul(v);


    texture.bind(); //binds the texture to geometry. (binds LED to globe.)
    shader.begin();

    shader.setUniformMatrix(shader.getUniformLocation("viewProjection"), p);
    mesh.render(shader, GL20.GL_TRIANGLES, 0, n_rows*n_columns*6);
    shader.end();

  }
  

}
