CO=doc
MARTINOUT=martin
TIBOROUT=tibor

all: tibor martin

tibor: tiborSettings $(TIBOROUT).pdf

martin: martinSettings $(MARTINOUT).pdf

tiborSettings:
	$(eval OUT=$(TIBOROUT))
	$(eval WHOS=tibor)

martinSettings:
	$(eval OUT=$(MARTINOUT))
	$(eval WHOS=martin)

$(TIBOROUT).pdf $(MARTINOUT).pdf: clean
	pdflatex -jobname=$(OUT) "\def\whos{$(WHOS)} \input{$(CO).tex}"
	bibtex $(OUT)
	pdflatex -jobname=$(OUT) "\def\whos{$(WHOS)} \input{$(CO).tex}"
	pdflatex -jobname=$(OUT) "\def\whos{$(WHOS)} \input{$(CO).tex}"

clean:
	rm -f *.dvi *.log *.aux $(MARTINOUT).blg $(MARTINOUT).bbl $(MARTINOUT).toc $(MARTINOUT).out $(MARTINOUT).lof $(MARTINOUT).ptc $(TIBOROUT).blg $(TIBOROUT).bbl $(TIBOROUT).toc $(TIBOROUT).out $(TIBOROUT).lof $(TIBOROUT).ptc
	rm -f $(MARTINOUT).pdf $(TIBOROUT).pdf
	rm -f *~
