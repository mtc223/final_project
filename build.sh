echo "Starting compilation"
cd mtc223
javac bm.java
javac numa.java
javac pm.java
javac tenant.java
javac ui.java
echo "Compilation successful, moving target files"
cd ..
mv mtc223/bm.class bin
mv mtc223/numa.class bin
mv mtc223/pm.class bin
mv mtc223/tenant.class bin
mv mtc223/ui.class bin
echo "Class files moved, creating jar"
cd bin
jar cfmv ui.jar Manifest.txt ui.class pm.class bm.class numa.class tenant.class
echo "jar created"
rm *.class
echo "removing class files"