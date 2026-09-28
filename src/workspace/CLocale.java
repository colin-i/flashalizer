package workspace;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

interface CLocale extends Library{
	CLocale INSTANCE=(CLocale)Native.loadLibrary(Platform.C_LIBRARY_NAME,CLocale.class);
	String setlocale(int category,String locale);

	static void useCLocaleForNumeric(){
		int category;
		if(Platform.isLinux())category=1;
		else if(Platform.isWindows()||Platform.isMac())category=4;
		else throw new UnsupportedOperationException("LC_NUMERIC is not configured for this platform");
		if(INSTANCE.setlocale(category,"C")==null)
			throw new IllegalStateException("Failed to set native LC_NUMERIC to C");
	}
}
