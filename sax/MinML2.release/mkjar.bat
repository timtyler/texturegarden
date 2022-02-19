rd out /s /q
md out
del MinML2.jar
dir .\*.java /B /S > files
javac -d out @files
move out\MinML2Test.class .
cd out
jar cvf MinML2.jar -C . .
move minml2.jar ..
cd ..

