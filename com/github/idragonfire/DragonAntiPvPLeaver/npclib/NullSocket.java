package com.github.idragonfire.DragonAntiPvPLeaver.npclib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NullSocket extends Socket {

	@Override
	public InputStream getInputStream() {
		final byte[] buf = new byte[5];
		return new ByteArrayInputStream(buf);
	}

	@Override
	public OutputStream getOutputStream() {
		return new ByteArrayOutputStream();
	}

}