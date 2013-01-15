clean:
	lein clean

package: clean
	lein bin
	cp target/es ~/bin
