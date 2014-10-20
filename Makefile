ci:
	git add `find src/ doc/ bin/` Makefile 
	git commit
	git push https://github.com/smorad/blinkylights master

client:
	mkdir bin/client/ || true
	javac src/client/paint.java
	mv src/client/*.class bin/client
