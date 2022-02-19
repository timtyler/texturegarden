rd out /s /q
md out
del MinML2Test.tini
dir .\*.java /B /S > files
javac -bootclasspath e:\win32app\tini\bin\tiniclasses.jar -d out @files
java TINIConvertor -f out -o MinML2Test.tini -d e:\win32app\tini\bin\tini.db

