#tar -xvf confluent-7.0.1.tar.gz
export CONFLUENT_HOME="./confluent-7.0.1"
echo "$CONFLUENT_HOME"
export PATH=$PATH:$CONFLUENT_HOME/bin
echo "$PATH"
confluent local services start
while true
do
  echo "sleeping..."
	sleep 10
done
