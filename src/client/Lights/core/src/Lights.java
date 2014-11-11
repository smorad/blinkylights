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
  int r = 255;
  int b = 0;
  int g = 0;
  int a = 255;
  
  Stage ui; //ui is my stage in where my actors are placed.
  Globe globe; //the globe representing the LEDs+position.

  //my subclass to add color buttons
  public class color_button extends Actor_button {
    int color_r, color_g, color_b, color_a;
    
    //subclass constructor
    public color_button(int w, int x, int y, int z, int index){
      initialize_actor_button(index); //initialize the button on parent class level.
      color_r = w; color_g = x;
      color_b = y; color_a = z;
    }
    
    @Override
    public void act(float delta){
      if(started){ 
        //set brush color
        r = color_r; g = color_g;  
        b = color_b; a = color_a;
        started = false;
      }
    }
  }
  
  @Override
  public void create () 
  {
    InputMultiplexer im = new InputMultiplexer(); //allows for multiple event handling.
    
    globe = new Globe(180, 28, .1f);
    ui = new Stage();
    
    color_button red = new color_button(255, 0, 0, 255, 1); //red color_button
    red.set_position(30, (int)(Gdx.graphics.getHeight()*.9) );
    
    color_button green = new color_button(0, 255, 0, 255, 2); //green color_button
    green.set_position(30, (int)(Gdx.graphics.getHeight()*.8));
    
    color_button blue = new color_button(0, 0, 255, 255, 3); //blue color_button
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
    
    im.addProcessor(ui);
    im.addProcessor(new InputAdapter() {
      public boolean touchDown(int screenX, int screenY, int pointer, int color_button) {
        Globe.Coord coord = globe.GetCoord(screenX, screenY);
        if (coord.valid) {
          globe.SetColorAt(coord, r, g, b, a); //sets color on initial click
          globe.PrintColors();		
        }
        return true;
      }

      public boolean touchDragged(int screenX, int screenY, int pointer) {
        Globe.Coord coord = globe.GetCoord(screenX, screenY);
        if (coord.valid) {
          globe.SetColorAt(coord, r, g, b, a);//sets color if mouse is held.
          globe.PrintColors();	
        }
        return false;
      }

      public boolean touchUp(int screenX, int screenY, int pointer, int color_button) {
        return false;
      }
      
    });
    globe.PrintColors();
    Gdx.input.setInputProcessor(im); //set to process input multiplexor
  }

  @Override
  public void render () 
  {
    globe.render();
    
    ui.act(Gdx.graphics.getDeltaTime()); //allows for actor events handling.
    ui.draw(); //draws the stage otherwise actors are invisible. functional, but invisible.
  }
}
