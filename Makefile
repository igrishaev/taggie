
repl:
	lein with-profile +test repl

release:
	lein release

.phony: test
test:
	lein test

snapshot:
	lein with-profile uberjar install
	lein with-profile uberjar deploy
