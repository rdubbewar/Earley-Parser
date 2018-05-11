ou are to experiment with and enhance a simple Earley's Algorithm Parser using a small grammar ("simple.gr"), supplied in the attached ZIP file. Such a parser and grammars are supplied (written in Java); if you prefer, you may write your own parser (make sure to document your code). Also provided is a sample of sentences parsed. Please see the documentation files for details on code structure and running the parser.

Evaluate the parser in its current form:
What sorts of sentences can it parse?
What types of sentences will it fail to parse?
What improvements to the grammar or parser are required to properly parse common failed sentences?
Look at the grammar. What changes to the grammar alone could improve the parsing (in particular, consider coordinated conjunctions)? Modify the grammar somewhat and evaluate the improvements. What new sentences may be parsed? What invalid sentences are now parsed (if any)?
Modify the code to output the parse tree(s) computed, rather than just the dynamic programming table and whether the sentence parses. You will need to add appropriate back-links into the DP table and write a routine to extract trees from the table. Show the tree(s) by printing the input sentence in a bracketed form, for example:
S[ NP[ Det[ the ] N[ man ] ] VP[ V[ saw ] NP[ PN[ her ] ] ] ]
Modify the code to implement a version of probabilistic parsing for the Earley algorithm using a similar method as that discussed in class for the CYK algorithm. Each dotted rule will have a probability (log-probability) that is the product (sum) of the rule's probability (log-probability) with the probabilities (log-probabilities) of all the completed children it covers so far. Test the system using a probabilized version of simple.gr (see "prob-simple.gr"). Test the system on ambiguous sentences to see if the highest probability parses are indeed the most "reasonable" parses. Write a couple of ideas of how you might improve the system yet further.
