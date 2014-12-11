package org.finomnis.fftimageeditor.Rendering;

import java.awt.event.ComponentEvent;
import java.util.concurrent.Semaphore;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLEventList;
import com.jogamp.opencl.gl.CLGLTexture2d;

public class CLDebugWindow implements GLEventListener {

	//private final GLUgl2 glu = new GLUgl2();

	private int width;
	private int height;
	private CLGLTexture2d<?> glClTexture = null;
	
	private GLCanvas canvas;
	private JFrame frame;
	
	private Semaphore initializationDone = new Semaphore(0);

	public CLDebugWindow(boolean visible) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initUI();
			}
		});
		
		try {
			initializationDone.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	private void initUI() {

		this.width = 600;
		this.height = 400;

		GLCapabilities config = RenderingEnvironment.getGLCaps();
		config.setSampleBuffers(true);
		config.setNumSamples(4);

		canvas = new GLCanvas(config);
		canvas.setSharedAutoDrawable(RenderingEnvironment.getSharedDrawable());
		canvas.addGLEventListener(this);


		
		frame = new JFrame("JOGL-JOCL Interoperability Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(canvas);
		canvas.setSize(width, height);
		frame.pack();

		frame.setVisible(true);

		resizeSharedTexture(64, 64);

		initializationDone.release();
	}

	private void resizeSharedTexture(int width, int height) {

		System.out.println("Resizing to " + width + "x" + height + " ...");
		
		GLContext glContext = GLContext.getCurrent();
		boolean movedContext = false;		
		
		if(glContext == null)
		{
			glContext = RenderingEnvironment.getCLGLContext().getGLContext();
			glContext.makeCurrent();
			movedContext = true;
		}
		
		GL2 gl = glContext.getGL().getGL2();

		if (glClTexture != null) {
			glClTexture.release();
			int[] deleteTextures = new int[1];
			deleteTextures[0] = glClTexture.getGLObjectID();
			gl.glDeleteTextures(1, deleteTextures, 0);
			glClTexture = null;
		}

		// Generate Texture
		int[] newTex = new int[1];
		gl.glGenTextures(1, newTex, 0);
		int glTexture = newTex[0];

		/*ByteBuffer b = ByteBuffer.allocate(3 * width * height);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int col = 0; col < 3; col++) {
					byte val;
					
					if(col == 1)
						val = (byte) 0;
					else
						val = (byte) 255;

					if (x < 30) {
						if(col == 1)
							val = (byte) 255;
						else
							val = (byte) 0;
						
					}
					
					if (y < 30) {
						if(col == 2)
							val = (byte) 255;
						else
							val = (byte) 0;
						
					}

					b.put((width * y + x) * 3 + col, val);

				}
			}
		}*/

		// Initialize texture
		gl.glBindTexture(GL.GL_TEXTURE_2D, glTexture);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_NEAREST);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0,
				GL.GL_RGB, GL.GL_UNSIGNED_BYTE, null);
		
		gl.glEnable(GL.GL_TEXTURE_2D);

		// Share texture with OpenCL
		glClTexture = RenderingEnvironment.getCLGLContext().createFromGLTexture2d(GL.GL_TEXTURE_2D, glTexture, 0, 0);
		
		// Release context if we moved it to the current thread
		if(movedContext)
		{
			glContext.release();
		}
		
		this.width = width;
		this.height = height;

		canvas.setSize(this.width, this.height);
		frame.pack();
		
	}

	@Override
	public void display(GLAutoDrawable drawable) {
				
		GL2 gl = drawable.getGL().getGL2();

		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, glClTexture.getGLObjectID());

		// draw a triangle filling the window
		gl.glBegin(GL.GL_TRIANGLES);

		// bind the texture

		gl.glColor3f(1, 1, 1);

		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex2f(-1, -1);

		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex2f(1, 1);

		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex2f(1, -1);

		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex2f(1, 1);

		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex2f(-1, -1);

		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex2f(-1, 1);

		// System.out.println(glu.gluErrorString(gl.glGetError()));

		gl.glEnd();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		System.out.println("Init CLDebugWindow");

	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		/*// TODO Auto-generated method stub
		System.out.println("Frame size:  " + frame.getWidth() + "x" + frame.getHeight());
		System.out.println("Canvas size: " + canvas.getWidth() + "x" + canvas.getHeight());

		int cWidth = canvas.getWidth();
		int cHeight = canvas.getHeight();
		
		float origRatio = width / (float)height;
		float cRatio = cWidth / (float)cHeight;
		
		if(cRatio > origRatio)
		{
			cWidth = (int)(cHeight * origRatio);
		}
		else
		{
			cHeight = (int)(cWidth / origRatio);
		}
		
		canvas.setSize(cWidth, cHeight);
		frame.pack();
		
		canvas.repaint();*/
		
	}

	public void setData(CLBuffer<?> input, int width, int height) {

		if(this.width != width || this.height != height)
		{
			resizeSharedTexture(width, height);
		}
		
		CLCommandQueue cc = RenderingEnvironment.getCLCommandQueue();
		
		CLEventList events  = new CLEventList(1);
		CLEventList events2 = new CLEventList(1);
		CLEventList events3 = new CLEventList(1);
		
		cc.putAcquireGLObject(glClTexture, events);
		cc.putCopyBufferToImage(input, glClTexture, events, events2);
		cc.putReleaseGLObject(glClTexture, events2, events3);
		
		events3.waitForEvents();
		
		canvas.repaint();
		
	}

}
