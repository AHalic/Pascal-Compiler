JAVA=java
JAVAC=javac

YEAR=$(shell pwd | grep -o '20..-.')

# TODO: change this two variables to yours path

# where your antlr is located
ROOT=/usr/local/lib
ANTLR_PATH=$(ROOT)/antlr-4.9.3-complete.jar

CLASS_PATH_OPTION=-cp .:$(ANTLR_PATH)


ANTLR4=$(JAVA) -jar $(ANTLR_PATH)
GRUN=$(JAVA) $(CLASS_PATH_OPTION) org.antlr.v4.gui.TestRig

# Directory to which the generated files go
GEN_PATH=parser

# Directory containing the tests
DATA=../../examples


# Change to the name of the test you want to run
FILE=../../examples/synerr01.pas


all: antlr javac
	@echo "Done."

antlr: src/pascalLexer.g4 src/pascalParser.g4
	cd src && $(ANTLR4) -no-listener -o $(GEN_PATH) pascalLexer.g4 pascalParser.g4

javac:
	cd src && $(JAVAC) $(CLASS_PATH_OPTION) $(GEN_PATH)/*.java

run:
	cd src/$(GEN_PATH) && $(GRUN) pascal program $(FILE)

runall:
	for FILE in ${DATA}/*.pas; do \
	 	cd src/$(GEN_PATH) && \
	 	echo -e "\n\nRunning $${FILE}" && \
		$(GRUN) pascal program $${FILE} && \
		echo -e "\n\n$${FILE} Done" && \
	 	cd .. ; \
	done;

clean:
	@rm -rf src/$(GEN_PATH)
