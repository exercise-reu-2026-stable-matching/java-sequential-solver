NSAMPLES=1000

javac Main.java && 
java -ea Main $1 $NSAMPLES | ./mean2.py > B_i_over_n=$1.csv