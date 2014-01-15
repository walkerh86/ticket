package net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.ImageIcon;

import util.Log;

public class ImageHttpResponse extends MyHttpResponse<ImageIcon>{
	private String mImageUrl;
	
	public ImageHttpResponse(String imgUrl, HttpResponseHandler handler, int step){
		super(handler,step);
		mImageUrl = imgUrl;
	}
	
	@Override
	protected ImageIcon parseContent(InputStream is){
		try{
			File imgFile = new File(mImageUrl);
			OutputStream out = new FileOutputStream(imgFile);
			int byteread = 0;
			byte[] tmp = new byte[1024];
			while ((byteread = is.read(tmp)) != -1) {
				out.write(tmp,0,byteread);
			}
		}catch(FileNotFoundException e){
			Log.i("ImageResponse e="+e+"]n");
		}catch(IOException e){
			Log.i("ImageResponse e="+e+"]n");
		}
		
		return new ImageIcon(mImageUrl);
	}
}
