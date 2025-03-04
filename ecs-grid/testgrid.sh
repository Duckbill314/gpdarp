#$ -S /bin/sh
#$ -wd /vol/grid-solar/sgeusers/huangwill
##$ -M huangwill@myvuw.ac.nz
##$ -m be 

GRID_PATH="/vol/grid-solar/sgeusers/huangwill/darp"
JAR_PATH=$GRID_PATH"/package"
DATA_PATH=$GRID_PATH"/data"
ALGO_PATH=$GRID_PATH"/algorithm"

mkdir -p /local/tmp/huangwill/$JOB_ID 

if [ -d /local/tmp/huangwill/$JOB_ID ]; then
        cd /local/tmp/huangwill/$JOB_ID
else
        echo "There's no job directory to change into "
        echo "Here's LOCAL TMP "
        ls -la /local/tmp
        echo "AND LOCAL TMP FRED "
        ls -la /local/tmp/huangwill
        echo "Exiting"
        exit 1
fi

cp $JAR_PATH/GPTest.jar .
cp -r $DATA_PATH ./data
cp -r $ALGO_PATH/params ./params
cp -r $ALGO_PATH/train ./train
sleep 2

/usr/lib/jvm/java-21-openjdk/bin/java -jar GPTest.jar -file params/test.params -p train-path=train/ -p num-trains=30

cp -r train/debug $ALGO_PATH
cp -r train/test $ALGO_PATH
cd $ALGO_PATH/test
pwd
rm -fr /local/tmp/huangwill/$JOB_ID
