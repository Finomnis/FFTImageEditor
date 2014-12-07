package org.finomnis.fftimageeditor.Rendering;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

public class CLEnvironment implements GLEventListener {

	public final static CLEnvironment INSTANCE = new CLEnvironment();
	
	private CLEnvironment()
	{
		
	}
	
	public void init()
	{
		GLProfile.initSingleton();

				
	}

	@Override
	public void display(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
