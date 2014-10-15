ci:
	git add `find src/ doc/ bin/` Makefile 
	git commit || true
	git push https://github.com/smorad/blinkylights master
