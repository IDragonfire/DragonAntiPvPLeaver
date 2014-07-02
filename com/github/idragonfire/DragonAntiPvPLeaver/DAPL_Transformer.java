package com.github.idragonfire.DragonAntiPvPLeaver;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class DAPL_Transformer {
	public static final String FIELD_DELAYED = "dragonfire_dapl_delay_disconnect";
	public static final String FIELD_CONTINUE = "dragonfire_dapl_continue_disconnect";
	public static String LINE_SEPERATOR = System.getProperty("line.separator");

	private String playerConnectionDisconnectMethodName;
	private String entityBaseTickMethod;

	public DAPL_Transformer() {
		setPlayerConnectionDisconnectMethodName(findPlayerConnectionDisconnectMethodName());
		setEntityBaseTickMethod(findEntityBaseTickMethod());
	}

	public String getRunningJarPath() {
		URL url = this.getClass().getProtectionDomain().getCodeSource()
				.getLocation();
		try {
			return new File(url.toURI()).getAbsolutePath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return url.getPath().replaceAll("%20", " ");
	}

	public void transform(CtClass cc) throws CannotCompileException,
			NotFoundException {
		System.out.println("transform");
		// load DAPL_Injection
		try {
			ClassPool.getDefault().insertClassPath(getRunningJarPath());
			ClassPool.getDefault().get("net.minecraft.server.DAPL_Injection")
					.toClass();
		} catch (Exception e) {
			e.printStackTrace();
		}

		cc.addField(CtField.make("public boolean " + FIELD_DELAYED
				+ " = false;", cc));
		cc.addField(CtField.make("public boolean " + FIELD_CONTINUE
				+ " = false;", cc));
		CtMethod disconnectMethod = cc.getDeclaredMethod(
				getPlayerConnectionDisconnectMethodName(),
				findPlayerConnectionDisconnectMethodParams());
		disconnectMethod.insertBefore(
		// check if plugin allow disconnect
				"{if(!this."
						+ FIELD_CONTINUE
						// if method called second player not present
						// but entity still there
						+ "){ if(this."
						+ FIELD_DELAYED
						+ ")"
						// process entity baseTick
						+ "{ this.player."
						+ getEntityBaseTickMethod()
						// if FakePlayer is dead, continue normal disconnect
						// TODO: use API method
						+ "(); if(this.player.dead) { this."
						+ FIELD_CONTINUE
						+ "=true;} "
						+ "return; }"
						+ "else {"
						// first the disconnect delayed
						+ "this."
						+ FIELD_DELAYED
						+ "=true;"
						// ask plugin if we can disconnect
						+ "if(!net.minecraft.server.DAPL_Injection.nmsDisconnectCall(this)) {"
						+ "return;}}}}");
		System.out.println("transformed");
		cc.toClass();
	}

	// atm 1.7.9
	// TODO: implement dynamic way
	@Deprecated
	public static String findEntityBaseTickMethod() {
		return "B";
	}

	// TODO: implement dynamic way
	@Deprecated
	public static String findPlayerConnectionDisconnectMethodName() {
		return "a";
	}

	// TODO: implement dynamic way
	public static CtClass[] findPlayerConnectionDisconnectMethodParams() {
		ClassPool pool = ClassPool.getDefault();
		try {
			CtClass cc = pool
					.get("net.minecraft.server.v1_7_R3.IChatBaseComponent");
			return new CtClass[] { cc };
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getNmsClass() {
		return "PlayerConnection.class";
	}

	public static void log(String msg) {
		System.out.println(msg);
	}

	public void transform() {
		try {
			ClassPool cp = ClassPool.getDefault();
			// TODO: implement dynmaic way
			CtClass cc = cp
					.get("net.minecraft.server.v1_7_R3.PlayerConnection");
			// check if class already injectsion, e.g. if server reloaded
			try {
				Class.forName("net.minecraft.server.DAPL_Injection");
				// if loaded -> no exception -> injected
				System.out.println("already injected");
				return;
			} catch (ClassNotFoundException e) {
				// if exception, class not loaded, all is k
			}
			transform(cc);
			System.out.println("pushed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPlayerConnectionDisconnectMethodName() {
		return playerConnectionDisconnectMethodName;
	}

	public void setPlayerConnectionDisconnectMethodName(
			String playerConnectionDisconnectMethodName) {
		this.playerConnectionDisconnectMethodName = playerConnectionDisconnectMethodName;
	}

	public String getEntityBaseTickMethod() {
		return entityBaseTickMethod;
	}

	public void setEntityBaseTickMethod(String entityBaseTickMethod) {
		this.entityBaseTickMethod = entityBaseTickMethod;
	}
}
