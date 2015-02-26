LEIN ?= lein
NAME = es
VERSION = $(shell git describe --tags)
BIN = $(NAME)-$(VERSION)
S3HOME = s3://download.elasticsearch.org/es2unix

package: target/$(BIN)

test: install
	bin/test

install: target/$(BIN)
	cp target/$(BIN) ~/bin/$(NAME)

release: target/$(BIN)
	s3c es.download put -P target/$(BIN) $(S3HOME)/$(BIN)
	s3c es.download cp $(S3HOME)/$(BIN) $(S3HOME)/$(NAME)

clean:
	$(LEIN) clean

target/$(BIN):
	mkdir -p etc
	echo $(VERSION) >etc/version.txt
	LEIN_SNAPSHOTS_IN_RELEASE=yes $(LEIN) bin

