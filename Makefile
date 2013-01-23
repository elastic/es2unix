VERSION = $(shell cat etc/version.txt)
S3HOME = s3://download.elasticsearch.org/es2unix

clean:
	lein clean

test:
	bin/test

package: clean test
	lein bin

install: package
	cp target/es ~/bin

deploy: install
	s3cmd -c $(S3CREDS) put -P target/es $(S3HOME)/es-$(VERSION)
	s3cmd -c $(S3CREDS) cp $(S3HOME)/es-$(VERSION) $(S3HOME)/es
