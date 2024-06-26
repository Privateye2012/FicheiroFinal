
.PHONY: build
build:
	antlr4 -Dlanguage=Java -visitor MontyPythonLexer.g4
	antlr4 -Dlanguage=Java -visitor MontyPythonParser.g4
	javac *.java

# Run the all examples with grun
.PHONY: tests
tests:
	rm -rf tests/results.txt
	for f in tests/*.mp; \
	do \
		echo "Running $$f" >> tests/results.txt ; \
		grun MontyPython program $$f >> tests/results.txt 2>>  tests/results.txt; \
	done

.PHONY: debug
debug:
	grun MontyPython program -gui exemplos/ex1.mp;

.PHONY: clean
clean:
		-rm -f MontyPython*.java *.class *.tokens *.interp

.PHONY: zip
zip:
	zip -r ../MontyPython.zip Makefile MontyPython*.g4 tests/ README.txt indent_atempt/