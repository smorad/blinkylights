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

public class Lights extends ApplicationAdapter
{

	
	Globe globe;

	@Override
	public void create () 
	{

		globe = new Globe(180, 28, .1f);

 
		Gdx.input.setInputProcessor(new InputAdapter() 
		{
		    public boolean touchDown(int screenX, int screenY, int pointer, int button) 
		    {
		    	Globe.Coord coord = globe.GetCoord(screenX, screenY);

		    	if (coord.valid)
		    	{
		    		globe.SetColorAt(coord, 255, 0, 0, 255);
		    		globe.PrintColors();		
		    	}

				return true;
		    }

    		public boolean touchDragged(int screenX, int screenY, int pointer) 
    		{
				Globe.Coord coord = globe.GetCoord(screenX, screenY);
				
		    	if (coord.valid)
		    	{
		    		globe.SetColorAt(coord, 255, 0, 0, 255);
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
	}

	@Override
	public void render () 
	{
		globe.render();
	}


}
