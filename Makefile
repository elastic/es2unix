NAME = es
VERSION = $(shell git ver)
BIN = $(NAME)-$(VERSION)
S3HOME = s3://download.elasticsearch.org/es2unix

clean:
	lein clean

test:
	bin/test

package: clean test
	mkdir -p etc
	echo -n $(VERSION) >etc/version.txt
	lein bin

install: package
	cp target/$(BIN) ~/bin/$(NAME)

release: package
	s3cmd -c $(S3CREDS) put -P target/$(BIN) $(S3HOME)/$(BIN)
	s3cmd -c $(S3CREDS) cp $(S3HOME)/$(BIN) $(S3HOME)/$(NAME)
