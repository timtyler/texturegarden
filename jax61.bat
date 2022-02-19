C:\j2sdk1.4.2\bin\java -cp .;C:\j2sdk1.4.2\jre\lib\rt.jar;C:\Incoming\Java\IBM\JAX\jax61.zip;C:\Documents\Java\TextureGarden -DJAVAHOME=C:\Program1\Java\JDKs\Sun com.ibm.jax.Batch TextureGarden.jax
REM -DJAVAHOME=%javahome%

copy TextureGarden.zip jax\TextureGarden.zip
copy textures.zip jax\textures.zip

del TextureGarden.zip
cd jax

C:\j2sdk1.4.2\bin\jar xf TextureGarden.zip
C:\j2sdk1.4.2\bin\jar cmf Manifest.mf TextureGarden.jar *.class textures.zip

copy TextureGarden.jar ..\plugin\tg.jar
cd ..
