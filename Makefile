JAVA=java
JAVAC=javac

ROOT=/usr/local/lib

ANTLR_PATH=$(ROOT)/antlr-4.9-complete.jar
CLASS_PATH_OPTION=-cp .:$(ANTLR_PATH)

ANTLR4=$(JAVA) -jar $(ANTLR_PATH)
GRUN=$(JAVA) $(CLASS_PATH_OPTION) org.antlr.v4.gui.TestRig

GEN_PATH=parser

MAIN_PATH=checker

BIN_PATH=bin

IN=examples
OUT=./out05

all: antlr javac
	@echo "Done."

antlr: pascalLexer.g4 pascalParser.g4
	$(ANTLR4) -no-listener -visitor -o $(GEN_PATH) pascalLexer.g4 pascalParser.g4

javac:
	rm -rf $(BIN_PATH)
	mkdir $(BIN_PATH)
	$(JAVAC) $(CLASS_PATH_OPTION) -d $(BIN_PATH) */*.java

run:
	$(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) $(MAIN_PATH)/Main $(FILE)

clean:
	@rm -rf $(GEN_PATH) $(BIN_PATH) $(OUT)
