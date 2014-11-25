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
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class Lights extends ApplicationAdapter implements ApplicationListener
{
  public static final String RED  = "colors/red.png";
  public static final String GREEN = "colors/green.png";
  public static final String BLUE = "colors/blue.png";
  public static final String ORANGE = "colors/orange.png";
  public static final String YELLOW = "colors/yellow.png";
  public static final String PURPLE = "colors/purple.png";
  public static final String WHITE = "colors/white.png";
  public static final String BLACK = "colors/black.png";

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
    public color_button(int w, int x, int y, int z, String color){
      initialize_actor_button(color); //initialize the button on parent class level.
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
    int rotate_pos = (int)(Gdx.graphics.getWidth()*.1);
    int color_pos =  (int)(Gdx.graphics.getHeight());
    
    Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
    InputMultiplexer im = new InputMultiplexer(); //allows for multiple event handling.
    globe = new Globe(180, 28, .1f);
    ui = new Stage();
    
    //button declarations - (r, g, b, a (alpha), location)
    color_button button_red = new color_button(255, 0, 0, 255, RED); //red color_button
    color_button button_green = new color_button(0, 255, 0, 255, GREEN); //green color_button
    color_button button_blue = new color_button(0, 0, 255, 255, BLUE); //blue color_button
    color_button button_orange = new color_button(255, 135, 0, 255, ORANGE); //orange color_button
    color_button button_yellow = new color_button(255, 255, 0, 255, YELLOW); //yellow color_button
    color_button button_purple = new color_button(185, 85, 210, 255, PURPLE); //purple color_button
    color_button button_white = new color_button(255, 255, 255, 255, WHITE); //white color_button
    color_button button_black = new color_button(0, 0, 0, 255, BLACK); //eraser
    
    TextButton button_left_rotate = new TextButton("<",skin);
    TextButton button_stop = new TextButton("X",skin);
    TextButton button_right_rotate = new TextButton(">",skin);
    final TextArea text_message = new TextArea("",skin);
    TextButton button_display_text = new TextButton("Display",skin);
    TextButton button_upload = new TextButton("Upload",skin);
    TextButton button_clear = new TextButton("Clear",skin);
    
    
    //button positioninng
    button_red.set_position(30, (int)(color_pos*.9)); //x_position, y_position
    button_green.set_position(30, (int)(color_pos*.8));
    button_blue.set_position(30, (int)(color_pos*.7));
    button_orange.set_position(30, (int)(color_pos*.6));
    button_yellow.set_position(30, (int)(color_pos*.5));
    button_purple.set_position(30, (int)(color_pos*.4));
    button_white.set_position(30, (int)(color_pos*.3));
    button_black.set_position(30, (int)(color_pos*.2));
    
    button_left_rotate.setBounds(rotate_pos-30, 0, 30, 30); //x_position, y_position, width, height)
    button_stop.setBounds(rotate_pos, 0, 30, 30);
    button_right_rotate.setBounds((int)(rotate_pos+30), 0, 30, 30);
    text_message.setBounds(200, 0, 200, 30); 
    button_display_text.setBounds(420, 0, 80, 30);
    button_upload.setBounds((int)(Gdx.graphics.getWidth()-80), 0, 80, 30);
    button_clear.setBounds((int)(Gdx.graphics.getWidth()-80), 30, 80, 30);
    
    
    //button functionality
    button_left_rotate.addListener(new ClickListener(){
      public void clicked(InputEvent event, float x, float y){ globe.add_speed((float).1); }
    });
    
    button_stop.addListener(new ClickListener(){ 
      public void clicked(InputEvent event, float x, float y){ globe.add_speed((float)0); }
    });
    
    button_right_rotate.addListener(new ClickListener(){
      public void clicked(InputEvent event, float x, float y){ globe.add_speed((float)-.1); }
    });
    
    button_clear.addListener(new ClickListener(){
      public void clicked(InputEvent event, float x, float y){
		globe.SetColor(0, 0, 0, 255);
	}
    });
    
    button_display_text.addListener(new ClickListener(){
      @Override 
      public void clicked(InputEvent event, float x, float y){    
        // When the button_display_text is clicked, get the message text or create a default string value
        //globe.layer_text(text_message.getText());   // + ("\n"); // Brute for a newline so readline gets a line
        System.out.println( text_message.getText() );
        globe.set_z(text_message.getText());
      }
    });
    
    //I believe this code is for touch screen capabilities on android platforms. not sure.
    /*
    button_red.setTouchable(Touchable.enabled); 
    button_green.setTouchable(Touchable.enabled);
    button_blue.setTouchable(Touchable.enabled);
    */

    //add the buttons to the stage.
    ui.addActor(button_red);
    ui.addActor(button_green);
    ui.addActor(button_blue);
    ui.addActor(button_orange);
    ui.addActor(button_yellow);
    ui.addActor(button_purple);
    ui.addActor(button_white);
    ui.addActor(button_black);
    ui.addActor(button_left_rotate);
    ui.addActor(button_stop);
    ui.addActor(button_right_rotate);
    ui.addActor(text_message);
    ui.addActor(button_display_text);
    ui.addActor(button_upload);
    ui.addActor(button_clear);
    
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
