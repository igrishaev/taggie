
toc:
	@cat Makefile | grep : | grep -v PHONY

repl:
	lein with-profile +test repl

release:
	lein release

.PHONY: test
test:
	lein test

snapshot:
	lein with-profile uberjar install
	lein with-profile uberjar deploy
