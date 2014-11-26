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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;

import com.badlogic.gdx.files.*;
import com.badlogic.gdx.Files.FileType;

public class Layer
{

   Layer(int width, int height, String text)
   {

      pix = new Pixmap(width, height, Pixmap.Format.RGBA8888);
      pixels = new byte[width * height * 4];
      buffer = pix.getPixels();

      this.width = width;
      this.height = height;

    // load the font
    //FileHandle handle = Gdx.files.getFileHandle("font/font.fnt", FileType.Internal);
    FileHandle handle = Gdx.files.getFileHandle("data/default.fnt", FileType.Internal);
    BitmapFont font = new BitmapFont(handle);

    // get the glypth info
      data = font.getData();
      fontPixmap = new Pixmap(Gdx.files.internal(data.imagePaths[0]));
      int text_length = text.length();

      for (int i = 0; i < text_length; i++) {
        DrawLetter(text.charAt(i), 0);
      }
   }

   BitmapFontData data;
   Pixmap fontPixmap;
   float advance = 0;
   char last;

   void DrawLetter(char letter, int at)
   {
       Glyph glyph = data.getGlyph(letter);


      advance += glyph.getKerning(letter);


      pix.drawPixmap(fontPixmap, (int)advance + glyph.xoffset, (3 - (glyph.height + glyph.yoffset)),
      glyph.srcX, glyph.srcY, glyph.width, glyph.height);

      advance += glyph.xadvance ;    

      last = letter;

   }

   public float x, y;
   public int width, height;

   private Pixmap pix;
   private byte pixels[];
   private ByteBuffer buffer;

   public int GetPixelAt(int at)
   {
      return buffer.get(at);
   }
   public void SetColor(int r, int g, int b, int a)
   {
      for (int i =0; i < width; i++ ) 
      {
         for (int j = 0; j < height; j++ ) 
         {
            SetColorAt(i, j,r, g, b, a);
         }
      }
   }


   //helper function for manual color setting.
   public void SetColorAt(int x, int yy, int r, int g, int b, int a)
   {
   	if(x < 0 || x >= width || y < 0 || y >= height) return;

      int y = (height - 1 - yy);
      buffer.put((x + y * width)*4 + 0, (byte)r);
      buffer.put((x + y * width)*4 + 1, (byte)g);
      buffer.put((x + y * width)*4 + 2, (byte)b);
      buffer.put((x + y * width)*4 + 3, (byte)a);
   }
}