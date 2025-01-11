#$ -S /bin/sh
#$ -wd /vol/grid-solar/sgeusers/huangwill
##$ -M huangwill@myvuw.ac.nz
##$ -m be 

JAR_PATH="/vol/grid-solar/sgeusers/huangwill/gphhdeadarp/package"
DATA_PATH="/vol/grid-solar/sgeusers/huangwill/gphhdeadarp/data"
ALGO_PATH="/vol/grid-solar/sgeusers/huangwill/gphhdeadarp/algorithm"

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

cp $JAR_PATH/SimpleEvolve.jar .
cp -r $DATA_PATH ./data
cp -r $ALGO_PATH/params ./params
sleep 2

/usr/lib/jvm/java-21-openjdk/bin/java -jar SimpleEvolve.jar -file params/train.params -p seed.0=$(($SGE_TASK_ID-1)) -p stat.file=job.$(($SGE_TASK_ID-1)).out.stat

cp params/*.stat $ALGO_PATH/train
cp *.csv $ALGO_PATH/train
cd $ALGO_PATH/train
pwd
rm -fr /local/tmp/huangwill/$JOB_ID
