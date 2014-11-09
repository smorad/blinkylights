package com.Lights;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.InputAdapter;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.*;
import java.nio.*;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public class Lights extends ApplicationAdapter implements ApplicationListener
{
  //variables that the color setting function pulls from. default red.
  int r = 255;  //red
  int b = 0;     //blue
  int g = 0;    //greem
  int a = 255;   //alpha

  //color_button is an Actor
  public class color_button extends Actor {
    Texture texture;// = new Texture(Gdx.files.internal("green.png"));
    float x_position = 0, y_position = 0;
    public boolean started = false;
    int color_r, color_g, color_b, color_a;

    public color_button(int w, int x, int y, int z, int index){
      color_r = w;
      color_g = x;
      color_b = y;
      color_a = z;
      
      //hackish way for button image. Eventually will want to change to using shapeRendering
      //to eliminate textures dependency on png file, but for now this works.
      if (index == 1) { texture = new Texture(Gdx.files.internal("red.png")); }
      if (index == 2) { texture = new Texture(Gdx.files.internal("green.png")); }
      if (index == 3) { texture = new Texture(Gdx.files.internal("blue.png")); }
      
      setBounds(x_position, y_position, texture.getWidth(), texture.getHeight());
      addListener(new InputListener(){
        public boolean touchDown (InputEvent event, float r, float g, int pointer, int button) {
          ((color_button)event.getTarget()).started = true;
          return true;
        }
      });
    }
    
    public void set_position(int x, int y){ 
      x_position = x - texture.getWidth()/2;
      y_position = y - texture.getHeight()/2;
      setBounds(x_position, y_position, texture.getWidth(), texture.getHeight());
    }
    
    @Override
    public void draw(Batch batch, float alpha){
      batch.draw(texture, x_position, y_position);
    }
    
    @Override
    public void act(float delta){
      if(started){
        r = color_r;
        g = color_g;
        b = color_b;
        a = color_a;
        started = false; //turns actor so the button can be pressed again.
      }
    }
  }
  
  private Stage ui; //ui is my stage in where my actors are placed.
  Globe globe;

  @Override
  public void create () 
  {
    InputMultiplexer im = new InputMultiplexer(); //allows for multiple event handling.
    ui = new Stage();
    
    color_button red = new color_button(255, 0, 0, 255, 1); //create button object
    red.set_position(30, (int)(Gdx.graphics.getHeight()*.9) );
    
    color_button green = new color_button(0, 255, 0, 255, 2);
    green.set_position(30, (int)(Gdx.graphics.getHeight()*.8));
    
    color_button blue = new color_button(0, 0, 255, 255, 3);
    blue.set_position(30, (int)(Gdx.graphics.getHeight()*.7));
    
    //I believe this code is for touch screen capabilities on android platforms. not sure.
    /*
    red.setTouchable(Touchable.enabled); 
    green.setTouchable(Touchable.enabled);
    blue.setTouchable(Touchable.enabled);
    */
    
    //add the buttons to the stage.
    ui.addActor(red);
    ui.addActor(blue);
    ui.addActor(green);
    
    im.addProcessor(ui); //add ui to be processed by input multiplexer
    
    
    
    globe = new Globe(180, 28, .1f);
    im.addProcessor(new InputAdapter()
    {
      public boolean touchDown(int screenX, int screenY, int pointer, int button) 
      {
        Globe.Coord coord = globe.GetCoord(screenX, screenY);

        if (coord.valid)
        {
          globe.SetColorAt(coord, r, g, b, a); //sets color on initial click
          globe.PrintColors();		
        }

        return true;
      }

      public boolean touchDragged(int screenX, int screenY, int pointer) 
      {
        Globe.Coord coord = globe.GetCoord(screenX, screenY);
        
        if (coord.valid)
        {
          globe.SetColorAt(coord, r, g, b, a); //sets color if mouse is held.
          globe.PrintColors();	
        }
        return false;
      }

      public boolean touchUp(int screenX, int screenY, int pointer, int button) 
      {
        return false;
      }
      
    });
    globe.PrintColors();
    

    Gdx.input.setInputProcessor(im); //set to process input multiplexer   
  }

  @Override
  public void render () 
  {
    globe.render();
    
    ui.act(Gdx.graphics.getDeltaTime()); //allows for actor event handling.
    ui.draw(); //draws the stage otherwise actors are invisible. functional, but invisible.
  }
}
