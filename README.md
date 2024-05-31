# FP-Growth for CSE304 Assignment #2

## Building
If you want to run this code in terminal, enter this sentences in project file.

```
mkdir out
cd out
mkdir t1
mkdir t2
```
```
javac -d out/t1 A2_G7_t1/src/*.java
javac -d out/t2 A2_G7_t2/src/*.java
```

After compiling the java file to out folder, Please move your **csv file** to **out/t1 and out/t2** folder.
Then enter this sentence in t1 folder.
```
java A2_G7_t1 {your csv file} {k}
```
In t2 folder.
```
java A2_G7_t2 {your csv file} {minPts} {Eps}
java A2_G7_t2 {your csv file} {minPts or Eps}
```
