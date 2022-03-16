JAVA=java
JAVAC=javac

# Paths
MAIN_PATH=src
GEN_PATH=src/parser
BIN_PATH=bin
ANTLR_PATH=./lib/antlr-4.9.3-complete.jar

# Antlr settings
ANTLR4=$(JAVA) -jar $(ANTLR_PATH)
GRUN=$(JAVA) $(CLASS_PATH_OPTION) org.antlr.v4.gui.TestRig
CLASS_PATH_OPTION=-cp .:$(ANTLR_PATH)

IN=examples
OUT=./out

all: antlr javac
	@echo "Done!"

antlr: src/antlr/PascalLexer.g4 src/antlr/PascalParser.g4
	@echo "Generating lexer and parser..."
	$(ANTLR4) -no-listener -visitor -o $(GEN_PATH) src/antlr/PascalLexer.g4 src/antlr/PascalParser.g4 -Xexact-output-dir

javac:
	@echo "Compiling compiler..."
	@rm -rf $(BIN_PATH)
	@mkdir $(BIN_PATH)
	$(JAVAC) $(CLASS_PATH_OPTION) -d $(BIN_PATH) ./src/*/*.java ./src/*.java
runall:
	@./runall.sh

run:
	$(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) Main $(FILE) $(OUTPUT)

run_dot:
	$(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) Main $(FILE) $(OUTPUT) 2> graph.dot
	@dot -Tpng graph.dot -o graph.png

clean:
	@rm -rf $(GEN_PATH) $(BIN_PATH) $(OUT)