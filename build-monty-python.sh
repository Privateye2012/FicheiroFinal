#!/bin/bash


export CLASSPATH=/opt/antlr4/antlr-4.13.1-complete.jar
export PATH=$PATH:/opt/antlr4

MPWD=`pwd`
JAVA_OUTPUT="$MPWD/target/generated-sources/antlr4/pt/uab/compilacao"
JAVA_PACKAGE="pt.uab.compilacao"
CLASS_OUTPUT="$MPWD/target/classes"

function init() {
    mkdir -p $JAVA_OUTPUT
}

#
# Build ANTLR with visitor
#
function executeAntlr() {
    cd $MPWD/src/main/antlr4
	antlr4 -Dlanguage=Java -package $JAVA_PACKAGE -o $JAVA_OUTPUT -visitor MontyPythonLexer.g4
	antlr4 -Dlanguage=Java -package $JAVA_PACKAGE -o $JAVA_OUTPUT -visitor MontyPythonParser.g4
}

#
# Copia os programas fonte todos para o destino que 
# vai permitir compilar tudo (gerado pelo antlr e feitos à mão)
#
copyJavaSrc() {
    cp $MPWD/src/main/java/pt/uab/compilacao/*.java $JAVA_OUTPUT
}

function build() {
    executeAntlr
    copyJavaSrc
    cd $JAVA_OUTPUT
    javac -cp $CLASSPATH *.java -d $CLASS_OUTPUT
}

#
# Verifica se o classpath está afectado e contem a lib do
# antlr
#
function checkClassPath() {
    if [ -z "$CLASSPATH" ] || [[ "$CLASSPATH" != *antlr4* ]]; then
        echo "A variável CLASSPATH não está definida ou não contém a lib do antlr4."
        exit 1
    fi
}

function clean() {
    rm -rf target
}

checkClassPath
clean
init
build