package org.finomnis.fftimageeditor;

import java.nio.ByteBuffer;

import org.finomnis.fftimageeditor.Rendering.RenderingEnvironment;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.llb.CL;

public class Main {

	public static void genData(CLBuffer<ByteBuffer> buf, int width, int height)
	{
		ByteBuffer b = buf.getBuffer();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int col = 0; col < 4; col++) {
					byte val = (byte)(Math.random()*255);
					
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
					
					if(col == 3) val = 0;

					b.put((width * y + x) * 4 + col, val);

				}
			}
		}
		RenderingEnvironment.getCLCommandQueue().putWriteBuffer(buf, true);
	}
	
	public static void main(String[] args) {

		RenderingEnvironment.init();
		
		int width = 512;
		int height = 512;
		
		CLBuffer<ByteBuffer> buf = RenderingEnvironment.getCLGLContext().createByteBuffer(width*height*4, CL.CL_MEM_READ_WRITE);
		
		long start = new java.util.Date().getTime();
		while(new java.util.Date().getTime() - start < 2000)
		{
			genData(buf, width, height);
			RenderingEnvironment.getCLDebugWindow().setData(buf, width, height);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		width = 128;
		height = 128;
		
		buf.release();
		buf = RenderingEnvironment.getCLGLContext().createByteBuffer(width*height*4, CL.CL_MEM_READ_WRITE);
		
		start = new java.util.Date().getTime();
		while(new java.util.Date().getTime() - start < 2000)
		{
			genData(buf, width, height);
			RenderingEnvironment.getCLDebugWindow().setData(buf, width, height);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
