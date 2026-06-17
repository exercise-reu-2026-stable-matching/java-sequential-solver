javac Main.java &&

SAMPLES=10000
ITER=$1
printf "n,%s\n" $(java Main $SAMPLES 0 0) # print header
for n in {1..500} 
do
    printf "%d,%s\n" $n $(java Main $SAMPLES $n $ITER | tail -n 1)
done
