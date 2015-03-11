
-donate https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=BQL9C44C4ZCGJ

-clone the project with: git clone https://github.com/colin-i/flashalizer.git
-download Java Native Access jar file at https://github.com/twall/jna and uncompress the archive into "bin"
-download Javassist jar file at https://github.com/jboss-javassist/javassist/releases and uncompress the archive into "bin"
-run: ant compile run ; clean with: ant clean


-if starting with an argument, the argument will be the project folder, same as File > Open
-ActionSWF is at http://oa.netau.net/programs/download/actionswf.zip
-when working with functions, if "action"/"action_sprite" is called, the write function for them will be at "swf_showframe"/"swf_sprite_showframe"