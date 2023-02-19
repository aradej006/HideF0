HideF0 steganography implementation.

More info in our article: Radej Adrian, Janicki Artur, In Proc. 23th International Conference on Text, Speech, and Dialogue - TSD 2020 / Sojka Petr et al. (Ed.), LNAI 2020, vol. 12284, Springer, pp.513-523, ISBN 978-3-030-58322-4. DOI:10.1007/978-3-030-58323-1_55

Building
--------
If you have apache maven installed, there is a pom.xml file in the root
directory.

Running on the Command line
---------------------------
encoder help: java -cp dist/jspeex.jar pl.pw.radeja.JSpeexEnc -h
or: java -jar dist/jspeex.jar -h
decoder help: java -cp dist/jspeex.jar pl.pw.radeja.JSpeexDec -h
ex:
encoding a wav file: java -cp dist/jspeex.jar pl.pw.radeja.JSpeexEnc input.wav output.spx
decoding any speex file: java -cp dist/jspeex.jar pl.pw.radeja.JSpeexDec input.spx output.wav


