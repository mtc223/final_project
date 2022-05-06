echo $1

if [ "$1" == "-b" ]; then
  sh build.sh;
fi

echo "Running jar"
cd bin
java -jar ui.jar