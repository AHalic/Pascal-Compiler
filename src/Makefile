# Modifique as variaveis conforme o seu setup.

JAVA=java
JAVAC=javac

# Eu uso ROOT como o diretório raiz para os meus labs.
YEAR=$(shell pwd | grep -o '20..-.')
ROOT=/usr/local/lib

ANTLR_PATH=$(ROOT)/antlr-4.9.3-complete.jar
CLASS_PATH_OPTION=-cp .:$(ANTLR_PATH)

# Comandos como descritos na página do ANTLR.
ANTLR4=$(JAVA) -jar $(ANTLR_PATH)
GRUN=$(JAVA) $(CLASS_PATH_OPTION) org.antlr.v4.gui.TestRig

# Diretório para aonde vão os arquivos gerados.
GEN_PATH=parser

# Diretório para os casos de teste
DATA=$(ROOT)/tests
IN=$(DATA)/in

all: antlr javac
	@echo "Done."

# Opção -no-listener foi usada para que o ANTLR não gere alguns arquivos
# desnecessários para o momento. Isto será explicado melhor nos próximos labs.
antlr: EZLexer.g EZParser.g
	$(ANTLR4) -no-listener -o $(GEN_PATH) EZLexer.g EZParser.g

javac:
	$(JAVAC) $(CLASS_PATH_OPTION) $(GEN_PATH)/*.java

# 'EZ' é o prefixo comum das duas gramáticas (EZLexer e EZParser).
# 'program' é a regra inicial de EZParser.
run:
	cd $(GEN_PATH) && $(GRUN) EZ program $(FILE)

runall:
	-for FILE in $(IN)/*.ezl; do \
	 	cd $(GEN_PATH) && \
	 	echo -e "\nRunning $${FILE}" && \
	 	$(GRUN) EZ program $${FILE} && \
	 	cd .. ; \
	done;

clean:
	@rm -rf $(GEN_PATH)
