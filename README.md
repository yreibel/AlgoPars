Jar already produced in the source files provided. To start the code in your console, you need to use the command "java -jar AlgoPars.jar fichierTest.algo".
The argument fichierTest.algo is the file containing the pseudo-code variables you wanna track. The pseudo code is only accepted in french language.
You can check out an example of pseudo-code in "fichierTest.algo".

You can regenerate the .class files by executing this command : 'javac -d bin src/algopars/*.java src/algopars/commande/*.java'
You can also regenerate the .jar file by using the following command : "jar cfm AlgoPars.jar Manifest.txt -C bin ."
