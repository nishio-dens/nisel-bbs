package bbs.util;

import java.security.MessageDigest;

import bbs.BBSConfiguration;

public class Digest {
	
	private static final String CHARACTER_ENCODING = BBSConfiguration.DEFAULT_ENCODING;
	  
	/**
	 * 文字列からダイジェスト生成
	 * @param data
	 * @return
	 * @throws Exception
	 */
	  public static String getStringDigest(String data) throws Exception {
	    MessageDigest md = MessageDigest.getInstance("SHA");
	    byte[] dat = data.getBytes(CHARACTER_ENCODING);
	    md.update(dat);
	    
	    dat = md.digest();
	    StringBuffer buf = new StringBuffer();
	    for (int i = 0; i < dat.length; i++) {
	        int d = dat[i];
	        if (d < 0) {
	          d += 256;
	        }
	        if (d < 16) {
	          buf.append("0");
	        }
	        buf.append(Integer.toString(d, 16));
	      }
	    return buf.toString();
	  }

}

