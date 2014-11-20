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

//button is an Actor
public class Actor_button extends Actor {
  Texture texture;
  float x_position = 0, y_position = 0;
  public boolean started = false;
  
  public void initialize_actor_button(int index) {
    if (index == 1) { texture = new Texture(Gdx.files.internal("red.png")); }
    if (index == 2) { texture = new Texture(Gdx.files.internal("green.png")); }
    if (index == 3) { texture = new Texture(Gdx.files.internal("blue.png")); }
	if (index == 4) { texture = new Texture(Gdx.files.internal("orange.png")); }
	if (index == 5) { texture = new Texture(Gdx.files.internal("yellow.png")); }
	if (index == 6) { texture = new Texture(Gdx.files.internal("purple.png")); }
    
    setBounds(x_position, y_position, texture.getWidth(), texture.getHeight());
    addListener(new InputListener(){
      public boolean touchDown (InputEvent event, float r, float g, int pointer, int Actor_button) {
        ((Actor_button)event.getTarget()).started = true;
        return true;
      }
    });
  }
  
  public void set_position(int x, int y) { 
    x_position = x - texture.getWidth()/2;
    y_position = y - texture.getHeight()/2;
    setBounds(x_position, y_position, texture.getWidth(), texture.getHeight());
  }
  
  @Override
  public void draw(Batch batch, float alpha) {
    batch.draw(texture, x_position, y_position);
  }
}