
toc:        # list targets (default action)
	@cat Makefile | grep : | grep -v PHONY | sort

repl:       # run repl
	lein with-profile +test repl

release:    # publish a new release
	lein release

.PHONY: test
test:       # run tests
	lein test

snapshot:   # install and release a snapshot version
	lein with-profile uberjar install
	lein with-profile uberjar deploy
