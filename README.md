# Shortest Path Calculator
This program is a  tool that tests the performance of Dijkstra's algorithm using two different priority queue data structures, the binary heap and the Fibonacci heap. The tool was developed as part of my senior thesis at King Abdulaziz University in 2013. 

**Note on ownership:** The Fibonacci heap and associated classes were originally implemented by [Keith Schwarz] (http://www.keithschwarz.com/). This project makes use of these implementations, with some modifications as appropriate. 

**Note on code:** I wrote this program as a college kid in 2013, before I knew there was such a thing as clean code. Please don't mind the fact that the code is a complete mess. Regardless, it works. Promise! 

## How to Run

To run the tool, execute the following command in the directory where the ```.jar``` file resides: 

```
> java -jar Run.jar 
```

This should launch the program. Follow the on-screen instructons to continue. 

## Input File Format 

To ensure the program functions properly, your input graph must be defined in ```.txt``` file according 
to a specific format. An example of a valid single-layer graph is the following: 

```
v0 A
v1 B
v2 C
v3 D
v4 E
v5 F
#
v0 v1 25.00
v0 v2 35.00
v1 v2 15.00
v1 v4 90.00
v2 v1 50.00
v2 v3 50.00
v2 v4 30.00
v3 v4 60.00
v3 v5 20.00
v4 v3 10.00
v4 v5 70.00
v5 v5 00.00
```

Where the lines above the  ```#``` denote nodes and their names, and the lines below denote the node connections
and their associated costs. (For example, ```v0 v1 25.00``` means there is a link of length 25 that goes from node ```v0```
to node ```v1```.)

Similarly, a valid multiplayer graph looks like this:

```
u0 A0
u1 B0
u2 C0
u3 D0
u4 E0
u5 F0
l0 A1
l1 B1
l2 C1
l3 D1
l4 E1
l5 F1
#
u0 u1 100.00
u0 u2 30.00
u1 u2 250.00
u1 u4 10.00
u2 u1 15.00
u2 u3 60.00
u2 u4 50.00
u3 u4 20.00
u3 u5 320.00
u4 u3 10.00
u4 u5 70.00
l0 l1 25.00
l0 l2 35.00
l1 l2 15.00
l1 l4 50.00
l2 l1 50.00
l2 l3 10.00
l2 l4 20.00
l3 l4 60.00
l3 l5 20.00
l4 l3 10.00
l4 l5 5.00
u0 l0 00.00
l0 u0 00.00
u1 l1 00.00
l1 u1 00.00
u2 l2 00.00
l2 u2 00.00
u3 l3 00.00
l3 u3 00.00
u4 l4 00.00
l4 u4 00.00
u5 l5 00.00
l5 u5 00.00
```
Where vertices that start with an L denote a lower-layer node and those that start with a U denote an upper-layer node. 
The tool will only recognize two-layer graphs. 


## Sample Images 

#### 1. Main menu

![alt tag] (http://i.imgur.com/s06DqfR.png) 

#### 2. Running the algorithm on a single-layer topology 

![alt tag] (http://i.imgur.com/t2PFwW6.png)

#### 3. Costum graph generator (included within the tool) 

![alt tag] (http://i.imgur.com/3iUzSHQ.png)

## Help

This program is provided "as-is" and may be used at your own risk, without any sort of warranty. For any questions or inquiries, please contact me at amalghannam@crimson.ua.edu. 


