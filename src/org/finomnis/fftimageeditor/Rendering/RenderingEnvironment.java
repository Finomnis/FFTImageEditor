package org.finomnis.fftimageeditor.Rendering;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLProfile;

import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLPlatform;
import com.jogamp.opencl.gl.CLGLContext;

public class RenderingEnvironment {

	private static CLDebugWindow clDebugWindow;
	
	private static CLGLContext clGlContext;
	private static CLDevice clDevice;
	private static CLCommandQueue clCommandQueue;
	private static GLAutoDrawable sharedDrawable;
	private static GLCapabilities glCaps;
	
	public static GLAutoDrawable getSharedDrawable()
	{
		return sharedDrawable;
	}
	
	public static GLCapabilities getGLCaps()
	{
		return glCaps;
	}
	
	public static CLGLContext getCLGLContext()
	{
		return clGlContext;
	}
	
	public static CLCommandQueue getCLCommandQueue()
	{
		return clCommandQueue;
	}
	
	public static CLDebugWindow getCLDebugWindow()
	{
		return clDebugWindow;
	}
	
	
	
	public static void init()
	{
		// init OpenGL
		GLProfile.initSingleton();
		
		// Set OpenGL caps
		glCaps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		
		// Create shared OpenGL context
		final GLProfile glp = glCaps.getGLProfile();
		sharedDrawable = GLDrawableFactory.getFactory(glp).createDummyAutoDrawable(null, true, glCaps, null);
		sharedDrawable.display();
				
		// search for OpenCL device
		CLDevice[] devices = CLPlatform.getDefault().listCLDevices();
		for (CLDevice d : devices) {
			if (d.isGLMemorySharingSupported()) {
				clDevice = d;
				break;
			}
		}
		if (null == clDevice) {
			throw new RuntimeException(
					"Couldn't retrieve OpenGL shared OpenCL device ...");
					//"Hallo Maddin, ich lasse dir liebe Gruesse da! Hab eine gute Zeit und hoffentlich bis bald! ..");
		}
		
		// Retrieve OpenGL context
		GLContext glContext = sharedDrawable.getContext();
		if (glContext == null) {
			throw new RuntimeException(
					"Couldn't retrieve GL context ..");
		}
		//System.out.println(glContext.isShared()?"Shared":"Not shared");
		
		// create OpenCL context before creating any OpenGL objects
		// you want to share with OpenCL (AMD driver requirement)
		glContext.makeCurrent();
		clGlContext = CLGLContext.create(glContext, clDevice);
		glContext.release();
		
		clCommandQueue = clDevice.createCommandQueue();
		
		clDebugWindow = new CLDebugWindow(true);

	}
	
}
